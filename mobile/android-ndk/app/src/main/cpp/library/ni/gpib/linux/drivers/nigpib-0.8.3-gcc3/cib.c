/*
 * C Language interface.
 * (%M% %I% - %G% %U%)
 */
#include "cib.h"
#include "ugpib.h"
#include <errno.h>

/*
extern		errno;
*/

int		ibsta, iberr;
unsigned int	ibcnt;
long		ibcntl;

static int	fp_gpib = -1;
static int	oserr();


/*
 * common function to set up ioctl, make call
 * and set global variables
 */
#ifdef __STDC__
static fn(int code, struct ibarg *ibarg)
#else
static fn(code, ibarg) struct ibarg *ibarg;
#endif
{
	char *path="/dev/gpib";

	if (fp_gpib==-1) {
		fp_gpib=open(path,O_RDWR);
		if (fp_gpib==-1) {
			if ((code == (int) IBFNcode(IBFIND))
			    || (code == (int) IBFNcode(IBDEV))) {
				ibarg->handle = -1;
			}
			return oserr();
		}
	}

	ibarg->size=sizeof(struct ibarg);
	if (ioctl(fp_gpib, code, ibarg) < 0)
		return oserr();
	ibcnt = ibarg->ibcntl;
	ibcntl = ibarg->ibcntl;
	iberr = ibarg->iberr;
	ibsta = ibarg->ibsta;

	return ibarg->ibsta;
}

/*
 * operating system error:
 *      error code EDVR indicates OS error code is in ibcnt
 */
static oserr() {
	iberr = EDVR;
	ibcntl = ibcnt = errno;
	return ibsta = ERR|CMPL;
}


#define IB_BDNAME_SIZE 256
#ifdef __STDC__
ibfind  (char *bdname)
#else
ibfind  (bdname) char *bdname;
#endif
{
	struct ibarg ibarg;
	struct stat fs;
	char name[IB_BDNAME_SIZE];

	if (*bdname=='/') {
		if (stat(bdname,&fs)!=0) {
			iberr=EDVR;
			ibsta=ERR|CMPL;
			return -1;
		}
	} else {
		name[0]='/';
		name[1]='d';
		name[2]='e';
		name[3]='v';
		name[4]='/';
		strncpy(&name[5],bdname,IB_BDNAME_SIZE-5);
		name[IB_BDNAME_SIZE-1]='0';
		if (stat(name,&fs)!=0) {
			iberr=EDVR;
			ibsta=ERR|CMPL;
			return -1;
		}
	}
	if (S_ISCHR(fs.st_mode)) {
		ibarg.handle=0;
		ibarg.n=fs.st_rdev;
		fn(IBFNcode(IBFIND),&ibarg);
		return ibarg.handle;
	} else {
		iberr=EDVR;
		ibsta=ERR|CMPL;
		return -1;
	}

}

#ifdef __STDC__
ibdev (int bindex, int pad, int sad, int tmo, int eot, int eos)
#else
ibdev (bindex,pad,sad,tmo,eot,eos)
#endif
{
	struct ibarg ibarg;
	int info[6];

	info[0]=bindex;
	info[1]=pad;
	info[2]=sad;
	info[3]=tmo;
	info[4]=eot;
	info[5]=eos;
	ibarg.handle=bindex;
	ibarg.n=sizeof(info);
	ibarg.buf=&info[0];
	fn(IBFNcode(IBDEV),&ibarg);
	return ibarg.handle;
}

/*
 * ibbna changes a device's access board.
 */
#ifdef __STDC__
ibbna(int g, char *bname)
#else
ibbna(g,bname) char bname[]; 
#endif
{
	struct ibarg ibarg;
	struct stat fs;
	char name[IB_BDNAME_SIZE];

	if (bname[0]=='/') {
		if (stat(bname,&fs)!=0) {
			iberr=EARG;
			ibsta=ERR|CMPL;
			return ibsta;
		}
	} else {
		name[0]='/';
		name[1]='d';
		name[2]='e';
		name[3]='v';
		name[4]='/';
		strncpy(&name[5],bname,IB_BDNAME_SIZE-5);
		name[IB_BDNAME_SIZE-1]='0';
		if (stat(name,&fs)!=0) {
			iberr=EARG;
			ibsta=ERR|CMPL;
			return ibsta;
		}
	}
	if (S_ISCHR(fs.st_mode)) {
		ibarg.handle=g;
		ibarg.n=fs.st_rdev;
		return fn(IBFNcode(IBBNA),&ibarg);
	} else {
		iberr=EARG;
		ibsta=ERR|CMPL;
		return ibsta;
	}
}


#ifdef __STDC__
ibdiag (int g, void *buf, long cnt)
#else
ibdiag  (g, buf, cnt) long *buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	ibarg.n=cnt;
	return fn(IBFNcode(IBDIAG), &ibarg); 
}

#ifdef __STDC__
ibrd (int g, void *buf, long cnt)
#else
ibrd  (g, buf, cnt) void *buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	ibarg.n=cnt;
	return fn(IBFNcode(IBRD), &ibarg); 
}

#ifdef __STDC__
ibrda (int g, void *buf, long cnt)
#else
ibrda  (g, buf, cnt) void *buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	ibarg.n=cnt;
	return fn(IBFNcode(IBRDA), &ibarg); 
}

#ifdef __STDC__
ibwrt (int g, void *buf, long cnt)
#else
ibwrt  (g, buf, cnt) void *buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	ibarg.n=cnt;
	return fn(IBFNcode(IBWRT), &ibarg); 
}

#ifdef __STDC__
ibwrta (int g, void *buf, long cnt)
#else
ibwrta  (g, buf, cnt) void *buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	ibarg.n=cnt;
	return fn(IBFNcode(IBWRTA), &ibarg); 
}

#ifdef __STDC__
ibcmd (int g, void *buf, long cnt)
#else
ibcmd (g, buf, cnt) void *buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	ibarg.n=cnt;
	return fn(IBFNcode(IBCMD), &ibarg); 
}

#ifdef __STDC__
ibcmda (int g, void *buf, long cnt)
#else
ibcmda (g, buf, cnt) void *buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	ibarg.n=cnt;
	return fn(IBFNcode(IBCMDA), &ibarg); 
}

#ifdef __STDC__
ibstop (int g)
#else
ibstop (g)
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	return fn(IBFNcode(IBSTOP), &ibarg); 
}

#ifdef __STDC__
ibtrg (int g)
#else
ibtrg (g)
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	return fn(IBFNcode(IBTRG), &ibarg); 
}

#ifdef __STDC__
ibclr (int g)
#else
ibclr (g)
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	return fn(IBFNcode(IBCLR), &ibarg); 
}

#ifdef __STDC__
ibpct (int g)
#else
ibpct (g)
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	return fn(IBFNcode(IBPCT), &ibarg); 
}

#ifdef __STDC__
ibllo (int g)
#else
ibllo (g)
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	return fn(IBFNcode(IBLLO), &ibarg); 
}

#ifdef __STDC__
ibloc (int g)
#else
ibloc (g)
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	return fn(IBFNcode(IBLOC), &ibarg); 
}

#ifdef __STDC__
ibsic (int g)
#else
ibsic (g)
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	return fn(IBFNcode(IBSIC), &ibarg); 
}

#ifdef __STDC__
ibrsp (int g, char *buf)
#else
ibrsp (g, buf) char *buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	return fn(IBFNcode(IBRSP), &ibarg); 
}

#ifdef __STDC__
ibrpp (int g, char *buf)
#else
ibrpp (g, buf) char * buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	return fn(IBFNcode(IBRPP), &ibarg); 
}

#ifdef __STDC__
iblines (int g, short *buf)
#else
iblines (g, buf) short * buf;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.buf=buf;
	return fn(IBFNcode(IBLINES), &ibarg); 
}

#ifdef __STDC__
ibwait (int g, int mask)
#else
ibwait (g, mask) int mask;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=mask;
	return fn(IBFNcode(IBWAIT), &ibarg); 
}

#ifdef __STDC__
ibrsv (int g, int v)
#else
ibrsv (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBRSV), &ibarg); 
}

#ifdef __STDC__
ibsre (int g, int v)
#else
ibsre (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBSRE), &ibarg); 
}

#ifdef __STDC__
ibonl (int g, int v)
#else
ibonl (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBONL), &ibarg); 
}

#ifdef __STDC__
ibgts (int g, int v)
#else
ibgts (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBGTS), &ibarg); 
}

#ifdef __STDC__
ibcac (int g, int v)
#else
ibcac (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBCAC), &ibarg); 
}

#ifdef __STDC__
ibpoke (int g, int option, int v)
#else
ibpoke (g, option, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=option;
	ibarg.n1=v;
	return fn(IBFNcode(IBPOKE), &ibarg); 
}

#ifdef __STDC__
ibsgnl (int g, int v)
#else
ibsgnl (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBSGNL), &ibarg); 
}


#ifdef __STDC__
ibrsc (int g, int v)
#else
ibrsc (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBRSC), &ibarg); 
}

#ifdef __STDC__
ibppc (int g, int v)
#else
ibppc (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBPPC), &ibarg); 
}

#ifdef __STDC__
ibist (int g, int v)
#else
ibist (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBIST), &ibarg); 
}

#ifdef __STDC__
ibeot (int g, int v)
#else
ibeot (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBEOT), &ibarg); 
}

#ifdef __STDC__
ibpad (int g, int v)
#else
ibpad (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBPAD), &ibarg); 
}

#ifdef __STDC__
ibsad (int g, int v)
#else
ibsad (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBSAD), &ibarg); 
}

#ifdef __STDC__
ibdma (int g, int v)
#else
ibdma (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBDMA), &ibarg); 
}

#ifdef __STDC__
ibeos (int g, int v)
#else
ibeos (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBEOS), &ibarg); 
}

#ifdef __STDC__
ibtmo (int g, int v)
#else
ibtmo (g, v) int v;
#endif
{ 
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=v;
	return fn(IBFNcode(IBTMO), &ibarg); 
}

#ifdef __STDC__
ibconfig(int g, int option, int v) 
#else
ibconfig(g, option, v) int option, v;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = g;
  	ibarg.n1 = option;
  	ibarg.n2 = v;
  	return fn(IBFNcode(IBCONFIG), &ibarg);
}

#ifdef __STDC__
ibask(int g, int option, int *ret) 
#else
ibask(g, option, ret) int option, *ret;
#endif
{
	struct ibarg ibarg;
        int result;

	ibarg.handle = g;
  	ibarg.n1 = option;
  	result = fn(IBFNcode(IBASK), &ibarg);
  	*ret = ibarg.n2;
        return result;
}

	
#ifdef __STDC__
ibln (int g, int pad, int sad, short *listen)
#else
ibln (g,pad,sad,listen) short *listen;
#endif
{
	struct ibarg ibarg;

	/*
	 * NOTE: Some older language interfaces may pack both "pad"
	 * and "sad" into "ibarg.n".  This arrangement is indicated
	 * to the driver by the use of the obsolete code IBLN_OLD.
	 */
	ibarg.handle = g;
	ibarg.n = pad;
	ibarg.n1 = sad;	
	ibarg.buf = listen;
	return fn(IBFNcode(IBLN), &ibarg);
}


/*
 * file I/O routines:
 */

#define FBSIZE  512             /* size of buffer for file I/O */
static char buf[FBSIZE*2];

/*
 * write file.
 * double buffering is used not for improved throughput, but
 * for determining the last buffer before it is sent.
 */
ibwrtf(g, file) char file[]; {
	int f, i, cnt[2], total=0;
	ushort wrtfail=0, sta=0, err=0;
	char eot;

	if ((f=open(file, O_RDONLY, 0)) < 0)
		return oserr();
	ibeot(g,0);
	eot=iberr;
	i= 0;
	if ((cnt[0]=read(f, buf, FBSIZE)) < 0) {
		oserr();
		goto bad;
	}
	while (cnt[i]) {
		i= !i ;
		if ((cnt[i]=read(f, &buf[i*FBSIZE], FBSIZE)) < 0) {
			oserr();
			goto bad;
		}
		if (cnt[i] == 0) { /* then the previous read reached the eof */
			ibeot(g,eot);
			if (ibsta&ERR)    /* restore eot mode */
				goto bad;
		}
		if ((ibwrt(g, &buf[!i * FBSIZE], cnt[!i])) & ERR) {
			wrtfail = 1;
			sta = ibsta;
			err = iberr;
			goto bad;
		}
		total += ibcntl;
	}
	goto ret;
bad:    
	ibeot(g,eot);
	if (wrtfail) {		/* restore the write fail status and err */
		ibsta = sta;
		iberr = err;
	}
ret:    
	close(f);
	ibcnt = total;
	ibcntl = total;
	return ibsta;
}

/*
 * read file.
 */
ibrdf(g, file) char file[]; {
	int f, total=0;

	if ((f=creat(file, 0644)) < 0) {
		iberr = EFSO;
		ibcnt = 0;
		ibcntl = 0;
		ibsta = ERR|CMPL;
		return ibsta;
	}
	do {    if (((ibrd(g, buf, FBSIZE*2) & ERR) && (iberr ==EDVR)) || (ibcntl>FBSIZE*2))
			goto bad;
		if (write (f, buf, ibcntl) != ibcntl)
			goto bad;
		total += ibcntl;
	} while ((ibsta & (END|ERR)) == 0);
bad:    close(f);;
	ibcnt = total;
	ibcntl = total;
	return ibsta;
}

#ifdef __STDC__
ibxtrc  (int g, void *buf, long cnt)
#else
ibxtrc  (g,buf,cnt)
void *buf;
#endif
{
	struct ibarg ibarg;

	ibarg.handle=g;
	ibarg.n=cnt;
	ibarg.buf=buf;
	return fn(IBFNcode(IBXTRC), &ibarg);
}

#ifdef __STDC__
ibwrtkey(int g, void *buf, int cnt)
#else
ibwrtkey(g,buf,cnt)
int g ;
void *buf;
int cnt ;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = g;
	ibarg.n = cnt ;
	ibarg.buf = buf ;
	return fn(IBFNcode(IBWRTKEY), &ibarg) ;
}
 
#ifdef __STDC__
ibrdkey(int g, void *buf, int cnt) 
#else 
ibrdkey(g,buf,cnt) 
int g ; 
void *buf;
int cnt ; 
#endif 
{
	struct ibarg ibarg;

	ibarg.handle = g;
	ibarg.n = cnt ; 
	ibarg.buf = buf ; 
	return fn(IBFNcode(IBRDKEY), &ibarg) ; 
} 


/*********************************************************
 * NI-488.2 functions 
 *********************************************************/

#ifdef __STDC__
void AllSpoll(int b, Addr4882_t *taddrs, short *res)
#else
void AllSpoll(b,taddrs,res) int b; Addr4882_t *taddrs; short *res; 
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = taddrs;
	ibarg.buf2 = res;
	fn(IBFNcode(ALLSPOLL),&ibarg);
} 

#ifdef __STDC__
void DevClearList(int b, Addr4882_t *addrlist)
#else
void DevClearList(b,addrlist) Addr4882_t *addrlist; 
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = addrlist;
	fn(IBFNcode(DEVCLEARLIST),&ibarg);
}

#ifdef __STDC__
void DevClear(int b, Addr4882_t addr)
#else
void DevClear(b,addr) int b; Addr4882_t addr;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.n = addr;
	fn(IBFNcode(DEVCLEAR),&ibarg);
}		

#ifdef __STDC__
void EnableLocal(int b, Addr4882_t *laddrs)
#else
void EnableLocal(b,laddrs) Addr4882_t *laddrs;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = laddrs;
	fn(IBFNcode(ENABLELOCAL),&ibarg);
}

#ifdef __STDC__
void EnableRemote(int b,  Addr4882_t *laddrs)
#else
void EnableRemote(b,laddrs)  Addr4882_t *laddrs;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = laddrs;
	fn(IBFNcode(ENABLEREMOTE),&ibarg);
}

#ifdef __STDC__
void FindRQS(int b, Addr4882_t *taddrs, short *dev_stat)
#else
void FindRQS(b,taddrs,dev_stat) Addr4882_t *taddrs; short *dev_stat;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = taddrs;
	fn(IBFNcode(FINDRQS),&ibarg);
	*dev_stat = (short)ibarg.n;
} 

#ifdef __STDC__
void FindLstn(int b,Addr4882_t *addrlist, Addr4882_t *reslist, int limit)
#else
void FindLstn(b,addrlist,reslist,limit) Addr4882_t *addrlist; Addr4882_t *reslist; int limit;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = addrlist;
	ibarg.buf2 = reslist;
	ibarg.n = limit;
	fn(IBFNcode(FINDLSTN),&ibarg);
} 

#ifdef __STDC__
void PassControl(int b, Addr4882_t talker)
#else
void PassControl(b,talker) Addr4882_t talker;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.n = talker;
	fn(IBFNcode(PASSCONTROL),&ibarg);
}	

#ifdef __STDC__
void PPollConfig(int b, Addr4882_t laddr, int  dataline, int  sense)
#else
void PPollConfig(b,laddr,dataline,sense) Addr4882_t laddr; int dataline; int sense;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.n = laddr;
	ibarg.n1 = dataline;
	ibarg.n2 = sense;
	fn(IBFNcode(PPOLLCONFIG),&ibarg);
}

#ifdef __STDC__
void PPollUnconfig(int b,Addr4882_t *laddrs)
#else
void PPollUnconfig(b,laddrs) Addr4882_t *laddrs;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = laddrs;
	fn(IBFNcode(PPOLLUNCONFIG),&ibarg);
}	

#ifdef __STDC__
void PPoll(int b, short *res_ptr)
#else
void PPoll(b,res_ptr) short *res_ptr;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	fn(IBFNcode(PPOLL),&ibarg);
	*res_ptr = (short)ibarg.n;
}

#ifdef __STDC__
void ReadStatusByte(int b, Addr4882_t talker, short *result)
#else
void ReadStatusByte(b,talker,result)Addr4882_t talker, *result;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.n = talker;
	fn(IBFNcode(READSTATUSBYTE),&ibarg);
	*result = (short)ibarg.n1;
} 

#ifdef __STDC__
void Receive(int b,Addr4882_t talker, void *buf, long cnt, int eotmode)
#else
void Receive(b,talker,buf,cnt,eotmode) Addr4882_t talker; void *buf; long cnt; int eotmode;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.n1 = talker;
	ibarg.buf = buf;
	ibarg.n = cnt;
	ibarg.n2 = eotmode;
	fn(IBFNcode(RECEIVE),&ibarg);
}

#ifdef __STDC__
void RcvRespMsg	(int b, void *buf, long cnt, int eotmode)
#else
void RcvRespMsg	(b,buf,cnt,eotmode) void *buf; long cnt; int eotmode;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf = buf;
	ibarg.n = cnt;
	ibarg.n1 = eotmode;
	fn(IBFNcode(RCVRESPMSG),&ibarg);
}

#ifdef __STDC__
void ReceiveSetup(int b, Addr4882_t talker)
#else
void ReceiveSetup(b,talker) Addr4882_t talker;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.n = talker;
	fn(IBFNcode(RECEIVESETUP),&ibarg);
}	

#ifdef __STDC__
void SendList(int b,Addr4882_t *listeners, void *buf, long cnt, int eotmode)
#else
void SendList(b,listeners,buf,cnt,eotmode)
Addr4882_t listeners[]; void *buf; long cnt; int eotmode;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = listeners;
	ibarg.buf = buf;
	ibarg.n = cnt;
	ibarg.n2 = eotmode;
	fn(IBFNcode(SENDLIST),&ibarg);
}	

#ifdef __STDC__
void Send(int b,Addr4882_t listener, void *buf, long cnt, int eotmode)
#else
void Send(b,listener,buf,cnt,eotmode) Addr4882_t listener; void *buf; long cnt; int eotmode;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.n1 = listener;
	ibarg.buf = buf;
	ibarg.n = cnt;
	ibarg.n2 = eotmode;
	fn(IBFNcode(SEND),&ibarg);
}	

#ifdef __STDC__
void SendCmds(int b, void *buf, long cnt)
#else
void SendCmds(b,buf,cnt) void *buf; long cnt;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf = buf;
	ibarg.n = cnt;
	fn(IBFNcode(SENDCMDS),&ibarg);
}	

#ifdef __STDC__
void SendDataBytes(int b, void *buf, long cnt, int eotmode)
#else
void SendDataBytes(b,buf,cnt,eotmode) void *buf; long cnt; int eotmode;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf = buf;
	ibarg.n = cnt;
	ibarg.n2 = eotmode;
	fn(IBFNcode(SENDDATABYTES),&ibarg);
}

#ifdef __STDC__
void SendIFC(int b)
#else
void SendIFC(b)
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	fn(IBFNcode(SENDIFC),&ibarg);
}	

#ifdef __STDC__
void ResetSys(int b,Addr4882_t *laddrs)
#else
void ResetSys(b,laddrs) Addr4882_t  *laddrs;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = laddrs;
	fn(IBFNcode(RESETSYS),&ibarg);
}

#ifdef __STDC__
void SendLLO(int b)
#else
void SendLLO(b)
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	fn(IBFNcode(SENDLLO),&ibarg);
}	

#ifdef __STDC__
void SendSetup(int b,Addr4882_t *listen)
#else
void SendSetup(b,listen) Addr4882_t *listen;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = listen;
	fn(IBFNcode(SENDSETUP),&ibarg);
}	

#ifdef __STDC__
void SetRWLS(int b,Addr4882_t *laddrs)
#else
void SetRWLS(b,laddrs) Addr4882_t *laddrs;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = laddrs;
	fn(IBFNcode(SETRWLS),&ibarg);
}	

#ifdef __STDC__
void TestSRQ(int b, short *result)
#else
void TestSRQ(b,result) short *result;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	fn(IBFNcode(TESTSRQ),&ibarg);
	*result=ibarg.n;
}		

#ifdef __STDC__
void TestSys(int b, Addr4882_t *addrs, short *result)
#else
void TestSys(b,addrs,result) Addr4882_t *addrs; short *result;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = addrs;
	ibarg.buf2 = result;
	fn(IBFNcode(TESTSYS),&ibarg);
}	

#ifdef __STDC__
void TriggerList(int b,Addr4882_t *laddrs)
#else
void TriggerList(b,laddrs)Addr4882_t  *laddrs;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.buf1 = laddrs;
	fn(IBFNcode(TRIGGERLIST),&ibarg);
}	

#ifdef __STDC__
void Trigger(int b,Addr4882_t laddr)
#else
void Trigger(b,laddr) Addr4882_t laddr;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	ibarg.n = laddr;
	fn(IBFNcode(TRIGGER),&ibarg);
}	

#ifdef __STDC__
void WaitSRQ(int b,short *result)
#else
void WaitSRQ(b,result)short *result;
#endif
{
	struct ibarg ibarg;

	ibarg.handle = b;
	fn(IBFNcode(WAITSRQ),&ibarg);
	*result = ibarg.n;
}
