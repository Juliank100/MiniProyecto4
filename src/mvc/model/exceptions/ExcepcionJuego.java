package mvc.model.exceptions;

/**
 * Excepción base para todas las excepciones del juego
 */
public class ExcepcionJuego extends Exception {
    public ExcepcionJuego(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionJuego(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}