package mvc.model.items;

import mvc.model.estados.EstadoAlterado;
import mvc.model.personajes.Personaje;

/**
 * Agua Bendita - Elimina maldiciones y estados alterados
 */
public class AguaBendita extends Item {
    public AguaBendita() {
        super("Agua Bendita");
    }
    
    @Override
    public boolean usar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " usa " + nombre + " sobre " + objetivo.getNombre());
        
        if (objetivo.getEstado() != EstadoAlterado.NORMAL) {
            objetivo.setEstado(EstadoAlterado.NORMAL);
            objetivo.setEstadoDuracion(0);
            System.out.println("✨ ¡Todos los estados alterados han sido eliminados!");
            return true;
        } else {
            System.out.println("⚠️ " + objetivo.getNombre() + " no tiene estados alterados.");
            return false;
        }
    }
}