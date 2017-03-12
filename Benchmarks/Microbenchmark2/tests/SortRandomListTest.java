import java.util.*;


public final class SortRandomListTest{

  static List<Integer> lst = new ArrayList<Integer>();

    static synchronized void generateAndSortRandomLists(int size){
      lst.clear();
      Random rand = new Random();
      for(int i =0; i < size; ++i){
        int value = rand.nextInt(size*2);
      }
      Collections.sort(lst);
    }

}
