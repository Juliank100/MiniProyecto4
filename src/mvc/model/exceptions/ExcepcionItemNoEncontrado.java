package mvc.model.exceptions;

/**
 * Se lanza cuando se intenta usar un ítem que no existe en el inventario
 */
public class ExcepcionItemNoEncontrado extends ExcepcionJuego {
    private String nombreItem;
    
    public ExcepcionItemNoEncontrado(String nombreItem) {
        super("El ítem '" + nombreItem + "' no se encuentra en el inventario");
        this.nombreItem = nombreItem;
    }
    
    public String getNombreItem() {
        return nombreItem;
    }
}