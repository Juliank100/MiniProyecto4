package mvc;

import mvc.view.gui.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * MainMVC - Punto de entrada para la versión GUI del juego
 * 
 * Este es el main que debes ejecutar para iniciar el juego con interfaz gráfica.
 * 
 * Para compilar y ejecutar:
 *   javac -d bin -sourcepath src $(find src -name "*.java")
 *   java -cp bin mvc.MainMVC
 */
public class MainMVC {
    public static void main(String[] args) {
        // Configurar Look & Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("✅ Look & Feel configurado correctamente");
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo establecer Look & Feel: " + e.getMessage());
            System.err.println("   Usando Look & Feel por defecto...");
        }
        
        // Iniciar la GUI en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            System.out.println("🎮 Iniciando Dragon Quest VIII - Simulador de Combate");
            System.out.println("📁 Cargando recursos...");
            
            try {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true);
                System.out.println("✅ Ventana principal iniciada correctamente");
            } catch (Exception e) {
                System.err.println("❌ Error al iniciar la ventana principal:");
                e.printStackTrace();
                
                // Mostrar diálogo de error
                javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "Error al iniciar el juego:\n" + e.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
}