package mvc.model.exceptions;

/**
 * Se lanza cuando se intenta realizar una acción con un personaje muerto
 */
public class ExcepcionPersonajeMuerto extends ExcepcionJuego {
    private String nombrePersonaje;
    
    public ExcepcionPersonajeMuerto(String nombrePersonaje) {
        super("El personaje '" + nombrePersonaje + "' está K.O. y no puede actuar");
        this.nombrePersonaje = nombrePersonaje;
    }
    
    public String getNombrePersonaje() {
        return nombrePersonaje;
    }
}