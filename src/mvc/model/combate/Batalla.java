package mvc.model.combate;

import java.util.*;

import mvc.model.habilidades.*;
import mvc.model.items.*;
import mvc.model.personajes.*;

/**
 * Batalla - Sistema de combate actualizado con Mini Jefe
 * 
 * CAMBIOS IMPORTANTES:
 * - Uno de los 3 enemigos es ahora un MiniBoss
 * - El Mini Jefe tiene estadísticas mejoradas y habilidades especiales
 * - Estado DORMIDO ahora implementado con 50% de probabilidad de despertar
 */
public class Batalla {
    private List<Personaje> heroes;
    private List<Personaje> enemigos;
    private Scanner sc;
    private InventarioGrupo inventario;
    private MiniBoss miniBoss; // Referencia al mini jefe

    public Batalla(Scanner sc) {
        this.sc = sc;
        heroes = new ArrayList<>();
        enemigos = new ArrayList<>();
        inventario = new InventarioGrupo();
        inicializar();
    }

    /**
     * Inicializa personajes, habilidades y el inventario compartido
     * ⭐ AHORA INCLUYE UN MINI JEFE
     */
    private void inicializar() {
        // ========================================
        // HÉROES (4 aventureros de Dragon Quest VIII)
        // ========================================
        
        Heroe heroe = new Heroe("Héroe", 100, 50, 20, 10, 15);
        heroe.agregarHabilidad(new DanioMagico("Bola de Fuego", 10, 30));
        heroe.agregarHabilidad(new Curacion("Curar", 8, 25));
        heroe.agregarHabilidad(new DanioMagico("Gigaslash", 15, 40));
        
        Heroe yangus = new Heroe("Yangus", 120, 30, 25, 15, 10);
        yangus.agregarHabilidad(new GolpeCritico("Hachazo Brutal", 5));
        yangus.agregarHabilidad(new Aturdimiento("Golpe Aturdidor", 8));
        yangus.agregarHabilidad(new Curacion("Primeros Auxilios", 10, 20));

        Heroe jessica = new Heroe("Jessica", 80, 70, 18, 8, 20);
        jessica.agregarHabilidad(new DanioMagico("Rayo", 12, 35));
        jessica.agregarHabilidad(new Veneno("Toxina", 8));
        jessica.agregarHabilidad(new DanioMagico("Kafrizzle", 20, 50));
        jessica.agregarHabilidad(new Dormir("Hipnosis", 10));

        Heroe angelo = new Heroe("Angelo", 90, 60, 22, 12, 18);
        angelo.agregarHabilidad(new Curacion("Bendición", 10, 30));
        angelo.agregarHabilidad(new Paralisis("Toque Sagrado", 10));
        angelo.agregarHabilidad(new CuracionGrupal("Curación Divina", 20, 25));
        angelo.agregarHabilidad(new RemoverEstado("Purificación", 8));

        heroes.add(heroe);
        heroes.add(yangus);
        heroes.add(jessica);
        heroes.add(angelo);

        // ========================================
        // ENEMIGOS (3 NORMALES + 1 MINI JEFE)
        // ========================================
        
        // Enemigo normal 1
        Enemigo fantasma = new Enemigo("Fantasma", 85, 40, 18, 8, 21, "estratégico");
        fantasma.agregarHabilidad(new Dormir("Pesadilla", 5));
        fantasma.agregarHabilidad(new Paralisis("Toque Espectral", 5));
        
        // Enemigo normal 2
        Enemigo slimeMetalico = new Enemigo("Slime Metálico", 50, 30, 15, 25, 30, "evasivo");
        slimeMetalico.agregarHabilidad(new Veneno("Baba Tóxica", 3));
        
        // Enemigo normal 3 - Nuevo enemigo
        Enemigo orcoGuerrero = new Enemigo("Orco Guerrero", 95, 35, 22, 12, 16, "agresivo");
        orcoGuerrero.agregarHabilidad(new GolpeCritico("Hachazo Salvaje", 8));
        orcoGuerrero.agregarHabilidad(new Aturdimiento("Golpe Atronador", 6));

        // ⭐ MINI JEFE (más fuerte que los enemigos normales)
        miniBoss = new MiniBoss("Dragón Oscuro", 120, 40, 25, 12, 18, "agresivo");
        miniBoss.agregarHabilidad(new DanioMagico("Aliento de Fuego", 0, 35));
        miniBoss.agregarHabilidad(new DanioMagico("Llamarada Infernal", 15, 50));
        miniBoss.agregarHabilidad(new Aturdimiento("Rugido Aterrador", 10));

        // Agregar enemigos en orden (mini jefe al final para más drama)
        enemigos.add(fantasma);
        enemigos.add(slimeMetalico);
        enemigos.add(orcoGuerrero);
        enemigos.add(miniBoss); // ⭐ El jefe principal

        // ========================================
        // INVENTARIO COMPARTIDO
        // ========================================
        inventario.agregarItem(new PocionCuracion("Poción pequeña", 30), 5);
        inventario.agregarItem(new PocionCuracion("Poción media", 60), 3);
        inventario.agregarItem(new PocionCuracion("Poción grande", 100), 1);
        inventario.agregarItem(new Antidoto("Antídoto"), 3);
        inventario.agregarItem(new PocionMagia("Éter", 20), 2);
        
        System.out.println("\n💼 Inventario inicial preparado.");
    }

    /**
     * Bucle principal de la batalla con mensajes especiales para el mini jefe
     */
    public void iniciarCombate() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║       ¡COMIENZA LA BATALLA EN EL REINO DE TRODAIN!           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println("\nLos cuatro héroes se preparan para enfrentar a las fuerzas del mal...");
        
        // Mensaje especial si hay un mini jefe
        if (miniBoss != null && miniBoss.estaVivo()) {
            System.out.println("\n⚠️  ¡ADVERTENCIA! Un poderoso enemigo lidera la batalla...");
        }
        
        System.out.println();

        int turno = 1;
        while (true) {
            // Mostrar estado visual
            System.out.println("\n┌───────────────────────────────────────────────────────────────┐");
            System.out.println("│                 TURNO " + turno + " - ESTADO DEL COMBATE              │");
            System.out.println("└───────────────────────────────────────────────────────────────┘");
            
            System.out.println("\n▸ HÉROES:");
            for (Personaje h : heroes) h.mostrarEstado();
            
            System.out.println("\n▸ ENEMIGOS:");
            for (Personaje e : enemigos) e.mostrarEstado();
            System.out.println("═══════════════════════════════════════════════════════════════\n");

            // Comprobar final de combate
            if (todosMuertos(enemigos)) {
                mostrarVictoria();
                break;
            }
            if (todosMuertos(heroes)) {
                mostrarDerrota();
                break;
            }
            
            // Mensaje especial cuando el mini jefe entra en modo furioso
            if (miniBoss != null && miniBoss.estaVivo() && miniBoss.esModoFurioso() && turno > 1) {
                System.out.println("🔥 ¡El " + miniBoss.getNombre() + " está en modo FURIOSO!");
            }

            // Construir lista de turno y ordenar por velocidad
            List<Personaje> orden = new ArrayList<>();
            orden.addAll(heroes);
            orden.addAll(enemigos);
            orden.sort((a, b) -> {
                if (b.getVelocidad() != a.getVelocidad()) return b.getVelocidad() - a.getVelocidad();
                // En caso de empate, los mini jefes van primero
                if (a instanceof MiniBoss) return -1;
                if (b instanceof MiniBoss) return 1;
                if (a instanceof Heroe && b instanceof Enemigo) return -1;
                if (a instanceof Enemigo && b instanceof Heroe) return 1;
                return 0;
            });

            // Ejecutar turnos en orden
            for (Personaje p : orden) {
                if (!p.estaVivo()) continue;

                if (p instanceof Heroe) {
                    ((Heroe) p).tomarTurno(heroes, enemigos, sc, inventario);
                } else {
                    // Enemigos y mini jefes
                    p.tomarTurno(enemigos, heroes, sc);
                }
                
                // Verificar si el combate terminó durante el turno
                if (todosMuertos(enemigos) || todosMuertos(heroes)) break;
            }
            
            turno++;
        }
    }

    /**
     * Verifica si todos en la lista están muertos
     */
    private boolean todosMuertos(List<Personaje> lista) {
        for (Personaje p : lista) {
            if (p.estaVivo()) return false;
        }
        return true;
    }

    /**
     * Muestra mensaje de victoria con mención especial al mini jefe
     */
    private void mostrarVictoria() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                          ¡VICTORIA!                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        
        if (miniBoss != null) {
            System.out.println("\n⭐ ¡Los héroes han derrotado al temible " + miniBoss.getNombre() + "!");
            System.out.println("   El Reino de Trodain está a salvo una vez más.");
        } else {
            System.out.println("\n   Los héroes han salvado el Reino de Trodain.");
        }
        
        System.out.println("\n🏆 RECOMPENSAS:");
        System.out.println("   • 500 Monedas de Oro");
        System.out.println("   • 250 Puntos de Experiencia");
        if (miniBoss != null) {
            System.out.println("   • Tesoro Legendario del Jefe");
        }
        System.out.println();
    }

    /**
     * Muestra mensaje de derrota
     */
    private void mostrarDerrota() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                          DERROTA...                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        
        if (miniBoss != null && miniBoss.estaVivo()) {
            System.out.println("\n💀 El " + miniBoss.getNombre() + " ha resultado ser demasiado poderoso...");
        }
        
        System.out.println("   El Reino de Trodain cae ante las fuerzas del mal.");
        System.out.println("   Pero los héroes volverán más fuertes...\n");
    }
}