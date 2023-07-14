/************************************************************************
 *
 *	NI-488.2M Kernel-level Device Driver
 *
 *	NI-488[2] AND NI-SUPPORT LAYER INCLUDE FILE
 *
 ************************************************************************/

/*
 * DEBUGGING MACROS
 *
 * All print statements should be done using the DBGprint() macro.
 * Some examples of the use of this macro are as follows...
 *
 *	DBGprint(DBG_BRANCH, ("do not send EOI"));
 *	DBGprint(DBG_DATA, ("isr1=0x%x isr2=0x%x", r_isr1, r_isr2));
 *
 * To maintain compatibility with the driver's internal printing
 * routines, you should use only the simple printf formats %d, %x,
 * and %s (see 'ni_printf' in ni_support.c).
 *
 * Note that the DBGin() macro must be used at the beginning of any
 * function in which the DBGprint() macro will be used.  The DBGin()
 * macro sets up the function name that will precede every print
 * statement within the function, so it is not necessary to repeat
 * the function name within the DBGprint string.  Some examples of
 * the use of the DBGin() macro are as follows...
 *
 *	DBGin("ibonl");
 *	DBGin("ibwrt");
 *
 * Also note that you should not include a newline ('\n') at the 
 * end of any print string because the DBGprint macro will insert
 * a newline automatically whenever the DBG_MPPL flag is not set.
 * If the DBG_MPPL flag *is* set, print statements will be separated
 * only by semicolons and output will wrap when it gets to the end
 * of the line/window.  This mode is useful when you are printing to
 * the screen using a dumb terminal or other limited size window and
 * want to condense as much output as possible on the screen at one
 * time.
 *
 * The first group of "DBG_" flags defined below can be used to
 * selectively enable certain groups of debugging statements.
 * The second group of flags determine the manner in which the
 * debug output is reported (default = internal condensed printing
 * to trace buffer only).
 */
#define DBG_ALL		0x0001		/* print ALL debug statements */
#define DBG_ENTRY	0x0002		/* print on function entry */
#define DBG_EXIT	0x0004		/* print on function exit */
#define DBG_BRANCH	0x0008		/* print branch announcements */

#define DBG_DATA	0x0010		/* print 1 or more data items */
#define DBG_ASYNC	0x0020		/* print interrupt/timer statements */
#define DBG_REG		0x0040		/* print register reads/writes */
#define DBG_SPEC	0x0080		/* print special (temporary) items */

#define DBG_LOOP	0x0100		/* print items within loops */
#define DBG_UNUSED1	0x0200		/* unused */
#define DBG_UNUSED2	0x0400		/* unused */
#define DBG_MPPL	0x0800		/* print multiple statements per line */

#define DBG_SCRPRT	0x1000		/* print statements to screen */
#define DBG_IBPRT	0x2000		/* print statements to GPIB port */
#define DBG_MUTEX	0x4000		/* print statements protected by mutex */
#define DBG_IDPROC	0x8000		/* print process ID w/each line */


#if (!GPIB_DEBUG)
/*------------------------------------------------------------------*
 * DEBUGGING DISABLED:  EVERYTHING GOES AWAY                        *
 *------------------------------------------------------------------*/

#define DBGin(id)	 { }
#define DBGinAsync(id)	 { }
#define DBGinCond(id)	 { }
#define DBGout()	 { }
#define DBGoutAsync()	 { }
#define DBGoutCond()	 { }
#define DBGprint(ms,ar)	 { }
#define DBGaprint(ms,ar) { }
#define DBGcprint(ms,ar) { }
#define DBGloopChk(ic,as){ }
#define DBGloopClr()	 { }

#define DBG_LOOP_GUARD

#define DBG_ASYNC_ARG
#define DBG_ASYNC_OP
#define DBG_ASYNC_YES
#define DBG_ASYNC_NO

#else					/* ------ GPIB_DEBUG ------ */
#if (GPIB_TRACE)

#define TPMAXCHARS	40000		/* trace print maximum line length */

#define Doprint		ni_printf
#define Doiprint	ni_printf
#else

#define Doprint		OSprintf
#define Doiprint	OSiprintf
#endif					/* ------ GPIB_TRACE ------ */


#if (!GPIB_MINITRACE)
/*------------------------------------------------------------------*
 * DEBUGGING ENABLED:  REGULAR PRINTING AND TRACING                 *
 *------------------------------------------------------------------*/

#define DBG_MASK_INIT	(DBG_ALL)

#define DBG_ASYNC_ARG	, int dbgAsync
#define DBG_ASYNC_OP	, dbgAsync
#define DBG_ASYNC_YES	, 1
#define DBG_ASYNC_NO	, 0

/*
 * Normal debug print
 */
#define DBGprint(ms,ar)	{ if (dbgMask & (ms | DBG_ALL)) {		     \
			      if (dbgMask & DBG_IDPROC)			     \
			          Doprint("(0x%x)%s:", OSpid, dbgFnID);	     \
			      else					     \
			          Doprint("%s:", dbgFnID);		     \
			      Doprint ar;				     \
			      Doprint((dbgMask & DBG_MPPL) ? ";  " : "\n");  \
			  }						     \
			}

/*
 * "Asynchronous" debug print
 */
#define DBGaprint(ms,ar){ if ((dbgMask & (DBG_ASYNC | DBG_ALL))		     \
			    && (dbgMask & (ms | DBG_ALL))) {		     \
			      Doiprint("(-ASYNC-)%s:", dbgAsyncFnID);	     \
			      Doiprint ar;				     \
			      Doiprint((dbgMask & DBG_MPPL) ? ";  " : "\n"); \
			  }						     \
			}

/*
 * "Conditional" debug print (sync/async)
 */
#define DBGcprint(ms,ar){ if (dbgAsync) {				     \
			      DBGaprint(ms,ar);				     \
			  }						     \
			  else {					     \
			      DBGprint(ms,ar);				     \
			  }						     \
			}

#define DBGin(id)	char *dbgFnID = id;				     \
			DBGprint(DBG_ENTRY, ("in"));

#define DBGinAsync(id)	char *dbgAsyncFnID = id;			     \
			DBGaprint(DBG_ENTRY, ("in"));

#define DBGinCond(id)	char *dbgFnID = id;				     \
			char *dbgAsyncFnID = id;			     \
			DBGcprint(DBG_ENTRY, ("in"));

#define DBGout()	DBGprint(DBG_EXIT, ("out"));

#define DBGoutAsync()	DBGaprint(DBG_EXIT, ("out"));

#define DBGoutCond()	DBGcprint(DBG_EXIT, ("out"));

/*
 * Infinite loop "circuit breakers"...
 * These macros can be used when an infinite loop is suspected
 * and you want to abort debug output before "good" data in the
 * print buffer is overwritten with useless, repetitive data.
 * See ni488_waitcallback() and ni_GetAccess() for example usage
 * of these macros.
 */
#define DBG_LOOP_GUARD	static int dbgLoopCntr = -1;

#define DBGloopChk(ic,as)						     \
			{ if (dbgLoopCntr < 0)				     \
			      dbgLoopCntr = ic;				     \
			  else if (dbgMask && !(--dbgLoopCntr)) {	     \
			      if (as)					     \
			          Doiprint("*** DEBUG OUTPUT ABORTED ***\n");\
			      else					     \
			          Doprint("*** DEBUG OUTPUT ABORTED ***\n"); \
			      dbgMask = 0;				     \
			  }						     \
			}

#define DBGloopClr()	{ if (dbgLoopCntr >= 0)				     \
			      dbgLoopCntr = -1;				     \
			}


#else					/* ---- GPIB_MINITRACE ---- */
/*------------------------------------------------------------------*
 * DEBUGGING ENABLED:  OPTIMIZED "MINI-TRACING" ONLY                *
 *------------------------------------------------------------------*/
/*
 * These definitions provide an alternative tracing facility that
 * should run significantly faster than the "printing" version
 * implemented above.
 *
 * MINITRACE==1: Run time tracing is configurable through ibpoke().
 *
 * MINITRACE==2: Tracing options are selected at compile time only
 * and CANNOT be changed at run time via ibpoke.  Statements that
 * do not belong to the DBG_MASK_INIT group below will expand to an
 * inaccessible block of code (e.g., "if (0)") that most compilers
 * will throw out, thus improving efficiency.  Although the dbgMask
 * variable is not used in the macros in this case, it can still be
 * "poked" to enable or disable tracing through an extra GPIB port
 * (see ni_putc).
 */

#define DBG_MASK_INIT	(DBG_ENTRY | DBG_EXIT | DBG_BRANCH)

#define DBG_ASYNC_ARG
#define DBG_ASYNC_OP
#define DBG_ASYNC_YES
#define DBG_ASYNC_NO

/*
 * NOTE: Trace entries will be written to the internal trace buffer
 *  in the following format...
 *
 *	|   even offset   |   odd offset    |
 *	|                 |                 |
 *	| F_F_F N_N_N_N_N | N_N_N_N_N_N_N_N |
 *	| F_F_F N_N_N_N_N | N_N_N_N_N_N_N_N |
 *	| F_F_F N_N_N_N_N | N_N_N_N_N_N_N_N |
 *	  :
 *
 *             FFF =  3-bit file code   (0 - 7)
 *  NNNNN NNNNNNNN = 13-bit line number (0 - 8191)
 *
 */
#if (GPIB_MINITRACE == 1)		/* ... Option 1 ... */
#define DBGprint(ms,ar)	{ if (dbgMask & (ms | DBG_ALL)) {		     \
			      ni_dotrace((IBFILECODE << 13) | (__LINE__));   \
			  }						     \
			}
#else					/* ... Option 2 ... */
#define DBGprint(ms,ar)	{ if (DBG_MASK_INIT & (ms | DBG_ALL)) {		     \
			      ni_dotrace((IBFILECODE << 13) | (__LINE__));   \
			  }						     \
			}
#endif

#define DBGaprint(ms,ar)  DBGprint((ms),      0)
#define DBGcprint(ms,ar)  DBGprint((ms),      0)
#define DBGin(id)	  DBGprint(DBG_ENTRY, 0)
#define DBGinAsync(id)	  DBGprint(DBG_ENTRY, 0)
#define DBGinCond(id)	  DBGprint(DBG_ENTRY, 0)
#define DBGout()	  DBGprint(DBG_EXIT,  0)
#define DBGoutAsync()	  DBGprint(DBG_EXIT,  0)
#define DBGoutCond()	  DBGprint(DBG_EXIT,  0)

/*
 * Infinite loop "circuit breakers"...
 * For MINITRACE option 2, these macros will only abort tracing
 * to an extra GPIB port (if enabled), not to the internal buffer.
 */
#define DBG_LOOP_GUARD	  static int dbgLoopCntr = -1;

#define DBGloopChk(ic,as)						     \
			{ if (dbgLoopCntr < 0)				     \
			      dbgLoopCntr = ic;				     \
			  else if (dbgMask && !(--dbgLoopCntr)) {	     \
			      ni_dotrace((IBFILECODE << 13) | 0xFFFF);	     \
			      dbgMask = 0;				     \
			  }						     \
			}

#define DBGloopClr()	{ if (dbgLoopCntr >= 0)				     \
			      dbgLoopCntr = -1;				     \
			}

#endif					/* ---- GPIB_MINITRACE ---- */
#endif					/* ------ GPIB_DEBUG ------ */


/************************************************************************/

#define FALSE		0
#define TRUE		1

#define BUS		0 
#define DEVICE		1

/*
 * General function return codes...
 */
#define BDONE		0
#define BERROR	       -1

#define GPIB_SUCCESS	1
#define GPIB_FAILURE	0

#define DIAG_HW_NONE    -1		/* HW unknown or not detected */

/*
 * Types of interfaces supported by this driver design...
 */
#define NOOP		0 		/* no interface 	*/
#define SCSIBOX		1		/* a scsi-a box		*/			
#define SBUSCARD	2		/* a sbus card		*/
#define ATCARD		3		/* a AT-GPIB card	*/

/*
 * Various memory allocation chunks...
 */
#define GROW_BUS	0x4		/* number of bus structures allocated at a time	*/
#define GROW_DEV	0x10		/* number of dev structures allocated at a time	*/
#define GROW_ID		0x10		/* number of idtondx structs alloc'd at a time	*/
#define GROW_AQ		0x50		/* number of autopoll structs alloc'd at a time	*/
					/*  (strictly a function of the number of	*/
					/*    buses and devices)			*/
/*
 * Bus flags
 */
#define B_ONLINE	(0x1 << 0) 
#define B_CICRQD	(0x1 << 1) 

/*
 * Device flags
 */
#define D_ONLINE	(0x1 << 0) 
#define STD_DEVICE	(0x1 << 1)
#define DEV_POLLED	(0x1 << 2) 

/*
 * Useful 'ibsta' combinations
 */
#define DEVSTATUS	(ERR|TIMO|END|RQS|CMPL) 
#define BRDONLY_BITS	(SRQI|DTAS|DCAS|LACS|TACS|LOK|REM|CIC|ATN)
#define DEVONLY_BITS	RQS
#define BAD_BITS	0x600
#define VOL_BITS	(DCAS|DTAS) 
#define NVOL_BITS	(SRQI|LOK|REM|CIC|ATN|TACS|LACS)
#define SIG_BITS	(SRQI|LOK|REM|CIC|TACS|LACS|DCAS|DTAS)

/*
 * Internal driver flags
 */
#define NI488_INTCALL	0x01
#define NI488_DOPOLL	0x02		/* Causes NI488 ibrsp to poll rather */
					/*  than return Sp byte from queue.  */ 
#define NI488_SYSSPACE	0x04		/* 488.2 calls use this to indicate  */
					/*  the buffer is a kernel buffer;   */
					/*  valid only if INTCALL is set.    */
#define NI488_DEVFIND	0x08		/* Device IBFIND/IBDEV in progress   */



#define DEV_HNDL_INIT	1000		/* device handle initializer         */

#define MAX_4882ADDR	512		/* max # addresses in a 488.2 call   */
#define MAXCMD		32
#define MAX_WAITERS	5		/* max # waiters per bus             */
#define MAXSPQ		16		/* max # spoll elements in queue     */

#define MAX_PAD		0x1E		/* Maximum legal Primary Address     */
#define MIN_PAD		0x00		/* Minimum legal Primary Address     */
#define MAX_SAD		0x7E		/* Maximum legal Secondary Address   */
#define MIN_SAD		0x60		/* Minimum legal Secondary Address   */

#define SAD_MASK 	0x60		/* or mask to make SAD from 0-31     */


/*
 * Low-level timeout settings
 */
#define SPOLLTMO	11		/* to wait for a serial poll to complete (T1s) */

#define ATNTMO_MS	50		/* to wait for ATN to become asserted */
#define CMDTMO_MS	100		/* to wait for an internal command to complete */
#define POLLTMO_MS	100		/* to wait for a parallel poll to complete */


 
/*
 * Enumerated type for functions in hardware specific layer...
 * It is enough for the hardware specific layer to implement only this
 * set of functions.  All the NI488 (board and device level) and NI488.2  
 * functions must be implemented in the upper level by calling one or
 * more of these lower level functions.
 */
enum
{
	HW_ENTER,			/* primary HW entry point */

	/*
	 * These functions are called through HW_ENTER...
	 */
	HW_ibCAC,	HW_ibONL,	HW_ibCMD,	HW_ibGTS,
	HW_ibPARM,	HW_ibPPC,	HW_ibRPP,	HW_ibRSC,
	HW_ibRSV,	HW_ibSIC,	HW_ibRSP,	HW_ibSRE,
	HW_ibSTAT,	HW_ibWAIT,	HW_ibRD,	HW_ibWRT,
	HW_ibLINES,	HW_ibLN,	HW_ibIST,	HW_ibLOC,
	HW_ibADDR,	HW_ibSGNL,	HW_ALLSP,
	
	/*
	 * These functions are called directly...
	 */
	HW_DMASTART,	HW_DMASTOP,	HW_EVENT,	HW_CNCLWAIT,
	
	/*
	 * Functions beyond this point can vary and are used
	 * internally by the different hardware layers...
	 */
	HW_INTERNAL
} ;


#define NO_FUNC				200
#define NO_LOWLEVEL_FUNC		40


/************************************************************************/

/*
 * Generic Scalar Type
 * For defining customizable fields in the stdBRDglobals structure.
 * These fields can be #define'd as needed for particular hardware
 * support layers (see <HW>.h files).
 */
union genericScalar
{
	long		gs_long;
	int		gs_int;
	short		gs_short;
	char		gs_char;
	unsigned long	gs_ulong;
	unsigned int	gs_uint;
	unsigned short	gs_ushort;
	unsigned char	gs_uchar;
	uint32		gs_uint32;
	uint16		gs_uint16;
	uint8		gs_uint8;
};
typedef union genericScalar genscalar_t;


/*
 * Standard BOARD Globals
 * Defined for all board-level GPIB interfaces.  Fields common across
 * multiple hardware interfaces and fields accessed from the common
 * layers of the driver should be defined directly in this structure.
 * Fields required only by a particular interface or bus architecture
 * should be defined in the appropriate <HW>.h file using the generic
 * fields provided.  See nivm_hw.h and nipci_hw.h for examples.
 */
struct stdBRDglobals
{
	int		hwareCap;	/* this field required for all HW types */
	char *		baseaddr;	/* virtualized ptr to board registers	*/
	ib_ioaddr_t	rawioaddr;	/* "unadorned" physical I/O address	*/
	int		irq;		/* interrupt request level (or line)	*/
	int		dmachan;	/* DMA channel				*/
	int		dmaflags;	/* DMA operation flags (see below)	*/
	int		dmaresid;	/* DMA residual count value		*/
	void *		dmaengine;	/* Pointer to DMAEngine_t struct (if defined) */

	genscalar_t	hw_generic0;	/* To be #define'd in <HW>.h as needed	*/
	genscalar_t	hw_generic1;	/* To be #define'd in <HW>.h as needed	*/
	genscalar_t	hw_generic2;	/* To be #define'd in <HW>.h as needed	*/
	genscalar_t	os_generic0;	/* To be #define'd in <OS>.h as needed	*/
	genscalar_t	os_generic1;	/* To be #define'd in <OS>.h as needed	*/
	genscalar_t	os_generic2;	/* To be #define'd in <OS>.h as needed	*/
};
typedef struct stdBRDglobals stdBRDglobals_t;


/*
 * Hardware Capabilities Supported
 */
#define HC_DMAOP	0x0001		/* DMA operation			*/
#define HC_VSTD		0x0002		/* Very high-speed timing (T1=350ns)	*/
#define HC_PP2		0x0004		/* Local PP configuration		*/
#define HC_HS488	0x0008		/* HS-488 handshaking			*/
#define HC_SGNL		0x0010		/* UNIX event signalling  (ibsgnl)	*/
#define HC_STDBRD	0x0020		/* Standard board globals		*/
#define HC_NOIBLN	0x0040		/* IBLN is non-functional (ECAP)	*/
#define HC_NO4882	0x0080		/* Does not support NI-488.2 routines	*/

/*
 * DMA Operation Flags
 */
#define DF_READ		0x0001		/* DMA Read to memory			*/
#define DF_8BIT		0x0002		/* PC 8-bit transfer mode (default=16)	*/
#define DF_TYPEB	0x0004		/* AT "Type B" timing     (default=ISA)	*/
#define DF_TYPEC	0x0008		/* AT "Type C" timing     (default=ISA)	*/
#define DF_CNT32	0x0010		/* 32-bit transfer counts (default=16)	*/
#define DF_ADDR32	0x0020		/* 32-bit phys. addresses (default=24)	*/


struct stdBOXglobals
{
	int		hwareCap;	/* this field required for all HW types */


	/* add whatever is needed in here */

};
typedef struct stdBOXglobals stdBOXglobals_t;


struct bus_param
{
	uint8		pad ;
	uint8		sad ;
	uint8		eosm ;
	uint8		eosb ;
	int		tmoMS ;
	int		flags ;
	int		hwflags ;
	int		cablelength ;	/* cable length in meters (for HS-488) */
	int		signo ;		/* signal number to post on selected events */
} ;
typedef struct bus_param bus_param_t ;

/*
 * A struct of the following type is allocated for every waiter
 */
struct waitrec 
{
	int		mask ;		/* mask of this waiter 				*/
	int		usecs ;		/* remaining micro secs to wait			*/
	struct ibarg	*cp ;		/* pointer to this waiters cp 			*/
	struct bustbl 	*bus ;		
	struct devtbl 	*dev ;
	struct lowlevel_param 	*hwp ;
	int  		timedout ;	/* indicates if this waiter timed out or not	*/
	int   		active ;	/* indicates if waiter active or not 		*/
	int 		lowlevel ;	/* indicates if this waiter has a low-level call pending */	
	int 		flag ;
	int 		id ;		/* timer id, if any - allocated by the system	*/
	ib_condvar_t 	w_cv ;
} ;
typedef struct waitrec waitrec_t ;


/*
 * Data structure for every bus present
 */
struct bustbl    
{
	char 		busName[MAX_BUSNAME_LENGTH] ;
	int		sysflags ;	/* misc. system/HW configuration info.		*/
	int		hwareType ;	/* type of GPIB hardware, defined by driver	*/
	int		hwareID ;	/* ibdiag-compatible hardware ID		*/
	int		busno ;		/* number of the bus 0 thru MAXBUS		*/
	int		brefcnt ;	/* reference count:# of board finds on this bus	*/
	int		drefcnt ;	/* reference count:# of devices using this bus	*/
	bus_param_t	def_param ;	/* default settings of thebus 			*/
	bus_param_t	cur_param ; 	/* current settings of the bus 			*/
	struct NI488Glob 	*g ;	/* pointer to driver global data struc		*/
	FuncPtr		*func ;		/* hardware specific function table 		*/
						
					/* Next four fields remember the system specific info 
					   for this bus instance. These are filled once during  
					   attach, info times and never modified.	*/
	ib_minor_t	minor ;		/* minor number of this node 			*/
	ib_dev_t	devt ;		/* device number of this bus 			*/

	ib_sigpid_t	sigpid ;	/* "id" of owning process for ibsgnl purposes	*/
	int		sigoldsta ;	/* old ibsta vector since previous driver entry	*/
	int		signewsta ;	/* new ibsta vector since previous driver entry	*/
	uint16		sigmsk ;	/* events mask for which signals may be posted	*/

 	int		busflag ;	/* indicate various states of the bus 		*/
	struct lowlevel_param  *hwp ;	/* pointer to currently executing low level param*/
	int		ibsta ;		
	int      	ibcnt ;
	int 		iberr ;
	u_char		prevRSV ;	/* prev value of RSV 				*/
	u_char 		prevPPC ; 	/* prev value of PPC 				*/
	u_char 		stuckSRQ ;	/* indictaes stuckSRQ condition or not 		*/
	u_char 		noautopoll ;	/* indicates whether autopolling should be attempted */
#ifdef NI4882_LAYER_SUPPORTED
	Addr4882_t	*addr ;		/* pointer to copyin 488.2 address lists	*/
#endif
	u_char 		cmdbuf[MAXCMD] ;
	u_char 		rep[2] ;	/* buffer used to detrmine if repeat addressing reqd. */

	void		*hwdata;	/* hardware-specific data			*/

	struct autoq	*spqlist ;	/* pointer to address based spq list		*/
	struct devtbl   *dev ;		/* pointer to current call's dev tbl entry	*/
	int		busy ;		/* variable indicating whether driver is busy	*/
	int 		num_wait ;	/* number of waiters */

	int		waitmask ;	/* union of masks of individual waiters		*/
	waitrec_t	w[MAX_WAITERS]; /* waiters records				*/
	int		wip ;		/* number of waiters in progress		*/
	u_char 		cancelled ;	/* indicates whether a wait was cancelled 	*/
	int 		ioip ;		/* indicates i/o in progress 			*/
	int		canip ;		/* indicates if cancel of a wait is in progress	*/
	ib_mutex_t	bmutex ;	/* mutex for this data structure 		*/
	struct bustbl 	*prev, *next ;
	struct bustbl 	*prevMemBlock;	/* list of previously alloc'd memory blocks	*/
	ib_condvar_t 	busy_cv ;       /* condition variable for driver busy;		*/
					/* dev is valid when busy			*/
	ib_condvar_t	wait_cv ;	/* condition variable to block calls other	*/
					/* than wait when wait in progress 		*/
	ib_condvar_t	io_cv ;		/* cond.var to block on while i/o in progress 	*/
	ib_condvar_t 	can_cv ;

	bustbl_os_t	os ;		/* OS dependent fields				*/
} ;

typedef struct bustbl bustbl_t ;

#define SF_FIFOAB	0x0001		/* Requires T488 FIFO order "A/B" (default=B/A)	*/


struct dev_param 
{
	uint8		pad ;
	uint8		sad ;
	uint8		eosm ;
	uint8		eosb ;
	int		tmoMS ;
	int		spolltmoMS ;
	int		flags ;
	int		bus ;		/* current access bus number 			*/
} ;
typedef  struct dev_param dev_param_t ;


/*
 * Data structure for every gpib device
 */
struct devtbl
{
	char   		devName[MAX_DEVNAME_LENGTH] ;
	dev_param_t	def_param ;	/* default settings of device - 		*/
	dev_param_t	cur_param ;	/* current settings of the device 		*/
	bustbl_t	*bp ;		/* pointer to the current bus structure		*/
	bustbl_t 	*bus ;		/* pointer to the default bus structure 	*/

	ib_minor_t	minor ;		/* minor number of this device			*/
	ib_dev_t	devt ;		/* device number 				*/

	int		devflag ;	/* various states of the device 		*/
	struct autoq 	*spq ;		/* pointer to the serial poll queue list element*/
	u_char 		prevPPC ;	/* prev value of PPC 				*/
	int		ibsta ;
	int		ibcnt ;
	int 		iberr ;

	int		busy ;		/* variable indicating whether hardware is busy	*/
	ib_pid_t	pid ;		/* id of process owning the data structure	*/
	int		handle ;
	waitrec_t	*w ; 		/* pointer to waiter stru of current waiter 	*/
	ib_mutex_t	dmutex ;	/* mutex for this data structure 		*/
	struct devtbl	*prev, *next ;
	struct devtbl 	*prevMemBlock;	/* list of previously alloc'd memory blocks	*/
	ib_condvar_t 	busy_cv ;       /* condition variable for hardware busy;	*/
					/* dev is valid when busy			*/

	devtbl_os_t	os ;		/* OS dependent fields				*/
} ;
typedef struct devtbl devtbl_t ;


/*
 * Structure of an element in the address based autopoll queue list
 */
struct autoq
{
	u_char 		pad ;		/* primary address				*/
	u_char		sad ;		/* secondary address 				*/
	u_char		flag ;		/* indicates whether polled or not 		*/
	int		cnt ;		/* number of devices that share this queue	*/
	int		tmoMS ;		/* the timeout value for ibcmd sequences	*/
	int		spolltmoMS ;	/* the timeout value for reading the spoll byte	*/
	int		nospq ;		/* number of elements in spq 			*/
	u_char		spq[MAXSPQ] ;	/* the serial poll queue for this address	*/
	struct autoq	*prev, *next ;
	struct autoq 	*prevMemBlock;	/* list of previously alloc'd memory blocks	*/
} ;
typedef struct autoq autoq_t ;


/*
 * Table that gives the mapping between the device or bus descriptor returned
 * to the user on an ibfind and the corresponding bustbl_t or devtbl_t.  At the start
 * or every call, this table is scanned to locate the device and/or bus in question.
 */
struct idTondx
{
	int		id ;		/* bus or device descriptor			*/
	void		*table ;	/* assocated pointer to bus or device structure */
	int		BusorDev ;	/* bus or device? 0-bus 1-device 		*/
	struct idTondx	*prev, *next ;
	struct idTondx 	*prevMemBlock;	/* list of previously alloc'd memory blocks	*/
} ;
typedef struct idTondx idTondx_t ;


/*
 * The driver's global data structure
 */
struct NI488Glob
{
	bustbl_t	*bus ;		/* addr of table of bus structures		*/
	devtbl_t 	*dev ;		/* addr of table of dev structures		*/
	FuncPtr		*func ;		/* generic function table			*/
	idTondx_t	*idtbl ;	/* addr of device descrip - index mapping 	*/
	bustbl_t	*freebus,*fb ;	/* list of free bus structs 			*/
	devtbl_t	*freedev,*fd ;	/* list of free dev structs			*/
	idTondx_t	*freeid,*fid ;	/* list of free idTondx structs 		*/
	autoq_t		*freeaq, *faq ;	/* list of free serial poll queue list elements	*/
	int		timo2ms[18] ; 	/* timo params to milli-seconds table		*/
	int		num_buses ;	/* total number of buses in bus			*/
	int		num_hw[NUM_HW_TYPES];
					/* number of specific hardware instances	*/
	int  		flags ; 	/* to indicate various states of the driver	*/
	int		DevHandle ;	/* the current value of device handle		*/
	int 		bus_busy ;	/* wait variable for bus list			*/
	int		dev_busy ;	/* wait variable for device list		*/
	int 		id_busy ;	/* wait variable for id list			*/
	int		aq_busy ;	/* wait variable for auto q list		*/
	ib_mutex_t	gmutex ;	/* mutex for this data structure 		*/
	ib_condvar_t	bus_cv ;	/* condition variable for freebus, fb, bus lists*/
	ib_condvar_t	dev_cv ;	/* condition variable for freedev, fd, dev lists*/
	ib_condvar_t	id_cv ;		/* condition variable for freeid, fid, idtbl	*/
	ib_condvar_t	aq_cv ;		/* condition variable for freeaq, faq, aq lisaq	*/
} ;
typedef struct NI488Glob NI488Glob_t ;

typedef struct ibarg ibarg_t ;

#define LLP_KERN	0x0001		/* I/O operation is to/from kernel memory */
#define LLP_USER	0		/* I/O operation is to/from user memory */


/*
 * Structure used by the h/w independent layer to call the hardware dependent layer;
 * also, the lower layer functions use this to call other functions in the same layer.
 * These functions will update the ibcnt, iberr, ibsta fields in this structure.
 */
struct lowlevel_param
{
	void *		buf;		/* virtual address of I/O buffer      */
	void *		pbuf;		/* physical (DMA) addr of I/O buffer  */
	unsigned int	cnt;		/* associated count                   */
	unsigned int	pcnt;		/* physical (DMA) count for "pbuf"    */
	uint8		pad;
	uint8		sad;
	int		tmoMS;
	uint8		eosm;
	uint8		eosb;
	int		v;		/* control variable                   */
	int		code;		/* code of the lower level function   */
	int		ibsta;		/* return values from lower level     */
	int		iberr;		/* return value from lower level      */
	int		ibcnt;		/* return value from lower level      */
	int		flags;		/* general flags (config, etc)        */      
	int		ioflags;	/* I/O-specific flags                 */      
};

typedef struct lowlevel_param lowlevel_param;
 

/***********************  E N D   O F   F I L E  ************************/

