import os

#JAR="TestInstrumentation.jar"
# ========================= TEST SUITE 1 =========================
# Compile the Test program
os.system('python compile.py')

#os.system('jar -cfm '+JAR+' manifest.mf *.class')
# (Optional) Run the sample program without static instrumentation
# os.system('java -jar TestInstrumentation.jar')

# Run with instrumentation
# NOTE, I have copied Agent.jar into the directory
#JAVA='/Library/Java/JavaVirtualMachines/jdk1.8.0_40.jdk/Contents/Home/bin/java'

#For Ubuntu just use java
JAVA='java'

#os.system(JAVA+' -javaagent:../Agent.jar -jar '+JAR)
# ========================= TEST SUITE 1 =========================

# ========================= TEST SUITE 2 =========================
JARPATH = "~/Dropbox/school/GraduateSchool/Courses/Comp250VIS/TreeMap/benchmarks/Sunflow/sunflow/"
JARFILE = "sunflow.jar -nogui ~/Dropbox/school/GraduateSchool/Courses/Comp250VIS/TreeMap/benchmarks/Sunflow/examples/shader_examples/VerySimple.sc"
os.system(JAVA+' -cp .:../.:'+JARPATH+' -javaagent:../Agent.jar -jar '+JARPATH+JARFILE)
# ========================= TEST SUITE 2 =========================

# Clean up whatever data was then output
os.system('python cleanData.py')
