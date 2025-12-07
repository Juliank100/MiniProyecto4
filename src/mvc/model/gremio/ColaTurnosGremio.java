package mvc.model.gremio;

import java.util.*;

/**
 * Sistema de turnos del gremio con prioridades
 * 
 * ESTRUCTURA DE DATOS UTILIZADA: PriorityQueue
 * JUSTIFICACIÓN:
 * - Atiende solicitudes por prioridad (no solo FIFO)
 * - Misiones urgentes se atienden antes que consultas rutinarias
 * - O(log n) para insertar y extraer el elemento de mayor prioridad
 * - Heap binario interno optimizado para este propósito
 * - Perfecto para sistemas de atención con niveles de urgencia
 */
public class ColaTurnosGremio {
    private final PriorityQueue<SolicitudGremio> colaPrioridad;
    private int contadorNumero;
    private final List<SolicitudGremio> historialAtendidos;
    
    public ColaTurnosGremio() {
        this.colaPrioridad = new PriorityQueue<>();
        this.contadorNumero = 1;
        this.historialAtendidos = new ArrayList<>();
    }
    
    /**
     * Agrega una nueva solicitud a la cola
     */
    public void agregarSolicitud(String aventurero, 
                                SolicitudGremio.TipoSolicitud tipo, 
                                SolicitudGremio.PrioridadSolicitud prioridad) {
        
        SolicitudGremio solicitud = new SolicitudGremio(
            contadorNumero++, aventurero, tipo, prioridad
        );
        
        colaPrioridad.offer(solicitud);
        
        System.out.println("➕ Nueva solicitud agregada:");
        System.out.println("   " + solicitud);
        System.out.println("   📊 Solicitudes en espera: " + colaPrioridad.size());
    }
    
    /**
     * Atiende la siguiente solicitud (mayor prioridad)
     */
    public SolicitudGremio atenderSiguiente() {
        if (colaPrioridad.isEmpty()) {
            System.out.println("✅ No hay solicitudes pendientes");
            return null;
        }
        
        SolicitudGremio solicitud = colaPrioridad.poll();
        historialAtendidos.add(solicitud);
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("🎯 ATENDIENDO SOLICITUD:");
        System.out.println("   " + solicitud);
        System.out.println("═══════════════════════════════════════\n");
        
        return solicitud;
    }
    
    /**
     * Consulta la siguiente solicitud sin atenderla (peek)
     */
    public SolicitudGremio verSiguiente() {
        return colaPrioridad.peek();
    }
    
    /**
     * Lista todas las solicitudes pendientes ordenadas por prioridad
     */
    public List<SolicitudGremio> listarPendientes() {
        List<SolicitudGremio> lista = new ArrayList<>(colaPrioridad);
        lista.sort(null); // Usar comparación natural
        return lista;
    }
    
    /**
     * Obtiene estadísticas de las solicitudes
     */
    public String obtenerEstadisticas() {
        int urgentes = 0, altas = 0, normales = 0, bajas = 0;
        
        for (SolicitudGremio sol : colaPrioridad) {
            switch (sol.getPrioridad()) {
                case URGENTE -> urgentes++;
                case ALTA -> altas++;
                case NORMAL -> normales++;
                case BAJA -> bajas++;
            }
        }
        
        return String.format("""
            📊 ESTADÍSTICAS DEL GREMIO
            ═══════════════════════════
            Solicitudes pendientes: %d
            • Urgentes: %d 🔴
            • Altas: %d 🟠
            • Normales: %d 🟡
            • Bajas: %d 🟢
            
            Solicitudes atendidas: %d
            """, colaPrioridad.size(), urgentes, altas, normales, bajas,
            historialAtendidos.size());
    }
    
    /**
     * Cancela una solicitud específica
     */
    public boolean cancelarSolicitud(int numero) {
        for (SolicitudGremio sol : colaPrioridad) {
            if (sol.getNumero() == numero) {
                colaPrioridad.remove(sol);
                System.out.println("❌ Solicitud #" + numero + " cancelada");
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene el historial de solicitudes atendidas
     */
    public List<SolicitudGremio> obtenerHistorialAtendidos() {
        return new ArrayList<>(historialAtendidos);
    }
    
    /**
     * Limpia todas las solicitudes pendientes
     */
    public void limpiarCola() {
        colaPrioridad.clear();
        System.out.println("🗑️ Cola de solicitudes limpiada");
    }
    
    public int getSolicitudesPendientes() {
        return colaPrioridad.size();
    }
    
    public int getSolicitudesAtendidas() {
        return historialAtendidos.size();
    }
    
    public boolean tieneAtencionPendiente() {
        return !colaPrioridad.isEmpty();
    }
}
