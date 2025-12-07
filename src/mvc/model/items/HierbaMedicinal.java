package mvc.model.items;

import mvc.model.personajes.Personaje;

/**
 * Hierba Medicinal - Restaura 30 HP (ítem básico de Dragon Quest)
 */
public class HierbaMedicinal extends Item {
    public HierbaMedicinal() {
        super("Hierba Medicinal");
    }
    
    @Override
    public boolean usar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " usa " + nombre + " sobre " + objetivo.getNombre());
        objetivo.curar(30);
        return true;
    }
}