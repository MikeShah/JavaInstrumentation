import java.util.*;

public final class appendToList{

    static ArrayList<String> lst = new ArrayList();

    static synchronized void append(){
      lst.add("aa");
    }

    static synchronized void remove(){
      lst.remove(0);
    }
}
