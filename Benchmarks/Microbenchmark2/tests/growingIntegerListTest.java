import java.util.*;


public final class growingIntegerListTest{

  static List<Integer> lst = new ArrayList<Integer>();
  // Grow the list by 1 and then update all of the values
  static synchronized void iterateThroughList(){
    lst.add(0);
    for(int i =0; i < lst.size(); ++i){
      int computation = (((lst.get(i)*3)+4)/2)*7;
      lst.set(i,computation);
    }
  }
}
