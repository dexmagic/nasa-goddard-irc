/************************************************************************
 *
 *	NI-488.2M Kernel-level Device Driver
 *
 *	CONFIGURATION INCLUDE FILE (used by driver and "ibconf")
 *
 ************************************************************************/

#define NBUSES			4	/* maximum number of "buses"	*/
#define NDEVICES		44	/* maximum number of devices	*/


#define MAX_BUSNAME_LENGTH	15	/* maximum name lengths...	*/
#define MAX_DEVNAME_LENGTH	15
#define MAX_NAME_LENGTH		15
#define MAX_HDLRNAME_LENGTH	16

#define VERSION_LENGTH		4	/* see "fixed_info" structure	*/

#define BUSMINOR		0	/* base of bus minor numbers	*/
#define DEVMINOR		51	/* base of device minor numbers	*/
#define PRIESTMINOR		50 	/* minor number of "priestess"	*/

#define DM_SIZE			24	/* should be multiple of 8 for	*/
					/*  good alignment -- see below	*/


#define DM_GLOBALS	"NI-488 Config: $GLOBALS"    /* 23 bytes + NULL */
#define DM_BOARDS	"NI-488 Config: $BOARDS "    /* 23 bytes + NULL */
#define DM_DEVICES	"NI-488 Config: $DEVICES"    /* 23 bytes + NULL */
#define DM_END		"NI-488 Config: $DATAEND"    /* 23 bytes + NULL */


/*
 * BOARD AND DEVICE CONFIGURATION STRUCTURES
 * Note that not all fields are used in all versions of GPIB driver.
 */
typedef struct {
        int	b_hwflags;         	/* user board configuration	*/ 
        int	b_ioflags;         	/* user io flags 		*/
        int	b_pad;  	 	/* primary GPIB address		*/
        int	b_sad;  	 	/* secondary GPIB address	*/
        int	b_eos;             	/* end-of-string character 	*/
        int	b_ppe;             	/* parallel poll enable byte	*/
        int	b_dma;             	/* 0: programmed I/O, 1: DMA 	*/
        int	b_sig;            	/* UNIX signal to send 		*/
        int	b_tmo;             	/* timeout value (see below) 	*/
	int	b_ppollibtime;		/* timeout for parallel poll 	*/
	int 	b_hwtype ;		/* hardware interface type	*/
	int 	b_scsitgt ;		/* SCSI target ID for SCSI box  */
	int 	b_busno ;		/* bus no - 0 thru n		*/
	int 	b_slotno;		/* slot no - 1 thru n		*/
	char	b_name[MAX_BUSNAME_LENGTH];
					/* board node name 		*/
	int 	b_baseaddr;		/* base address of the board    */
	int	b_cablelgth;		/* HS488 cable length in meters	*/

} ibconf_Board_t;


typedef struct {
        int	d_ioflags;
        int	d_bna;			/* access board number 		*/
        int	d_pad;  	 	/* primary GPIB address		*/
        int	d_sad;  	 	/* secondary GPIB address	*/
        int	d_eos;                	/* end-of-string character 	*/
        int	d_tmo;                	/* timeout value 		*/
        int	d_ppe;                	/* parallel poll enable byte 	*/
	int	d_spollibtime;		/* timeout for serial poll 	*/
        char	d_name[MAX_DEVNAME_LENGTH];
					/* device node name		*/

} ibconf_Device_t ;


/*
 * GLOBAL CONFIGURATION STRUCTURE
 * Note that not all fields are used in all versions of GPIB driver.
 *
 * The "g_flags" field is included primarily to retain backward
 * compatibility with earlier definitions of ibconf_Global_t, which
 * included just a single unsigned integer.  No unified drivers to
 * date (960308) have made use of this field.
 *
 * The "handler_version" info should come from the same #define that
 * initializes the "fixed_info" structure used by ibdiag.  Refer to
 * the DRIVER_VERSION definition in the <OS>.c files.
 */
typedef struct {
	int	g_flags;		/* general purpose flags	*/
	char	g_handler_name[MAX_HDLRNAME_LENGTH+1];
					/* driver description + NULL	*/
	char	g_handler_version[VERSION_LENGTH];
					/* driver version number	*/

} ibconf_Global_t ;


typedef ibconf_Global_t	ib_globals_t;
typedef ibconf_Board_t	ib_boards_t[NBUSES];
typedef ibconf_Device_t	ib_devices_t[NDEVICES];


typedef struct {
	long		ibc_globals_size;
	char		ibc_globals_mark[DM_SIZE];
	ib_globals_t	ibc_globals;

	long		ibc_boards_size;
	char		ibc_boards_mark[DM_SIZE];
	ib_boards_t	ibc_boards;

	long		ibc_devices_size;
	char		ibc_devices_mark[DM_SIZE];
	ib_devices_t	ibc_devices;

} ibconf_data_t;


/*
 * Bus-level configuration flags (b_hwflags)
 */
#define HW_PP2		0x0001		/* support local parallel poll */
#define HW_TTICK	0x0002		/* if set, b_tmo is ticks not scale factor */
#define HW_IST		0x0004		/* individual status bit */
#define HW_SRE		0x0008		/* assert REN when SC */
#define HW_SC		0x0010		/* interface is system controller */
#define HW_TRI		0x0020		/* use high-speed timing */
#define HW_NOSPOLL	0x0040		/* do not perform automatic serial polling */
#define HW_VSTD		0x0080		/* use Very Short T1 delay (very high-speed) */
#define HW_CICPROT	0x0100		/* cic protocol enabled	*/
#define HW_LLO		0x0200		/* changed from 0400(=0x100=CICPROT), JRB */
#define HW_DMA		0x0400		/* use DMA for all reads and writes */
#define HW_HS488	0x0800		/* enable HS-488 protocol */
 
/*
 * Board and device user flags (b_ioflags)
 */
#define EOT		0x0001		/* assert EOI with last byte of each write */
#define BDREOS		0x0004		/* terminate read on eos */
#define BDXEOS		0x0008		/* assert EOI with eos byte */
#define BDBIN		0x0010		/* eight-bit compare */
#define BDEOSM		0x001c		/* combined end-of-string modes */
#define NIEOSM		0x1c00		/* combined EOSM for "ni488.c" */

#define HLD		0x0002		/* holdoff handshake at end of each read */
#define TDCL		0x0020		/* terminate I/O on device clear */
#define TASC		0x0040		/* terminate I/O on address status change */

#define REPADDR		0x0100		/* Repeat address devices before every I/O  */
#define UNADDR		0x0200		/* Unaddress devices after every I/O */


/***********************  E N D   O F   F I L E  ************************/

