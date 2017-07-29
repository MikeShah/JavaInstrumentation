/*

(1)

		Purpose: This class gathers information about threads and locks

*/

// import Instrumentation classes
import java.lang.instrument.Instrumentation;
import java.lang.management.*;
import sun.management.*;

public class ThreadData {

	long threadId;	// Unique thread iD Number remains unchanged througout lifetime.
	String threadName;
	Thread.State threadState;	// NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED
	long blockedCount;
	long blockedTime;
	long waitedCount;
	long waitedTime;


	ThreadData(boolean _holdsLock){
			boolean holdsLock = _holdsLock;
		//if(ProfilingController.threadMXBean!=null){
			//ThreadInfo ti = ProfilingController.threadMXBean.getThreadInfo(threadID);
			/*
			threadId 		= ti.getThreadId();
			threadName 	= ti.getThreadName();
			threadState = ti.getThreadState();

			blockedCount 	= ti.getBlockedCount();
			blockedTime 	= ti.getBlockedTime();
			waitedCount 	= ti.getWaitedCount();
			waitedTime 		= ti.getWaitedTime();
			*/
		//}

	}

	public String dumpCSV(){
		return threadId			+	ProfilingController.DelimiterSymbol +	threadName	+	ProfilingController.DelimiterSymbol +	threadState	+	ProfilingController.DelimiterSymbol	+	blockedCount	+	ProfilingController.DelimiterSymbol	+	blockedTime	+	ProfilingController.DelimiterSymbol	+	waitedCount	+	ProfilingController.DelimiterSymbol	+	waitedTime;
	}

}
