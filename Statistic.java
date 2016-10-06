import java.util.*;

	// Statistic is a Piece of Data (POD) used
	// in the hashmap for a function.
	// The hashmap of 'functionMap'  and 'statisticMap' in the Profiling Controller match each other 
	class Statistic{
		ArrayList<Long> timeList; // How much time a method took to execute
		ArrayList<Long> threadIDList; // The ID of the thread that exectued.
		ArrayList<String> callerList; // The function caller of this function	

		/// Initializer called from any constructor
		private void init(){
			if(timeList==null){
				timeList = new ArrayList<Long>();
			}
			
			if(threadIDList==null){
				threadIDList = new ArrayList<Long>();
			}

			if(callerList==null){
				callerList = new ArrayList<String>();
			}
		}
		/// Default constructor
		public Statistic(){
			init();
		}
		// Adds a log of how long a method executed for.
		public void addTime(Long t, Long id, String caller){
			if(timeList==null || threadIDList==null || callerList==null){
				init();
			}

			timeList.add(t);
			threadIDList.add(id);
			callerList.add(caller);
		}

		// Outputs the statistic in a nice way
		public String dump(){
			String result="";
			long avg = 0;
			// Output every time and the thread id
			for(int i =0; i < timeList.size();i++){
				result += "("+threadIDList.get(i)+")("+callerList.get(i)+")"+ timeList.get(i).toString()+ProfilingController.DelimiterSymbol;
				avg += timeList.get(i);
			}

			if(timeList.size()>0){
				avg = avg / timeList.size();
			}

			return "Average of "+timeList.size()+" Runs = "+avg+ ProfilingController.DelimiterSymbol+"Runs = {"+result+"}";
		}

		// Params: If CSV is true, then output comma separated list
		public String dumpCSV(){
			String result="";
			long avg = 0;
			// Output every time and the thread id
			for(int i =0; i < timeList.size();i++){
				result += threadIDList.get(i)+ProfilingController.DelimiterSymbol+callerList.get(i)+ProfilingController.DelimiterSymbol+ timeList.get(i).toString()+ProfilingController.DelimiterSymbol;
				avg += timeList.get(i);
			}

			if(timeList.size()>0){
				avg = avg / timeList.size();
			}

			return "Runs"+ProfilingController.DelimiterSymbol+timeList.size()+ProfilingController.DelimiterSymbol+"Runs Avg"+ProfilingController.DelimiterSymbol+avg+ ProfilingController.DelimiterSymbol+"Runs"+ProfilingController.DelimiterSymbol+result+ProfilingController.DelimiterSymbol;
		}


		// Output the statistic in a parsable way
		// param: instance is the instance of data from the tree at one particular point.
		//	 (i.e. the 4th execution of a method would be an instance value of 3)
		//	 
		//	 Error Conditions Handled:
		//	 (1) instance is less than threadIDList or timeList range
		public String dumpParse(int instance){
			String result="";
			//System.out.println("dumpParse:"+instance);
			// If the instance is out of bounds, then return empty.
			if(instance > threadIDList.size() || instance > timeList.size()){
				return result;
			}			

			//long avg = 0;
			// Output every time and the thread id
			//for(int i =0; i < timeList.size();i++){
				result += "("+threadIDList.get(instance)+")("+callerList.get(instance)+")"+ timeList.get(instance).toString();
			//	avg += timeList.get(i);
			//}

			//if(timeList.size()>0){
			//	avg = avg / timeList.size();
			//}

			return "[["+result+"]]";			
		}

	}
