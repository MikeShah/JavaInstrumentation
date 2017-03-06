import java.util.*;

class bigObject{
  String s1,s2,s3,s4,s5,s6,s7,s8,s9;
  public bigObject(){
    s1 = "0000000000";
    s2 = "1111111111";
    s3 = "2222222222";
    s4 = "3333333333";
    s5 = "4444444444";
    s6 = "5555555555";
    s7 = "6666666666";
    s8 = "7777777777";
    s9 = "8888888888";
  }
}

public final class AllocationTest{
  // TODO: Look at these notes later on
  // http://www.jguru.com/faq/view.jsp?EID=430247
    static ArrayList<bigObject> lst = new ArrayList<bigObject>();

    static synchronized void append(){
      lst.add(new bigObject());
    }
    static synchronized void remove(){
      lst.remove(0);
    }

}
