f = open("SunflowClasses.txt")

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
		fullfunctionName = (tokens[0]+tokens[2]).replace(':','.').replace('<','').replace('>','')
		functionNames.add(fullfunctionName)	
		cleanToken = cleanTokens(tokens)
		classNames.add(cleanToken[0])
			

# Write results to be read from a file
functionNamesFile = open("FunctionNames.txt","w")
classNamesFile = open("ClassNames.txt","w")

for i in functionNames:
	functionNamesFile.write(i)

for i in classNames:
	if '$' not in i:		# TODO: Investigate if inner classes are breaking analysis
		classNamesFile.write(i+"\n")

functionNamesFile.close()
classNamesFile.close()



