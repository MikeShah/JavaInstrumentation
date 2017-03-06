public class ProcessorHook extends Thread {

    @Override
    public void run(){
      System.out.println("Forcing shutdown");
      ProfilingController.calculateAbsoluteTime();
      ProfilingController.dumpFunctionMapCSV();
      ProfilingController.dumpFunctionHistograms();
      ProfilingController.printCallTree();

    }
}
