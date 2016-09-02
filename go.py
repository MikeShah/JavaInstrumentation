import subprocess

subprocess.call(['python','run.py'])
subprocess.call(['cd','SampleProgram'])
subprocess.call(['python','run.py'])
subprocess.call(['cd','..'])

#os.system('python run.py')
#python SampleProgram/run.py
