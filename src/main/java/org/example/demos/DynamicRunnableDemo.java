package org.example.demos;

import org.example.compiler.DynamicClassBuilder;
import org.example.compiler.DynamicCompiler;

public class DynamicRunnableDemo {

    public static void main(String[] args) throws Exception {
        String runnableBody = ""
                + "public void run() {\n"
                + "    javax.swing.JOptionPane.showMessageDialog(null, \"Hallo aus dynamischem Code!\");\n"
                + "}";

        String fullClass = DynamicClassBuilder.wrapAsRunnable("MyRunnable", runnableBody);

        Runnable runnable = DynamicCompiler.compileRunnable("MyRunnable", fullClass);
        runnable.run(); // Zeigt Swing-Nachricht an
    }
}
