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


## 🌸 Beispiel: Paint was malen lassen

**Beispiel-Request:**

```json
{
"code": "// PNG auf Desktop erstellen\nString userHome = System.getProperty(\"user.home\");\njava.io.File file = new java.io.File(userHome + \"/Desktop/BlumeLeinwand.png\");\nint width = 1600;\nint height = 1000;\njava.awt.image.BufferedImage img = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);\njava.awt.Graphics2D g = img.createGraphics();\ng.setColor(java.awt.Color.WHITE);\ng.fillRect(0, 0, width, height);\ng.dispose();\njavax.imageio.ImageIO.write(img, \"png\", file);\n\n// Paint öffnen\nRuntime.getRuntime().exec(\"mspaint \\\"\" + file.getAbsolutePath() + \"\\\"\");\nThread.sleep(3000);\n\njava.awt.Robot robot = new java.awt.Robot();\njava.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();\nint cx = screenSize.width / 2;\nint cy = screenSize.height / 2;\n\n// Pinsel aktivieren\nrobot.mouseMove(100, 70);\nThread.sleep(200);\nrobot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nThread.sleep(100);\nrobot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nThread.sleep(200);\n\n// Gelb wählen (Mitte)\nrobot.mouseMove(1200, 820);\nThread.sleep(100);\nrobot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nThread.sleep(100);\nrobot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nThread.sleep(100);\n\n// Gelber Kreis in der Mitte\nint radius = 30;\nrobot.mouseMove(cx + radius, cy);\nrobot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nfor (int i = 0; i <= 360; i++) {\n    double angle = Math.toRadians(i);\n    int x = cx + (int)(radius * Math.cos(angle));\n    int y = cy + (int)(radius * Math.sin(angle));\n    robot.mouseMove(x, y);\n    Thread.sleep(1);\n}\nrobot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nThread.sleep(200);\n\n// Rot wählen (Blüten)\nrobot.mouseMove(1180, 820);\nThread.sleep(100);\nrobot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nThread.sleep(100);\nrobot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nThread.sleep(100);\n\nint[][] petals = {\n  {cx, cy - 80}, {cx + 80, cy}, {cx, cy + 80}, {cx - 80, cy}\n};\nfor (int[] p : petals) {\n    int px = p[0];\n    int py = p[1];\n    robot.mouseMove(px + 40, py);\n    robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\n    for (int i = 0; i <= 360; i++) {\n        double a = Math.toRadians(i);\n        int x = px + (int)(40 * Math.cos(a));\n        int y = py + (int)(40 * Math.sin(a));\n        robot.mouseMove(x, y);\n        Thread.sleep(1);\n    }\n    robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\n    Thread.sleep(200);\n}\n\n// Grün wählen (Stängel)\nrobot.mouseMove(1150, 820);\nThread.sleep(100);\nrobot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nThread.sleep(100);\nrobot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nThread.sleep(100);\n\n// Stängel nach unten zeichnen\nrobot.mouseMove(cx, cy + 80);\nrobot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\nfor (int y = cy + 80; y <= cy + 200; y++) {\n    robot.mouseMove(cx, y);\n    Thread.sleep(2);\n}\nrobot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);\n\nreturn \"Blume zentriert gemalt!\";"
}
```

**Antwort:**

```json
{
  "output": "Blume zentriert gemalt!"
}
```

➡ Dadurch wird mspaint geöffnet und ein vorher erstelltes Bild bemalt.

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