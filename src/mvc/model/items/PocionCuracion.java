package mvc.model.items;

import mvc.model.personajes.Personaje;

/** Pocion que cura HP del objetivo. */
public class PocionCuracion extends Item {
    private int cantidad;

    public PocionCuracion(String nombre, int cantidad) {
        super(nombre);
        this.cantidad = cantidad;
    }

    @Override
    public boolean usar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " usa " + nombre + " sobre " + objetivo.getNombre());
        objetivo.curar(cantidad);
        return true;
    }
}
