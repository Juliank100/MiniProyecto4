package mvc.model.personajes;

import java.util.*;

import mvc.model.habilidades.Habilidad;

/**
 * MiniBoss - Enemigo jefe especial con características mejoradas
 * 
 * Características especiales:
 * - Estadísticas superiores (1.5x más HP, ataque y defensa)
 * - Puede actuar DOS VECES por turno cuando tiene menos del 50% HP
 * - Tiene habilidades más poderosas
 * - Mayor velocidad que enemigos normales
 * - Resistencia a estados alterados (50% de probabilidad de resistir)
 */
public class MiniBoss extends Enemigo {
    private String comportamiento;
    private boolean defendiendo = false;
    private boolean modoFurioso = false; // Se activa con menos de 50% HP
    private int turnosActuados = 0;
    
    // Multiplicadores especiales para jefes
    private static final double MULTIPLICADOR_HP = 1.8;
    private static final double MULTIPLICADOR_ATAQUE = 1.5;
    private static final double MULTIPLICADOR_DEFENSA = 1.3;
    private static final double UMBRAL_FURIA = 0.5; // 50% HP
    private static final int RESISTENCIA_ESTADO = 50; // 50% de resistir estados

    public MiniBoss(String nombre, int hp, int mp, int ataque, int defensa, int velocidad, String comportamiento) {
        super(nombre, 
              (int)(hp * MULTIPLICADOR_HP), 
              mp, 
              (int)(ataque * MULTIPLICADOR_ATAQUE), 
              (int)(defensa * MULTIPLICADOR_DEFENSA), 
              velocidad + 5,// +5 velocidad extra
              comportamiento);
        
        this.comportamiento = comportamiento;
        
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║  ⚠️  ¡HA APARECIDO UN MINI JEFE!  ⚠️          ║");
        System.out.println("║  " + nombre + " - Enemigo poderoso              ║");
        System.out.println("╚════════════════════════════════════════════════╝");
    }

    @Override
    public void tomarTurno(List<Personaje> aliados, List<Personaje> enemigos, Scanner sc) {
        if (!estaVivo()) return;

        // Resetear defensa si estaba defendiendo
        if (defendiendo) {
            defensa -= 5;
            defendiendo = false;
        }

        boolean puede = procesarEstadosAntesDeActuar();
        if (!puede) return;

        // Verificar si entra en modo furioso
        if (!modoFurioso && hpActual < hpMax * UMBRAL_FURIA) {
            activarModoFurioso();
        }

        System.out.println("\n🔥 Turno de " + nombre + " (MINI JEFE)");
        if (modoFurioso) {
            System.out.println("⚡ ¡" + nombre + " está FURIOSO! ¡Puede atacar dos veces!");
        }

        // En modo furioso, actúa dos veces
        int acciones = modoFurioso ? 2 : 1;
        
        for (int i = 0; i < acciones; i++) {
            if (i > 0) {
                System.out.println("⚔️ " + nombre + " continúa su ataque...");
            }
            ejecutarAccion(enemigos);
        }

        turnosActuados++;
    }

    /**
     * Activa el modo furioso del jefe
     */
    private void activarModoFurioso() {
        modoFurioso = true;
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║  💢 " + nombre + " ENTRA EN MODO FURIOSO! 💢     ║");
        System.out.println("║  Su poder aumenta dramáticamente...           ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        
        // Aumentar atributos en modo furioso
        ataque = (int)(ataque * 1.2);
        velocidad += 3;
    }

    /**
     * Ejecuta una acción según el comportamiento del jefe
     */
    private void ejecutarAccion(List<Personaje> enemigos) {
        Random rand = new Random();
        
        switch (comportamiento) {
            case "agresivo" -> comportamientoAgresivoJefe(enemigos, rand);
            case "defensivo" -> comportamientoDefensivoJefe(enemigos, rand);
            case "estratégico" -> comportamientoEstrategicoJefe(enemigos, rand);
            case "evasivo" -> comportamientoEvasivoJefe(enemigos, rand);
            default -> ataquePoderoso(enemigos);
        }
    }

    private void comportamientoAgresivoJefe(List<Personaje> enemigos, Random rand) {
        // 80% usar habilidad más poderosa si tiene MP
        if (!habilidades.isEmpty() && mpActual > 0 && rand.nextInt(100) < 80) {
            usarHabilidadMasPoderosa(enemigos);
        } else {
            ataquePoderoso(enemigos);
        }
    }

    private void comportamientoDefensivoJefe(List<Personaje> enemigos, Random rand) {
        // Si tiene menos del 30% de HP, 70% chance de defenderse
        if (hpActual < hpMax * 0.3 && rand.nextInt(100) < 70) {
            defender();
        } else if (!habilidades.isEmpty() && mpActual > 0 && rand.nextInt(100) < 60) {
            usarHabilidadMasPoderosa(enemigos);
        } else {
            ataquePoderoso(enemigos);
        }
    }

    private void comportamientoEstrategicoJefe(List<Personaje> enemigos, Random rand) {
        // Prioriza habilidades de control y debuffs
        if (!habilidades.isEmpty() && mpActual > 0 && rand.nextInt(100) < 85) {
            usarHabilidadMasPoderosa(enemigos);
        } else {
            ataquePoderoso(enemigos);
        }
    }

    private void comportamientoEvasivoJefe(List<Personaje> enemigos, Random rand) {
        int accion = rand.nextInt(100);
        if (accion < 40) {
            esquivar();
        } else if (accion < 80 && !habilidades.isEmpty() && mpActual > 0) {
            usarHabilidadMasPoderosa(enemigos);
        } else {
            ataquePoderoso(enemigos);
        }
    }

    /**
     * Usa la habilidad más poderosa disponible
     */
    private void usarHabilidadMasPoderosa(List<Personaje> enemigos) {
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : enemigos) if (p.estaVivo()) vivos.add(p);
        if (vivos.isEmpty()) return;

        if (habilidades.isEmpty()) {
            ataquePoderoso(enemigos);
            return;
        }

        // Elegir la primera habilidad (asumiendo que son poderosas)
        Habilidad h = habilidades.get(new Random().nextInt(habilidades.size()));
        
        Personaje objetivo = vivos.get(new Random().nextInt(vivos.size()));
        
        System.out.println("💥 " + nombre + " usa su técnica especial: " + h.getNombre() + "!");
        
        int costoEstimado = 5 + new Random().nextInt(10);
        if (mpActual >= costoEstimado) {
            mpActual -= costoEstimado;
            h.ejecutar(this, objetivo);
        } else {
            System.out.println(nombre + " no tiene suficiente MP. ¡Ataca con furia!");
            ataquePoderoso(enemigos);
        }
    }

    /**
     * Ataque más poderoso que el de enemigos normales
     */
    private void ataquePoderoso(List<Personaje> enemigos) {
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : enemigos) if (p.estaVivo()) vivos.add(p);
        if (vivos.isEmpty()) return;

        // Elegir al objetivo con menos HP (estrategia inteligente)
        Personaje objetivo = vivos.stream()
            .min(Comparator.comparingInt(p -> p.hpActual))
            .orElse(vivos.get(0));
        
        System.out.println("⚔️ " + nombre + " lanza un ATAQUE DEVASTADOR contra " + objetivo.getNombre() + "!");
        
        // Daño aumentado (ataque base + bonificación de jefe)
        int dañoTotal = ataque + new Random().nextInt(10);
        objetivo.recibirDaño(dañoTotal);
    }

    private void defender() {
        System.out.println("🛡️ " + nombre + " toma una postura defensiva. ¡Su defensa aumenta!");
        defensa += 8; // Más que enemigos normales
        defendiendo = true;
    }

    private void esquivar() {
        System.out.println("💨 " + nombre + " se prepara para esquivar con agilidad sorprendente!");
        velocidad += 7; // Más que enemigos normales
    }

    /**
     * Sobrescribe aplicarEstado para agregar resistencia
     */
    @Override
    public void aplicarEstado(mvc.model.estados.EstadoAlterado nuevo) {
        Random rand = new Random();
        
        // 50% de probabilidad de resistir estados alterados
        if (rand.nextInt(100) < RESISTENCIA_ESTADO) {
            System.out.println("💪 ¡" + nombre + " resiste el estado " + nuevo + "! Es muy poderoso.");
            return;
        }
        
        // Si no resiste, aplica el estado normalmente
        System.out.println("⚠️ " + nombre + " es afectado por " + nuevo + ".");
        super.aplicarEstado(nuevo);
    }

    /**
     * Método especial: ataque de área (afecta a varios héroes)
     */
    public void ataqueDeArea(List<Personaje> enemigos) {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║  💥 " + nombre + " USA ATAQUE DE ÁREA! 💥         ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : enemigos) if (p.estaVivo()) vivos.add(p);
        
        // Ataca a todos los enemigos vivos con daño reducido
        for (Personaje objetivo : vivos) {
            int dañoArea = ataque / 2 + new Random().nextInt(5);
            System.out.println("🌊 ¡La onda de choque alcanza a " + objetivo.getNombre() + "!");
            objetivo.recibirDaño(dañoArea);
        }
    }

    public boolean esModoFurioso() {
        return modoFurioso;
    }

    public int getTurnosActuados() {
        return turnosActuados;
    }
}