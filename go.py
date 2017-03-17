import subprocess
import os

print '--Make sure you saved and compiled all of the benchmarks you are running!--'
print '--Run the experiment multiple times--'
print '--Consider throwing out the first execution!--'
os.system('python compile_and_run.py ; cd Benchmarks/ ; python compile_and_run.py ; cd ..')
