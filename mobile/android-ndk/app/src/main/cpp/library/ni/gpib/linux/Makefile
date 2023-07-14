# The Makefile for generating a shared library named libJavaGPIB.so

# The location of the gpib driver files
DRIVER_DIR = ./drivers/nigpib-0.8.3-gcc3

# The directory into which the library will be installed so that
# executables can use it
INSTALL_DIR = ../../../../../../lib/ni

CC = gcc
CFLAGS = -I$(DRIVER_DIR) -I/usr/local/java/include -I/usr/local/java/include/linux
#CFLAGS = 

VPATH = $(DRIVER_DIR)

# The name of the library
SO_NAME = libGPIB.so

# Flags needed during compilation of a shared library
LCFLAGS = -fPIC

# Flags needed during linking of a shared library
LDFLAGS = -shared

all : $(SO_NAME)

$(SO_NAME) : *GPIB.c *GPIB.h cib.c cib.h ugpib.h
	$(CC) $(LCFLAGS) $(LDFLAGS) $(CFLAGS) *GPIB.c $(DRIVER_DIR)/cib.c -o $(SO_NAME)

install : $(SO_NAME)
	install --mode=a+x,a+r,a-w,u+w $(SO_NAME) $(INSTALL_DIR)

#depend:
#	makedepend -I$(DRIVER_DIR) GPIB.c

