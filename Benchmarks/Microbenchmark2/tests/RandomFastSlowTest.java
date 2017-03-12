import java.util.*;


public final class RandomFastSlowTest{

    final static int iterations = 10;

    static synchronized void functionCall(){
      Random rand = new Random();
      for(int i =0; i < iterations; ++i){
        if(rand.nextInt(2)==0){
          fastPath();
        }else{
          slowPath();
        }
      }
    }

    static synchronized int fastPath(){
      int sum = 0;
      for(int i= 0; i < 50; ++i){
        sum += i;
      }
      return sum;
    }

    static synchronized int slowPath(){
      int sum = 0;
      for(int i= 0; i < 50000000; ++i){
        sum += i;
      }
      return sum;
    }

}
