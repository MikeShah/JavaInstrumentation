import java.util.*;


public final class SortArrayListTest{
    static List<String> lst = new ArrayList<String>();

    final static String alphabet = "abcdefghijklmnopqrstuvwxyz";
    final static int N = alphabet.length();

    static synchronized void addRandom4LetterWord(){
        String s = "";
        Random r = new Random();
        for (int i = 0; i < 4; i++) {
            s += alphabet.charAt(r.nextInt(N));
        }
        lst.add(s);
    }

    static synchronized void sortList(){
      Collections.sort(lst);
    }

}
