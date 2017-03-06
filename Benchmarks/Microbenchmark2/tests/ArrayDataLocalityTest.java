import java.util.*;


public final class ArrayDataLocalityTest{

    static Integer myIntArray[][] = new Integer[1000][1000];

    static synchronized void setup(){
      for(int x = 0; x < 1000; ++x){
        for(int y = 0; y < 1000; ++y){
          myIntArray[x][y] = 1;
        }
      }
    }

    static synchronized void increment(){
      for(int x = 0; x < 1000; ++x){
        for(int y = 0; y < 1000; ++y){
          myIntArray[x][y] ++;
        }
      }
    }

}
