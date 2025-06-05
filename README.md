# 🕒 Java Code Execution Tool – Swing & REST

Dieses Projekt erlaubt die Ausführung von benutzerdefiniertem Java-Code zur Laufzeit – entweder über eine **lokale Swing-Oberfläche** oder per **REST-API**.

## 📦 Projektaufbau

```
src/
├── org.example
│   ├── compiler.api            # InMemoryJavaCompiler, DynamicClassBuilder
│   ├── controller.rest         # REST-Handler (JDK HTTPServer)
│   ├── controller.swing        # Swing UI
│   └── Main.java               # Startet HTTP-Server
```

## 🚀 Funktionen

- Kompiliert Java-Code zur Laufzeit aus Text (via `javax.tools`)
- Unterstützt `Runnable` (für Swing) und `Callable<String>` (für REST)
- Kapselt Swing und REST vollständig getrennt
- JSON-Ein-/Ausgabe über REST mit [Gson](https://github.com/google/gson)

---

## 🖥 Swing UI

Die Swing-Oberfläche (`DynamicCodeUI`) erlaubt es, Code direkt einzugeben und auszuführen.

**Beispiel-Code (läuft direkt in der UI):**

```java
public void run() {
    javax.swing.JOptionPane.showMessageDialog(null, "Hallo aus Swing");
}
```

- Eingabe erfolgt in einem `JTextArea`
- Code muss eine `run()`-Methode enthalten
- Bei Erfolg wird die Methode ausgeführt, Fehler werden angezeigt

---

## 🌐 REST-API

Die REST-API nutzt den in Java 8 enthaltenen `com.sun.net.httpserver.HttpServer`  
und lauscht auf Port **8080** unter:

```
POST http://localhost:8080/run
Content-Type: application/json
```

### 🔁 Request Body:

```json
{
  "code": "return \"Hallo von REST\";"
}
```

### 🔄 Response Body:

```json
{
  "output": "Hallo von REST"
}
```

---

## 🕒 Beispiel: Swing-Uhr über REST starten

**Beispiel-Request:**

```json
{
  "code": "javax.swing.JFrame frame = new javax.swing.JFrame(\"Uhrzeit\");\njavax.swing.JLabel label = new javax.swing.JLabel();\nlabel.setFont(new java.awt.Font(\"Monospaced\", java.awt.Font.BOLD, 24));\nlabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);\nframe.getContentPane().add(label);\nframe.setSize(300, 100);\nframe.setLocationRelativeTo(null);\nframe.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);\nframe.setVisible(true);\njavax.swing.Timer timer = new javax.swing.Timer(1000, e -> {\n    java.time.LocalTime time = java.time.LocalTime.now();\n    label.setText(time.toString());\n});\ntimer.start();\nreturn \"Uhr gestartet\";"
}
```

**Antwort:**

```json
{
  "output": "Uhr gestartet"
}
```

➡ Dadurch wird eine Swing-Uhr geöffnet, die jede Sekunde die aktuelle Uhrzeit anzeigt.

---

## 🧪 Test mit curl

```bash
curl -X POST http://localhost:8080/run \
     -H "Content-Type: application/json" \
     -d '{"code":"return \"Hallo von REST!\";"}'
```

---

## ⚠ Hinweise

- Das Programm muss **nicht im Headless-Modus** laufen, da Swing-Fenster erstellt werden
- In REST muss der übergebene Code eine `call()`-kompatible Methode beinhalten (`return` nicht vergessen!)
- Die Compiler-Logik basiert auf Java 8 (`javax.tools.JavaCompiler`)

---

## 📜 Lizenz

Private Nutzung und Erweiterung erlaubt. Kein Hosting des REST-Endpunkts ohne geeignete Sicherheitsmaßnahmen empfohlen.