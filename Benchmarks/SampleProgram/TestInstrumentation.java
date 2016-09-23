/*
	
	Purpose: This is a demo program, that implements a small program.
			 The program itself, creates a new class of type 'Lion' that
			 merely runs, and then sleeps right away.
	
*/

public class TestInstrumentation {
 
	public static void main(String args[]) throws InterruptedException {
		Sleeping s = new Sleeping();
		s.RandomSleep();
		s.RandomSleep2();
		s.RandomSleep();
		s.RandomSleep();
		s.RandomSleep();
		s.RandomSleep();
		s.RandomSleep();
		//s.add(3,4);
		s.RandomSleep2();
	}
}