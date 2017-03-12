import java.util.*;


public final class NestedSynchronizedClassesTest{

    final static int iterations = 10;
    final static int value = 10;
    static int newValue;

    static synchronized void slow_or_fast_Path(){
      for(int i =0; i< iterations; ++i){
        if(i%2==0){
          slowPath_NestedFourDeep();
        }else{
          fastPath_NestedTwoDeep();
        }
      }
    }

    static synchronized void slowPath_NestedFourDeep(){
      SecondClass.secondCall(value);
    }

    static synchronized void fastPath_NestedTwoDeep(){
      FourthClass.fourthCall(value);
    }

}

final class SecondClass{

    static synchronized void secondCall(int value){
      ThirdClass.thirdCall(value);
    }
}

final class ThirdClass{

    static synchronized void thirdCall(int value){
      FourthClass.fourthCall(value);
    }
}

final class FourthClass{

    static synchronized void fourthCall(int value){
      NestedSynchronizedClassesTest.newValue = value + 1;
    }
}
