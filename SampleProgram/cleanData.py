#
# The purpose of this script is to make the parsing much easier in Java when
# we load our input.
#
# This file associates all of the data collected when a function exits
# to the Methods entries
#
import sys

# File we are going to open
f = open("CallTreeStream.txt",'r')
# Load all of the lines into a list
lst = f.readlines()


# We can get away with this method, because the indentation should match up
# and serve as a unique identifier.
pos = 0
methodName = ""
while(pos < len(lst)):
    if "__Entry" in lst[pos]:
        name = lst[pos]
        methodName = name[0:name.index("__Entry")]
        for j in lst[pos:]:
            if "__Exit" in j:
                methodSearching = j[0:j.index("__Exit")]
                if methodSearching == methodName:
                    lst[pos] = methodName + j[j.index("|"):]
                    lst.remove(j)
                    print lst[pos],
                    print len(lst)
        pos = pos+1
    else:
        pos = pos + 1


# Output the file
f2 = open("CleanCallTreeStream.txt",'w')

print "Generating output for list of length:"+str(len(lst))

for i in lst:
    # Only output the data that is complete with timing information
    if "|" in i:
        f2.write(i)
