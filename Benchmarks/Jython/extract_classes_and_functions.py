f = open("jythonclasses.txt")

# read in all of the lines
lst = f.readlines()

functionNames = set()
classNames = set()

# Removes all weird characters
def cleanTokens(tokens):
	newLst = []
	for s in tokens:
		s = s.replace('<','')
		s = s.replace('>','')
		s = s.replace(':','')
		s = s.replace('\n','')
		s = s.replace('(','')
		s = s.replace(')','')
		s = s.replace('[','')
		s = s.replace(']','')
		s = s.replace(' ','')	
#		s = s.replace('.','/')
		newLst.append(s)
	return newLst

for i in range(0,len(lst)):
	if lst[i].startswith('<'):
		tokens = lst[i].split(' ')
		cleanToken = cleanTokens(tokens)
		classNames.add(cleanToken[0])
		functionNames.add(cleanToken[2])		

# Write results to be read from a file
functionNamesFile = open("FunctionNames.txt","w")
classNamesFile = open("ClassNames.txt","w")

for i in functionNames:
	functionNamesFile.write(i+"\n")

for i in classNames:
	classNamesFile.write(i+"\n")

functionNamesFile.close()
classNamesFile.close()



