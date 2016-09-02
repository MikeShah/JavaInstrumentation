/*

(1)

		Purpose: This class gathers information about threads and locks

*/

// import Instrumentation classes
import java.lang.instrument.Instrumentation;
import java.lang.management.*;

public class ThreadData {

	long threadId;	// Unique thread iD Number remains unchanged througout lifetime.
	String threadName;
	Thread.State threadState;	// NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED

	ThreadInfo ti;

	ThreadData(){

	}

	public void logData(){
		threadId = ti.getThreadId();
		threadName = ti.getThreadName();
		threadState = ti.getThreadState();
	}

}