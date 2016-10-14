import os
import time
# Configuration options for the build.
# These are knobs the user can change
KNOB_PLATFORM="LINUX" # Could also be MAC
KNOB_TEST="TEST1"
INSTRUMENTATION="ON" # If this is set to OFF, then run without instrumentation, but output time of execution


def runTest(benchmark,command, command_noagent):
	if INSTRUMENTATION=="ON":
		os.system(command)
		# Clean up whatever data was then output
		os.system('python cleanData.py '+AGENTARGS)
	else:
		start_time = time.time();
		os.system(command_noagent)
		elapsed_time = time.time()-start_time
		print "==============================elapsed Time for"+AGENTARGS+": "+str(elapsed_time)+" seconds"


# Compile the Test programs
#os.system('python build.py')

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
AGENTARGS="01_Microbenchmark1"
JARFILE = "TestInstrumentation.jar"
ARGS=""
command=JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
# Run without the java agent
command_noagent=JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#runTest(AGENTARGS,command, command_noagent)



# ========================= TEST SUITE JYTHON ====================
JARPATH = "./Jython/"
JARFILE = "jython-standalone-2.5.4-rc1.jar"
AGENTARGS="02_Jython"
ARGS 	= "./Jython/test01.py"
command	= JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#runTest(AGENTARGS,command, command_noagent)

# ========================= TEST SUITE Sunflow =========================
#JARPATH is the classpath to the root directory containing the jar so packages are properly loaded up.
JARPATH = "./Sunflow/sunflow/"
#JARFILE includes the actual .jar file as well as any arguments needed
JARFILE = "sunflow.jar"
AGENTARGS="03_Sunflow"
#ARGS = "-nogui /h/mshah08/Desktop/JavaDistribution/JavaInstrumentation/Benchmarks/Sunflow/examples/shader_examples/VerySimple.sc"
ARGS = "-nogui /home/mike/Desktop/JavaDistribution/JavaInstrumentation/Benchmarks/Sunflow/examples/shader_examples/VerySimple.sc"

# Run without agent 
#command = JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#os.system(command)
# Run with Agent
command=JAVA+' -cp .:../../.:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
#print command
command_noagent=JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#runTest(AGENTARGS,command, command_noagent)


# ========================= TEST SUITE AVORA ====================
JARPATH = "./Avrora/"
JARFILE = "avrora-beta-1.7.117.jar"
AGENTARGS="04_Avrora"
ARGS 	= "-simulate simple.c"
command	= JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
runTest(AGENTARGS,command, command_noagent)