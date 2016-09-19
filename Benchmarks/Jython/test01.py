def printStuff():
	print "Hello from Jython"

def printOdds(lst):
	for i in lst:
		if i%2 !=0:
			print i,

numbers = [1,2,3,4,5,6]

for i in range(0,len(numbers)):
	print i,

print
printOdds(numbers)
print
printStuff()







