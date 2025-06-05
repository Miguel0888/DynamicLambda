package org.example.controller.swing;

import org.example.compiler.api.InMemoryJavaCompiler;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;

import static org.example.compiler.api.DynamicClassBuilder.wrapAsRunnable;

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
                String wrappedCode = wrapAsRunnable("UserCode", code);
                Runnable runnable = new InMemoryJavaCompiler().compile("UserCode", wrappedCode, Runnable.class);
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
}
