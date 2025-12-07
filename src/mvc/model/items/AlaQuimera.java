package mvc.model.items;

import mvc.model.personajes.Personaje;

/**
 * Ala de Quimera - Escapar de batalla (no implementado en combate obligatorio)
 * Restaura 20 HP como efecto alternativo
 */
public class AlaQuimera extends Item {
    public AlaQuimera() {
        super("Ala de Quimera");
    }
    
    @Override
    public boolean usar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " usa " + nombre);
        System.out.println("⚠️ No puedes escapar de este combate, pero recuperas algo de energía...");
        objetivo.curar(20);
        return true;
    }
}