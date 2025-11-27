package mvc.model.habilidades;

import mvc.model.estados.EstadoAlterado;
import mvc.model.personajes.Personaje;

/** Aplica el estado PARALIZADO al objetivo (chance de fallar turno). */
public class Paralisis extends Habilidad {
    public Paralisis(String nombre, int costoMP) { super(nombre, costoMP); }

    @Override
    public void ejecutar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " paraliza a " + objetivo.getNombre());
        objetivo.aplicarEstado(EstadoAlterado.PARALIZADO);
    }
}
