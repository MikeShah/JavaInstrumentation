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
	static String classNamesToInstrument = "/home/mike/Desktop/JavaDistribution/JavaInstrumentation/SampleProgram/ClassNames.txt";
	static String functionsToInstrument  = "/home/mike/Desktop/JavaDistribution/JavaInstrumentation/SampleProgram/FunctionNames.txt";

	// Runs at the start of the program
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("===(DurationAgent.java) Started executing premain===");
		ProfilingController.setup(classNamesToInstrument);
		ProfilingController.setFunctions(functionsToInstrument);
		inst.addTransformer(new SleepingClassFileTransformer());
		System.out.println("===(DurationAgent.java) Finished executing premain===");
	}

}
