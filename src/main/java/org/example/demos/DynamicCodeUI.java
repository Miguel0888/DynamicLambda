package org.example.demos;

import javax.swing.*;
import javax.tools.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.*;

public class DynamicCodeUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DynamicCodeUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Dynamischer Java-Code");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JTextArea codeArea = new JTextArea();
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        codeArea.setText(
                "public void run() {\n" +
                        "    javax.swing.JOptionPane.showMessageDialog(null, \"Hallo aus dynamischem Code!\");\n" +
                        "}"
        );

        JButton runButton = new JButton("Ausführen");
        runButton.addActionListener(e -> {
            String code = codeArea.getText();
            try {
                Runnable runnable = compileAndLoadRunnable("UserCode", wrapAsRunnable("UserCode", code));
                runnable.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Fehler:\n" + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        frame.getContentPane().add(new JScrollPane(codeArea), BorderLayout.CENTER);
        frame.getContentPane().add(runButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static String wrapAsRunnable(String className, String methodBody) {
        return "public class " + className + " implements java.lang.Runnable {\n" + methodBody + "\n}";
    }

    private static Runnable compileAndLoadRunnable(String className, String sourceCode) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        MemoryJavaFileManager fileManager = new MemoryJavaFileManager(
                compiler.getStandardFileManager(null, null, null));

        JavaFileObject sourceFile = new JavaSourceFromString(className, sourceCode);
        boolean success = compiler.getTask(null, fileManager, null, null, null, Collections.singletonList(sourceFile)).call();

        if (!success) {
            throw new IllegalStateException("Kompilierung fehlgeschlagen");
        }

        ClassLoader classLoader = fileManager.getClassLoader(null);
        Class<?> clazz = classLoader.loadClass(className);
        return (Runnable) clazz.newInstance();
    }

    // --- Hilfsklassen ---

    static class JavaSourceFromString extends SimpleJavaFileObject {
        private final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    static class MemoryJavaFileObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        MemoryJavaFileObject(String className, Kind kind) {
            super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
        }

        @Override
        public OutputStream openOutputStream() {
            return outputStream;
        }

        public byte[] getBytes() {
            return outputStream.toByteArray();
        }
    }

    static class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final Map<String, MemoryJavaFileObject> compiledClasses = new HashMap<>();

        MemoryJavaFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className,
                                                   JavaFileObject.Kind kind, FileObject sibling) {
            MemoryJavaFileObject fileObject = new MemoryJavaFileObject(className, kind);
            compiledClasses.put(className, fileObject);
            return fileObject;
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return new ClassLoader(getClass().getClassLoader()) {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    MemoryJavaFileObject file = compiledClasses.get(name);
                    if (file != null) {
                        byte[] bytes = file.getBytes();
                        return defineClass(name, bytes, 0, bytes.length);
                    }
                    return super.findClass(name);
                }
            };
        }
    }
}
