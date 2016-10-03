import os

# Configuration options for the build.
# These are knobs the user can change
KNOB_PLATFORM="LINUX" # Could also be MAC
KNOB_TEST="TEST1"

# Compile the Test programs
os.system('python build.py')

# Remove old text files
# os.system('rm *.txt')


# Simple Test Program
# (Optional) Run the sample program without static instrumentation
# os.system('java -jar TestInstrumentation.jar')


# Run with instrumentation
# NOTE, I have copied Agent.jar into the directory
if KNOB_PLATFORM=="MAC":
	JAVA='/Library/Java/JavaVirtualMachines/jdk1.8.0_40.jdk/Contents/Home/bin/java'
else:
	#For Ubuntu just use java
	JAVAC = 'javac'
	JAR = 'jar'
	JAVA = 'java'

#os.system(JAVA+' -javaagent:../Agent.jar -jar '+JAR)
# ========================= TEST SUITE 1 =========================


#JARPATH is the classpath to the root directory containing the jar so packages are properly loaded up.
JARPATH = "./SampleProgram/"
#JARFILE includes the actual .jar file as well as any arguments needed
JARFILE = "TestInstrumentation.jar"
ARGS=""
#os.system(JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar -jar '+JARPATH+JARFILE+" "+ARGS)


# ========================= TEST SUITE JYTHON ====================
JARPATH = "./Jython/"
JARFILE = "jython-standalone-2.5.4-rc1.jar"
ARGS 	= "./Jython/test01.py"
os.system(JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar -jar '+JARPATH+JARFILE+" "+ARGS)

# ========================= TEST SUITE Sunflow =========================
#JARPATH is the classpath to the root directory containing the jar so packages are properly loaded up.
JARPATH = "./Sunflow/sunflow/"
#JARFILE includes the actual .jar file as well as any arguments needed
JARFILE = "sunflow.jar"
ARGS = "-nogui /h/mshah08/Desktop/JavaDistribution/JavaInstrumentation/Benchmarks/Sunflow/examples/shader_examples/VerySimple.sc"

# Run without agent 
#command = JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#os.system(command)
# Run with Agent
command=JAVA+' -cp .:../../.:../.:'+JARPATH+' -javaagent:../Agent.jar -jar '+JARPATH+JARFILE+" "+ARGS
#print command
#os.system(command)

# Clean up whatever data was then output
os.system('python cleanData.py')
