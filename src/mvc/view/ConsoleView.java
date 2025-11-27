package mvc.view;

import mvc.controller.GameController;

import java.util.Scanner;

/**
 * ConsoleView - ejemplo de vista que usa GameController para iniciar flujo por consola.
 * Esto no cambia la lógica: simplemente entrega un punto de entrada "vista".
 */
public class ConsoleView {
    private final GameController controller;

    public ConsoleView(GameController controller) {
        this.controller = controller;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== MVC - Modo Consola ===");
        System.out.println("1) Iniciar batalla (consola)");
        System.out.println("2) Salir");
        System.out.print("> ");
        int opt = -1;
        try {
            opt = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Opción inválida, saliendo.");
            sc.close();
            return;
        }

        switch(opt) {
            case 1:
                System.out.println("Iniciando batalla (se delega a la lógica existente)...");
                // Esta llamada delega a Batalla a través del modelo.
                boolean ok = controller.startBattleConsole(sc);
                System.out.println("Batalla finalizada. OK=" + ok);
                break;
            default:
                System.out.println("Saliendo.");
        }
        sc.close();
        controller.shutdown();
    }
}
