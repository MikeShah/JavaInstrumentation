public final class arithmetic{

    static int accum;

    static synchronized void add(int a){
      accum += a;
    }

    static synchronized void sub(int a){
      accum -= a;
    }
}
