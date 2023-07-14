/************************************************************************
 *
 *	NI-488 Driver for UNIX GPIB Interfaces
 *	Copyright (c) 1998 National Instruments Corporation
 *	All rights reserved.
 *
 *	OS-DEPENDENT LAYER for Linux 2.0.x for 80x86
 *
 ************************************************************************/

#define __KERNEL__ 1

#define GPIB_DEBUG 0

#define NULL 0

#define REALLY_SLOW_IO 1
#define _LOOSE_KERNEL_NAMES 1

#if LINUX_VERSION_CODE >= 0x020100
#include <asm/spinlock.h>
#endif
#include <linux/module.h>
#include <linux/config.h>
#include <linux/sched.h>
#include <linux/delay.h>
#include <linux/errno.h>
#include <linux/signal.h>
#include <linux/timer.h>
#include <linux/mm.h>
#include <linux/fs.h>
#include <linux/kernel.h>
#include <linux/hdreg.h>
#include <linux/genhd.h>
#if LINUX_VERSION_CODE >= 0x020100
#include <linux/slab.h>
#else
#include <linux/malloc.h>
#endif
#include <linux/string.h>
#include <linux/ioport.h>
#include <linux/pci.h>

#include <asm/irq.h>
#include <asm/io.h>
#include <asm/dma.h>
#include <asm/semaphore.h>
#include <asm/delay.h>

#if LINUX_VERSION_CODE >= 0x020100
#include <asm/uaccess.h>
#else
#include <linux/bios32.h>

static inline unsigned long
copy_to_user(void *to, const void *from, unsigned long n)
{
        memcpy_tofs(to,from,n);
        return 0;
}

static inline unsigned long
copy_from_user(void *to, const void *from, unsigned long n)
{
        memcpy_fromfs(to,from,n);
        return 0;
}
#define ioremap vremap
#define iounmap vfree

#endif



#include "cib.h"		/* NI-488.2M driver include files... */
#include "ugpib.h"
#include "ibconf.h" 

#include "ib_linux.h"

#include "ib_ni.h"

#if GPIB_DEBUG
static int      dbgMask         = DBG_MASK_INIT;
static char     *dbgFnID        = "*****";
static char     *dbgAsyncFnID   = "*****";
#endif

/************************************************************************
 * Required OS-Support Routines for Other NI-488.2M Driver Layers
 ************************************************************************/

/*
 * The routines in this section provide the primary operating system
 * interface to the other layers of the driver.  These routines should
 * be called using the corresponding macros defined in "ib.h".
 * Most of these routines are very similar in functionality to the
 * equivalent routines found in Sun Solaris 2 and other UNIX operating
 * systems.  Note that if a direct mapping from a macro to an equivalent
 * system routine is possible, then the corresponding "ib_" routine
 * would not be required in this file -- the macro could simply evaluate
 * to the actual system call.
 *
 * Although some of these "ib_" routines are not actually necessary
 * under Linux, they are all listed here for reference purposes:
 *
 *   void ib_mutex_enter(ib_mutex_t *mutex)
 *   void ib_mutex_exit(ib_mutex_t *mutex)
 *   void ib_mutex_init(ib_mutex_t *mutex, char *name, int type, void *arg)
 *   void ib_mutex_destroy(ib_mutex_t *mutex)
 *
 *   void ib_cv_init(ib_condvar_t *cvp, char *name, int type, void *arg)
 *   void ib_cv_destroy(ib_condvar_t *cvp)
 *   void ib_cv_wait(ib_condvar_t *cvp, ib_mutex_t *mutex)
 *   void ib_cv_signal(ib_condvar_t *cvp)
 *
 *   caddr_t ib_kmem_zalloc(int size)
 *   void ib_kmem_free(caddr_t ptr, int size)
 *   void ib_bzero(char *ptr, int size)
 *   void ib_bcopy(char *src, char *dest, int length)
 *   int ib_copyin(caddr_t userbuf, caddr_t driverbuf, int length)
 *   int ib_copyout(caddr_t driverbuf, caddr_t userbuf, int length)
 *
 *   int ib_getmajor(dev_t dev)
 *   ib_minor_t ib_getminor(dev_t dev)
 *   int ib_makedevice(bustbl_t *bus)
 *
 *   void ib_delay(int usecs)
 *   int ib_timeout(FuncPtr func, caddr_t arg, int usecs DBG_ASYNC_ARG)
 *   int ib_untimeout(int id DBG_ASYNC_ARG)
 *
 *   ib_pid_t ib_getpid()
 *
 *   uint8 ib_memIn(uint8 *addr, int sysflag)
 *   uint16 ib_memIn16(uint16 *addr, int sysflag)
 *   void ib_memOut(uint8 *addr, uint8 value, int sysflag)
 *   void ib_memOut16(uint16 *addr, uint16 value, int sysflag)
 *
 *   int ib_iopeek()
 *   int ib_iopoke()
 */


#if LINUX_VERSION_CODE < 0x020100
#define spin_lock_irqsave(m, f)    { save_flags(f); cli(); }
#define spin_unlock_irqrestore(m, f) { restore_flags(f); }
#endif

#if LINUX_VERSION_CODE < 0x020300
#if !defined(init_MUTEX_LOCKED) && defined(MUTEX_LOCKED)
/* Macro didn't appear until 2.2.17; is an inline function in 2.4 */
#define init_MUTEX(x)                           *(x)=MUTEX
#define init_MUTEX_LOCKED(x)                    *(x)=MUTEX_LOCKED
#endif
#endif

/*
 * MUTEX_ENTER
 * Enter a mutex.  If a "non-event" mutex is not currently available,
 * the process will wait until it is.  An "event" mutex is implemented
 * as simply the blocking out of all interrupts -- such mutexes should
 * be held for only limited periods of time.  No waiting required.
 */

void ib_mutex_enter(ib_mutex_t *mutex)
{
	DBGin("ib_mutex_enter");
	if (mutex->evblock) {
		spin_lock_irqsave(&mutex->ib_lock.lock, mutex->flags);
	} else {
		spin_lock_irqsave(&mutex->ib_lock.lock, mutex->flags);
		while (mutex->busy) {
			mutex->wanted++;
			down_interruptible(&mutex->ib_sem.sem);
			mutex->wanted--;
		}
		mutex->busy = 1;
		spin_unlock_irqrestore(&mutex->ib_lock.lock, mutex->flags);
	}
	DBGout();
}


/*
 * MUTEX_EXIT
 * Exit a mutex and wake up anyone that may be waiting on a
 * non-event mutex.
 */
void ib_mutex_exit(ib_mutex_t *mutex)
{
	DBGin("ib_mutex_exit");
	if (mutex->evblock) {
		spin_unlock_irqrestore(&mutex->ib_lock.lock, mutex->flags);
	} else {
		spin_lock_irqsave(&mutex->ib_lock.lock, mutex->flags);
		mutex->busy = 0;
		if (mutex->wanted)
			up(&mutex->ib_sem.sem);
		spin_unlock_irqrestore(&mutex->ib_lock.lock, mutex->flags);
	}
	DBGout();
}


void ib_mutex_init(ib_mutex_t *mutex, char *name, int type, void *arg)
{
	unsigned long flags;
	DBGin("ib_mutex_init");
	save_flags(flags);
	cli();
	mutex->busy = 0;
	mutex->wanted = 0;
	mutex->evblock = (type == IB_EVMUTEX_DRIVER);
        init_MUTEX_LOCKED(&mutex->ib_sem.sem);
#if LINUX_VERSION_CODE >= 0x020100
        spin_lock_init(&mutex->ib_lock.lock);
#endif
	restore_flags(flags);
	DBGout();
}


void ib_mutex_destroy(ib_mutex_t *mutex)
{
	/* nothing needs to be done here */
}


/*
 * CONDITION VARIABLE SUPPORT ROUTINES
 * Note that a condition variable, by definition, is "stateless", meaning
 * that a signal on a condition variable should only awaken a process that
 * is currently waiting on the event.  A signal that occurs *before* there
 * is someone waiting on it should not be noticed.  These routines attempt
 * to mimic this behavior under Linux as closely as possible.
 */
void ib_cv_init(ib_condvar_t *cvp, char *name, int type, void *arg)
{
	unsigned long flags;
	DBGin("ib_cv_init");
	save_flags(flags);
	cli();
	cvp->wanted = 0;
        init_MUTEX_LOCKED(&cvp->ib_sem.sem);
#if LINUX_VERSION_CODE >= 0x020100
        spin_lock_init(&cvp->ib_lock.lock);
#endif
	restore_flags(flags);
	DBGout();
}


void ib_cv_destroy(ib_condvar_t *cvp)
{
	/* nothing needs to be done here */
}


/*
 * CV_WAIT
 * Note that only one process gets to wake up per CV signal.
 */
void ib_cv_wait(ib_condvar_t *cvp, ib_mutex_t *mutex)
{
	DBGin("ib_cv_wait");
	if (mutex->evblock) {
		/*
		 * NOTE: For event-blocking mutexes, the
		 * cli is assumed to have been done by 'mutex_enter'
		 */
		cvp->wanted++;
		spin_unlock_irqrestore(&mutex->ib_lock.lock, mutex->flags);
		down(&cvp->ib_sem.sem);
		spin_lock_irqsave(&mutex->ib_lock.lock, mutex->flags);
		cvp->wanted--;
		if (!cvp->wanted)
			init_MUTEX_LOCKED(&cvp->ib_sem.sem);
	}
	else {
		unsigned long flags;
		spin_lock_irqsave(&mutex->ib_lock.lock, flags);
		cvp->wanted++;
		IB_mutex_exit(mutex);
		down_interruptible(&cvp->ib_sem.sem);
		IB_mutex_enter(mutex);
		cvp->wanted--;
		if (!cvp->wanted)
			init_MUTEX_LOCKED(&cvp->ib_sem.sem);
		spin_unlock_irqrestore(&mutex->ib_lock.lock, flags);
	}
	DBGout();
}


/*
 * CV_SIGNAL
 * Signal the condition variable and wake up ONE process that is
 * waiting for the event to occur.
 */
void ib_cv_signal(ib_condvar_t *cvp)
{
	unsigned long flags;
	DBGin("ib_cv_signal");
	spin_lock_irqsave(&cvp->ib_lock.lock, flags);
	if (cvp->wanted) {
		up(&cvp->ib_sem.sem);
	}
	spin_unlock_irqrestore(&cvp->ib_lock.lock, flags);
	DBGout();
}

/* ------------------------------------------------------------- */

/*
 * MEMORY ALLOCATION ROUTINES
 * Note that on Solaris, the equivalent "kmem_zalloc" routine
 * requires a second argument to indicate whether the call
 * can sleep.  Our driver always passes in the constant KM_NOSLEEP
 * for this second argument.
 */
caddr_t ib_kmem_zalloc(int size)
{
	/*
 	 * MALLOC and FREE are recommended over kalloc, kfree 
 	 * in new drivers supporting DEC OSF/1 3.x.
	 */
	caddr_t	ptr;

	DBGin("ib_kmem_zalloc");
	ptr =  kmalloc(size, GFP_KERNEL);
	if (ptr)
		memset(ptr, 0, size);
	DBGout();
	return ptr;
}


void ib_kmem_free(caddr_t ptr, int size)
{
	DBGin("ib_kmem_free");
	if (ptr != (caddr_t) NULL)
		kfree(ptr);		/* size is not used! */
	DBGout();
}


void ib_bzero(char *ptr, int size)
{
	memset(ptr, 0, size);
}


void ib_bcopy(void *src, void *dest, int length)
{
	memcpy(dest, src, length);
}


int ib_copyin(caddr_t userbuf, caddr_t driverbuf, int length)
{
        copy_from_user(driverbuf, userbuf, length); 
        return GPIB_SUCCESS;
}


int ib_copyout(caddr_t driverbuf, caddr_t userbuf, int length)
{
        copy_to_user(userbuf, driverbuf, length); 
        return GPIB_SUCCESS;
}

/* ------------------------------------------------------------------ */

/*
 * DEVICE MANAGEMENT ROUTINES
 */
int ib_getmajor(kdev_t dev)
{
	return MAJOR(dev);
}


ib_minor_t ib_getminor(kdev_t dev)
{
	return MINOR(dev);
}

int ib_makedevice(bustbl_t *bus)
{
        if (!bus->devt)
                bus->devt = MKDEV(gpib_major, bus->minor);
}

/*
 * TIMING SUPPORT ROUTINES
 */
void ib_delay(int usecs)
{
	udelay(usecs);
}

static ib_timeout_t	g_timeouttbl[NTIMEOUT] = {0};

int ib_timeout(FuncPtr func, caddr_t arg, int usecs)
{
	int	ticks;
	int	i, id;

	DBGin("ib_timeout");
	id = -1;
	for (i = 0; i < NTIMEOUT; i++) {
		if (g_timeouttbl[i].function == NULL) {
			g_timeouttbl[i].function = (void (*)(unsigned long))func;
			g_timeouttbl[i].data = (int)arg;
			id = i + 1;
			break;
		}
	}
#if (HZ >= 10)
#if (HZ >= 100)
	ticks = ((HZ/100 * usecs) / 10000) + 1;
#else
	ticks = ((HZ/10 * usecs) / 100000) + 1;
#endif
#else
	ticks = ((HZ/1 * usecs) / 1000000) + 1;
#endif
	DBGprint(DBG_DATA, ("timerid=%d usecs=%d ticks=%d", id, usecs, ticks));
        g_timeouttbl[i].expires = jiffies + ticks;
	add_timer(&g_timeouttbl[i]);

	DBGout();
	return id;
}


int ib_untimeout(int id)
{
	int	idx;
	int	utmostat;

	DBGin("ib_untimeout in");
	if ((id > 0) && (id <= NTIMEOUT)) {
		idx = id - 1;
		cli(); /* block clock interrupts */
                utmostat = del_timer(&g_timeouttbl[idx]);
		DBGprint(DBG_DATA, ("timerid=%d untimeout=%d", id, utmostat));
		g_timeouttbl[idx].function = NULL;
		sti();
	}
	else if (id > NTIMEOUT)
		id = -1;

	DBGout();
	return id;
}


/*
 * PROCESS MANAGEMENT ROUTINES
 */
ib_pid_t ib_getpid()
{
	return current->pid;
}

void ib_initsignal(bustbl_t *bus)
{
	DBGin("ib_initsignal");
	bus->sigpid = current;
	DBGout();
}


void ib_resetsignal(bustbl_t *bus)
{
	DBGin("ib_resetsignal");
	return;					/* unneeded for Linux */
	DBGout();
}


void ib_postsignal(bustbl_t *bus)
{
	DBGin("ib_postsignal");
	send_sig(bus->cur_param.signo, bus->sigpid, 0);
	DBGout();
}


/*
 * MEMORY I/O ROUTINES
 */
uint8 ib_memIn(uint8 *addr, int sysflag)
{
	uint8	value;
	int     err;

	if (sysflag) {
		if (addr == NULL) {
			DBGprint(DBG_SPEC, ("NULL POINTER!"));
			return 0;
		}
		return *addr;
	}
	else {
		err = verify_area(VERIFY_READ, addr, sizeof(uint8));
                if (err) return 0;
#if LINUX_VERSION_CODE >= 0x020100
                get_user(value, addr);
#else
                value = get_user(addr);
#endif
		return value;
	}
}


uint16 ib_memIn16(uint16 *addr, int sysflag)
{
	uint16	value;
	int     err;

	if (sysflag)
		return *addr;
	else {
		err = verify_area(VERIFY_READ, addr, sizeof(uint16));
                if (err) return 0;
#if LINUX_VERSION_CODE >= 0x020100
                get_user(value, addr);
#else
                value = get_user(addr);
#endif
		return value;
	}
}


void ib_memOut(uint8 *addr, uint8 value, int sysflag)
{
	if (sysflag)
		*addr = value;
	else {
                int err = verify_area(VERIFY_WRITE, addr, sizeof(uint8));
		DBGprint(DBG_SPEC, ("copyout err=0x%x", err));
                if (err) return;
		put_user(value, addr);
	}
}


void ib_memOut16(uint16 *addr, uint16 value, int sysflag)
{
	if (sysflag)
		*addr = value;
	else {
		int err = verify_area(VERIFY_WRITE, addr, sizeof(uint16));
		DBGprint(DBG_SPEC, ("copyout err=0x%x", err));
                if (err) return;
		put_user(value, addr);
	}
}


/*
 * IOPEEK
 * Attempt to read an 8-bit register at the specified I/O address.
 * Return 0 on failure, 1 on success, or 1 if the success or failure
 * of the operation could could not be determined.
 */
int ib_iopeek()
{
	return 1;
}


/*
 * IOPOKE
 * Attempt to write an 8-bit register at the specified I/O address.
 * Return 0 on failure, 1 on success, or 1 if the success or failure
 * of the operation could could not be determined.
 */
int ib_iopoke()
{
	return 1;
}


/* Remap PCI addresses above 1M so they can be accessed directly by
   the kernel.  The address given to ioremap must begin on a page boundary,
   so round down and adjust the size accordingly. */
unsigned long ib_remap_pci_addr(unsigned long addr, unsigned long size)
{
     unsigned long newaddr = addr & PAGE_MASK;
     unsigned long ofst = addr - newaddr;
     newaddr = (unsigned long)ioremap(newaddr, size + ofst);
     if (newaddr)
         newaddr += ofst;
     return newaddr;
}

void ib_free_pci_addr(unsigned long addr)
{
	if (addr) 
		iounmap((void*)(addr & PAGE_MASK));
}

/* new */
caddr_t ib_dma_kmem_zalloc(int size)
{
	caddr_t	ptr;

	ptr =  kmalloc(size, GFP_KERNEL|GFP_DMA);
	if (ptr)
		memset(ptr, 0, size);
	return ptr;
}

/************************************************************************
 * DMA Support Routines for OS and Hardware Layers
 ************************************************************************/

/*
 * DMAALLOC
 * Perform any up-front DMA resource allocation needed before an I/O
 * call gets underway.  This function should be called only once per
 * ni488_ibrd/ibwrt driver call.
 */
int ib_DMAalloc(bustbl_t *bus, lowlevel_param *hwp) 
{
	DBGin("ib_DMAalloc");
	/*
	 * Nothing needs to be done here for now.
	 */
	DBGout();
	return BDONE;
}


/*
 * DMARELSE
 * Perform any release or clean-up of DMA resources needed after an I/O
 * call has completed.  This function should be called only once per
 * ni488_ibrd/ibwrt driver call.
 */
int ib_DMArelse(bustbl_t *bus, lowlevel_param *hwp) 
{
	DBGin("ib_DMArelse");
	/*
	 * Nothing needs to be done here for now.
	 */
	DBGout();
	return BDONE;
}


/*
 * ADJDMACNT
 * Adjust DMA I/O count for the maximum transferable value.
 */
int ib_adjDMAcnt(bustbl_t *bus, lowlevel_param *hwp) 
{
	DBGin("ib_adjDMAcnt");

        if (bus->hwareType == HW_ATGPIB) {
		int32 maxcount;

		if (((unsigned) hwp->buf) & (PAGE_SIZE - 1))
			maxcount = (-((unsigned) hwp->buf)) & (PAGE_SIZE - 1);
		else
			maxcount = PAGE_SIZE;
		DBGprint(DBG_DATA, ("maxcount=%d (0x%x)", maxcount, maxcount));

		if (hwp->cnt > maxcount) {
			DBGprint(DBG_BRANCH, ("cnt adjusted to maxcount"));
			hwp->cnt = maxcount;
			DBGout();
                        return 1;
		}
        } else {
#ifdef USE32BITDMACOUNTS
		stdBRDglobals_t		*stg;
		stg = (stdBRDglobals_t *) bus->hwdata;
                if (stg->dmaflags & DF_CNT32) {
                        if (hwp->cnt > OSDMA_MAXCNT) {
				DBGprint(DBG_DATA, ("cnt adjusted to 0x%x", OSDMA_MAXCNT));
                                hwp->cnt = OSDMA_MAXCNT;
                                return 1;
                        }
                }
                else
#endif
		if (hwp->cnt > 0xFFFE) { /* maximum 16-bit counter value */
                        DBGprint(DBG_DATA, ("cnt adjusted to 0xFFFE"));
                        hwp->cnt = 0xFFFE;
                        return 1;
                }
        }

	DBGout();
	return 0;
}


/*
 * DMASTART
 * Prepare and initiate a DMA I/O operation.
 * Called from the hardware layer.
 */
int ib_DMAstart(bustbl_t *bus, lowlevel_param *hwp) 
{
	stdBRDglobals_t		*stg;
	int			err;
        unsigned int		buf, cnt;
	int 			hwType;
	caddr_t			buffer;
	DBGin("ib_DMAstart");

	stg = (stdBRDglobals_t *) bus->hwdata;

        hwType = bus->hwareType;
        buffer = hwp->buf;
        cnt = hwp->cnt;
        bus->hwp = hwp;
        stg->dmaresid = 0;			/* will be adjusted later */

	if (!(stg->dmaflags & DF_READ)) {
		if (hwp->ioflags & LLP_KERN)
                	memcpy(bus->os.dmabuf, buffer, cnt);
		else {
                        ib_copyin(buffer, bus->os.dmabuf, cnt);
                }
        }
        if (hwType==HW_ATGPIB) {
#if LINUX_VERSION_CODE >= 0x020100
		unsigned long flags;
		flags=claim_dma_lock();
#else
                cli();
#endif
                disable_dma(stg->dmachan);
	        clear_dma_ff(stg->dmachan);
                set_dma_count(stg->dmachan, cnt);
                buf = virt_to_phys(bus->os.dmabuf);
                set_dma_addr(stg->dmachan, buf);

		if (stg->dmaflags & DF_READ) {
                        set_dma_mode(stg->dmachan, DMA_MODE_READ);
                }
                else {
                        set_dma_mode(stg->dmachan, DMA_MODE_WRITE);
                }
                enable_dma(stg->dmachan);
#if LINUX_VERSION_CODE >= 0x020100
		release_dma_lock(flags);
#else		        
                sti();
#endif
                }
        else if (hwType==HW_PCIGPIB) {
               /* hack so as not to have to include the PCI/MITE DMAEngine
                  headers */
                struct DMAEngine_t {
                    void *d1, *d2;
                    uint32 num_cookies;
                    };
	        ((struct DMAEngine_t *)(stg->dmaengine))->num_cookies = 1;
        }

	hwp->pbuf = (void *)virt_to_phys(bus->os.dmabuf);
	hwp->pcnt = hwp->cnt;		/* required by HW layer */
	(*bus->func[HW_DMASTART])(bus, 0);
	ib_DMAstop(bus DBG_ASYNC_NO);	/* I/O already done */

	if (stg->dmaflags & DF_READ) {
		if (hwp->ioflags & LLP_KERN) {
	                memcpy(buffer, bus->os.dmabuf, cnt);
                }
		else {
                        ib_copyout(bus->os.dmabuf, buffer, cnt);
                }
        }

	DBGprint(DBG_DATA, ("dmaresid=%d", stg->dmaresid));
	DBGout();
	return stg->dmaresid;
}

/*
 * DMASTOP
 * Stop and clean up after a DMA I/O operation.
 * Called from the OS layer, usually during interrupt servicing.
 */
void ib_DMAstop(bustbl_t *bus DBG_ASYNC_ARG) 
{
	stdBRDglobals_t		*stg;
	int 			hwType;

	DBGinCond("ib_DMAstop");
	stg = (stdBRDglobals_t *) bus->hwdata;
        hwType = bus->hwareType;

	(*bus->func[HW_DMASTOP])(bus DBG_ASYNC_OP);

	if (hwType==HW_ATGPIB) {
		disable_dma(stg->dmachan);
		if (stg->dmaflags & DF_8BIT) {
		        clear_dma_ff(stg->dmachan);
				stg->dmaresid = get_dma_residue(stg->dmachan);
                }
        }
	bus->hwp->cnt -= bus->hwp->pcnt;

	DBGoutCond();
}

#define PCI_NI_VENDOR_ID		0x1093
#define PCI_NI_PCIGPIB_DEVICE_ID	0xc801
#define PCI_NI_PCIGPIBP_DEVICE_ID	0xc811
#define PCI_NI_PXIGPIB_DEVICE_ID	0xc821
#define PCI_NI_PMCGPIB_DEVICE_ID	0xc831

static int supported_ni_devices[] = { PCI_NI_PCIGPIBP_DEVICE_ID, PCI_NI_PCIGPIB_DEVICE_ID, PCI_NI_PXIGPIB_DEVICE_ID, PCI_NI_PMCGPIB_DEVICE_ID };

#if LINUX_VERSION_CODE < 0x20300
#define PCI_BASE_ADDRESS(dev, n) ((dev)->base_address[n])
#else
#define PCI_BASE_ADDRESS(dev, n) ((dev)->resource[n].start)
#endif

int ib_pci_detect(int index, int *pci_index, uint32 *base0, uint32 *base1, char *irq) {
        int rval = 0;

#if LINUX_VERSION_CODE >= 0x020100
        struct pci_dev *dev;
        int i = 0, j = 0, dev_id;
        unsigned short cmd_reg;

	while (dev_id = supported_ni_devices[j++])
	{
	    dev = NULL;
	    while ((dev= pci_find_device(PCI_NI_VENDOR_ID, dev_id, dev)))
	    {
                if (dev->vendor != PCI_NI_VENDOR_ID || dev->device != dev_id)
		    continue;
                if (i++ != *pci_index)
		    continue;
                *base0 = PCI_BASE_ADDRESS(dev, 0);
                *base1 = PCI_BASE_ADDRESS(dev, 1);
                *irq   = dev->irq;
		if (!*irq) {
		    printk("nigpib: no IRQ allocated in PCI config space:\n");
		    printk("nigpib: base0: %08x base1: %08x irq:%d\n",
		    		*base0, *base1, *irq);
		    rval = -ENODEV;
		    goto pci_out;
		}
                pci_set_master(dev);
                pci_read_config_word(dev, PCI_COMMAND, &cmd_reg);
                if (!(cmd_reg & PCI_COMMAND_MEMORY) ||
                    !(cmd_reg & PCI_COMMAND_MASTER)) {

                    DBGprint(DBG_BRANCH, ("PCI config space is not set up correctly."));
                    DBGprint(DBG_BRANCH, ("The driver needs to enable MSTREN and MEMEN."));

                    cmd_reg |= (PCI_COMMAND_MEMORY | PCI_COMMAND_MASTER);
                    pci_write_config_word(dev, PCI_COMMAND, cmd_reg);
                    pci_read_config_word(dev, PCI_COMMAND, &cmd_reg);

                    DBGprint(DBG_DATA, ("Command register after the manual write: 0x%x", cmd_reg));

                    if (!(cmd_reg & PCI_COMMAND_MEMORY) ||
                        !(cmd_reg & PCI_COMMAND_MASTER)) {
                        DBGprint(DBG_BRANCH, ("Fatal Error: MSTREN, MEMEN still not set in command register!!"));
                        printk("nigpib: failed to enable PCI busmastering\n");
			rval = -ENODEV;
			goto pci_out;
			}
		}
                break;
	    }
	    if (dev)
		break;
	}
	if (!dev) {
	    rval = -ENODEV;
	    goto pci_out;
	}
#else /* Linux 2.0.x */
	unsigned char busNo, deviceNo;

        if (!pcibios_present()) {
		rval = -ENODEV;
		goto pci_out;
        }

	if (pcibios_find_device(PCI_NI_VENDOR_ID, PCI_NI_PCIGPIB_DEVICE_ID, *pci_index,
                                &busNo, &deviceNo) != 0
	    && pcibios_find_device(PCI_NI_VENDOR_ID, PCI_NI_PCIGPIBP_DEVICE_ID, *pci_index,
                                &busNo, &deviceNo) != 0
	    && pcibios_find_device(PCI_NI_VENDOR_ID, PCI_NI_PXIGPIBP_DEVICE_ID, *pci_index,
                                &busNo, &deviceNo) != 0
	    && pcibios_find_device(PCI_NI_VENDOR_ID, PCI_NI_PMCGPIBP_DEVICE_ID, *pci_index,
                                &busNo, &deviceNo) != 0

	    ) {
		rval = -ENODEV;
		goto pci_out;
	}

	pcibios_read_config_dword(busNo, deviceNo, PCI_BASE_ADDRESS_0, base0);
	pcibios_read_config_dword(busNo, deviceNo, PCI_BASE_ADDRESS_1, base1);

	pcibios_read_config_byte(busNo, deviceNo, PCI_INTERRUPT_LINE, irq);

#endif

        *base0 &= PCI_BASE_ADDRESS_MEM_MASK;
        *base1 &= PCI_BASE_ADDRESS_MEM_MASK;

	ibinfo[index].ib_phys_pbar0 = *base0;
	ibinfo[index].ib_phys_pbar1 = *base1;

	DBGprint(DBG_DATA, ("Index: %ld Base0: 0x%lx Base1: 0x%lx Irq: %d", *pci_index, *base0, *base1, *irq));

	if (*base0 > 0x100000)
                *base0 = ib_remap_pci_addr(*base0, 0x800);
	if (*base1 > 0x100000)
                *base1 = ib_remap_pci_addr(*base1, 0x4000);

        if (!*base0 || !*base1) {
                printk("gpib: can't remap base addresses (0x%lx, 0x%lx)\n", base0, base1);
                rval = -ENODEV;
	}
pci_out:
        if (!rval)
                (*pci_index)++;
        return rval;
}

int ib_register_chrdev(unsigned int major, const char *name, struct file_operations *ops) { return register_chrdev(major, name, ops); }
int ib_unregister_chrdev(unsigned int major, const char * name) {
    return unregister_chrdev(major, name); }
int ib_request_dma(unsigned int dmanr, const char * device_id) {
    return request_dma(dmanr, device_id); }
void ib_request_region(unsigned long from, unsigned long extent,const char *name) {
    request_region(from, extent, name); }
void ib_release_region(unsigned long from, unsigned long extent) {
    return release_region(from, extent); }
int ib_request_irq(unsigned int irq,
                   void (*handler)(int, void *, struct pt_regs *),
                   unsigned long flags,
                   const char *device,
                   void *dev_id) {
    return request_irq(irq, handler, flags, device, dev_id); }
void ib_free_dma(unsigned int dmar) { free_dma(dmar); }
void ib_free_irq(unsigned int irq, void *dev_id) { free_irq(irq, dev_id); }

int ib_verify_read(const void *mem, unsigned long count) {
 return verify_area(VERIFY_READ, mem, count); }
int ib_verify_write(const void *mem, unsigned long count) {
 return verify_area(VERIFY_WRITE, mem, count); }

int ib_printk(const char *fmt, ...)
{
        va_list args;
        char buf[1024];
        va_start(args, fmt);
        vsprintf(buf, fmt, args);
        va_end(args);
        printk ("%s", buf);
}

#ifdef MODULE
void ib_inc_module(void) { MOD_INC_USE_COUNT; }
void ib_dec_module(void) { MOD_DEC_USE_COUNT; }
int ib_mod_in_use(void) { return MOD_IN_USE; }
#endif

/*
 * Driver Entry points and other Configuration routines
 */

#if LINUX_VERSION_CODE >= 0x020100
ssize_t ib_read(struct file *file, char *buf, size_t count, loff_t *ppos);
ssize_t ib_write(struct file *file, const char *buf, size_t count, loff_t *ppos);
int ib_close(struct inode *inode, struct file *file);
#else
int ib_read(struct inode *inode, struct file *file, char *buf, int count);
int ib_write(struct inode *inode, struct file *file, const char *buf, int count);
void ib_close(struct inode *inode, struct file *file);
#endif
int ib_ioctl(struct inode *inode, struct file *file, unsigned int cmd, unsigned long arg);
int ib_open(struct inode *inode, struct file *file);

static struct file_operations ib_fops = {
#if LINUX_VERSION_CODE >= 0x020300
    owner:THIS_MODULE,
#endif
    read:ib_read,
    write:ib_write,
    ioctl:ib_ioctl,
    open:ib_open,
    release:ib_close
};

ib_major_t	gpib_major;

int ib_open(struct inode *inode, struct file *file)
{
	ib_pid_t	pid;
	ib_minor_t	minor;
	ibarg_t		cp;
	int		rval = 0;

#if (!GPIB_TRACE)
	DBGin("ib_open");
#else
	char *dbgFnID = "ib_open";

	if (IB_getminor(inode->i_rdev) == DEBUGMINOR)
		return 0;
	DBGprint(DBG_ENTRY, ("in"));
#endif
	DBGprint(DBG_ENTRY, ("++++++++"));

	gpib_major = IB_getmajor(inode->i_rdev) ;
	minor = IB_getminor(inode->i_rdev);
	pid = IB_getpid();
	DBGprint(DBG_DATA, ("pid=0x%lx", pid));
	DBGprint(DBG_DATA, ("major=%d minor=%d", gpib_major, minor));

        cp.n = inode->i_rdev;
        rval = ib_attach(minor, &cp);
        if (rval == 0)
	        ib_inc_module();
	DBGprint(DBG_DATA, ("rval=%d", rval));

	DBGout();
	DBGprint(DBG_EXIT, ("--------"));
	return(rval);
}


#if LINUX_VERSION_CODE >= 0x020100
int ib_close(struct inode *inode, struct file *file)
#else
void ib_close(struct inode *inode, struct file *file)
#endif
{
	ib_minor_t	minor;
	ibarg_t		cp;
        int		rval = 0;

#if (!GPIB_TRACE)
	DBGin("ib_close");
#else
	char *dbgFnID = "ib_close";

	if (IB_getminor(inode->i_rdev) == DEBUGMINOR)
#if LINUX_VERSION_CODE >= 0x020100
		return 0;
#else
		return;
#endif
	DBGprint(DBG_ENTRY, ("in"));
#endif
	DBGprint(DBG_ENTRY, ("++++++++"));
	minor = IB_getminor(inode->i_rdev);

        cp.n = inode->i_rdev;
        rval = ib_unattach(minor, &cp);
        if (rval == 0)
	        ib_dec_module();
	DBGout();
	DBGprint(DBG_EXIT, ("--------"));
#if LINUX_VERSION_CODE >= 0x020100
        return rval;
#endif
}

#if LINUX_VERSION_CODE >= 0x020100
ssize_t ib_read(struct file *file, char *buf, size_t count, loff_t *ppos)
{
	int rval;
        struct inode *inode = file->f_dentry->d_inode;
        DBGin("ib_read");
	rval = ib_rw(inode->i_rdev, buf, count, IBRD);
	DBGout();
	return rval;
}

ssize_t ib_write(struct file *file, const char *buf, size_t count, loff_t *ppos)
{
	int rval;
        struct inode *inode = file->f_dentry->d_inode;

        DBGin("ib_write");
	rval = ib_rw(inode->i_rdev, (char*)buf, count, IBWRT);
	DBGout();
	return rval;
}
#else
int ib_read(struct inode *inode, struct file *file, char *buf, int count)
{
	int rval;

        DBGin("ib_read");
	rval = ib_rw(inode->i_rdev, buf, count, IBRD);
	DBGout();
	return rval;
}


int ib_write(struct inode *inode, struct file *file, const char *buf, int count)
{
	int rval;
        DBGin("ib_write");
	rval = ib_rw(inode->i_rdev, (char*)buf, count, IBWRT);
	DBGout();
	return rval;
}
#endif

int ib_ioctl(struct inode *inode, struct file *file, unsigned int cmd, unsigned long arg)
{
	int rval;
 
        DBGin("ib_ioctl");
        rval = ib_exec(IB_getminor(inode->i_rdev), cmd, arg);
	DBGout();
	return rval;
}

#define GPIB_MAJOR 31


/*
 * HARDWARE INSTALLATION SETTINGS
 * Number of buses (NBUSES) is configured to be '4' in ibconf.h.
 * Need to update this static initialization part if NBUSES is changed.
 *
 * The ibinfo_t structure is defined in ib.h.  For the AT-GPIB, the
 * ib_baseaddr field and ib_intrNum field must be determined and
 * hardcoded here by the application/driver developer.  For the PCI-GPIB,
 * the ib_baseaddr field is not used, and the ib_intrNum and ib_pbarX
 * fields are set dynamically by the driver in ib_probe.
 */
ibinfo_t ibinfo[NBUSES] = {
	{					/* GPIB0 */
	        0x2c0, 				/* Configured for AT-GPIB */
		11, 0, 0, 5, 0, 0, NULL
	},
	{					/* GPIB1 */
	        INVALID_BASE_ADDRESS,		/* Available for PCI-GPIB */
		INVALID_INTERRUPT_NUM, 0, 0, 0, 0, 0, NULL
	},
	{					/* GPIB2 */
	        INVALID_BASE_ADDRESS,		/* Available for PCI-GPIB */
		INVALID_INTERRUPT_NUM, 0, 0, 0, 0, 0, NULL
	},
	{					/* GPIB3 */
	        INVALID_BASE_ADDRESS,		/* Available for PCI-GPIB */
		INVALID_INTERRUPT_NUM, 0, 0, 0, 0, 0, NULL
	}
};


#ifdef MODULE

#if LINUX_VERSION_CODE >= 0x020117
MODULE_PARM(io, "1-4i");
MODULE_PARM(irq,"1-4i");
MODULE_PARM(dma,"1-4i");
#endif

static int io[NBUSES] = { 0x2c0 };
static int irq[NBUSES] = { 11 };
static int dma[NBUSES] = { 5 };

static void ib_shutdown_module(void);

int init_module(void)
{
	int i, rval;
        for (i=0; i < NBUSES; i++) {
                if (io[i] >= 0) ibinfo[i].ib_baseaddr = io[i];
                if (irq[i] >= 0) ibinfo[i].ib_intrNum = irq[i];
                ibinfo[i].ib_dmachan = dma[i];
        }
        rval = ib_register_chrdev(GPIB_MAJOR, "gpib", &ib_fops);
        if (rval) {
                printk("unable to get major %d for gpib\n", GPIB_MAJOR);
                return rval;
        }
	rval = ib_init();
        if (rval)
                ib_shutdown_module();
        return rval;
}

static void ib_shutdown_module(void)
{
	int index;

	for (index = 0; index < NBUSES; index++) {
                if (ibinfo[index].ib_intrNum != INVALID_INTERRUPT_NUM &&
                	ibinfo[index].ib_intrNum > 1)
                    ib_free_irq(ibinfo[index].ib_intrNum, ibinfo[index].ib_bus);
		if (ibinfo[index].ib_baseaddr > 0 &&
		    ibinfo[index].ib_baseaddr < 0x1000)
                    ib_release_region((unsigned long)ibinfo[index].ib_baseaddr, 0x20UL);
 
                if (ibinfo[index].ib_dmachan != 0)
                    ib_free_dma(ibinfo[index].ib_dmachan);

                if (ibinfo[index].ib_phys_pbar0 > 0x100000)
                        ib_free_pci_addr(ibinfo[index].ib_pbar0);
                if (ibinfo[index].ib_phys_pbar1 > 0x100000)
                        ib_free_pci_addr(ibinfo[index].ib_pbar1);

                ib_release(index);
        }

        if (ib_unregister_chrdev(GPIB_MAJOR, "gpib") != 0) {
                printk("gpib: unable to unregister device\n");
        } else {
                printk("gpib: succesfully removed \n");
        }
        return;
}

void cleanup_module(void)
{
        if (ib_mod_in_use()) {
                printk("gpib: device busy\n");
        }
        ib_shutdown_module();
}
#endif
