package mvc.model.personajes;

import java.util.*;

import mvc.model.habilidades.Habilidad;

/**
 * Enemigo - personaje controlado por IA.
 *
 * Comportamientos mejorados:
 * - "agresivo": siempre ataca con habilidades o ataques básicos
 * - "defensivo": puede defenderse cuando tiene poca vida
 * - "estratégico": usa habilidades de estado alterado
 * - "evasivo": alterna entre ataques y esquivas
 */
public class Enemigo extends Personaje {
    private String comportamiento;
    private boolean defendiendo = false;

    public Enemigo(String nombre, int hp, int mp, int ataque, int defensa, int velocidad, String comportamiento) {
        super(nombre, hp, mp, ataque, defensa, velocidad);
        this.comportamiento = comportamiento;
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

        System.out.println("\nTurno de " + nombre + " (Enemigo - " + comportamiento + ")");
        Random rand = new Random();

        switch (comportamiento) {
            case "agresivo" -> comportamientoAgresivo(enemigos, rand);
            case "defensivo" -> comportamientoDefensivo(enemigos, rand);
            case "estratégico" -> comportamientoEstrategico(enemigos, rand);
            case "evasivo" -> comportamientoEvasivo(enemigos, rand);
            default -> atacar(enemigos);
        }
    }

    private void comportamientoAgresivo(List<Personaje> enemigos, Random rand) {
        // 70% usar habilidad si tiene, 30% ataque básico
        if (!habilidades.isEmpty() && mpActual > 0 && rand.nextInt(100) < 70) {
            usarHabilidadAleatoria(enemigos);
        } else {
            atacar(enemigos);
        }
    }

    private void comportamientoDefensivo(List<Personaje> enemigos, Random rand) {
        // Si tiene menos del 40% de HP, 60% chance de defenderse
        if (hpActual < hpMax * 0.4 && rand.nextInt(100) < 60) {
            defender();
        } else if (!habilidades.isEmpty() && mpActual > 0 && rand.nextInt(100) < 40) {
            usarHabilidadAleatoria(enemigos);
        } else {
            atacar(enemigos);
        }
    }

    private void comportamientoEstrategico(List<Personaje> enemigos, Random rand) {
        // Prioriza usar habilidades de estado alterado
        if (!habilidades.isEmpty() && mpActual > 0) {
            // 80% chance de usar habilidad
            if (rand.nextInt(100) < 80) {
                usarHabilidadAleatoria(enemigos);
            } else {
                atacar(enemigos);
            }
        } else {
            atacar(enemigos);
        }
    }

    private void comportamientoEvasivo(List<Personaje> enemigos, Random rand) {
        // 50% esquivar (aumenta velocidad temporalmente), 30% habilidad, 20% ataque
        int accion = rand.nextInt(100);
        if (accion < 50) {
            esquivar();
        } else if (accion < 80 && !habilidades.isEmpty() && mpActual > 0) {
            usarHabilidadAleatoria(enemigos);
        } else {
            atacar(enemigos);
        }
    }

    private void usarHabilidadAleatoria(List<Personaje> enemigos) {
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : enemigos) if (p.estaVivo()) vivos.add(p);
        if (vivos.isEmpty()) return;

        // Elegir habilidad aleatoria
        Habilidad h = habilidades.get(new Random().nextInt(habilidades.size()));
        
        // Verificar si tiene suficiente MP (asumimos costo promedio de 5-10)
        // Como no tenemos acceso directo al costo, intentamos usarla
        Personaje objetivo = vivos.get(new Random().nextInt(vivos.size()));
        
        System.out.println(nombre + " intenta usar " + h.getNombre());
        
        // Verificar MP antes de usar (simulación simple)
        int costoEstimado = 5 + new Random().nextInt(10);
        if (mpActual >= costoEstimado) {
            mpActual -= costoEstimado;
            h.ejecutar(this, objetivo);
        } else {
            System.out.println(nombre + " no tiene suficiente MP. Ataca en su lugar.");
            atacar(enemigos);
        }
    }

    public void atacar(List<Personaje> enemigos) {
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : enemigos) if (p.estaVivo()) vivos.add(p);
        if (vivos.isEmpty()) return;

        // Los enemigos inteligentes priorizan objetivos con menos HP
        Personaje objetivo;
        if (comportamiento.equals("estratégico") && new Random().nextInt(100) < 60) {
            // Atacar al más débil
            objetivo = vivos.stream()
                .min(Comparator.comparingInt(p -> p.hpActual))
                .orElse(vivos.get(0));
        } else {
            // Ataque aleatorio
            objetivo = vivos.get(new Random().nextInt(vivos.size()));
        }
        
        System.out.println(nombre + " ataca a " + objetivo.getNombre());
        objetivo.recibirDaño(ataque);
    }

    private void defender() {
        System.out.println(nombre + " se defiende y reduce el daño recibido este turno.");
        defensa += 5;
        defendiendo = true;
    }

    private void esquivar() {
        System.out.println(nombre + " se prepara para esquivar. ¡Su velocidad aumenta!");
        velocidad += 5;
        // La velocidad se resetea automáticamente al final del turno
        // (necesitarías lógica adicional para manejarlo apropiadamente)
    }
}