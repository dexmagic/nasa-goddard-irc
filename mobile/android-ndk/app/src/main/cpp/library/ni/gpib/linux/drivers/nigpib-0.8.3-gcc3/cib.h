/*
 * CIB Include File for Linux
 */

#ifndef __GPIBDRIVER__			/* ---------------------------- */
#define _INCLUDE_XOPEN_SOURCE
#define _INCLUDE_POSIX_SOURCE
#define _INCLUDE_HPUX_SOURCE
#include <sys/types.h>

#include <sys/stat.h>
#include <sys/ioctl.h>
#include <sys/fcntl.h>

#define ANSIC
#endif /* __GPIBDRIVER__ */		/* ---------------------------- */


struct ibarg {
	int		size;		/* size of ibarg */
	int		handle;		
        int		ibsta;		/* return status */
        int		iberr;		/* return error code */
        long		ibcntl;		/* return count */
        void		*buf;		/* buffer address */
        void		*buf1;		/* buffer address */
        void		*buf2;		/* buffer address */
        long		n;		/* count or argument */
        long		n1;		/* count or argument */
        long		n2;		/* count or argument */
};


/*
 * The IBFNcode macro is used within cib.c to convert all of the
 * NI-488[.2] function codes into the appropriate ioctl-compatible
 * codes for the host operating system.  For example, an "ioctl"
 * call to execute the "ibrd" function would look like this...
 *
 * 	ioctl(fd, IBFNcode(IBRD), &ibarg);
 *
 */
#define IBFNcode(n)	_IOWR(0, (n), struct ibarg)

/*
 * Unified driver function codes for NI-488
 */
#define IBDIAG		5 		/* special */
#define IBTRG		10
#define IBCLR		11
#define IBCMD		12
#define IBXTRC		14
#define IBRSP		15
#define IBRPP		16
#define IBBNA		17
#define IBWAIT		18
#define IBONL		19
#define IBRSV		20
#define IBGTS		21
#define IBCAC		22
#define IBPOKE		23
#define IBSGNL		24
#define IBSRE		25
#define IBRSC		26
#define IBRD		27
#define IBWRT		28
#define IBLOC		29
#define IBPCT		30
#define IBSIC		31
#define IBPPC		33
#define IBIST		32
#define IBLINES		34
#define IBEOT		35
#define IBPAD		36
#define IBSAD		37
#define IBDMA		38
#define IBEOS		39
#define IBTMO		40
#define IBWRTF		41
#define IBRDF		42
#define IBLLO		43
#define IBSTAT		44
#define IBLN_OLD	45
#define IBDEV		46
#define IBLN		47

/*
 * Unified driver function codes for NI-488.2
 */
#define ALLSPOLL	50
#define DEVCLEARLIST	51
#define ENABLELOCAL	52
#define ENABLEREMOTE	53
#define FINDRQS		54
#define FINDLSTN	55
#define PASSCONTROL	56
#define PPOLLCONFIG	57
#define PPOLLUNCONFIG	58
#define PPOLL		59
#define RECEIVE		60
#define RCVRESPMSG	61
#define RECEIVESETUP	62
#define RESETSYS	63
#define SENDCMDS	64
#define SENDDATABYTES	65
#define SENDIFC		66
#define SENDLIST	67
#define SENDLLO		68
#define SENDSETUP	69
#define SETRWLS		70
#define TESTSYS		71
#define TRIGGERLIST	72
#define DEVCLEAR	73
#define READSTATUSBYTE	74
#define SEND		75
#define TESTSRQ		76
#define TRIGGER		77
#define WAITSRQ		78

#define IBFIND		131
#define IBCONFIG	132
#define IBASK		133

#define IBWRTA		150
#define IBRDA		151
#define IBCMDA		152
#define IBSTOP		153
#define IBWRTKEY	154
#define IBRDKEY		155

