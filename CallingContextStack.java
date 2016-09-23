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

	private void init(){
		threadCallStacks = new ConcurrentHashMap<Long,BlockingDeque<String>>();
	}

	// Check that our threadCallStack is initialized
	private void checkInit(){
		if(threadCallStacks==null){
			init();
		}
	}

	// Constructor
	CallingContextStack(){
		init();
	}

	// threadID is the thread ID of the stack we are modifying
	public void push(long threadID, String m){
		
		checkInit();
		
		if(threadCallStacks.containsKey(threadID)){
			BlockingDeque<String> temp = threadCallStacks.get(threadID);
			temp.push(m);
			threadCallStacks.put(threadID,temp);
		}else{
			BlockingDeque<String> temp = new LinkedBlockingDeque<String>();
			temp.push(m);
			threadCallStacks.put(threadID,temp);
		}
		
	}

	public void pop(long threadID){
		checkInit();
		
		if(threadCallStacks.containsKey(threadID)){
			BlockingDeque<String> temp = threadCallStacks.get(threadID);
			if(temp.size()>0){
				temp.pop();
				threadCallStacks.put(threadID,temp);
			}
		}
	}

	public String peek(long threadID){
		checkInit();
		
		String result = "(peek)main";
		if(threadCallStacks.containsKey(threadID)){
			BlockingDeque<String> temp = threadCallStacks.get(threadID);
			if(temp.size()>0){
				result = temp.peek();
			}
		}
		return result;
	}

	public String peekCaller(long threadID){
		checkInit();
		
		String result = "(peekCaller)main";
		if(threadCallStacks.containsKey(threadID)){
			BlockingDeque<String> temp = threadCallStacks.get(threadID);
			if(temp.size()>1){
				String placeholder = temp.pop();
				result = temp.peek();
				temp.push(placeholder); // I think no need to push back on stack here
				threadCallStacks.put(threadID,temp);
			}
		}
		return result;
		
	}

}