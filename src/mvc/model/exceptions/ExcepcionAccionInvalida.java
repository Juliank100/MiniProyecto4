package mvc.model.exceptions;

/**
 * Se lanza cuando se intenta realizar una acción inválida en el combate
 */
public class ExcepcionAccionInvalida extends ExcepcionJuego {
    public ExcepcionAccionInvalida(String mensaje) {
        super(mensaje);
    }
}