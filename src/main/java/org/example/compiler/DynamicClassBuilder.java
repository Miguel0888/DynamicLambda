package org.example.compiler;

public class DynamicClassBuilder {

    /**
     * Wraps the provided code in a class definition that implements Runnable.
     *
     * @param className The name of the class to create.
     * @param methodBody The body of the run method.
     * @return A string containing the full class definition.
     */
    public static String wrapAsRunnable(String className, String methodBody) {
        return ""
                + "public class " + className + " implements java.lang.Runnable {\n"
                + methodBody + "\n"
                + "}";
    }
}
