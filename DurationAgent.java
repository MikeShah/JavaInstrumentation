/*

(1)

		Purpose: This is the Agent Class.
				 Agent classes contain a 'premain' that is executed prior to
				 the main program.

		In order to instrument the class, we have to compile this file as a .jar
		and then load it. We can load it when we run a Java program with the following
		arguments: -javagent:jarpath[=options]

*/

// import Instrumentation classes
import java.lang.instrument.Instrumentation;

public class DurationAgent {

	// A string to a file which contains a list of classes to instrument.
	static String classNamesToInstrument = "./Benchmarks/SampleProgram/ClassNames.txt";
	static String functionsToInstrument  = "./Benchmarks/SampleProgram/FunctionNames.txt";

	// SampleProgram- Z Drive
//	static String classNamesToInstrument = "/h/mshah08/Desktop/JavaInstrumentation/Benchmarks/SampleProgram/ClassNames.txt";
//	static String functionsToInstrument  = "/h/mshah08/Desktop/JavaInstrumentation/Benchmarks/SampleProgram/FunctionNames.txt";


	// Jython - Laptop
//	static String classNamesToInstrument = "/home/mike/Desktop/JavaDistribution/JavaInstrumentation/Benchmarks/Jython/ClassNames.txt";
//	static String functionsToInstrument  = "/home/mike/Desktop/JavaDistribution/JavaInstrumentation/Benchmarks/Jython/FunctionNames.txt";

	// Sunflow - Ubunutu Desktop
//	static String classNamesToInstrument = "/home/mshah08/Desktop/JavaDistribution/JavaInstrumentation/Benchmarks/Sunflow/ClassNames.txt";
//	static String functionsToInstrument  = "/home/mshah08/Desktop/JavaDistribution/JavaInstrumentation/Benchmarks/Sunflow/FunctionNames.txt";

	// Sunflow - ZDrive
//	static String classNamesToInstrument = "/h/mshah08/Desktop/JavaDistribution/JavaInstrumentation/Benchmarks/Sunflow/ClassNames.txt";
//	static String functionsToInstrument  = "/h/mshah08/Desktop/JavaDistribution/JavaInstrumentation/Benchmarks/Sunflow/FunctionNames.txt";
// Runs at the start of the program
/*public static void main(String[] agentArgs) {
	//agentmain(agentArgs);
	System.out.println("Booting dynamically");
	for(String s: agentArgs){
		println(s);
	}
}
*/

	// Runs at the start of the program
	public static void premain(String agentArgs, Instrumentation inst) {
		agentmain(agentArgs,inst);
	}


	public static void agentmain(String agentArgs, Instrumentation inst){
		if (!inst.isRedefineClassesSupported()){
		            throw new RuntimeException("this JVM does not support redefinition of classes");
		}
			System.out.println("===(DurationAgent.java) Started executing premain===");
			System.out.println("Argumentstring="+agentArgs);
			ProfilingController.setAgentArgs(agentArgs);
			ProfilingController.setup(classNamesToInstrument);
			ProfilingController.setFunctions(functionsToInstrument);
			// Find every class in the program and add it to the list of classes to be instrumented.
			inst.addTransformer(new FindAllClassesTransformer());
			// Instrument classes that we have found.
			inst.addTransformer(new SleepingClassFileTransformer());
			System.out.println("===(DurationAgent.java) Finished executing premain===");
		}

}
