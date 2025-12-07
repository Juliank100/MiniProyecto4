package mvc.model.items;

import mvc.model.personajes.Personaje;

/**
 * Pluma del Mundo - Resucita a un personaje caído con 50% HP
 */
public class PlumaMundo extends Item {
    public PlumaMundo() {
        super("Pluma del Mundo");
    }
    
    @Override
    public boolean usar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " usa " + nombre + " sobre " + objetivo.getNombre());
        
        if (!objetivo.estaVivo()) {
            int hpRestaurado = objetivo.getHpMax() / 2;
            objetivo.setHpActual(hpRestaurado);
            System.out.println("✨ ¡" + objetivo.getNombre() + " ha sido resucitado con " + hpRestaurado + " HP!");
            return true;
        } else {
            System.out.println("⚠️ " + objetivo.getNombre() + " no está K.O.");
            return false;
        }
    }
}