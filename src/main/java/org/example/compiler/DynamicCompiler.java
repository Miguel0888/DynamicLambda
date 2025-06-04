package org.example.compiler;

import org.example.util.JavaSourceFromString;
import org.example.util.MemoryJavaFileManager;

import javax.tools.*;
import java.util.*;

public class DynamicCompiler {

    public static Runnable compileRunnable(String className, String sourceCode) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        MemoryJavaFileManager fileManager = new MemoryJavaFileManager(
                compiler.getStandardFileManager(null, null, null));

        JavaFileObject sourceFile = new JavaSourceFromString(className, sourceCode);
        boolean result = compiler.getTask(null, fileManager, null, null, null, Collections.singletonList(sourceFile)).call();

        if (!result) {
            throw new IllegalStateException("Compilation failed");
        }

        ClassLoader classLoader = fileManager.getClassLoader(null);
        Class<?> clazz = classLoader.loadClass(className);
        return (Runnable) clazz.newInstance();
    }
}
