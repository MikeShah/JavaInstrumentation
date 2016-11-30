import os

# (0) Setup the Compiler
JAVAC = 'javac'
JAR = 'jar'
JAVA = 'java'

JAVAC7='/usr/lib/jvm/java-7-oracle/bin/javac'
JAR7='/usr/lib/jvm/java-7-oracle/bin/jar'
JAVA7='/usr/lib/jvm/java-7-oracle/bin/java'

# (0) Attempt to clean files out
# Delete the old files (do a clean)
# os.system("rm *.class")
#os.system("rm Agent.jar")

# (1) Setup the Agent
# Compile the Agent
javassistJARPath = '"./javassist-3.21.0/javassist.jar"'
BUILD_AGENT_STRING=JAVAC+' -cp '+javassistJARPath+':. *.java'
print BUILD_AGENT_STRING
os.system(BUILD_AGENT_STRING)

# Run the program
#os.system('java HelloWorld')
# Build a .jar
os.system(JAR+' -cfm Agent.jar manifest.mf *.class javassist-3.21.0')
# Run the .jar
#os.system('java -cp HelloWorld.jar HelloWorld')

# (2) Setup the Test program


# (3) Run agent on Test program
#os.system('java -classpath ./SampleProgram SampleProgram/TestInstrumentation.jar SampleProgram/Sleeping SampleProgram/TestInstrumentation')

# Run the Test Program with the agent

# Analyze the jar
