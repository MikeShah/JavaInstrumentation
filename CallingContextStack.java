/*

(1)

		Purpose: This class maintains a call stack for every thread.

*/

// import Instrumentation classes
import java.lang.instrument.Instrumentation;
import java.lang.management.*;
import java.util.concurrent.*;



public class CallingContextStack {


	/// Hashmap that takes a threadID as the key, and updates the calling context stack
	/// This way we can keep track of which functions were called by the others.
	//                              threadID          call stack
	public static ConcurrentHashMap<Long,BlockingDeque<String>> threadCallStacks;

	// Constructor
	CallingContextStack(){
		threadCallStacks = new ConcurrentHashMap<Long,BlockingDeque<String>>();
	}

	// threadID is the thread ID of the stack we are modifying
	public void push(Long threadID, String m){
		
		if(!threadCallStacks.containsKey(threadID)){
			BlockingDeque<String> temp = threadCallStacks.get(threadID);
			temp.push(m);
			threadCallStacks.put(threadID,temp);
		}else{
			BlockingDeque<String> temp = new LinkedBlockingDeque<String>();
			temp.push(m);
			threadCallStacks.put(threadID,temp);
		}
	}

	public void pop(Long threadID){
		if(threadCallStacks.containsKey(threadID)){
			BlockingDeque<String> temp = threadCallStacks.get(threadID);
			if(temp.size()>0){
				temp.pop();
				threadCallStacks.put(threadID,temp);
			}
		}
	}

	public String peek(Long threadID){
		String result = "-error-stack empty";
		if(threadCallStacks.containsKey(threadID)){
			BlockingDeque<String> temp = threadCallStacks.get(threadID);
			if(temp.size()>0){
				result = temp.peek();
			}
		}
		return result;
	}

	public String peekCaller(Long threadID){
		return "blah";
		/*
		String result = "-error-stack empty";
		if(threadCallStacks.containsKey(threadID)){
			BlockingDeque<String> temp = threadCallStacks.get(threadID);
			if(temp.size()>1){
				String placeholder = temp.pop();
				result = temp.peek();
				//temp.push(); // I think no need to push back on stack here
			}
		}
		return result;
		*/
	}

}