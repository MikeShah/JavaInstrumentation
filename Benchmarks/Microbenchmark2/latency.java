

import java.io.*;

public class latency {

    public static void main(String[] args) {
        tests t = new tests();
        t.t1_arithmetic();
        t.t1_appendToList();
        t.t1_hashmapTest();
        t.t1_AllocationTest();
        t.t1_StringTest();
        t.t1_SortLinkedListTest();
        t.t1_SortArrayListTest();
        t.t1_StackTest();
        t.t1_ArrayDataLocalityTest();
        t.t1_ArrayListOfArraylistTest();
    }

}
