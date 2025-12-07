package mvc.model.gremio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una solicitud en el gremio de aventureros
 */
public class SolicitudGremio implements Comparable<SolicitudGremio> {
    private final int numero;
    private final String nombreAventurero;
    private final TipoSolicitud tipo;
    private final PrioridadSolicitud prioridad;
    private final LocalDateTime horaLlegada;
    
    public enum TipoSolicitud {
        REGISTRO_NUEVO("Registro Nuevo Aventurero"),
        MISION_URGENTE("Misión Urgente"),
        COMPRA_EQUIPO("Compra de Equipo"),
        VENTA_ITEMS("Venta de Ítems"),
        INFORMACION("Consulta de Información"),
        CURACION("Servicio de Curación");
        
        private final String descripcion;
        TipoSolicitud(String desc) { this.descripcion = desc; }
        public String getDescripcion() { return descripcion; }
    }
    
    public enum PrioridadSolicitud {
        URGENTE(1, "🔴 URGENTE"),
        ALTA(2, "🟠 ALTA"),
        NORMAL(3, "🟡 NORMAL"),
        BAJA(4, "🟢 BAJA");
        
        private final int nivel;
        private final String icono;
        
        PrioridadSolicitud(int nivel, String icono) {
            this.nivel = nivel;
            this.icono = icono;
        }
        
        public int getNivel() { return nivel; }
        public String getIcono() { return icono; }
    }
    
    public SolicitudGremio(int numero, String nombreAventurero, 
                          TipoSolicitud tipo, PrioridadSolicitud prioridad) {
        this.numero = numero;
        this.nombreAventurero = nombreAventurero;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.horaLlegada = LocalDateTime.now();
    }
    
    /**
     * Comparación para Priority Queue:
     * 1. Primero por prioridad (menor número = mayor prioridad)
     * 2. Si igual prioridad, por hora de llegada (FIFO)
     */
    @Override
    public int compareTo(SolicitudGremio otra) {
        // Comparar por nivel de prioridad
        int comparacionPrioridad = Integer.compare(
            this.prioridad.getNivel(), 
            otra.prioridad.getNivel()
        );
        
        if (comparacionPrioridad != 0) {
            return comparacionPrioridad;
        }
        
        // Si igual prioridad, ordenar por hora de llegada (FIFO)
        return this.horaLlegada.compareTo(otra.horaLlegada);
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.format("[#%03d] %s | %s - %s | Llegada: %s",
            numero, prioridad.getIcono(), nombreAventurero, 
            tipo.getDescripcion(), horaLlegada.format(formatter));
    }
    
    // Getters
    public int getNumero() { return numero; }
    public String getNombreAventurero() { return nombreAventurero; }
    public TipoSolicitud getTipo() { return tipo; }
    public PrioridadSolicitud getPrioridad() { return prioridad; }
    public LocalDateTime getHoraLlegada() { return horaLlegada; }
}