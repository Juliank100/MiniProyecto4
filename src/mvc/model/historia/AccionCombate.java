package mvc.model.historia;

import mvc.model.estados.EstadoAlterado;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una acción realizada en combate (para deshacer/rehacer)
 */
public class AccionCombate {
    private final TipoAccion tipo;
    private final String nombreActuante;
    private final String nombreObjetivo;
    private final int valorAnterior;
    private final int valorNuevo;
    private final EstadoAlterado estadoAnterior;
    private final EstadoAlterado estadoNuevo;
    private final String descripcion;
    private final LocalDateTime timestamp;
    
    public static enum TipoAccion {
        ATAQUE, HABILIDAD, ITEM, CAMBIO_ESTADO, CURAR_HP, CURAR_MP
    }
    
    // Constructor para cambios de HP/MP
    public AccionCombate(TipoAccion tipo, String actuante, String objetivo, 
                         int valorAnterior, int valorNuevo, String descripcion) {
        this.tipo = tipo;
        this.nombreActuante = actuante;
        this.nombreObjetivo = objetivo;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.estadoAnterior = null;
        this.estadoNuevo = null;
        this.descripcion = descripcion;
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor para cambios de estado
    public AccionCombate(String actuante, String objetivo, 
                        EstadoAlterado estadoAnterior, EstadoAlterado estadoNuevo, 
                        String descripcion) {
        this.tipo = TipoAccion.CAMBIO_ESTADO;
        this.nombreActuante = actuante;
        this.nombreObjetivo = objetivo;
        this.valorAnterior = 0;
        this.valorNuevo = 0;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.descripcion = descripcion;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public TipoAccion getTipo() { return tipo; }
    public String getNombreActuante() { return nombreActuante; }
    public String getNombreObjetivo() { return nombreObjetivo; }
    public int getValorAnterior() { return valorAnterior; }
    public int getValorNuevo() { return valorNuevo; }
    public EstadoAlterado getEstadoAnterior() { return estadoAnterior; }
    public EstadoAlterado getEstadoNuevo() { return estadoNuevo; }
    public String getDescripcion() { return descripcion; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.format("[%s] %s", timestamp.format(formatter), descripcion);
    }
}