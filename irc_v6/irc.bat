set IRC_CLASSPATH=-cp build\classes;.
set IRC_CLASSPATH=%IRC_CLASSPATH%;lib\irc.jar

java %IRC_CLASSPATH% gov.nasa.gsfc.irc.app.Irc %1 %2 %3 %4 %5 %6 %7