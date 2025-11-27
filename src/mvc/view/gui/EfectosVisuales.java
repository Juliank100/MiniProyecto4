package mvc.view.gui;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * EfectosVisuales - Clase para manejar efectos visuales y sonoros
 * del combate (ataques, curaciones, estados alterados, etc.)
 */
public class EfectosVisuales {
    
    /**
     * Reproduce un efecto de sonido corto (no en loop)
     */
    public static void reproducirSonido(String ruta) {
        try {
            URL url = EfectosVisuales.class.getResource(ruta);
            if (url == null) {
                System.err.println("⚠️ No se encontró el sonido: " + ruta);
                return;
            }
            
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
            
            // Cerrar el clip cuando termine
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("⚠️ Error al reproducir sonido: " + e.getMessage());
        }
    }
    
    /**
     * Crea un efecto de "flash" en un panel
     */
    public static void flashPanel(JPanel panel, Color color, int duracionMs) {
        Color originalBg = panel.getBackground();
        panel.setBackground(color);
        
        Timer timer = new Timer(duracionMs, e -> panel.setBackground(originalBg));
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Anima un cambio de valor en una barra de progreso
     */
    public static void animarBarra(JProgressBar barra, int valorInicial, int valorFinal, int duracionMs) {
        int pasos = 20;
        int intervalo = duracionMs / pasos;
        int diferencia = valorFinal - valorInicial;
        int incremento = diferencia / pasos;
        
        Timer timer = new Timer(intervalo, null);
        final int[] valorActual = {valorInicial};
        final int[] contador = {0};
        
        timer.addActionListener(e -> {
            contador[0]++;
            
            if (contador[0] >= pasos) {
                barra.setValue(valorFinal);
                timer.stop();
            } else {
                valorActual[0] += incremento;
                barra.setValue(valorActual[0]);
            }
        });
        
        timer.start();
    }
    
    /**
     * Muestra un texto flotante sobre un componente
     */
    public static void mostrarTextoFlotante(JComponent componente, String texto, Color color) {
        JWindow ventana = new JWindow();
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Monospaced", Font.BOLD, 20));
        label.setForeground(color);
        label.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 180));
        
        ventana.add(label);
        ventana.pack();
        
        // Posicionar sobre el componente
        Point loc = componente.getLocationOnScreen();
        int x = loc.x + (componente.getWidth() - ventana.getWidth()) / 2;
        int y = loc.y + (componente.getHeight() - ventana.getHeight()) / 2;
        ventana.setLocation(x, y);
        
        ventana.setVisible(true);
        
        // Fade out y cerrar
        Timer timer = new Timer(1500, e -> {
            ventana.setVisible(false);
            ventana.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Efecto de daño: flash rojo + texto
     */
    public static void efectoDanio(JPanel panel, int cantidad) {
        flashPanel(panel, new Color(255, 0, 0, 100), 300);
        mostrarTextoFlotante(panel, "-" + cantidad + " HP", Color.RED);
        reproducirSonido("/sonidos/hit.wav");
    }
    
    /**
     * Efecto de curación: flash verde + texto
     */
    public static void efectoCuracion(JPanel panel, int cantidad) {
        flashPanel(panel, new Color(0, 255, 0, 100), 300);
        mostrarTextoFlotante(panel, "+" + cantidad + " HP", Color.GREEN);
        reproducirSonido("/sonidos/heal.wav");
    }
    
    /**
     * Efecto de estado alterado: flash amarillo
     */
    public static void efectoEstadoAlterado(JPanel panel, String estado) {
        flashPanel(panel, new Color(255, 255, 0, 100), 400);
        mostrarTextoFlotante(panel, estado, Color.YELLOW);
        reproducirSonido("/sonidos/status.wav");
    }
    
    /**
     * Efecto crítico: flash blanco + texto grande
     */
    public static void efectoCritico(JPanel panel) {
        flashPanel(panel, new Color(255, 255, 255, 150), 500);
        mostrarTextoFlotante(panel, "¡CRÍTICO!", Color.ORANGE);
        reproducirSonido("/sonidos/critical.wav");
    }
}