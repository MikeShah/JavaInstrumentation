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

public class FindAllClassesTransformer implements ClassFileTransformer {

    // Just populates the ProfilingController with all possible classes.
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;

        try{
            if(className != null && !className.contains("java") && !className.contains("sun") && !className.contains("ProfilingController") ){
                System.out.println("Adding:"+className);
                ProfilingController.classNames.add(className.replace('/','.'));            
            }            
        }catch(Exception ex){
            
        }

        return byteCode;
    }
}