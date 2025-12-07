package mvc.model.exceptions;

/**
 * Se lanza cuando se intenta agregar un objeto a un inventario lleno
 */
public class ExcepcionInventarioLleno extends ExcepcionJuego {
    private int capacidadMaxima;
    
    public ExcepcionInventarioLleno(int capacidadMaxima) {
        super("El inventario está lleno. Capacidad máxima: " + capacidadMaxima);
        this.capacidadMaxima = capacidadMaxima;
    }
    
    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }
}