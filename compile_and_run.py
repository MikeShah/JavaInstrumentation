import os

# (0) Setup the Compiler
JAVAC = 'javac'
JAR = 'jar'
JAVA = 'java'

# (0) Attempt to clean files out
# Delete the old files (do a clean)
# os.system("rm *.class")
#os.system("rm Agent.jar")

# (1) Setup the Agent
# Compile the Agent
javassistJARPath = '"./jboss-javassist-javassist-89c91fa/javassist.jar"'
BUILD_AGENT_STRING=JAVAC+' -cp '+javassistJARPath+':. *.java'
print BUILD_AGENT_STRING
os.system(BUILD_AGENT_STRING)

# Run the program
#os.system('java HelloWorld')
# Build a .jar
os.system(JAR+' -cfm Agent.jar manifest.mf *.class')
# Run the .jar
#os.system('java -cp HelloWorld.jar HelloWorld')

# (2) Setup the Test program


# (3) Run agent on Test program
#os.system('java -classpath ./SampleProgram SampleProgram/TestInstrumentation.jar SampleProgram/Sleeping SampleProgram/TestInstrumentation')

# Run the Test Program with the agent

# Analyze the jar
