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

	// A stream that buffers and builds a Call Tree of the program.
	public static PrintWriter streamCallTreeWriter;
	// A stream that builds the funtion mapping list
	public static PrintWriter streamFunctionMapWriter; 

	// The number of functions in the program.
	// Also used as a unique ID for all functions in the program.
	static int functionCount;

	/// The indentation level for the call graph.
	static int prettyPrint = 0;

	/// Add indentation before a function entry
	public static void addEntry(){
		for(int i=0;i<prettyPrint; ++i){
			System.out.print(" ");
		}
		prettyPrint++;
	}

	/// Add indentation before a function entry
	public static void addExit(){
		prettyPrint--;
		for(int i=0;i<prettyPrint; ++i){
			System.out.print(" ");
		}	
	}

	/// Immedietely write to our output file
	public static synchronized void addToCallTreeList(String s){
		// Immedietely write to a stream
		callTreeList.add(s);
		//if(s.contains("__Entry")){
		streamCallTreeWriter.write(s+"\n");
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
    public static void setup(String fileName){
    	if(classNames==null){
    		init();
    	}

        // Instantiate our class names list        
        try{
       	if(streamCallTreeWriter==null){
    		streamCallTreeWriter = new PrintWriter(new BufferedWriter(new FileWriter("CallTreeStream.txt")));
    	}

    	if(streamFunctionMapWriter==null){
    		streamFunctionMapWriter = new PrintWriter(new BufferedWriter(new FileWriter("functionMap.txt")));
    	}

            // Create a new buffered reader that takes in a list of classnames.
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            System.out.println("Trying stuff");

            File temp = new File(fileName);
            if(temp.exists()){
            	System.out.println("File exists");
            }else{
            	System.out.println("File does not exist I guess dude?");
            }

            // Store each class in this list
            while((line = reader.readLine()) != null){
            	if(	   line.indexOf("reflect")>0 
            		|| line.indexOf("java.")>=0  
            		|| line.indexOf("javax.")>=0 
            		|| line.indexOf("sun.") >=0
            	   ){
            		// blah
            	}else{
                	classNames.add(line);            		
            	}
            }
        }catch(Exception ex){

        }
        
        ProfilingController.classNames.add("Sleeping");
        ProfilingController.classNames.add("TestInstrumentation");
    }

	// Initialize the constructor
	// init() may also be called from other functions to ensure
	// that the datastructures are created before using them.
	private static void init(){
		// If our function map is null, then allocate memory for it.
		if(functionMap==null){
			//System.out.println("functionMap allocted");
			functionMap = new ConcurrentHashMap<Integer,String>();
			functionCount = 0;
		}
		// If our statistic map is null, then allocat memory for it.
		if(statisticMap==null){
			//System.out.println("statisticMap allocted");
			statisticMap = new ConcurrentHashMap<Integer,Statistic>();
		}
		if(callTreeList==null){
			callTreeList = new CopyOnWriteArrayList<String>();
		}
		if(classNames==null){
			classNames = new CopyOnWriteArrayList<String>();
		}
		if(callOccuranceMap==null){
			callOccuranceMap = new ConcurrentHashMap<String,Integer>();
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
	public static void addFunc(String functionName){
		if (functionMap==null){
			//System.out.println("attempted addFunc");
			init();
		}

		if(functionMap.contains(functionName)){
			// Do something>
		}
		else{
			// Associate a unique key(function Count) to a function name
			functionMap.put(functionCount,functionName);
			statisticMap.put(functionCount,new Statistic());
			functionCount++;
			// Allocate statistics for the function
		}

		//log(functionName);
	}

	// Increases the occurances of a function
	public static synchronized void log(String functionName, long time, long threadID){
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
				temp.addTime(time,threadID);
				statisticMap.put(id,temp);
			}
		}else{
			init();
		}

	}

	// Prints out the call as a single String
	public static synchronized String printCallTree(){
		if(callOccuranceMap==null){
			init();
		}

		String result="";//Runs|AVG time|{(ThreadID)time}\n";
		for(int i =0; i < callTreeList.size();++i){
			// First get the name of the function, then mark its position
			String funcName = callTreeList.get(i).trim();
			// function name with spaces preserved in front of it, so we can still parse our output
			String funcNameWithSpaces = callTreeList.get(i).substring(0,callTreeList.get(i).length()-"__Entry".length());
			// Setup the string that gets exported
		   	if(funcName.contains("__Entry")){
		   		funcName = funcName.substring(0,funcName.length()-"__Entry".length());
   				// Retrieve the ID of the function in our map to
   				// update its values
   				Integer id = 0;
   				for(id=0; id < functionMap.size(); ++id){
		    		String functionName = functionMap.get(id).toString(); 
		    		if(functionName.equals(funcName)){
		    			break;
		    		}
		    	}
		    	// Update our occurance map, so that we output the correct
		    	// values in our call tree.
				if(!callOccuranceMap.containsKey(funcName)){
					callOccuranceMap.put(funcName,0);
				}else{
					// Otherwise increment the value in the hashmap
					callOccuranceMap.put(funcName,callOccuranceMap.get(funcName)+1);
				}
		   		Statistic temp = statisticMap.get(id);
		   		result = funcNameWithSpaces;
				result +=temp.dumpParse(callOccuranceMap.get(funcName)) + "\n";
			}
			
		}
		// TODO: Write this to file
		//System.out.println(result);
		try{
			PrintWriter writer = new PrintWriter("out.txt","UTF-8");
			writer.println(result);
			writer.close();
		}catch(Exception ex){

		}
		return result;
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


}
