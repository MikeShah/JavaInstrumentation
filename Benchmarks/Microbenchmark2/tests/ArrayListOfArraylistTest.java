import java.util.*;


public final class ArrayListOfArraylistTest{

    static ArrayList<ArrayList<Integer>> arr_arr = new ArrayList<ArrayList<Integer>>();

    static synchronized void setup(){
      for(int x = 0; x < 1000; ++x){
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for(int y = 0; y < 1000; ++y){
          temp.add(y);
        }
        arr_arr.add(temp);
      }
    }

    static synchronized void increment(){
      for(int x = 0; x < 1000; ++x){
        ArrayList<Integer> temp = arr_arr.get(x);
        for(int y = 0; y < temp.size(); ++y){
          temp.set(y,temp.get(y)+1);
        }
      }
    }

}
