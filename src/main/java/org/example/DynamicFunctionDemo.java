package org.example;

import javax.tools.*;
import java.net.*;
import java.util.*;
import java.util.function.Function;

public class DynamicLambdaDemo {

    public static void main(String[] args) throws Exception {
        String className = "MyLambdaImpl";
        String code = ""
                + "import java.util.function.Function;\n"
                + "public class " + className + " implements Function<String, String> {\n"
                + "    public String apply(String input) {\n"
                + "        return \"Hallo \" + input.toUpperCase();\n"
                + "    }\n"
                + "}\n";

        // Kompiliere zur Laufzeit
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileObject file = new JavaSourceFromString(className, code);

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaFileManager fileManager = new MemoryJavaFileManager(
                compiler.getStandardFileManager(diagnostics, null, null));

        Boolean success = compiler.getTask(null, fileManager, diagnostics, null, null, Arrays.asList(file)).call();

        if (!success) {
            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                System.err.println(diagnostic);
            }
            return;
        }

        // Lade und instanziiere Klasse
        ClassLoader classLoader = fileManager.getClassLoader(null);
        Class<?> clazz = classLoader.loadClass(className);
        @SuppressWarnings("unchecked")
        Function<String, String> lambda = (Function<String, String>) clazz.getConstructor().newInstance();

        // Verwende dynamisch generierte Funktion
        System.out.println(lambda.apply("welt"));
    }

    // Hilfsklasse: Source-Code im Speicher
    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;
        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
