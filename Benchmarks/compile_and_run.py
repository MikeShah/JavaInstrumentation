import os
import time
import threading
import csv
from matplotlib import pyplot
import glob
import numpy

print("Executing compile_and_run")
print("Getting ready to run benchmarks")

# Configuration options for the build.
# These are knobs the user can change
KNOB_PLATFORM="LINUX" # Could also be MAC
KNOB_TEST="TEST1"
INSTRUMENTATION="ON" # If this is set to OFF, then run without instrumentation, but output time of execution
THREADING="OFF" #If this is set to ON, then all tests are run. Warning, this should be disabled for final test results

threads = []
if INSTRUMENTATION=="ON":
	print "INSTRUMENTION=ON"
if THREADING=="ON":
	print "WARNING, THREADING IS ON, if you're collecting real data, turn this OFF!"

time.sleep(3)

# Calls a test.
# If KNOBS for INSTRUMENTATION are on or off, a report is given back
def runTest(benchmark,command, command_noagent):
	if INSTRUMENTATION=="ON":
		print command
		os.system(command)
		# Clean up whatever data was then output
		os.system('python cleanData.py '+AGENTARGS)
		# Build histograms for all of our programs that we run
	else:
		print command_noagent
		start_time = time.time();
		os.system(command_noagent)
		elapsed_time = time.time()-start_time
		print "==============================elapsed Time for"+AGENTARGS+": "+str(elapsed_time)+" seconds"

# Calls runTest in a thread
def runThreadingTest(benchmark,command, command_noagent):
	if THREADING=="ON":
		thread1 = threading.Thread(target=runTest,args=(benchmark,command, command_noagent))
		thread1.daemon = True
		thread1.start()
	else:
		runTest(benchmark,command, command_noagent)
	buildHistograms(benchmark)


def IQR(dist):
	return numpy.percentile(dist,75) - numpy.percentile(dist,25)

# TODO possibly save to a .pdf or svg to get better resolution for papers
def buildHistograms(AGENTARGS):
	path = AGENTARGS+"/IndividualFunctionData/"
	files = glob.glob(path+"*.csv")
	for f in files:
		with open(f,'r') as csvfile:
			reader = csv.reader(csvfile,delimiter=",")
			dataList = list(reader)

			# x-dimension is the number of runs
			x = range(1,len(dataList[0])+1)
			if x > 0:
				fig,yscatterPlot = pyplot.subplots(figsize=(10,10))
				# Setup a grid
				yscatterPlot.grid(True)
				#yscatterPlot.axis([0.0,0.0,len(x),int(max(dataList[0]))+1]) # Set the axis bounds
				# Build our data plot
				yscatterPlot.scatter(x,dataList)
				# Show it to the user
				yscatterPlot.plot(alpha=0.6)

				pyplot.title(str(f))
				pyplot.savefig(f+"_scatter_"+".png")

				fig,histogramPlot = pyplot.subplots()
				dataListAsFloat=numpy.array(dataList[0]).astype(numpy.float)
				binwidth = (2*IQR(dataListAsFloat))/ ((len(dataListAsFloat))**(1./3.))
				print "bins:"+str(binwidth)
				print "len:"+str(len(dataListAsFloat))
				if binwidth <= 0.0:
					histogramPlot.hist(dataListAsFloat,histtype='bar',bins=5)
				else:
					histogramPlot.hist(dataListAsFloat,histtype='bar',bins=numpy.arange(min(dataListAsFloat),max(dataListAsFloat)+binwidth,binwidth))
				#histogramPlot.hist(dataListAsFloat,histtype='bar',bins=20)
				pyplot.savefig(f+"_hist_"+".png")



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
	JAVA='/Library/Java/JavaVirtualMachines/jdk1.8.0_40.jdk/Contents/Home/bin/java -XX:CompileThreshold=1 '
else:
	#For Ubuntu just use java
	JAVAC = 'javac'
	JAR = 'jar'
	JAVA = 'java -XX:CompileThreshold=1 '
	# Uncomment this to use java7 JAVA = '/usr/lib/jvm/java-7-oracle/bin/java -XX:CompileThreshold=1 '

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
# Special recompile for the benchmark
#####runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE JYTHON ====================
JARPATH = "./Jython/"
JARFILE = "jython-standalone-2.5.4-rc1.jar"
AGENTARGS="02_Jython"
ARGS 	= "./Jython/test01.py"
TONS_OF_JRES = "/usr/lib/jvm/java-8-oracle/jre/lib:/usr/lib/jvm/java-8-oracle/lib:"
command	= JAVA+' -cp '+TONS_OF_JRES+'.:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#####runThreadingTest(AGENTARGS,command, command_noagent)
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
command_noagent=JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#####runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE AVORA ====================
JARPATH = "./Avrora/"
JARFILE = "avrora-beta-1.7.117.jar"
AGENTARGS="04_Avrora"
ARGS 	= "-simulate ./Avrora/simple.od"
command	= JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#####runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE PAW-SERVER ====================
JARPATH = "./Paw/"
JARFILE = "paw-server.jar"
AGENTARGS="05_Paw"
ARGS 	= ""
TIMEOUT = "timeout 5"
command	= TIMEOUT+" "+JAVA+' -cp '+JARPATH+'sax2.jar:.:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=TIMEOUT+" "+JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#####runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE Batik-rasterizer====================
JARPATH = "./Batik/"
JARFILE = "batik-rasterizer.jar"
AGENTARGS="06_Batik"
ARGS 	= "./Batik/samples/batikFX.svg"
command	= TIMEOUT+" "+JAVA+' -cp :.:../.:'+JARPATH+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=TIMEOUT+" "+JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#####runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE H2====================
JARPATH = "./H2/"
JARFILE = "./bin/h2-1.3.174.jar"
AGENTARGS="07_H2"
ARGS 	= "-tcp"
TIMEOUT = "timeout 5"
command	= TIMEOUT+" "+JAVA+' -cp .:../.:'+JARPATH+':./H2/bin/:./H2/bin/org/:./H2/bin/:./H2/bin/org/h2/ -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=TIMEOUT+" "+JAVA+' -cp .:../.:'+JARPATH+' -jar '+JARPATH+JARFILE+" "+ARGS
#####runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE YCad====================
JARPATH = "./ycad/"
JARFILE = "lib/ycad.jar"
AGENTARGS="08_ycad"
ARGS 	= "com.ysystems.ycad.app.ycadtest.YcadTest"
TIMEOUT = "timeout 5"
command = " "+JAVA+"-javaagent:../Agent.jar="+AGENTARGS+" -cp "+JARPATH+JARFILE+" com.ysystems.ycad.app.ycadtest.YcadTest"
command_noagent=TIMEOUT+" "+JAVA+" -cp "+JARPATH+JARFILE+" com.ysystems.ycad.app.ycadtest.YcadTest"
#####runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE FOP====================
# java -cp .:../.:./fop/:./../../../javassist-3.21.0:./../../../javassist-3.21.0/javassist.jar:./../../../../Agent.jar -javaagent:Agent.jar=09_fop -jar fop.jar
JARPATH = "./fop/"
JARFILE = "build/fop.jar"
AGENTARGS="09_fop"
ARGS 	= "-fo ./fop/examples/fo/advanced/barcode.fo -pdf foo.pdf"
TIMEOUT = ""
command	= JAVA+' -cp '+JARPATH+":../Agent.jar:../javassist-3.21.0/javassist.jar:"+JARPATH+JARFILE+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=JAVA+' -cp .:../.:'+JARPATH+":./build/"+' -jar '+JARPATH+JARFILE+" "+ARGS
#####runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE PMD====================
# java -cp ./lib/pmd-4.2.4.jar:./lib/jaxen-1.1.1.jar:./lib/asm-3.1.jar net.sourceforge.pmd.PMD ./src/net/sourceforge/pmd/ html unusedcode -javaagent:./../../../Agent.jar="010_pmd"
JARPATH = "./pmd-4.2.4/"
JARFILE = "lib/pmd-4.2.4.jar"
AGENTARGS="010_pmd"
ARGS 	= "./pmd/pmd-4.2.4/src/net/sourceforge/pmd/ html unusedcode"
TIMEOUT = ""
command	= JAVA+' -verbose:class -cp '+JARPATH+'lib/pmd-4.2.4.jar:'+JARPATH+'lib/jaxen-1.1.1.jar:'+JARPATH+'lib/asm-3.1.jar:./pmd-4.2.4:./pmd-4.2.4/src net.sourceforge.pmd.PMD -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=JAVA+' -cp .:../.:'+JARPATH+":./build/"+' -jar '+JARPATH+JARFILE+" "+ARGS
#####runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE MICROBENCHMARK 2====================
JARPATH = "./Microbenchmark2/"
JARFILE = "Microbenchmark2.jar"
AGENTARGS="011_Microbenchmark2"
ARGS 	= ""
TIMEOUT = ""
command =  JAVA+' -javaagent:../Agent.jar='+AGENTARGS+" -cp .:Microbenchmark2:Microbenchmark2/tests/:Microbenchmark2.jar latency"
#command	= JAVA+' -cp '+JARPATH+':.'+' -javaagent:../Agent.jar='+AGENTARGS+' -jar '+JARPATH+JARFILE+" "+ARGS
command_noagent=JAVA+" -cp .:Microbenchmark2:Microbenchmark2/tests/:Microbenchmark2.jar latency"
runThreadingTest(AGENTARGS,command, command_noagent)
# ========================= TEST SUITE sj3d====================
# https://github.com/Calvin-L/sj3d
JARPATH = "./sj3d/"
JARFILE = "sj3d.jar"
AGENTARGS="012_sj3d"
ARGS 	= "demo.SJ3DDemo"
TIMEOUT = "timeout 5 "
#java  -cp sj3d.jar:./sj3d:. demo.SJ3DDemo
command	=TIMEOUT+ JAVA+'-javaagent:../Agent.jar='+AGENTARGS+' -cp '+JARPATH+JARFILE+':'+JARPATH+' '+ARGS
command_noagent=TIMEOUT+ JAVA+' -cp '+JARPATH+JARFILE+':'+JARPATH+' '+ARGS
#####runThreadingTest(AGENTARGS,command, command_noagent)

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
