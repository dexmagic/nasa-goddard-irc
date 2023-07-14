#//=== File Prolog ============================================================
#//
#//This code was developed by NASA, Goddard Space Flight Center, Code 580
#//for the Instrument Remote Control (IRC) project.
#//
#//--- Notes ------------------------------------------------------------------
#//
#//--- Warning ----------------------------------------------------------------
#//This software is property of the National Aeronautics and Space
#//Administration. Unauthorized use or duplication of this software is
#//strictly prohibited. Authorized users are subject to the following
#//restrictions:
#//*    Neither the author, their corporation, nor NASA is responsible for
#//any consequence of the use of this software.
#//*    The origin of this software must not be misrepresented either by
#//explicit claim or by omission.
#//*    Altered versions of this software must be plainly marked as such.
#//*    This notice may not be removed or altered.
#//
#//=== End File Prolog ========================================================

#
# Example program to read XDR basisset data streams
# from IRC.  The Output Adapter to use in BasisSetXdrTcpEncoder.
#
#
# 
# $Id: sampleXdrClient.py,v 1.3 2006/05/23 16:09:58 smaher_cvs Exp $
# $Author: smaher_cvs $
#

import socket;
import xdrlib;

# 0 = minimal logging, 1, 2, 3 supported
LOGGING = 0

# Header packet types (defined in IRC)
DESCRIPTOR_PACKET = 1
DATA_PACKET = 2
ERROR_PACKET = 3
    
# Data buffer types (defined in IRC [XdrTcpEncoder])
BYTE_TYPE = 0
CHAR_TYPE = 1
SHORT_TYPE = 2
INT_TYPE = 3
LONG_TYPE = 4
FLOAT_TYPE = 5
DOUBLE_TYPE = 6
OBJECT_TYPE = 7
    
#
# Main loop to open socket and read data
#
def clientStart(host, port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    sock.connect((host, port))
    print 'Connected to', port, 'on', host

    sendRequest(sock)

    # Perpetually read data and/or descriptor packets
    #
    while True:
        header = readHeader(sock)
    
        if header.packetType == ERROR_PACKET:
            print "Error:", header.message
                
        if header.packetType == DESCRIPTOR_PACKET:
    
            descriptor = readBasisBundleDescriptor(sock)
            trailer = readTrailer(sock)
            
        if header.packetType == DATA_PACKET:
    
            basisSet = readBasisSet(header, sock)
            trailer = readTrailer(sock)
            if trailer.sequenceNumber != header.sequenceNumber:
                print "Header and trailer sequence numbers didn't match!"
            else:
                # Some diagnostic info ..
                print "First time value = ", basisSet.basisBuffer[0] 
                
    sock.close()

    
# Sends a data set request
#
#
def sendRequest(sock):
    

    if LOGGING:
        print "Sending request"    

    requestXML1 = '''<DataSetRequest>
                <Format>xdr</Format>
                <BasisBundleRequest>
                    <BasisBundleName>    SensorData.MarkIII
                    </BasisBundleName>
                    <DataBufferName>
                        {DAC \[.*,0\]}
                    </DataBufferName>
                    <RequestAmount>1000</RequestAmount>
                    <!--
                    <RequestUnit>s</RequestUnit>
                    <Downsampling>1</Downsampling>            -->    
                </BasisBundleRequest>
                <BasisBundleRequest>
                    <BasisBundleName>    StreamConfiguration.MarkIII
                    </BasisBundleName>
                    <DataBufferName>
                        B
                    </DataBufferName>
                    <DataBufferName>
                        C
                    </DataBufferName>    
                    <RequestAmount>1</RequestAmount>
                    <Downsampling>1</Downsampling>            
                </BasisBundleRequest>
            </DataSetRequest>'''
            
    requestXML2 = '''<DataSetRequest>
                <Format>xdr</Format>
                <BasisBundleRequest>
                    <BasisBundleName>    SensorData.MarkIII
                    </BasisBundleName>
                    <DataBufferName>
                        DAC [0,0]
                    </DataBufferName>
                    <RequestAmount>1000</RequestAmount>           
                </BasisBundleRequest>
            </DataSetRequest>'''
            
    p = xdrlib.Packer()
    p.pack_int(len(requestXML1))
    sock.send(p.get_buffer())
    
    sockSendAll(sock, requestXML1)


#
# Read an IRC basis set
#
# sock open socket with IRC basis set server
#
# Returns basis set class with basisBuffer and dataBuffers attributes
#
def readBasisSet(header, sock):
    if LOGGING:
        print 'Reading basis set'
        
    class basisSet: pass        
    basisSet.basisBuffer = readDataBuffer(sock)

    # dataBuffers is list of data buffers
    basisSet.dataBuffers = []
    for i in range(header.numberOfBuffers - 1):
        basisSet.dataBuffers.append(readDataBuffer(sock))
        
    if LOGGING:
        print 'Done reading basis set'          

    return basisSet

#    
# Read a data buffer
#
# sock open socket with IRC basis set server
#
def readDataBuffer(sock):

    if LOGGING > 1:
        print 'Reading data buffer'    
    
    data = []
        
    unpacker = xdrlib.Unpacker(recvFragmentGroup(sock))
    
    # The name of the data buffer
    
    name = unpacker.unpack_string();
    
    if LOGGING > 2:
        print 'Name = ', name
        
    # The type of the data
    type = unpacker.unpack_uint()
    
    # How many samples
    numberOfSamples = unpacker.unpack_uint()    
    
    # Read the data
    if type == DOUBLE_TYPE:
        data = unpacker.unpack_farray(numberOfSamples, unpacker.unpack_double)

    if type == FLOAT_TYPE:
        data = unpacker.unpack_farray(numberOfSamples, unpacker.unpack_float)        

    if type == LONG_TYPE:
        # Longs are call "hypers" in XDR-ese
        data = unpacker.unpack_farray(numberOfSamples, unpacker.unpack_hyper)
                                      
    if type == INT_TYPE:
        data = unpacker.unpack_farray(numberOfSamples, unpacker.unpack_int)        
        
    if type == SHORT_TYPE:
        # Shorts are encoded as ints
        data = unpacker.unpack_farray(numberOfSamples, unpacker.unpack_int)
        
    if type == BYTE_TYPE:
        # bytes are encoded as ints
        data = unpacker.unpack_farray(numberOfSamples, unpacker.unpack_int)    
        
    if type == CHAR_TYPE:
        # bytes are encoded as ints
        print 'Character encoding not yet supported'  
   
    unpacker.done()
    return data

 
# Read a basis bundle descriptor
#
# Not implemented - just reads past bytes
#
# sock open socket with IRC basis set server
#
def readBasisBundleDescriptor(sock):
    
    if LOGGING:
        print "Reading Descriptor"
        
    unpacker = xdrlib.Unpacker(recvFragmentGroup(sock))        
    
    
# Read an IRC "Header"
#
# sock open socket with IRC basis set server
#
def readHeader(sock):
    
    if LOGGING:
        print "Reading Header"
        
    unpacker = xdrlib.Unpacker(recvFragmentGroup(sock))
    class header: pass
    header.basisBundleId = unpacker.unpack_string()
    header.numberOfBuffers = unpacker.unpack_uint()
    header.packetType = unpacker.unpack_uint()    
    header.numberOfSamples = unpacker.unpack_uint()
    header.sequenceNumber = unpacker.unpack_uint()
    header.message = unpacker.unpack_string()    
    unpacker.done()

    if LOGGING:
        print 'Header BBId', header.basisBundleId
        print 'Header PacketType', header.packetType    
        print 'Header NumSamples', header.numberOfSamples  
        print 'Header NumberOfBuffers', header.numberOfBuffers
        print 'Header SequenceNumber', header.sequenceNumber   
        print 'Header Message', header.message          
        print 'Done reading header'           
     
    return header

# Read an IRC "Trailer"
#
# sock open socket with IRC basis set server
#
def readTrailer(sock):
    
    if LOGGING:
        print "Reading Trailer"    
        
    unpacker = xdrlib.Unpacker(recvFragmentGroup(sock))
    class trailer: pass
    trailer.basisBundleId = unpacker.unpack_string()
    trailer.sequenceNumber = unpacker.unpack_uint()
    unpacker.done()
    
    if LOGGING:
        print 'Trailer BBId', trailer.basisBundleId  
        print 'Trailer sequenceNumber', trailer.sequenceNumber
        print 'Done reading trailer'     
        
    return trailer    
   
    
#
# Receive a "fragment group".
#
# A fragment group is what is sent between the "beginEncoding()"
# and "endEncoding()" methods in XdrTcpEncoder in the IRC.  These
# generally surround large, cohesive objects: header, trailer, and
# single data buffers.
#
# The IRC XDR implementation fragments the fragment group
# buffer boundaries.  The fragments have a 4 byte header
# that contains the length of the fragment and a "last fragment"
# flag.  This method reads all fragments until the last fragment
# returns the resulting data (with the fragment headers removed).
#
# sock open socket with IRC basis set server
#
def recvFragmentGroup(sock):
    
    lastFragment = 0
    alldata = ''
    while lastFragment == 0:
        
        #
        # Receive "fragment header" and decode length
        #
        data = sockRecvAll(sock, 4)
        unpacker = xdrlib.Unpacker(data)
        header = unpacker.unpack_uint()
        fragmentLength = header & 0x7fffffff
        lastFragment = (header & 1 << 31) >> 31L  # produces 'future warning' on 2.3

        if LOGGING > 2:
            print "Reading fragment of length =", fragmentLength        
            
        if fragmentLength > 0:
            # Received fragment length's worth of bytes
            if alldata == '':
                alldata = sockRecvAll(sock, fragmentLength)                  
            else:   
                alldata = alldata + sockRecvAll(sock, fragmentLength)                          
        
        
    if LOGGING > 1:
        print "Read fragmentGroup of length =", len(alldata)
        
    return alldata
    
#
# Like most languages, python is
# not guaranteed the number of bytes
# read from a socket, so this method
# keeps trying until all data is read.
#
# sock - open socket
# numberBytes - number of bytes to read
# 
#
def sockRecvAll(sock, numberBytes):
    msg = ''
    while len(msg) < numberBytes:
        chunk = sock.recv(numberBytes -len(msg))
        if chunk == '':
            raise RuntimeError, "socket connection broken"
        msg = msg + chunk
    return msg    
        
        
def sockSendAll(sock, msg):

    
    if LOGGING:
        print "Sending message ", msg
    totalsent = 0
    while totalsent < len(msg):
        if LOGGING > 2:
            print "Sending message ", msg[totalsent:]
        sent = sock.send(msg[totalsent:])
        if sent == 0:
            raise RuntimeError, "socket connection broken"
        totalsent = totalsent + sent
    if LOGGING:
        print "Done Sending message."
#
# Start
#
if __name__ == '__main__':
    print "Starting client"
    PORT = 9000   
    print clientStart("localhost", PORT)      

    
#--- Development History  ---------------------------------------------------
#
#  $Log: sampleXdrClient.py,v $
#  Revision 1.3  2006/05/23 16:09:58  smaher_cvs
#  Added XML DataSetRequest and support for data buffer names being sent.
#    