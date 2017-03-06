import java.util.*;

public final class hashmapTest{
  // TODO: Look at these notes later on
  // http://www.jguru.com/faq/view.jsp?EID=430247
    static HashMap<Integer,Integer> numbers = new HashMap<Integer,Integer>();

    static synchronized void append(int key, int value){
      numbers.put(key,value);
    }

    static synchronized void remove(int key){
      numbers.remove(key);
    }
}
