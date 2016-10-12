// Import instrumentation classes 
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.*;
import java.security.ProtectionDomain;
// javassist allows for Java bytecode manipulation at the Java Source and bytecode level.
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.*;

import java.util.*;
import java.io.*;
 
/*
    Purpose: This class implements 'ClassFileTransformer' which is required
             in order to manipulate .class files.
*/ 

public class SleepingClassFileTransformer implements ClassFileTransformer {

/// This is the transformation that is called
/// on every method.
//
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;

        // FIXME: See if there is a way to instrument inner classes
        if(className.contains("$")){
            return byteCode;
        }

	   for(int x = 0; x < ProfilingController.classNames.size(); ++x){
            /// Note that we have to use slashes here (i.e. org/something/something) instead of the '.'
            if (className.equals(ProfilingController.classNames.get(x).replace('.','/'))) {
        	//System.out.println("============= (SleepClassFileTransformer.java) Transforming and Instrumenting Classes ================== ");       
	        //System.out.println("\tAttempted transform() on class: "+ProfilingController.classNames.get(x));
                try {
                    final ClassPool cp = ClassPool.getDefault();
                    // The class here (i.e. org.something.something)
                    final CtClass cc = cp.get(ProfilingController.classNames.get(x));             

                    // Modify all of the classes methods
                    CtBehavior[] methods = cc.getDeclaredBehaviors();
                    System.out.println("\tInstrumenting: "+methods.length+" methods in class "+cc.getName());
                    System.out.flush();
                    // Loop that instruments all of the methods
                    for(int i =0; i < methods.length;i++){
                        // If the method is empty, then we do not need to instrument it.
                        if(methods[i].isEmpty()==false && isNative(methods[i])==false){
                            // TODO: Figure out how to find the single entry point
                            System.out.println(methods[i].getLongName());
                            if(methods[i].getLongName().contains("main")){
                                // Special case for instrumenting our 'main' method
                                // Note that we have to use 'getDeclaredMethod' here -- TODO: WHY use this?
                                final CtBehavior mainmethod = cc.getDeclaredMethod("main");
                                mainmethod.addLocalVariable("absoluteProgramTime", CtClass.longType);
                                String startTime   = "absoluteProgramTime = System.nanoTime();";
                                mainmethod.insertBefore(startTime);
                                mainmethod.insertAfter("{ProfilingController.setAbsoluteTime(absoluteProgramTime);"
                                                      +" ProfilingController.dumpFunctionMapCSV();ProfilingController.printCallTree();}");
                            }else{
                                instrumentMethod(methods[i]);
                                // We instrument all methods with call stack information such that we know who the caller is.
                                //instrumentMethodWithCallStack(methods[i]);
                            }
                        }
                    }
                    // Modify the bytecode
                    byteCode = cc.toBytecode();
                    cc.detach();
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                    System.out.flush();
                    ex.printStackTrace();
                }
            }
        }

	//System.out.println("============= (SleepClassFileTransformer.java) Transforming and Instrumenting Classes ================== "); 
        
        return byteCode;
    }

    // Useful function from how-to-avoid-javassist-cannotcompileexception-no-method-body
    // This is because we cannot instrument Native methods
    public boolean isNative(CtBehavior method){
        return Modifier.isNative(method.getModifiers());
    }

    // Useful function from how-to-avoid-javassist-cannotcompileexception-no-method-body
    // This is because we cannot instrument Native methods
    // This method in particular looks for synchornized methods.
    public boolean isSynchronized(CtBehavior method){
        return Modifier.isSynchronized(method.getModifiers());
    }    


    // Here we instrument all methods such that when they are called
    // their method name is added to the call stack.
    private void instrumentMethodWithCallStack(CtBehavior method) throws NotFoundException, CannotCompileException{
        method.insertBefore("ProfilingController.ccs.push(Thread.currentThread().getId(),"+method.getName()+");");
        method.insertAfter("ProfilingController.ccs.pop(Thread.currentThread().getId());");
    }




    // Updates a method such that it has a timer
    // This function actually modifies the method inserting code at each entry and exit.	 
    private void instrumentMethod(CtBehavior method) throws NotFoundException, CannotCompileException{
        
        // Retrieve the method name
        String m_name = method.getLongName();
        // Check if the method is in our functions that we want to instrument
        if(!ProfilingController.isInFunctionNames(m_name)){
            System.out.println(m_name+" is not in list of function names to be instrumented");
            //return;
        }else{
            System.out.println("\t\tStarting Instrumentation of function:"+m_name);  
        }
        // Add a method to our final map
        // Instrument the function by adding it to our Profiling HashMap
        boolean firstTimeInstrumented = ProfilingController.addFunc(m_name);
        // If we've already instrumented the method, then do not move forward 
        // In theory, this should never ber called!
        if(!firstTimeInstrumented){
            System.out.println(m_name+" has already been instrumented!");
            return;
        }

        // Return a one or zero if the method is synchronized.
        int synchronizedMethod = 0;
        if(isSynchronized(method)){
            synchronizedMethod=1;
        }else{
            // Only instrument the calling instructions
            //return;
        }
 
        // Add in an ArrayList that contains a set of floats
       //     CtClass arrListClass = ClassPool.getDefault().get("java.util.ArrayList");
       //     CtField f = new CtField(arrListClass,"AverageTime",CtClass.floatType);
            
        // TODO: Need to log thread entry and thread exit in a separate thread? Perhaps even the indentation
        //       Perhaps the ThreadID is the key?

        // Update the bytecode
        method.addLocalVariable("elapsedTime", CtClass.longType);
        method.addLocalVariable("mainThreadId", CtClass.longType);

        String getThreadID = "mainThreadId = Thread.currentThread().getId();";
        String buildCallTree = "ProfilingController.addToCallTreeList(ProfilingController.getSpaces(true)+\""+m_name+"__Entry\");";
                                // "ProfilingController.callTreeList.add(ProfilingController.getSpaces()+\""+m_name+"\"__Entry\");";
        String addEntry    = "ProfilingController.addEntry();";
        

                           //+ "System.out.println(\"Thread ID: \"+mainThreadId);";
        //String printEntry  = "System.out.println(\""+m_name+"__Entry\");";
        String startTime   = "elapsedTime = System.nanoTime();";
        String callStackStart = "ProfilingController.ccspush(mainThreadId,\""+m_name+"\");";
        // method.insertBefore(getThreadID+callStackStart+buildCallTree+addEntry+startTime+printEntry);
        method.insertBefore(getThreadID+startTime);
        method.insertBefore(getThreadID+callStackStart+buildCallTree+addEntry+startTime);



        // Add tracing ability, so we know how many times a function was called.
        method.insertAfter("{elapsedTime = System.nanoTime() - elapsedTime;"
                         + "ProfilingController.ccspop(mainThreadId);"
                         //+ "System.out.println(ProfilingController.getStackTrace());"       // Note that we get the caller here
                         + "ProfilingController.log(\""+m_name+"\",elapsedTime,mainThreadId,ProfilingController.getCaller(mainThreadId));" // 
                         + "ProfilingController.addExit();"
                         + "ProfilingController.addToCallTreeList(ProfilingController.getSpaces(true)+\""+m_name+"__Exit\" +\"|\"+elapsedTime+\"|\"+mainThreadId+\"|\"+"+synchronizedMethod+");}");
                         //+ "System.out.println(\""+m_name+"__Exit\");"
                         //+ "System.out.println(\""+m_name+" executed in ms: \" + elapsedTime);}");

      System.out.println("\tFinished Instrumentation of function:"+m_name);  
        
    }



    // Modifies bytecode, such that it 
    //  1.) Searches if a  
    //  2.) 
    //  3.) 
    /*
    public static void modifyByteCode(CtMethod cm) {

        cm.instrument(
            new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException{
                    if (m.getClassName().equals("Point") && m.getMethodName().equals("move"))
                        m.replace("{ $1 = 0; $_ = $proceed($$); }");
                    }
            });
    }
    */



}
