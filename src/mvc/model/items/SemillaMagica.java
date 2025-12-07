package mvc.model.items;

import mvc.model.personajes.Personaje;

/**
 * Semilla Mágica - Restaura 10 MP
 */
public class SemillaMagica extends Item {
    public SemillaMagica() {
        super("Semilla Mágica");
    }
    
    @Override
    public boolean usar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " usa " + nombre + " sobre " + objetivo.getNombre());
        
        int mpAnterior = objetivo.getMpActual();
        objetivo.restaurarMP(10);
        
        if (objetivo.getMpActual() > mpAnterior) {
            return true;
        } else {
            System.out.println("⚠️ El MP ya está al máximo.");
            return false;
        }
    }
}
