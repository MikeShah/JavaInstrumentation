// The class to be instrumented

public class Sleeping {

/*
	public int add(int a, int b){
		return a + b;
	}
*/
	public void RandomSleep() throws InterruptedException {
		// Randomly sleeps between 500ms and 1200ms
		long randomSleepDuration = (long)(500 + Math.random()*700);
		System.out.printf("RandomSleep(): Sleeping for %d ms..\n",randomSleepDuration);
		Thread.sleep(randomSleepDuration);
	}

	public synchronized void RandomSleep2() throws InterruptedException {
		// Randomly sleeps between 500ms and 1200ms
		long randomSleepDuration = (long)(500 + Math.random()*700);
		System.out.printf("RandomSleep2(): Sleeping for %d ms..\n",randomSleepDuration);
		Thread.sleep(randomSleepDuration);
		a();
	}

	public void a(){
		b();
	}

	public void b(){
		c();
		d();
	}

	public void c(){
		System.out.println("Yaaaaa c ");
	}

	public void d(){
				System.out.println("Yaaaaa d");
				e(5);
	}
	
	public synchronized void e(int a){
				System.out.println("Yaaaaa e(int)");
				e(2,7);
	}
	public synchronized void e(int a, int b){
				System.out.println("Yaaaaa e(int,int)");
	}

}