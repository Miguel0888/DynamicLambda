package org.example.demos;

import org.example.util.JavaSourceFromString;
import org.example.util.MemoryJavaFileManager;

import javax.tools.*;
import java.util.*;
import java.util.function.Function;

public class DynamicFunctionDemo {

    public static void main(String[] args) throws Exception {
        String className = "DynamicGreeting";
        String sourceCode =
                "import java.util.function.Function;\n" +
                        "public class " + className + " implements Function<String, String> {\n" +
                        "    public String apply(String input) {\n" +
                        "        return \"Hallo, \" + input.toUpperCase();\n" +
                        "    }\n" +
                        "}";

        // JavaCompiler vorbereiten
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        MemoryJavaFileManager fileManager = new MemoryJavaFileManager(
                compiler.getStandardFileManager(null, null, null));

        JavaFileObject sourceFile = new JavaSourceFromString(className, sourceCode);
        boolean result = compiler.getTask(null, fileManager, null, null, null, Collections.singletonList(sourceFile)).call();

        if (!result) {
            throw new IllegalStateException("Compilation failed");
        }

        // Klasse aus dem RAM laden
        ClassLoader classLoader = fileManager.getClassLoader(null);
        Class<?> clazz = classLoader.loadClass(className);
        Function<String, String> function = (Function<String, String>) clazz.newInstance();

        // Ausführen
        String resultText = function.apply("welt");
        System.out.println("Ergebnis: " + resultText);
    }
}
