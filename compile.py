import os
# A simple compile script to be called to build the program.

# Compile the Test program
os.system('javac -cp jboss-javassist-javassist-89c91fa/javassist.jar *.java')
# Build a jar of the test program
JAR = 'TestInstrumentation.jar'
