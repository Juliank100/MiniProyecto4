package mvc.model.items;

import mvc.model.personajes.Personaje;

/**
 * Item - clase base para objetos utilizables.
 * - Cada ítem define su efecto en usar(usuario, objetivo).
 */
public abstract class Item {
    protected String nombre;

    public Item(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }

    /**
     * Aplica el efecto del ítem.
     * @param usuario quien usa el ítem (quien lo consume).
     * @param objetivo personaje sobre quien se aplica el efecto.
     * @return true si el uso tuvo efecto (y se debe consumir del inventario).
     */
    public abstract boolean usar(Personaje usuario, Personaje objetivo);
}

