Introduction

This 6.0b3 release is an interim release provided as the first version that 
has received approval for Open Source from NASA. Version 6 is a complete 
rewrite of the core framework and incorporates lessons learned from many years 
of instrument and device control projects. Unfortunately the User's Guide and 
Developer's Guide documentation are in the process of being updated and are 
not available at this time.


Building the Source

There are build scripts provided for Windows and UNIX; however they may need 
some modification for a specific platform and environment. To rebuild the source 
type "build compile" at a command prompt.


Running a Simple Example

There are two ways to run a simple demo of a signal generator. Using Ant and
the build shell script type "build runExample" at a command prompt or using the 
"irc" shell script type "irc -Presources/configurations/signalGeneratorExample".

Both of these methods should bring up the IRC default application window and a
chart visualization window plotting the output of a signal generator algorithm.

See the link "http://cvs588.gsfc.nasa.gov:443/WebStart/irc/irc.html" 
for some examples using WebStart.

Additional examples will be available when they are approved for release.


History

6.0b3 - Initial open source release.
