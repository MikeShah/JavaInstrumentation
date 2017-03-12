import os

os.system('javac -cp .:tests *.java ./tests/*.java')
os.system('jar -cvfm Microbenchmark2.jar MANIFEST.MF *.class')


