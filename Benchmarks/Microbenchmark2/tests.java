import java.util.*;

public class tests {

	private final int ITERATIONS = 10;

	// arithmetic.java
	public void t1_arithmetic(){
		for(int i =0; i < ITERATIONS; ++i){
			arithmetic.add(2);
			arithmetic.sub(1);
		}
	}

	// arithmetic.java
	public void t1_appendToList(){
		for(int i =0; i < ITERATIONS; ++i){
			appendToList.append();
			appendToList.remove();
		}
	}

	// hashmapTest.java
	public void t1_hashmapTest(){
		for(int i =0; i < ITERATIONS; ++i){
			hashmapTest.append(i,i);
		}
		for(int i =0; i < ITERATIONS; ++i){
			hashmapTest.remove(i);
		}
	}

	// AllocationTest.java
	public void t1_AllocationTest(){
		for(int i =0; i < ITERATIONS; ++i){
			AllocationTest.append();
		}
		for(int i =0; i < ITERATIONS; ++i){
			AllocationTest.remove();
		}
	}

	// StringTest.java
	public void t1_StringTest(){
		for(int i =0; i < ITERATIONS; ++i){
			StringTest.append();
		}
	}

	// SortLinkedListTest.java
	public void t1_SortLinkedListTest(){
		for(int i =0; i < ITERATIONS; ++i){
			SortLinkedListTest.addRandom4LetterWord();
		}
		SortLinkedListTest.sortList();
	}

	// SortArrayListTest.java
	public void t1_SortArrayListTest(){
		for(int i =0; i < ITERATIONS; ++i){
			SortArrayListTest.addRandom4LetterWord();
		}
		SortArrayListTest.sortList();
	}

	// StackTest.java
	public void t1_StackTest(){
		for(int i =0; i < ITERATIONS; ++i){
			StackTest.add();
		}
		for(int i =0; i < ITERATIONS; ++i){
			StackTest.remove();
		}
	}

	// StackTest.java
	public void t1_ArrayDataLocalityTest(){
		ArrayDataLocalityTest.setup();

		for(int i =0; i < ITERATIONS; ++i){
			ArrayDataLocalityTest.increment();
		}
	}

	// ArrayListOfArraylistTest.java
	public void t1_ArrayListOfArraylistTest(){
		ArrayListOfArraylistTest.setup();
		
		for(int i =0; i < ITERATIONS; ++i){
			ArrayListOfArraylistTest.increment();
		}
	}


}
