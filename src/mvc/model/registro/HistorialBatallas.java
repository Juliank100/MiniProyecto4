package mvc.model.registro;

import java.util.*;

/**
 * Historial de batallas completadas
 * 
 * ESTRUCTURA DE DATOS UTILIZADA: LinkedList (como Queue)
 * JUSTIFICACIÓN:
 * - Las batallas se agregan al final (encolar)
 * - Se pueden consultar en orden cronológico
 * - Permite limitar el tamaño (eliminar las más antiguas)
 * - O(1) para agregar al final
 * - Iteración eficiente para consultas
 */
public class HistorialBatallas {
    private final Queue<RegistroBatalla> batallas;
    private final int CAPACIDAD_MAXIMA = 50;
    private int contadorId;
    
    public HistorialBatallas() {
        this.batallas = new LinkedList<>();
        this.contadorId = 1;
    }
    
    /**
     * Registra una nueva batalla completada
     */
    public void registrarBatalla(boolean victoria, int turnos, 
                                 List<String> heroes, List<String> enemigos) {
        
        int exp = victoria ? (50 * enemigos.size()) : 10;
        int oro = victoria ? (100 * enemigos.size()) : 0;
        
        RegistroBatalla registro = new RegistroBatalla(
            contadorId++,
            victoria,
            turnos,
            heroes.toArray(new String[0]),
            enemigos.toArray(new String[0]),
            exp,
            oro
        );
        
        batallas.offer(registro); // Agregar al final de la cola
        
        // Limitar tamaño
        if (batallas.size() > CAPACIDAD_MAXIMA) {
            batallas.poll(); // Eliminar la más antigua
        }
        
        System.out.println("📊 Batalla registrada: " + registro);
    }
    
    /**
     * Obtiene estadísticas del jugador
     */
    public EstadisticasJugador obtenerEstadisticas() {
        int victorias = 0;
        int derrotas = 0;
        int expTotal = 0;
        int oroTotal = 0;
        int turnosTotal = 0;
        
        for (RegistroBatalla batalla : batallas) {
            if (batalla.isVictoria()) {
                victorias++;
                expTotal += batalla.getExperienciaGanada();
                oroTotal += batalla.getOroGanado();
            } else {
                derrotas++;
            }
            turnosTotal += batalla.getTurnosTranscurridos();
        }
        
        return new EstadisticasJugador(
            victorias, derrotas, expTotal, oroTotal, 
            batallas.size() > 0 ? turnosTotal / batallas.size() : 0
        );
    }
    
    /**
     * Lista las últimas N batallas
     */
    public List<RegistroBatalla> obtenerUltimasBatallas(int cantidad) {
        List<RegistroBatalla> resultado = new ArrayList<>(batallas);
        Collections.reverse(resultado); // Más reciente primero
        
        if (resultado.size() > cantidad) {
            return resultado.subList(0, cantidad);
        }
        return resultado;
    }
    
    /**
     * Obtiene todas las batallas
     */
    public List<RegistroBatalla> obtenerTodasBatallas() {
        return new ArrayList<>(batallas);
    }
    
    public int getTotalBatallas() {
        return batallas.size();
    }
    
    // Clase interna para estadísticas
    public static class EstadisticasJugador {
        public final int victorias;
        public final int derrotas;
        public final int experienciaTotal;
        public final int oroTotal;
        public final int promedioTurnos;
        
        public EstadisticasJugador(int victorias, int derrotas, int exp, int oro, int promTurnos) {
            this.victorias = victorias;
            this.derrotas = derrotas;
            this.experienciaTotal = exp;
            this.oroTotal = oro;
            this.promedioTurnos = promTurnos;
        }
        
        public double getTasaVictoria() {
            int total = victorias + derrotas;
            return total > 0 ? (victorias * 100.0 / total) : 0;
        }
        
        @Override
        public String toString() {
            return String.format("""
                📊 ESTADÍSTICAS DEL JUGADOR
                ═══════════════════════════
                Victorias: %d ✅
                Derrotas: %d ❌
                Tasa de Victoria: %.1f%%
                Experiencia Total: %d EXP
                Oro Total: %d 💰
                Promedio de Turnos: %d
                """, victorias, derrotas, getTasaVictoria(), 
                experienciaTotal, oroTotal, promedioTurnos);
        }
    }
}
