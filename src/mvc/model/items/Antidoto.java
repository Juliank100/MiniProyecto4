package mvc.model.items;

import mvc.model.estados.EstadoAlterado;
import mvc.model.personajes.Personaje;

/**
 * Antídoto - elimina el estado VENENO del objetivo.
 */
public class Antidoto extends Item {
    public Antidoto(String nombre) {
        super(nombre);
    }

    @Override
    public boolean usar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " usa " + nombre + " sobre " + objetivo.getNombre());

        if (objetivo.estaEnEstado(EstadoAlterado.VENENO)) {
            objetivo.quitarEstado(EstadoAlterado.VENENO);
            return true; // se consume el ítem
        } else {
            System.out.println(objetivo.getNombre() + " no está envenenado. No hace efecto.");
            return false; // no se consume si no tuvo efecto
        }
    }
}
