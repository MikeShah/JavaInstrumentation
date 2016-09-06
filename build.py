import os
# A simple compile script to be called to build the program.

# (1) Setup the Agent
# Compile the Agent
javassistJARPath = '"./jboss-javassist-javassist-89c91fa/javassist.jar"'
BUILD_AGENT_STRING='javac -cp '+javassistJARPath+':. *.java'
print BUILD_AGENT_STRING
os.system(BUILD_AGENT_STRING)


