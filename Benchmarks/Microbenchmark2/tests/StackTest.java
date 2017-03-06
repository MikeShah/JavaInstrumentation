import java.util.*;


public final class StackTest{
    static Stack s = new Stack();


    static synchronized void add(){
      s.push(new Integer(1));
    }

    static synchronized void remove(){
      s.pop();
    }

}
