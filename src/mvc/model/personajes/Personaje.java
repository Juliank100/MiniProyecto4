package mvc.model.personajes;

import mvc.model.estados.EstadoAlterado;
import mvc.model.habilidades.Habilidad;

import java.util.*;

/**
 * Personaje - Clase base con estado DORMIDO actualizado
 * 
 * CAMBIO IMPORTANTE:
 * - Estado DORMIDO ahora tiene 50% de probabilidad de despertar cada turno
 * - Si no despierta, pierde el turno completamente
 */
public abstract class Personaje {
    protected String nombre;
    protected int hpMax, mpMax;
    protected int ataque, defensa;
    protected int velocidad;
    protected int hpActual, mpActual;
    protected List<Habilidad> habilidades;

    protected EstadoAlterado estado = EstadoAlterado.NORMAL;
    protected int estadoDuracion = 0;

    protected static final Random RAND = new Random();

    public Personaje(String nombre, int hp, int mp, int ataque, int defensa, int velocidad) {
        this.nombre = nombre;
        this.hpMax = hp;
        this.mpMax = mp;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.hpActual = hp;
        this.mpActual = mp;
        this.habilidades = new ArrayList<>();
    }

    public boolean estaVivo() {
        return hpActual > 0;
    }

    public void recibirDaño(int cantidad) {
        int daño = Math.max(1, cantidad - defensa);
        hpActual -= daño;
        if (hpActual < 0) hpActual = 0;
        System.out.println(nombre + " recibe " + daño + " puntos de daño. (HP: " + hpActual + "/" + hpMax + ")");
        
        if (hpActual == 0) {
            System.out.println("💀 ¡" + nombre + " ha caído en combate!");
        }
    }

    public void curar(int cantidad) {
        int hpAnterior = hpActual;
        hpActual = Math.min(hpMax, hpActual + cantidad);
        int curado = hpActual - hpAnterior;
        System.out.println(nombre + " recupera " + curado + " HP. (HP: " + hpActual + "/" + hpMax + ")");
    }

    public void restaurarMP(int cantidad) {
        int mpAnterior = mpActual;
        mpActual = Math.min(mpMax, mpActual + cantidad);
        int restaurado = mpActual - mpAnterior;
        System.out.println(nombre + " recupera " + restaurado + " MP. (MP: " + mpActual + "/" + mpMax + ")");
    }

    /**
     * Aplica un nuevo estado alterado
     */
    public void aplicarEstado(EstadoAlterado nuevo) {
        if (estado != EstadoAlterado.NORMAL) {
            System.out.println(nombre + " ya tiene un estado (" + estado + "). No se aplica " + nuevo + ".");
            return;
        }
        estado = nuevo;

        // Duración según tipo de estado
        switch (nuevo) {
            case VENENO -> estadoDuracion = 3;
            case PARALIZADO -> estadoDuracion = 3;
            case DORMIDO -> estadoDuracion = 4; // Aumentada para el nuevo sistema
            default -> estadoDuracion = 0;
        }
        System.out.println("⚠️ " + nombre + " sufre el estado " + nuevo + " durante " + estadoDuracion + " turnos.");
    }

    /**
     * Procesa los efectos del estado alterado ANTES de actuar
     * 
     * CAMBIO IMPORTANTE - Estado DORMIDO:
     * - 50% de probabilidad de despertar cada turno
     * - Si no despierta, pierde el turno completamente
     * 
     * @return true si puede actuar, false si pierde el turno
     */
    public boolean procesarEstadosAntesDeActuar() {
        if (!estaVivo()) return false;

        switch (estado) {
            case VENENO -> {
                int daño = Math.max(1, hpMax / 10);
                hpActual -= daño;
                if (hpActual < 0) hpActual = 0;
                System.out.println("🟢 " + nombre + " sufre " + daño + " de daño por veneno. (HP: " + hpActual + ")");
            }
            
            case DORMIDO -> {
                // ⭐ IMPLEMENTACIÓN NUEVA: 50% de probabilidad de despertar
                if (RAND.nextInt(100) < 50) {
                    System.out.println("😴 " + nombre + " se despierta!");
                    estado = EstadoAlterado.NORMAL;
                    estadoDuracion = 0;
                    return true; // Puede actuar
                } else {
                    System.out.println("💤 " + nombre + " está profundamente dormido y no puede actuar este turno.");
                    reducirDuracionEstado();
                    return false; // Pierde el turno
                }
            }
            
            case PARALIZADO -> {
                if (RAND.nextInt(100) < 30) {
                    System.out.println("⚡ " + nombre + " está paralizado y no puede moverse este turno.");
                    reducirDuracionEstado();
                    return false;
                } else {
                    System.out.println("💪 " + nombre + " logra moverse a pesar de la parálisis!");
                }
            }
            
            default -> {}
        }
        
        reducirDuracionEstado();
        return true;
    }

    /**
     * Reduce la duración del estado activo
     */
    private void reducirDuracionEstado() {
        if (estado != EstadoAlterado.NORMAL) {
            estadoDuracion--;
            if (estadoDuracion <= 0) {
                System.out.println("✅ " + nombre + " ya no está afectado por " + estado + ".");
                estado = EstadoAlterado.NORMAL;
                estadoDuracion = 0;
            }
        }
    }

    public boolean puedeActuar() {
        return estado == EstadoAlterado.NORMAL || estado == EstadoAlterado.VENENO;
    }

    /**
     * Permite eliminar un estado alterado específico
     */
    public boolean quitarEstado(EstadoAlterado e) {
        if (estado == e) {
            System.out.println("✨ " + nombre + " se recupera del estado " + e + ".");
            estado = EstadoAlterado.NORMAL;
            estadoDuracion = 0;
            return true;
        }
        return false;
    }

    /**
     * Verifica si el personaje está bajo cierto estado alterado
     */
    public boolean estaEnEstado(EstadoAlterado e) {
        return estado == e;
    }

    public void setEstado(EstadoAlterado nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void setEstadoDuracion(int duracion) {
        this.estadoDuracion = duracion;
    }

    /**
     * ⭐ NUEVO: Consume MP del personaje
     */
    public boolean consumirMP(int cantidad) {
        if (mpActual >= cantidad) {
            mpActual -= cantidad;
            return true;
        }
        return false;
    }

    /**
     * ⭐ NUEVO: Setter para HP actual
     */
    public void setHpActual(int hp) {
        this.hpActual = Math.max(0, Math.min(hp, hpMax));
    }

    /**
     * ⭐ NUEVO: Setter para MP actual
     */
    public void setMpActual(int mp) {
        this.mpActual = Math.max(0, Math.min(mp, mpMax));
    }

    /**
     * ⭐ NUEVO: Obtiene la lista de habilidades
     */
    public List<Habilidad> getHabilidades() {
        return new ArrayList<>(habilidades);
    }

    // Método abstracto
    public abstract void tomarTurno(List<Personaje> aliados, List<Personaje> enemigos, Scanner sc);

    // Getters
    public int getVelocidad() { return velocidad; }
    public String getNombre() { return nombre; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getHpActual() { return hpActual; }
    public int getHpMax() { return hpMax; }
    public int getMpActual() { return mpActual; }
    public int getMpMax() { return mpMax; }
    public EstadoAlterado getEstado() { return estado; }
    public int getEstadoDuracion() { return estadoDuracion; }
    
    public void agregarHabilidad(Habilidad h) { 
        habilidades.add(h);
        System.out.println("📚 " + nombre + " aprende: " + h.getNombre());
    }

    /**
     * Muestra barras de HP y MP junto al estado actual
     */
    public void mostrarEstado() {
        String barraHP = generarBarra(hpActual, hpMax, 20, "♥");
        String barraMP = generarBarra(mpActual, mpMax, 10, "★");
        
        String estadoStr = estado == EstadoAlterado.NORMAL ? "Normal" : estado.toString();
        String icono = this instanceof Heroe ? "⚔️" : (this instanceof MiniBoss ? "👹" : "👾");
        
        System.out.printf("%s %-10s | HP: %-3d/%-3d %-22s | MP: %-3d/%-3d %-12s | Estado: %-10s\n",
                icono, nombre, hpActual, hpMax, barraHP, mpActual, mpMax, barraMP, estadoStr);
    }

    /**
     * Genera una barra visual proporcional al valor actual
     */
    private String generarBarra(int actual, int max, int largo, String simbolo) {
        int relleno = 0;
        if (max > 0) relleno = (int) ((double) actual / max * largo);
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < largo; i++) {
            if (i < relleno) barra.append("█");
            else barra.append("·");
        }
        barra.append("]");
        
        double porcentaje = max > 0 ? (double) actual / max : 0;
        if (porcentaje > 0.5) {
            return barra.toString(); // Verde
        } else if (porcentaje > 0.25) {
            return barra.toString(); // Amarillo
        } else {
            return barra.toString(); // Rojo
        }
    }
}