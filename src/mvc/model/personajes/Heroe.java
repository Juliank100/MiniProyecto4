package mvc.model.personajes;

import mvc.model.habilidades.Habilidad;
import mvc.model.items.*;

import java.util.*;

/**
 * Heroe - personaje controlado por el jugador.
 * - Menu de acciones: Atacar, Usar habilidad, Usar ítem.
 * - En esta versión, al usar ítems el héroe interactúa con el InventarioGrupo (compartido).
 *
 * NOTA: se sobrescribe tomarTurno con un método que recibe InventarioGrupo para acceso.
 */
public class Heroe extends Personaje {

    public Heroe(String nombre, int hp, int mp, int ataque, int defensa, int velocidad) {
        super(nombre, hp, mp, ataque, defensa, velocidad);
    }

    /**
     * Versión para la batalla que recibe inventario compartido.
     * Los héroes pueden elegir usar ítems del inventario del grupo.
     */
    public void tomarTurno(List<Personaje> aliados, List<Personaje> enemigos, Scanner sc, InventarioGrupo inventario) {
        if (!estaVivo()) return;

        boolean puede = procesarEstadosAntesDeActuar();
        if (!puede) return;

        System.out.println("\nTurno de " + nombre + " (HP: " + hpActual + " MP: " + mpActual + ")");
        System.out.println("1. Atacar");
        System.out.println("2. Usar habilidad");
        System.out.println("3. Usar ítem");
        System.out.print("Elige acción: ");
        int opcion = sc.nextInt();

        switch (opcion) {
            case 1 -> atacar(enemigos, sc);
            case 2 -> usarHabilidad(enemigos, sc);
            case 3 -> usarItem(aliados, enemigos, sc, inventario);
            default -> System.out.println("Opción inválida.");
        }
    }

    /** Método legacy para compatibilidad con Enemigo.tomarTurno signature override */
    @Override
    public void tomarTurno(List<Personaje> aliados, List<Personaje> enemigos, Scanner sc) {
        // Si se invoca sin inventario, llamamos con inventario vacío temporal.
        tomarTurno(aliados, enemigos, sc, new InventarioGrupo());
    }

    /** Atacar: permite seleccionar objetivo entre enemigos vivos. */
    private void atacar(List<Personaje> enemigos, Scanner sc) {
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : enemigos) if (p.estaVivo()) vivos.add(p);
        if (vivos.isEmpty()) return;

        // Mostrar opciones de objetivo
        System.out.println("Seleccione objetivo:");
        for (int i = 0; i < vivos.size(); i++) {
            System.out.println((i + 1) + ". " + vivos.get(i).getNombre() + " (HP: " + vivos.get(i).hpActual + ")");
        }
        int eleccion = sc.nextInt();
        if (eleccion < 1 || eleccion > vivos.size()) {
            System.out.println("Elección inválida.");
            return;
        }
        Personaje objetivo = vivos.get(eleccion - 1);
        System.out.println(nombre + " ataca a " + objetivo.getNombre());
        objetivo.recibirDaño(ataque);
    }

    /** Usar habilidad: elige habilidad y objetivo entre enemigos vivos. */
    private void usarHabilidad(List<Personaje> enemigos, Scanner sc) {
        if (habilidades.isEmpty()) {
            System.out.println("No tienes habilidades.");
            return;
        }
        System.out.println("Elige una habilidad:");
        for (int i = 0; i < habilidades.size(); i++) {
            System.out.println((i + 1) + ". " + habilidades.get(i).getNombre());
        }
        int eleccion = sc.nextInt();
        if (eleccion < 1 || eleccion > habilidades.size()) {
            System.out.println("Elección inválida.");
            return;
        }
        Habilidad h = habilidades.get(eleccion - 1);

        // Si la habilidad puede usarse en aliado (curación), preguntamos; por simplicidad
        // aquí asumimos que la mayoría apuntan a enemigos salvo Curacion que cura al usuario.
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : enemigos) if (p.estaVivo()) vivos.add(p);
        if (vivos.isEmpty()) return;

        System.out.println("Selecciona objetivo:");
        for (int i = 0; i < vivos.size(); i++) {
            System.out.println((i + 1) + ". " + vivos.get(i).getNombre());
        }
        int objetivoIdx = sc.nextInt();
        if (objetivoIdx < 1 || objetivoIdx > vivos.size()) {
            System.out.println("Elección inválida.");
            return;
        }
        Personaje objetivo = vivos.get(objetivoIdx - 1);
        
        // Verificar y consumir MP
        if (consumirMP(h.costoMP)) {
            h.ejecutar(this, objetivo);
        } else {
            System.out.println("⚠️ No tienes suficiente MP para usar " + h.getNombre());
        }
    }

    /**
     * Usar ítem: muestra inventario compartido, permite elegir ítem y objetivo (aliado o sí mismo).
     * - InventarioGrupo maneja cantidades y el uso del ítem real.
     */
    private void usarItem(List<Personaje> aliados, List<Personaje> enemigos, Scanner sc, InventarioGrupo inventario) {
        if (inventario.estaVacio()) {
            System.out.println("El inventario del grupo está vacío.");
            return;
        }

        // Mostrar ítems disponibles
        System.out.println("Ítems disponibles:");
        List<Item> items = inventario.listarItemsUnicos();
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getNombre() + " x" + inventario.getCantidad(items.get(i)));
        }
        System.out.print("Elige ítem (número): ");
        int idx = sc.nextInt();
        if (idx < 1 || idx > items.size()) {
            System.out.println("Selección inválida.");
            return;
        }
        Item elegido = items.get(idx - 1);

        // Decidir sobre quién usarlo: aliados o uno mismo o enemigos (según ítem).
        // Preguntamos si desea usar en aliado o en sí mismo.
        System.out.println("Usar en:");
        System.out.println("1. Uno de los héroes");
        System.out.println("2. Un enemigo");
        System.out.print("Elige objetivo: ");
        int tipo = sc.nextInt();

        Personaje objetivo = null;
        if (tipo == 1) {
            // Seleccionar héroe objetivo
            List<Personaje> vivosAliados = new ArrayList<>();
            for (Personaje p : aliados) if (p.estaVivo()) vivosAliados.add(p);
            for (int i = 0; i < vivosAliados.size(); i++) {
                System.out.println((i + 1) + ". " + vivosAliados.get(i).getNombre() + " (HP: " + vivosAliados.get(i).hpActual + ")");
            }
            int sel = sc.nextInt();
            if (sel < 1 || sel > vivosAliados.size()) {
                System.out.println("Selección inválida.");
                return;
            }
            objetivo = vivosAliados.get(sel - 1);
        } else if (tipo == 2) {
            // Seleccionar enemigo objetivo
            List<Personaje> vivosEnemigos = new ArrayList<>();
            for (Personaje p : enemigos) if (p.estaVivo()) vivosEnemigos.add(p);
            for (int i = 0; i < vivosEnemigos.size(); i++) {
                System.out.println((i + 1) + ". " + vivosEnemigos.get(i).getNombre() + " (HP: " + vivosEnemigos.get(i).hpActual + ")");
            }
            int sel = sc.nextInt();
            if (sel < 1 || sel > vivosEnemigos.size()) {
                System.out.println("Selección inválida.");
                return;
            }
            objetivo = vivosEnemigos.get(sel - 1);
        } else {
            System.out.println("Opción inválida.");
            return;
        }

        // Usar el ítem (InventarioGrupo gestiona la cantidad y la ejecución)
        boolean usado = inventario.usarItem(elegido, this, objetivo);
        if (usado) {
            System.out.println("Se usó " + elegido.getNombre() + " sobre " + objetivo.getNombre() + ".");
        } else {
            System.out.println("No se pudo usar el ítem.");
        }
    }
}