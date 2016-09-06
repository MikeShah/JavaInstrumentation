# A simple compile script to be called to build the program.

import os


# Compile the Test program
os.system('javac *.java')
# Build a jar of the test program
JAR = 'TestInstrumentation.jar'


os.system('jar -cfm '+JAR+' manifest.mf *.class')

#os.system('jar cvfm '+JAR+' ./manifest.mf -C ./*.class .')

