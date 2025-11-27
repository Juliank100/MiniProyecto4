package mvc.view.gui;

import mvc.controller.GameController;

import javax.swing.SwingUtilities;


/**
 * GUIAdapter - adaptador ligero que abre la VentanaPrincipal existente.
 *
 * Importante: VentanaPrincipal es la ventana GUI que ya existe en el proyecto.
 * Este adaptador **no altera** la lógica interna de esa clase; solo la instancia
 * para que la vista GUI pueda coexistir con el controlador MVC.
 *
 * Si quieres enlazar eventos de botones (p.ej. iniciar combate) con GameController,
 * tendrías que exponer listeners en VentanaPrincipal o modificarla. Para evitar
 * tocar su código, este adaptador solo abre la ventana.
 */
public class GUIAdapter {
    private VentanaPrincipal ventanaPrincipal;

    public GUIAdapter(GameController c) {
    }
    public void show() {
        SwingUtilities.invokeLater(() -> {
            try {
                ventanaPrincipal = new VentanaPrincipal();
                ventanaPrincipal.setVisible(true);
                // Si VentanaPrincipal tiene métodos públicos para registrar listeners,
                // aquí es donde los registraríamos, por ejemplo:
                // vp.setOnStartListener( () -> controller.startBattleAsync(new Scanner(System.in)) );
                //
                // Pero como no queremos modificar la lógica original, lo dejamos instanciado.
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
