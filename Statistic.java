import java.util.*;

	// Statistic is a Piece of Data (POD) used
	// in the hashmap for a function.
	// The hashmap of 'functionMap'  and 'statisticMap' in the Profiling Controller match each other
	public class Statistic{
		ArrayList<Long> timeList; // How much time a method took to execute
		ArrayList<Long> threadIDList; // The ID of the thread that exectued.
		ArrayList<String> callerList; // The function caller of this function
		ArrayList<Boolean> threadDataList; // The function caller of this function


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
			if(threadDataList==null){
				threadDataList = new ArrayList<Boolean>();
			}
		}
		/// Default constructor
		public Statistic(){
			init();
		}
		// Adds a log of how long a method executed for.
		public void addTime(Long t, Long id, String caller, boolean td){
			if(timeList==null || threadIDList==null || callerList==null || threadDataList==null){
				init();
			}

			timeList.add(t);
			threadIDList.add(id);
			callerList.add(caller);
			threadDataList.add(td);
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
		// If concise is true, then it will only output a sum of the time, and a
		// total of the divergent points.
		public String dumpCSV(){
			String result="";

			// Output every time and the thread id
			if(ProfilingController.KNOB_COMPRESSED_CSV){
				long timeSum = 0L;
				for(int i =0; i < timeList.size();i++){
					timeSum+=timeList.get(i);
				}
				result = "Total Calls="+timeSum+ProfilingController.DelimiterSymbol;
			}else{
				for(int i =0; i < timeList.size();i++){
					result += threadIDList.get(i)+ProfilingController.DelimiterSymbol+callerList.get(i)+ProfilingController.DelimiterSymbol+ timeList.get(i).toString()+ProfilingController.DelimiterSymbol;
				}
			}

			ArrayList<Integer> divergingPoints = bestFitLineLeastSquares();//bestFitLine();
			double percentageOfDivergingPoints = (double)divergingPoints.size()/(double)timeList.size();
			String divergingPointsList = "";
			if(ProfilingController.KNOB_COMPRESSED_CSV){
					divergingPointsList += "Total="+divergingPoints.size();
			}
			else{
				for(int i = 0; i < divergingPoints.size(); ++ i){
					divergingPointsList += divergingPoints.get(i)+",";
				}
			}


			// Outputs the total number of executions, Average execution time, and then individual runs
			return 	ProfilingController.DelimiterSymbol+timeList.size()+
					ProfilingController.DelimiterSymbol+getAvg()+
					ProfilingController.DelimiterSymbol+getMax()+
					ProfilingController.DelimiterSymbol+getMin()+
					ProfilingController.DelimiterSymbol+getStdDev()+
					ProfilingController.DelimiterSymbol+percentageOfDivergingPoints+
					ProfilingController.DelimiterSymbol+divergingPointsList+
					ProfilingController.DelimiterSymbol+result+
					ProfilingController.DelimiterSymbol;
		}


		// Some helper math functions
		// Computes the average of the entire timelist
		private double getAvg(){
			return computeAverage(timeList, 0, timeList.size());
		}

		// Computes the average of a range
		private double computeAverage(ArrayList<Long> lst, int start, int end){
			double avg = 0;

			if(lst.size()>0){
				for(int i =start; i < end; i++){
					avg += lst.get(i);
				}

				avg = avg / lst.size();
			}
			return avg;
		}

		// Highest runtime
		private long getMax(){
			long max = Long.MIN_VALUE;

			for(int i =0; i < timeList.size();i++){
				if(timeList.get(i)>max){
					max = timeList.get(i);
				}
			}

			return max;
		}

		// Lowest runtime
		private long getMin(){
			long min = Long.MAX_VALUE;
			for(int i =0; i < timeList.size();i++){
				if(timeList.get(i)<min){
					min = timeList.get(i);
				}
			}
			return min;
		}

		// Calculate standard deviation
		private double getStdDev(){
			double stddev = -1.0;
			// (1) Compute the average
			double avg = getAvg();
			// (2) Compute the variances
			ArrayList<Double> varianceAverages = new ArrayList<Double>();
			for(int i =0; i < timeList.size();i++){
				double variance = (timeList.get(i)-avg)*(timeList.get(i)-avg);
				varianceAverages.add(variance);
			}
			// (3) Get average of variances
			double variance_mean = 0;
			for(int i =0; i < varianceAverages.size();i++){
				variance_mean += varianceAverages.get(i);
			}
			variance_mean = variance_mean/varianceAverages.size();
			// (4) Take square root
			stddev = Math.sqrt(variance_mean);

			return stddev;
		}

		// Computes the best fit line
		// X-axis is the number of the execution (time in program)
		// Y-axis is the total execution time.
		/*
			// Variables used to create line equation
			xsum - The sum of all the values in the x column.
			ysum - The sum of all the values in the y column.
			xysum - The sum of the products of the xn and yn that are recorded at the same time (vertical on this chart).
			x2sum - The total of each value in the x column squared and then added together.
			y2sum - The total of each value in the y column squared and then added together.
			N - The total number of elements (or trials in your experiment).

			m =
				(Nxysum) - (xsumysum)
				(Nx2sum) - (xsumxsum)

			b =
				(x2sumysum) - (xsumxysum)
				(Nx2sum) - (xsumxsum)

			The purpose of this function is to find where the function
			diverges.

			Based off: http://sciencefair.math.iit.edu/analysis/linereg/hand/
		*/
		public ArrayList<Integer> bestFitLine(){

			ArrayList<Integer> divergingPoints = new ArrayList<Integer>();

			double xsum  = timeList.size();	// In the future this could time in the program execution
			double ysum  =  getAvg()*timeList.size();
			double xysum = 0;
			double x2sum = 0;
			double y2sum = 0;
			for(int x =0; x < timeList.size(); ++x){
				xysum += (x+1)*timeList.get(x);
				x2sum += (x+1);
				y2sum += timeList.get(x);
			}

			// The number of executions
			double N 	 = timeList.size();
			// Slope of the line
			double m = ((N*xysum) - (xsum*ysum)) / ((N*x2sum) -(xsum*xsum));
			// y-intercept
			double b = ((N*x2sum)-(xsum*xsum)) / ((N*x2sum)-(xsum*xsum));
			// Retrieve the stddev
			double stddev = getStdDev();

			// Equation of a line then becomes y=mx + b
			// We then use this to find all points that are greater than one
			// 1 standard deviation (That is, one standard deviation away from the trend line).
			for(int x =0; x < timeList.size(); ++x){
				double y = m*x+b;
				if(Math.abs(y - timeList.get(x)) > stddev){
					divergingPoints.add(x);
				}
			}

			return divergingPoints;
		}

		public ArrayList<Integer> bestFitLineLeastSquares(){

			ArrayList<Integer> divergingPoints = new ArrayList<Integer>();

			// End execution if ther eis nothing to cmopute
			if(timeList.size()==0){
				return divergingPoints;
			}

			// (1) Compute means of x and y
			// Not used arithmetic sum for xMean to quickly compute
			double xMean = ((timeList.size()*timeList.size()+1)/2) / timeList.size();
			double yMean =  getAvg();
			// (2) compute slope
			double m = 1;
			double rise = 0;
			double run = 0;

			for(int i =0; i < timeList.size(); ++i){
						rise += (i-xMean)*(timeList.get(i)-yMean);
						run += (i-xMean)*(i-xMean);
			}
			// Compute the final slope if m is not zero
			if(m!=0){
				m = rise/run;
			}
			// (3) Compute y-intercept
			double b = yMean - (m*xMean);

			// Retrieve the stddev
			double stddev = getStdDev();
			// (4) The final equation is y = mx + b
			for(int x = 0; x < timeList.size(); ++x){
					// projected y value is
					double y = (m*x) + b;
					// Now if that point is greater than the stddev, then output it
					if( Math.abs(timeList.get(x) - y) >  stddev){
						divergingPoints.add(x);
					}
			}

			return divergingPoints;
		}


		// Computes a list of points that diverage from the previous 'percent' (pass as an argument)
		// of iterations of the function.
		//
		// Algorithm:
		// 1.) Multiply total executions by percent to figure out the interval size
		// 2.) Compare current item 'i' with adjacent elements equal to the size of the interval.
		// 		 a. ) Edge Case: Our first 'interval' is considered the baseline truth
		public List<Integer> simpleMovingAverage(double percent){
			int intervalSize = (int)(timeList.size()*percent);

			List<Integer> results = new LinkedList<Integer>();

			for(int i = intervalSize; i < timeList.size()-intervalSize; ++i){
				double currentWindow = computeAverage(timeList,i,i+intervalSize);
			}

			return results;
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
