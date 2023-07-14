ifeq (/usr/src/linux/.config,$(wildcard /usr/src/linux/.config))
  include /usr/src/linux/.config
endif

KVERS := $(shell uname -r)
ifndef KINCDIR
  ifeq (/lib/modules/${KVERS}/build/include,$(wildcard /lib/modules/${KVERS}/build/include))
    KINCDIR = /lib/modules/${KVERS}/build/include
  else
    ifeq (/usr/src/linux/include/linux,$(wildcard /usr/src/linux/include/linux))
      KINCDIR = /usr/src/linux/include
    else
      ifeq (/usr/include/linux,$(wildcard /usr/include/linux))
        KINCDIR = /usr/include
      endif
    endif
  endif
endif

ifeq (${KINCDIR}/linux/modversions.h,$(wildcard ${KINCDIR}/linux/modversions.h))
  CONFIG_MODVERSIONS = 1
endif

SMPTEST := $(shell sh -c '(echo \\\#ifdef CONFIG_SMP; echo HASSMP; echo \\\#endif ) | gcc -E -include $(KINCDIR)/linux/autoconf.h - | grep "^HASSMP$$" | tail -1')

ifneq ($(SMPTEST),)
  CONFIG_SMP = 1
endif

IVERS := $(shell echo 'UTS_RELEASE' | gcc -E -include $(KINCDIR)/linux/version.h - | tail -1 | sed -e 's,",,g' -e 's,[ 	]*,,g' || uname -r)
MODFLAGS = -DMODULE

ifdef CONFIG_MODVERSIONS
  MODFLAGS += -DMODVERSIONS -include $(KINCDIR)/linux/modversions.h
endif
SMP=
ifdef CONFIG_SMP
  MODFLAGS += -D__SMP__
  SMP=smp
endif

HEADERS = ib_linux.h cib.h ugpib.h ibconf.h ib_linux.h ib_ni.h
IBLINUX = ib_linux-$(IVERS)$(SMP).o

CC=$(shell kgcc -v >/dev/null 2>&1 && echo kgcc || echo gcc)
# These flags are only used for the driver object
CPPFLAGS= -D__GPIBDRIVER__ 
CFLAGS= -c -O2 -D__KERNEL__ -I$(KINCDIR) $(MODFLAGS)

all::	pretest nigpib.o

pretest::	
	@if test $(IVERS) != $(KVERS) ; then \
		echo "ERROR! Running kernel $(KVERS) doesn't match kernel include files ($(IVERS))."; \
		echo "Please fix your kernel include files or reboot to the appropriate kernel."; \
		echo "To bypass this warning, type 'make nigpib.o' directly."; \
		rm -f nigpib.o; \
		exit 1; \
	fi

nigpib.o: ib_ni.o $(IBLINUX) module_license.o
	ld -r -o nigpib.o  ib_ni.o $(IBLINUX) module_license.o
	cp nigpib.o nigpib-$(IVERS)$(SMP).o

install: nigpib.o
	@test -d "/lib/modules/$(IVERS)/misc" || \
	(echo "Cannot find modules directory"; exit 1)
	cp nigpib.o /lib/modules/$(IVERS)/misc
	@if test -f /etc/conf.modules; then \
		grep 'alias char-major-31 nigpib' /etc/conf.modules >/dev/null || echo 'alias char-major-31 nigpib' >> /etc/conf.modules ; \
		/sbin/depmod -a ;\
	elif test -f /etc/modules.conf; then \
		grep 'alias char-major-31 nigpib' /etc/modules.conf >/dev/null || echo 'alias char-major-31 nigpib' >> /etc/modules.conf ; \
		/sbin/depmod -a ;\
	else \
		echo "Please add an 'insmod nigpib' to your system startup scripts or enable autoloading with kerneld." ; \
	fi


prebuilt::
	../makevers 2.0.36 0
	../makevers 2.2.5 0
	../makevers 2.2.5 1
	../makevers 2.2.11 0
	../makevers 2.2.11 1
	../makevers 2.2.14 0
	../makevers 2.2.14 1
	../makevers 2.2.17 0
	../makevers 2.2.17 1
	../makevers 2.2.19 0
	../makevers 2.2.19 1
	../makevers 2.4.2 0
	../makevers 2.4.2 1

#
# Generate the driver object from the source files.
#
$(IBLINUX): ib_linux.c $(HEADERS)
	$(CC) $(CFLAGS) $(CPPFLAGS) $(INCLUDE) -o $(IBLINUX) ib_linux.c

#
# Generate the language interface object from the source files.
#
cib.o: $(CIB_SOURCE)
	$(CC) $(INCLUDE) -c cib.c -o cib.o

module_license.o: $(CIB_SOURCE)
	$(CC) $(INCLUDE) -c module_license.c -o module_license.o

#
# Remove all object files and the driver.
#
clean:
	rm -f $(IBLINUX) ib_linux-*.o nigpib*.o
