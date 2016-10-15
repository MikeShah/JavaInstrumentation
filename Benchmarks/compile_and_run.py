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
runTest(AGENTARGS,command, command_noagent)

# ========================= TEST SUITE JYTHON ====================
JARPATH = "./Jython/"
JARFILE = "jython-standalone-2.5.4-rc1.jar"
AGENTARGS="02_Jython"
ARGS 	= "./Jython/test01.py"
command	= JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
runTest(AGENTARGS,command, command_noagent)

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
runTest(AGENTARGS,command, command_noagent)

# ========================= TEST SUITE AVORA ====================
JARPATH = "./Avrora/"
JARFILE = "avrora-beta-1.7.117.jar"
AGENTARGS="04_Avrora"
ARGS 	= "-simulate ./Avrora/simple.od"
command	= JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
runTest(AGENTARGS,command, command_noagent)

# ========================= TEST SUITE PAW-SERVER ====================
JARPATH = "./Paw/"
JARFILE = "paw-server.jar"
AGENTARGS="05_Paw"
ARGS 	= ""
TIMEOUT = "timeout 5"
command	= TIMEOUT+" "+JAVA+' -cp '+JARPATH+'sax2.jar:.:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=TIMEOUT+" "+JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
runTest(AGENTARGS,command, command_noagent)

# ========================= TEST SUITE Batik-rasterizer====================
JARPATH = "./Batik/"
JARFILE = "batik-rasterizer.jar"
AGENTARGS="06_Batik"
ARGS 	= "./Batik/samples/batikFX.svg"
command	= TIMEOUT+" "+JAVA+' -cp :.:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=TIMEOUT+" "+JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
runTest(AGENTARGS,command, command_noagent)

# ========================= TEST SUITE H2====================
JARPATH = "./H2/"
JARFILE = "./bin/h2-1.3.174.jar"
AGENTARGS="07_H2"
ARGS 	= "-pg"
TIMEOUT = "timeout 5"
command	= TIMEOUT+" "+JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=TIMEOUT+" "+JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
runTest(AGENTARGS,command, command_noagent)

# ========================= TEST SUITE YCad====================
JARPATH = "./ycad/"
JARFILE = "lib/ycad.jar"
AGENTARGS="08_ycad"
ARGS 	= "com.ysystems.ycad.app.ycadtest.YcadTest"
TIMEOUT = "timeout 5"
command = " "+JAVA+" -cp "+JARPATH+JARFILE+" com.ysystems.ycad.app.ycadtest.YcadTest -javaagent:../Agent.jar="+AGENTARGS
command_noagent=TIMEOUT+" "+JAVA+" -cp "+JARPATH+JARFILE+" com.ysystems.ycad.app.ycadtest.YcadTest"
runTest(AGENTARGS,command, command_noagent)




'''
Without Instrumentation
==============================elapsed Time for02_Jython:	0.964368104935	+1.04506397247	+1.01006007195 	= 1.006497383
==============================elapsed Time for03_Sunflow: 	0.501012802124	+0.476013898849	+0.484155893326	= 0.487060865
==============================elapsed Time for04_Avrora: 	0.190737009048	+0.19505906105	+0.232183933258 = 0.205993334
==============================elapsed Time for05_Paw: 		0.185260057449	+0.159786939621	+0.164944171906 = 0.169997056
==============================elapsed Time for06_Batik: 	1.31056690216	+1.52277207375	+1.53270792961 	= 1.455348969
==============================elapsed Time for07_H2: 		5.32267403603	+5.32264208794	+5.32219004631 	= 5.322502057
==============================elapsed Time for08_ycad: 		5.36289811134	+5.27545404434	+5.28295207024 	= 5.307101409

With Instrumentation
Avrora
4489269192
51831599602

Sunflow
495999615

H2
200153286
418637297


'''