package mvc.model.exceptions;

/**
 * Se lanza cuando hay un error al guardar o cargar una partida
 */
public class ExcepcionGuardadoPartida extends ExcepcionJuego {
    public ExcepcionGuardadoPartida(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionGuardadoPartida(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}