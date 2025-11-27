package mvc.controller;

import mvc.model.GameModel;
import mvc.view.gui.GUIAdapter;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * GameController - orquesta acciones entre el modelo y la vista.
 * No modifica la lógica interna: llama al modelo, que delega en Batalla.
 */
public class GameController {
    private final GameModel model;
    private final ExecutorService executor;

    public GameController(GameModel model) {
        this.model = model;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Inicia la batalla en modo consola (bloqueante, se ejecuta en el hilo actual)
     * Si quieres que sea asíncrono, usa startBattleAsync.
     */
    public boolean startBattleConsole(Scanner sc) {
        return model.iniciarBatallaConsola(sc);
    }

    /**
     * Inicia la batalla en un hilo aparte (no recomendable si necesitas interacción
     * por consola desde el hilo principal).
     */
    public void startBattleAsync(final Scanner sc) {
        executor.submit(() -> model.iniciarBatallaConsola(sc));
    }

    /**
     * Conecta la GUI existente: el GUIAdapter crea/abre la ventana existente
     * (VentanaPrincipal) y actúa como adaptador entre eventos de UI y el controlador.
     *
     * Este método no inspecciona ni altera internamente la interfaz, solo la instancia.
     */
    public void attachGui() {
        GUIAdapter gui = new GUIAdapter(this);
        gui.show();
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    // Añade métodos públicos para acciones que necesite la vista:
    // e.g. public void usarItem(int heroeIndex, String itemId) { ... }
    // En la mayoría de casos necesitarás exponer llamadas que deleguen en model.getBatallaRaw()
    // usando los métodos públicos que Batalla ya tenga.
}
