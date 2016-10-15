/*

		Purpose: This is a singleton class accessible by all of the 
				 classes, and keeps a record of all of the instrumentation
				 done in the program.

*/

// import Instrumentation classes
import java.lang.instrument.Instrumentation;
import java.util.concurrent.*;
import java.io.*;


public final class ProfilingController {

	// Maps a unique key to a function
	// The functionMap maps an integer to a name
	public static ConcurrentHashMap<Integer,String> functionMap;
	// The statisticMap maps a Key of a function to statistics about how
	// many times it has executed.
	public static ConcurrentHashMap<Integer,Statistic> statisticMap;
	// Writes all of the function entries and exits.
	public static CopyOnWriteArrayList<String> callTreeList;
	// The callOccuranceMap is used for output
	// This means when we want to output an entire callTree, since it is chronologically ordered
	// we output the correct instance of the statistic data in the Statistic Map (for example, when
	// seeing how many ms it took the 4th execution of a method)
	public static ConcurrentHashMap<String,Integer> callOccuranceMap;
	// Store all of the class names that we profile in our program
	public static CopyOnWriteArrayList<String> classNames;
	// Store all of the function names that we profile in our program
	public static CopyOnWriteArrayList<String> functionNames;
	// A stream that buffers and builds a Call Tree of the program.
	public static PrintWriter streamCallTreeWriter;
	// A stream that builds the funtion mapping list
	public static PrintWriter streamFunctionMapWriter; 
	// Total time spent executing the program. This is computed by instrumenting "main"
	public static Long absoluteProgramTime;
	// The number of functions in the program.
	// Also used as a unique ID for all functions in the program.
	static int functionCount;
	/// The indentation level for the call graph. This in general is the number of spaces before a function
	static int prettyPrint = 0;
	// Maintains theh calling context (call stack) for threads.
	public static CallingContextStack ccs;
	// Delimeter symbol
	public static String DelimiterSymbol="@";
	// The Java agent arguments string passed in when the agent first runs
	public static String agentargs="NO_ARGUMENTS_DEFAULT";
	// The default output directory for every file
	public static String outputDIR="dump";
	// Knob to determine if we collect only critical section information
	public static Boolean KNOB_INSTRUMENT_ONLY_CRITICAL_SECTIONS = true;

	// Sets the agent arguments in our internal profiling controller.
	// By default the java agent only takes in a string, so if we need to parse it
	// that work can be done here to separate out differnt command line parameters.
	public static void setAgentArgs(String s){
		agentargs = s;
		outputDIR="./"+agentargs;
		new File(outputDIR).mkdirs();
	}


	/// Add indentation before a function entry
	public static void addEntry(){
		boolean outputSpaces = false;
		if(outputSpaces){
			for(int i=0;i<prettyPrint; ++i){
				System.out.print(" ");
			}
		}
		prettyPrint++;
	}

	/// Add indentation before a function entry
	public static void addExit(){
		prettyPrint--;
		boolean outputSpaces = false;
		if(outputSpaces){
			for(int i=0;i<prettyPrint; ++i){
				System.out.print(" ");
			}	
		}
	}

	/// Immedietely write to our output file
	public static synchronized void addToCallTreeList(String s){
		// Immedietely write to a stream
		callTreeList.add(s);
		//if(s.contains("__Entry")){
		streamCallTreeWriter.write(s+"\n");
		streamCallTreeWriter.flush();
		//}
	}

	/// Returns a string with the amount of tabs to pretty print.
	/// This is primarily for pretty printing the output, and useful
	/// when parsing the call trees output.
	/// param: toString - if it is true, then actually pretty print the spaces, otherwise just
	///					 output a number indicated how much indentation exists
	public static String getSpaces(boolean toString){
		String spaces = "";
		if(toString==true){
			for(int i=0;i<prettyPrint; ++i){
				spaces += " ";
			}
		}else{
			spaces = Integer.toString(prettyPrint);
		}
		return spaces;
	}

    ///@brief Store all of the classes that we want to instrument in our program
	///
    /// The setup method loads all of the classes, and their associated methods
    /// that need to be instrumented.
    public static synchronized void setup(String fileName){
    	if(classNames==null){
    		init();
    	}

        // Instantiate our class names list        
        try{
       	    if(streamCallTreeWriter==null){
    			streamCallTreeWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputDIR+"/CallTreeStream.txt")));
    	    }
    	    if(streamFunctionMapWriter==null){
    			streamFunctionMapWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputDIR+"/functionMap.csv")));
            }

            // Check that our file exists and is not a directory
            File f = new File(fileName);
            if(f.exists() && !f.isDirectory()){    
	            // Create a new buffered reader that takes in a list of classnames.
	            BufferedReader reader = new BufferedReader(new FileReader(fileName));
	            System.out.println("Loading Classes");

				String line = reader.readLine();
	            // Store each class in this list
	            while(line != null){
	                classNames.add(line);    
	                // System.out.println("Adding Class to Transform:"+line);  
	                line = reader.readLine();  		
	            }
            }
            else{
            	System.out.println("ProfilingController.setup() -- fileName for classes does not exist!");
            }
        }catch(Exception ex){

        }

        //classNames.add("Sleeping");
        //classNames.add("TestInstrumentation");
    }


    ///@brief Store all of the Functions that we want to instrument in our program
	///
    /// The setup method loads all of the classes, and their associated methods
    /// that need to be instrumente. This function sets all of the functions specifically
    /// that we want to instrument. 
    /// Note that setup must first be called!
    public static synchronized void setFunctions(String fileName){
    	// Check that our file exists and is not a directory
    	try{
			File f = new File(fileName);
            if(f.exists() && !f.isDirectory()){    
	            // Create a new buffered reader that takes in a list of classnames.
	            BufferedReader reader = new BufferedReader(new FileReader(fileName));
	            System.out.println("Loading List of Functions to Instrument (Classes must be loaded first)");

				String line = reader.readLine();
	            // Store each class in this list
	            while(line != null){
	                functionNames.add(line);    
	                //System.out.println("Setting Function to Instrument:"+line);  
	                line = reader.readLine();  		
	            }
            }
            else{
            	System.out.println("ProfilingController.setFunctions() -- fileName for functions does not exist!");
            }    		
    	}catch(Exception ex){

    	}
            
    }

    /// @brief 
    ///
    /// Checks to see if a function is in the list of functions we want to instrument.
    public static synchronized boolean isInFunctionNames(String s){
    	for(int i =0; i < functionNames.size(); ++i){
    		if (functionNames.get(i).equals(s)){
    			System.out.flush();
    			return true;
    		}
    	}
    	return false;
    }


	// Initialize the constructor
	// init() may also be called from other functions to ensure
	// that the datastructures are created before using them.
	private static synchronized void init(){
		// If our function map is null, then allocate memory for it.
		if(functionMap == null){
			//System.out.println("functionMap allocted");
			functionMap 	 = new ConcurrentHashMap<Integer,String>();
			functionCount 	 = 0;
		}
		// If our statistic map is null, then allocat memory for it.
		if(statisticMap == null){
			//System.out.println("statisticMap allocted");
			statisticMap 	 = new ConcurrentHashMap<Integer,Statistic>();
		}
		if(callTreeList == null){
			callTreeList 	 = new CopyOnWriteArrayList<String>();
		}
		if(classNames   == null){
			classNames 	 = new CopyOnWriteArrayList<String>();
		}
		if(functionNames== null){
			functionNames = new CopyOnWriteArrayList<String>();
		}
		if(callOccuranceMap==null){
			callOccuranceMap = new ConcurrentHashMap<String,Integer>();
		}
		if(ccs==null){
			ccs = new CallingContextStack();
		}
	}

	// Default constructor
	// This constructor needs to call into init() to make sure the
	// data structures are allocated.
	public ProfilingController(){
		init();
		//System.out.println("ProfilingController Constructed");
	}

	// Add a new method, and assign it a unique key
	// Returns true if a success, and the function was added
	public static synchronized boolean addFunc(String functionName){
		if (functionMap==null){
			//System.out.println("attempted addFunc");
			init();
		}
		boolean result = false;

		if(functionMap.contains(functionName)){
			// Do something
			// TODO: Add some sort of error message I suppose
			System.out.println("Error -- function "+functionName+" already added");
			result = false;
		}
		else{
			// Associate a unique key(function Count) to a function name
			// Note that the functionmap and statisticMap share ID's
			functionMap.put(functionCount,functionName);
			statisticMap.put(functionCount,new Statistic());
			functionCount++;
			result = true;
			// Allocate statistics for the function
		}

		return result;
		//log(functionName);
	}

	// Increases the occurances of a function
	public static synchronized void log(String functionName, long time, long threadID, String caller){
		if (functionMap==null){
			System.out.println("attempted log1");
			init();
		}

		// Based on the functionName, get the key, and update the values.
		int id = 0;
		for (Integer keyType: functionMap.keySet()){
            		String value = functionMap.get(keyType).toString();  
            		if(value.equals(functionName)){
            			//System.out.println("Found: "+value+"=="+functionName+" at id:"+id);
            			break;
            		}		  
	            id++;
		} 

		if(statisticMap!=null){
			// 	Modify the occurance count
			Statistic temp = statisticMap.get(id);
			if(temp!=null){
				temp.addTime(time,threadID,caller);
				statisticMap.put(id,temp);
			}
		}else{
			init();
		}

	}

	private static synchronized String removeLeadingNumeric(String s){
		int i = 0;
		while(s.charAt(i)>=48 && s.charAt(i) <= 57){
			++i;			
		}
		
		return s.substring(i);
	}

	// Prints out the call as a single String
	public static synchronized void printCallTree(){
		if(callOccuranceMap==null || statisticMap==null){
			init();
		}

		FileWriter writer;	

		try{
			writer = new FileWriter(outputDIR+"/printCallTree.txt");

			String result="";//Runs|AVG time|{(ThreadID)time}\n";
			for(int i =0; i < callTreeList.size();++i){
				// First get the name of the function, then mark its position
				String funcName = callTreeList.get(i).trim();
				// function name with spaces preserved in front of it, so we can still parse our output
				String funcNameWithSpaces = callTreeList.get(i).substring(0,callTreeList.get(i).length()-"__Entry".length());
				// Setup the string that gets exported
			   	if(funcName.contains("__Entry")){
			   		// Remove __Entry from name
					funcName = funcName.substring(0,funcName.length()-"__Entry".length());
					funcName = removeLeadingNumeric(funcName);
	   				// Retrieve the ID of the function in our map to
	   				// update its values
	   				Integer id = 0;
	   				for(id=0; id < functionMap.size(); ++id){
		    			String functionName = functionMap.get(id).toString(); 
		    			if(functionName.equals(funcName)){
		    				break;
		    			}
		    		}
					// If the id is not found, then output an error message
					if(id >= functionMap.size()){
						System.out.println("Function: \""+funcName+"\" has not been found!");
						System.out.println("Available functions are:");
						for(int j= 0; j < functionMap.size(); ++j){
							System.out.println("----"+functionMap.get(j).toString());
						}
					}
			    		// Update our occurance map, so that we output the correct
			    		// values in our call tree.
			    		// This occurance map is basically a one time use item
			    		// for when we are outputting the call tree. It will ensure that
			    		// for each function we are outputting a unique occurence of it
			    		// in our resulting string, and that we get the appropriate instance
			    		// from the statisticMap, using the dumpParse function.
					if(!callOccuranceMap.containsKey(funcName)){
						callOccuranceMap.put(funcName,0);
					}else{
						// Otherwise increment the value in the hashmap
						callOccuranceMap.put(funcName,callOccuranceMap.get(funcName)+1);
					}
					// First we pick out the function from the statistic map using the 'id'
				
			   		Statistic temp = statisticMap.get(id);
			   		//System.out.println("ID:"+id);	
			   		writer.write(funcNameWithSpaces);
					writer.write(temp.dumpParse(callOccuranceMap.get(funcName)) + "\n");
					//}
				}
				
			}
			writer.close();
		}catch(Exception ex){

		}
		finally{

		}
	
	}

	// Prints out all of the keys and function names.
	public static synchronized void dump(){
		if (functionMap==null){
			System.out.println("dump is null, add a function first");
			return;
		}

		streamFunctionMapWriter.write("============================================================================\n");
		streamFunctionMapWriter.write("==== functionMap("+functionMap.size()+") Dump of <key,function name> ==== and statistics\n");
		streamFunctionMapWriter.write("============================================================================\n");

		for(Integer i = 0; i < functionMap.size(); ++i){
		    String functionName = functionMap.get(i).toString();  
		    Statistic temp = statisticMap.get(i);
		    String output = temp.dump();
		    //System.out.println(i.toString() + " = " + value + statisticMap.get(i).dump());  
		    streamFunctionMapWriter.write(i.toString()+" "+functionName + " Statistics-"+output+'\n');  
		}
		streamFunctionMapWriter.write("============================================================================\n");
		streamFunctionMapWriter.flush();
	}

	public static synchronized void setAbsoluteTime(long start){
		absoluteProgramTime = System.nanoTime() - start;
	}

	/// Dumps the functionMap as a CSV File
	public static synchronized void dumpFunctionMapCSV(){
		if (functionMap==null){
			System.out.println("dump is null, add a function first");
			return;
		}


		streamFunctionMapWriter.write("functionMap("+functionMap.size()+") Dump of <key function name>"+
										DelimiterSymbol+"name"+
										DelimiterSymbol+"Total Runs"+
										DelimiterSymbol+"Runs Avg(ns)"+
										DelimiterSymbol+"Max(ns)"+
										DelimiterSymbol+"Min(ns)"+
										DelimiterSymbol+"Std Dev"+
										DelimiterSymbol+"% of Diverging Executions"+
										DelimiterSymbol+"Diverging Execution"+
										DelimiterSymbol+"ThreadID"+
										DelimiterSymbol+"Caller"+
										DelimiterSymbol+"Time(ns)\n");

		// Total time spent in critical sections
		long totalTimeInCriticalSections = 0L;
		// Iteratoe through i# of functions in the function map.
		for(Integer i = 0; i < functionMap.size(); ++i){
		    String functionName = functionMap.get(i).toString();  
		    Statistic temp = statisticMap.get(i);
		    // Add up the time spent in each execution
		    for(int j =0; j < temp.timeList.size(); ++j){
		    	totalTimeInCriticalSections+= temp.timeList.get(j);
		    }
		    String output = temp.dumpCSV();
		    //System.out.println(i.toString() + " = " + value + statisticMap.get(i).dump());  
		    streamFunctionMapWriter.write(i.toString()+DelimiterSymbol+functionName + output+'\n');  
		}
		// Output the final Absolute Time
		streamFunctionMapWriter.write('\n');
		streamFunctionMapWriter.write(DelimiterSymbol+"total functions profiled"+'\n');
		streamFunctionMapWriter.write("\n\nAbsoluteProgramTime"+DelimiterSymbol+ProfilingController.absoluteProgramTime.toString()+'\n');  
		// Time spent in only the Critical Sections
		streamFunctionMapWriter.write("Critical Section Time"+DelimiterSymbol+totalTimeInCriticalSections+'\n');  

		streamFunctionMapWriter.flush();
	}

	public static String getStackTrace(){
        System.out.print("\tCaller:"+Thread.currentThread().getStackTrace()[3].getMethodName());
        //e.printStackTrace(pw);
        return ""; // stack trace as a string
    }

    public static synchronized String getCaller(long threadID){
    	if(ccs==null){
    		init();
    	}

    	String result = ccs.peek(threadID);
    	return result;
    }
    
    public static synchronized void ccspush(long threadID, String funcName){
    	
    	if(ccs==null){
    		init();
    	}

    	ccs.push(threadID,funcName);
    	
    }    

    public static synchronized void ccspop(long threadID){
    	if(ccs==null){
    		init();
    	}

    	ccs.pop(threadID);
    }

}
