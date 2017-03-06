import os

os.system('javac -cp .:tests *.java ./tests/*.java')
os.system('jar -cvfm Microbenchmark2.jar MANIFEST.MF *.class')
#os.system('java -cp .:tests -jar Microbenchmark2.jar')


# Super lazy way to just find all of the java files from a root directory
#os.system("javac $(grep -ir -m1 --include='*.java' . | cut -f1 -d':' | tr '\n' ' ')")
os.system("java -cp .:tests:Microbenchmark2.jar latency")
