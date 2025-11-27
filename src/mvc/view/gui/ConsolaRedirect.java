package mvc.view.gui;

import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * ConsolaRedirect - Redirige System.out al JTextArea del log de combate
 * Esto permite que todos los System.out.println de las clases Personaje,
 * Habilidad, Item, etc. aparezcan automáticamente en la interfaz gráfica.
 */
public class ConsolaRedirect extends OutputStream {
    private JTextArea textArea;
    private StringBuilder buffer;

    public ConsolaRedirect(JTextArea textArea) {
        this.textArea = textArea;
        this.buffer = new StringBuilder();
    }

    @Override
    public void write(int b) {
        // Agregar byte al buffer
        buffer.append((char) b);
        
        // Si es salto de línea, volcar al JTextArea
        if (b == '\n') {
            final String text = buffer.toString();
            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
            buffer.setLength(0); // Limpiar buffer
        }
    }

    /**
     * Método estático para configurar la redirección
     */
    public static void configurarRedireccion(JTextArea textArea) {
        ConsolaRedirect redirect = new ConsolaRedirect(textArea);
        PrintStream ps = new PrintStream(redirect, true);
        System.setOut(ps);
    }

    /**
     * Restaurar System.out original
     */
    public static void restaurarSalida(PrintStream original) {
        System.setOut(original);
    }
}