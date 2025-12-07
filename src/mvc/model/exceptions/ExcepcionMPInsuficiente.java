package mvc.model.exceptions;

/**
 * Se lanza cuando un personaje intenta usar una habilidad sin MP suficiente
 */
public class ExcepcionMPInsuficiente extends ExcepcionJuego {
    private int mpRequerido;
    private int mpActual;
    
    public ExcepcionMPInsuficiente(int mpRequerido, int mpActual) {
        super(String.format("MP insuficiente. Requerido: %d, Actual: %d", mpRequerido, mpActual));
        this.mpRequerido = mpRequerido;
        this.mpActual = mpActual;
    }
    
    public int getMpRequerido() {
        return mpRequerido;
    }
    
    public int getMpActual() {
        return mpActual;
    }
}
