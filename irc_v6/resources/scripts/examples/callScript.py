print "Calling Script:", scriptName

descriptor = getScriptDescriptor(scriptName)
callScript(descriptor)

# Or could do the following:
# callScript(getScriptDescriptor(scriptName))
