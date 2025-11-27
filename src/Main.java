// ===== Main.java =====
//para ejecutar el juego desde consola
import java.util.Scanner;

import mvc.model.combate.Batalla;

/**
 * Main - punto de entrada del programa.
 *
 * Responsabilidad:
 * - Mostrar un menú simple (Iniciar combate / Salir).
 * - Crear un único Scanner compartido para evitar cerrar System.in accidentalmente.
 * - Inicializar Batalla pasándole el Scanner para su uso durante todo el juego.
 *
 * Diseño:
 * - Mantener el control de flujo del programa aquí facilita reusar Batalla sin preocuparse
 *   por la apertura/cierre del flujo de entrada.
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n=== DRAGON QUEST VIII - Simulación de Combate ===");
            System.out.println("1. Iniciar combate");
            System.out.println("2. Salir");
            System.out.print("Elige una opción: ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1 -> {
                    // Se crea una instancia de Batalla pasando el scanner para que Batalla
                    // pueda leer elecciones de los héroes desde consola sin crear otro Scanner.
                    Batalla batalla = new Batalla(sc);
                    batalla.iniciarCombate();
                }
                case 2 -> System.out.println("¡Hasta luego, aventurero!");
                default -> System.out.println("Opción inválida. Intenta de nuevo.");
            }

        } while (opcion != 2);

        // Cerramos el scanner al final del programa (buenas prácticas).
        sc.close();
    }
}
