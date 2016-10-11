#
# The purpose of this script is to make the parsing much easier in Java when
# we load our input.
#
# This file associates all of the data collected when a function exits
# to the Methods entries
#
import sys


ROOTDIR=sys.argv[1]

# File we are going to open
f = open("./"+ROOTDIR+"/CallTreeStream.txt",'r')
# Load all of the lines into a list
lst = f.readlines()

print "================Cleaning data=================="


newLst = []

# We can get away with this method, because the indentation should match up
# and serve as a unique identifier.
pos = 0
methodName = ""
totalEntries = len(lst)
while(pos < totalEntries):
    if "__Entry" in lst[pos]:
        name = lst[pos]
        methodName = name[0:name.index("__Entry")]
        for j in lst[pos:]:
            if "__Exit" in j:
                methodSearching = j[0:j.index("__Exit")]
                if methodSearching == methodName:
		    newLst.append(methodName+j[j.index("|"):])             
		    lst[pos] = methodName + j[j.index("|"):]
                    #lst.remove(j)
                    if len(newLst) % 10000 == 0:
                        print str(len(newLst))+" of "+str(totalEntries)
                    # Uncomment when we want to print the output
		            # print lst[pos],
                    # print len(lst)
		    break			
        pos = pos+1
    else:
        pos = pos + 1



# Output the file
f2 = open("./"+ROOTDIR+"/CleanCallTreeStream.txt",'w')

print "Generating output for list of length:"+str(len(lst))
print "Writing output to CleanCallTreeStream.txt"

for i in newLst:
    # Only output the data that is complete with timing information
    if "|" in i:
        f2.write(i)
