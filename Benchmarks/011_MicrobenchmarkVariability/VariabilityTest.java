import java.util.*;

/*
	Simple bank class that handles and logs transactions
*/

class bank{
	private float balance;
	public List<Float> history;

	public bank(){
		balance = 0;
		history = new LinkedList<Float>();
	}

	public synchronized void withdrawFast(float amount){
		balance -= amount;
		history.add(-amount);
	}

	public synchronized void depositFast(float amount){
		balance += amount;
		history.add(amount);
	}

	public synchronized void withdrawSlow (float amount) throws InterruptedException{
		Thread.sleep(1000);
		balance -= amount;
		history.add(-amount);
	}

	public synchronized void depositSlow(float amount) throws InterruptedException{
		Thread.sleep(1000);
		balance += amount;
		history.add(amount);
	}

	public float getBalance(){
		return balance;
	}

	// Chooses one path or the other and executes
	public synchronized void checkHistory() throws InterruptedException{
		Random rand = new Random();
		int  n = (int)rand.nextInt(2);
		if(n==1){
			System.out.println("Thread.sleep(50);");
			Thread.sleep(50);
		}else{
			System.out.println("Thread.sleep(1000);");
			Thread.sleep(1000);
		}

	}

}

public class VariabilityTest{

	long startTime = 0L;
	long totalTime = 1000000000L*10L;
	bank b;

	public VariabilityTest(){
		b = new bank();
		
		t1();
		t2();
		t3();
		t4();
	}

	public void t1(){
		startTime = System.nanoTime();

		while(System.nanoTime() - startTime < totalTime){
			b.withdrawFast(100);
			System.out.println("t1:"+b.getBalance());
			b.depositFast(100);
			System.out.println("t1:"+b.getBalance());
		} 

		System.out.println("t1 Time elapsed: "+(System.nanoTime() - startTime));
	}

	public void t2(){
		startTime = System.nanoTime();

		try{
			while(System.nanoTime() - startTime < totalTime){
				b.withdrawSlow(100);
				System.out.println("t2:"+b.getBalance());
				b.depositSlow(100);
				System.out.println("t2:"+b.getBalance());
			} 

			System.out.println("t2 Time elapsed: "+(System.nanoTime() - startTime));		
		}
		catch(InterruptedException e){

		}
	}

	public void t3(){
		startTime = System.nanoTime();

			while(System.nanoTime() - startTime < totalTime){
				new Thread(){
					public void run() {
						try{
							b.withdrawSlow(100);
						}
						catch(InterruptedException e){

						}
					}
				}.start();


				new Thread(){
					public void run() {
						try{
							b.depositSlow(100);
						}
						catch(InterruptedException e){

						}
					}
				}.start();
			}

			System.out.println("t3 Time elapsed: "+(System.nanoTime() - startTime));		

	}
		
	public void t4(){
		startTime = System.nanoTime();
		
		try{
			while(System.nanoTime() - startTime < totalTime){
				b.checkHistory();
			} 

			System.out.println("t4 Time elapsed: "+(System.nanoTime() - startTime));		
		}
		catch(InterruptedException e){

		}
	}


}