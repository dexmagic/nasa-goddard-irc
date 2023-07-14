#=== File Prolog ============================================================
# This code was developed by NASA Goddard Space
# Flight Center, Code 580 for the Instrument Remote Control (IRC)
# project.
#--- Notes ------------------------------------------------------------------
#
# This script establishes the IRC JPython scripting environment: It
# imports various IRC Java packages, and also defines the various
# scripting primitives.
#
#--- Development History  ---------------------------------------------------
#
# $Log:$
#
#
#--- Warning ----------------------------------------------------------------
# This software is property of the National Aeronautics and Space
# Administration. Unauthorized use or duplication of this software is
# strictly prohibited. Authorized users are subject to the following
# restrictions:
#   *	Neither the author, their corporation, nor NASA is responsible for
#	any consequence of the use of this software.
#   *	The origin of this software must not be misrepresented either by
#	explicit claim or by omission.
#  *	Altered versions of this software must be plainly marked as such.
#  *	This notice may not be removed or altered.
#
#=== End File Prolog ========================================================


##########################################################################
# Import various Python packages
##########################################################################

from time import sleep
from types import *


##########################################################################
# Import various Java packages, both for internal use and
# to expose to script writers.
##########################################################################

import sys; 
sys.add_package("gov.nasa.gsfc.irc.app")
sys.add_package("gov.nasa.gsfc.irc.components")
sys.add_package("gov.nasa.gsfc.irc.description")
sys.add_package("gov.nasa.gsfc.irc.devices")
sys.add_package("gov.nasa.gsfc.irc.messages")
sys.add_package("gov.nasa.gsfc.irc.scripts")

print "Running IRC Jython setup script"

# Import useful static methods from Irc class
from gov.nasa.gsfc.irc.app.Irc import getGlobal
from gov.nasa.gsfc.irc.app.Irc import putGlobal
from gov.nasa.gsfc.irc.app.Irc import store
from gov.nasa.gsfc.irc.app.Irc import retrieveFromStore
from gov.nasa.gsfc.irc.app.Irc import deleteFromStore
from gov.nasa.gsfc.irc.app.Irc import checkpointStore
from gov.nasa.gsfc.irc.app.Irc import getPersistentStore
from gov.nasa.gsfc.irc.app.Irc import getGuiFactory

# Import all static methods from the Messages and Scripts classes
from gov.nasa.gsfc.irc.messages.Messages import *
from gov.nasa.gsfc.irc.scripts.Scripts   import *

# Import classes that are likely to be used by scripts

from java.lang import Boolean, Double, Float, Integer, Long, Short

from gov.nasa.gsfc.irc.devices     import DeviceProxy
from gov.nasa.gsfc.irc.app         import Irc
from gov.nasa.gsfc.irc.components  import ComponentManager


##########################################################################
# Internal constants/globals
##########################################################################

IRC__timeoutArg = "_timeout"
IRC__doneSemaphore = None


##########################################################################
# Primitive operations
##########################################################################

def pause(duration):
    sleep(duration / 1000.0)


##########################################################################
# Semaphore to indicate completion
#
# If somebody calls keepInterpreterUntilSignal(), some thread (e.g.
# in a PE) must call signalDone() at some point.  This is used to
# allow a script which spawns a background thread to hold onto its
# interpreter until it is really done.
#
# Note that the scripting infrastructure takes care of calling
# waitUntilDone() before releasing the interpreter; there is probably
# no need for a script ever to call this.
#
##########################################################################

def waitUntilDone():
    global IRC__doneSemaphore
    if (IRC__doneSemaphore != None):
        IRC__doneSemaphore.remove()
        IRC__doneSemaphore = None

def keepInterpreterUntilSignal():
    global IRC__doneSemaphore
    IRC__doneSemaphore = BlockingQueue(1)

def signalDone():
    global IRC__doneSemaphore
    if (IRC__doneSemaphore != None):
        IRC__doneSemaphore.add(None)
