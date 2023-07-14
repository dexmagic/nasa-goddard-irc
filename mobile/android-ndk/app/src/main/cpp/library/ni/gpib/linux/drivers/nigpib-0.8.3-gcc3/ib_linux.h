/************************************************************************
 *
 *	NI-488 Driver for UNIX GPIB Interfaces
 *	Copyright (c) 1998 National Instruments Corporation
 *	All rights reserved.
 *
 *	OS-DEPENDENT LAYER INCLUDE FILE for Linux 2.0.x for 80x86
 *
 *
 ************************************************************************/

/*
 * The following constant, when defined, lets the other layers of the
 * driver know that the NI-488.2 function layer has been compiled in
 * and is available to HW interfaces that can support it.
 */
#define NI4882_LAYER_SUPPORTED

#define REALLY_SLOW_IO  1

#define HW_ATGPIB       0
#define HW_EISAGPIB	1
#define HW_PCIGPIB	2

#undef NIPCI_NODMA			/* DMA is supported for PCI-GPIB */
#undef NIEISA_NODMA			/* DMA is supported for AT-GPIB/TNT */

#if !USE_DMA
#undef NIAT_NODMA
#define NIAT_NODMA   1
#undef NIEISA_NODMA
#define NIEISA_NODMA 1
#undef NIPCI_NODMA
#define NIPCI_NODMA  1
#endif

#undef SCATTER_GATHER_SUPPORTED		/* scatter gather facility is not provided */

#define NUM_HW_TYPES    3	/* Total no. of HW types supported by driver */

#define DEBUGMINOR	200		/* minor number of debug entry node */

#define IB_CV_DRIVER	  1		/* condition variable type code */
#define IB_MUTEX_DRIVER	  1		/* standard mutex type code */
#define IB_EVMUTEX_DRIVER 2		/* "event-blocking" mutex code */

#define IB_MINSIG	1		/* minimum signal supported for ibsgnl */
#define IB_MAXSIG	32		/* maximum signal supported for ibsgnl */

#define OSDMA_MAXCNT	0x10000		/* maximum DMA transfer count */

/*
 * Common data types used by this driver
 */
typedef	int		(*FuncPtr)();
typedef unsigned char	  uint8;
typedef unsigned short	  uint16;
typedef unsigned int	  uint32;
typedef char		  int8;
typedef short		  int16;
typedef int		  int32;

typedef caddr_t		  ib_caddr_t;
typedef kdev_t		  ib_dev_t;
typedef int		  ib_major_t;
typedef int		  ib_minor_t;
typedef pid_t		  ib_pid_t;
typedef struct task_struct *ib_sigpid_t;
typedef int               ib_ioaddr_t;
typedef caddr_t           ib_dma_handle_t;
typedef unsigned long     ib_ulong_t;

#define SEMPAD   8


typedef union {
        struct semaphore sem;
        unsigned long pad[SEMPAD];
} ib_sem_t;

typedef union {
#if LINUX_VERSION_CODE >= 0x020100
        spinlock_t lock;
#endif
        unsigned long pad[SEMPAD];
} ib_lock_t;

typedef struct ib_mutex {
	int		busy;
	int		wanted;
	int		evblock;	/* mutex blocks asynchronous events? */
	unsigned long	flags;
        ib_sem_t	ib_sem;
        ib_lock_t	ib_lock;
} ib_mutex_t;

typedef struct ib_condvar {
	int		wanted;
        ib_sem_t	ib_sem;
        ib_lock_t	ib_lock;
} ib_condvar_t;


/************************************************************************/

typedef struct bustbl_os {
        char	       *dmabuf;
} bustbl_os_t;

typedef struct devtbl_os {
        int		dummy;	/* nothing needed */
} devtbl_os_t;

#define INVALID_BASE_ADDRESS	(-1)
#define INVALID_INTERRUPT_NUM	(-1)

/*
 * Internal structure used during driver initialization
 */
typedef struct {
	ib_ioaddr_t     ib_baseaddr;    /* The base address of TNT register window */
        int             ib_intrNum;     /* Interrupt number used by this board*/
	uint32		ib_pbar0;
	uint32		ib_pbar1;
	int		ib_dmachan;
	uint32		ib_phys_pbar0;
	uint32		ib_phys_pbar1;
	void           *ib_bus;		/* filled in later */
} ibinfo_t;

extern ibinfo_t ibinfo[NBUSES];

#define NTIMEOUT	10

typedef struct timer_list ib_timeout_t;


/*
 * PCI-specific fields required in the stdBRDglobals structure
 */
#define pci_devno   os_generic0.gs_uint32   /* Device number assigned	*/


/************************************************************************/

/*
 * Turbo488 FIFO Template...
 *  This structure is defined here, rather than in the HW include file,
 *  because the order of FIFO A and B can vary depending on the system
 *  architecture.
 */
typedef union {				/* FIFO access type		*/
    struct {
	uint8	b;			/*  +0 8-bit FIFO "B" register	*/
	uint8	a;			/*  +1 8-bit FIFO "A" register	*/

    }		f8;			/* two 8-bit FIFO registers A/B	*/
    uint16	f16;			/* single 16-bit FIFO register	*/

} fifo_t;

extern ib_major_t	gpib_major;

/*
 * OS Interface Macros...
 */
#define IB_va_alist			va_alist
#define IB_va_dcl			va_dcl
#define IB_va_ldcl(p)			va_list p;
#define IB_va_start(p)			va_start(p)
#define IB_va_arg(p,t)			va_arg(p,t)
#define IB_va_arg1(p,t)			va_arg(p,t)
#define IB_va_end(p)			va_end(p)

#define OSpid				current->pid /* process "ID" number */

#define OSprintf                        ib_printk
#define OSiprintf                       ib_printk

#define IB_mutex_enter(mutex)		ib_mutex_enter(mutex)
#define IB_mutex_exit(mutex)		ib_mutex_exit(mutex)
#define IB_mutex_init(mutex, name)	ib_mutex_init((mutex), (name), IB_MUTEX_DRIVER, NULL)
#define IB_mutex_intrinit(mutex, name)	ib_mutex_init((mutex), (name), IB_EVMUTEX_DRIVER, NULL)
#define IB_mutex_timoinit(mutex, name)	ib_mutex_init((mutex), (name), IB_EVMUTEX_DRIVER, NULL)
#define IB_mutex_destroy(mutex)		ib_mutex_destroy(mutex)

#define IB_cv_init(cvp, name)		ib_cv_init((cvp), (name), IB_CV_DRIVER, NULL)
#define IB_cv_destroy(cvp)		ib_cv_destroy(cvp)
#define IB_cv_wait(cvp, mutex)		ib_cv_wait((cvp), (mutex))
#define IB_cv_signal(cvp)		ib_cv_signal(cvp)

#define IB_kmem_zalloc(size)		ib_kmem_zalloc(size)
#define IB_kmem_free(ptr, size)		ib_kmem_free((caddr_t)(ptr), (size))
#define IB_bzero(ptr, size)		ib_bzero((ptr), (size))
#define IB_bcopy(src, dest, length)	ib_bcopy((src), (dest), (length))
#define IB_copyin(ubuf, kbuf, length)	ib_copyin((ubuf), (kbuf), (length))
#define IB_copyout(kbuf, ubuf, length)	ib_copyout((kbuf), (ubuf), (length))

#define IB_getmajor(dev)		ib_getmajor(dev)
#define IB_getminor(dev)		ib_getminor(dev)
#define IB_makedevice(bus)		ib_makedevice(bus)

#define IB_delay(usecs)			ib_delay(usecs)
#define IB_timeout(func, arg, usecs)	ib_timeout(((FuncPtr) func), (arg), (usecs))
#define IB_untimeout(id)		ib_untimeout(id)

#define IB_getpid()			ib_getpid()
#define IB_initsignal(bus)		ib_initsignal(bus)
#define IB_resetsignal(bus)		ib_resetsignal(bus)
#define IB_postsignal(bus)		ib_postsignal(bus)


/*
 * Memory and I/O address access macros...
 */
#define MEMaddr(value)			(&(value))

#define MEMin(addr, sysflag)		ib_memIn((addr), (sysflag))
#define MEMin16(addr, sysflag)		ib_memIn16((addr), (sysflag))
#define MEMout(addr, value, sysflag)	ib_memOut((addr), (value), (sysflag))
#define MEMout16(addr, value, sysflag)	ib_memOut16((addr), (value), (sysflag))


#define GPIBtestIn(addr)		(1)
#define GPIBtestOut(addr)		(1)

/* low addresses up to 0xffff must be I/O ports */
#define GPIBin(addr)			((uint32)&ib->addr < 0x10000 ? inb_p((int)&ib->addr) : *(volatile uint8*)(&ib->addr))
#define GPIBout(addr,value)		((uint32)&ib->addr < 0x10000 ? outb_p(value, (int)&ib->addr) : (*(volatile uint8*)&ib->addr = (value)))
#define GPIBin16(addr)			((uint32)&ib->addr < 0x10000 ? inw_p((int)&ib->addr) : *(volatile uint16*)(&ib->addr))
#define GPIBout16(addr,value)		((uint32)&ib->addr < 0x10000 ? outw_p(value, (int)&ib->addr) : (*(volatile uint16*)&ib->addr = (value)))

#define GPIBpgin(x)			(GPIBout(auxmr, AUX_PAGE), GPIBin(x))
#define GPIBpgout(x,v)			(GPIBout(auxmr, AUX_PAGE), GPIBout(x,v))


#define SCROMin32(addr)                 (*((volatile uint32*)(addr)))
#define MITEin32(addr)                  (*((volatile uint32*)(addr)))
#define MITEout32(addr, value)          (*((volatile uint32*)(addr))) = (uint32) value

#define DMA_MITEin8(addr)                       (*((volatile uint8*)(addr)))
#define DMA_MITEout8(addr, value)               (*((volatile uint8*)(addr))) = (uint8) value
#define DMA_MITEin16(addr)                      (*((volatile uint16*)(addr)))
#define DMA_MITEout16(addr, value)              (*((volatile uint16*)(addr))) = (uint16) value
#define DMA_MITEin32(addr)                      (*((volatile uint32*)(addr)))
#define DMA_MITEout32(addr, value)              (*((volatile uint32*)(addr))) = (uint32) value

#define NugMemin32(addr, nug_pool_hndl)         (*((volatile uint32*)(addr)))
#define NugMemout32(addr, value, nug_pool_hndl) (*((volatile uint32*)(addr))) = (uint32) value

#define DmaMalloc(num, size)                    (IB_kmem_zalloc((num) * (size)))
#define DmaFree(ptr, size)                      (IB_kmem_free((char *)(ptr), (size)))

/* ib_linux.c prototpes */

struct bustbl;
struct lowlevel_param;

void     ib_mutex_enter(ib_mutex_t *mutex);
void     ib_mutex_exit(ib_mutex_t *mutex);
void     ib_mutex_init(ib_mutex_t *mutex, char *name, int type, void *arg);
void     ib_mutex_destroy(ib_mutex_t *mutex);

void     ib_cv_init(ib_condvar_t *cvp, char *name, int type, void *arg);
void     ib_cv_destroy(ib_condvar_t *cvp);
void     ib_cv_wait(ib_condvar_t *cvp, ib_mutex_t *mutex);
void     ib_cv_signal(ib_condvar_t *cvp);

caddr_t  ib_kmem_zalloc(int size);
void     ib_kmem_free(caddr_t ptr, int size);
void     ib_bzero(char *ptr, int size);
void     ib_bcopy(void *src, void *dest, int length);
int      ib_copyin(caddr_t userbuf, caddr_t driverbuf, int length);
int      ib_copyout(caddr_t driverbuf, caddr_t userbuf, int length);

int      ib_getmajor(kdev_t dev);
int      ib_getminor(kdev_t dev);
int      ib_makedevice(struct bustbl *bus);

void     ib_delay(int usecs);
int      ib_timeout(FuncPtr func, caddr_t arg, int usecs);
int      ib_untimeout(int id);
ib_pid_t ib_getpid(void);
void	ib_initsignal(struct bustbl *bus);
void	ib_resetsignal(struct bustbl *bus);
void	ib_postsignal(struct bustbl *bus);

uint8    ib_memIn(uint8 *addr, int sysflag);
uint16   ib_memIn16(uint16 *addr, int sysflag);
void     ib_memOut(uint8 *addr, uint8 value, int sysflag);
void     ib_memOut16(uint16 *addr, uint16 value, int sysflag);
int      ib_iopeek();
int      ib_iopoke();

#if !GPIB_DEBUG || GPIB_MINITRACE
#define LX_DBG_ASYNC_ARG
#else
#define LX_DBG_ASYNC_ARG   , int dbgAsync
#endif
int ib_DMAalloc(struct bustbl *bus, struct lowlevel_param *hwp);
int ib_DMArelse(struct bustbl *bus, struct lowlevel_param *hwp);
int ib_adjDMAcnt(struct bustbl *bus, struct lowlevel_param *hwp);
int ib_DMAstart(struct bustbl *bus, struct lowlevel_param *hwp);
void ib_DMAstop(struct bustbl *bus LX_DBG_ASYNC_ARG);

int ib_pci_detect(int index, int *pci_index, uint32 *base0, uint32 *base1, char *irq);
unsigned long ib_remap_pci_addr(unsigned long addr, unsigned long size);
void ib_free_pci_addr(unsigned long addr);

caddr_t ib_dma_kmem_zalloc(int size);
int ib_register_chrdev(unsigned int major, const char *name, struct file_operations *ops);
int ib_unregister_chrdev(unsigned int major, const char * name);
int ib_request_dma(unsigned int dmanr, const char * device_id);
void ib_request_region(unsigned long from, unsigned long extent,const char *name);
void ib_release_region(unsigned long from, unsigned long extent);
int ib_request_irq(unsigned int irq,
                   void (*handler)(int, void *, struct pt_regs *),
                   unsigned long flags,
                   const char *device,
                   void *dev_id);
void ib_free_dma(unsigned int dmar);
void ib_free_irq(unsigned int irq, void *dev_id);
int ib_verify_read(const void *mem, unsigned long count);
int ib_verify_write(const void *mem, unsigned long count);
int ib_printk(const char *fmt, ...);

#ifdef MODULE
void ib_inc_module(void);
void ib_dec_module(void);
int ib_mod_in_use(void);
#endif

int ib_rw(kdev_t dev, char *buffer, int count, int rwmode);
int ib_exec(int minor, unsigned int cmd, unsigned long arg);
int ib_attach(ib_minor_t minor, struct ibarg *cp);
int ib_unattach(ib_minor_t minor, struct ibarg *cp);
int ib_init(void);

/***********************  E N D   O F   F I L E  ************************/

