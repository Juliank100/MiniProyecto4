package mvc.model.habilidades;

import mvc.model.estados.EstadoAlterado;
import mvc.model.personajes.Personaje;

/** Aplica el estado DORMIDO al objetivo (no actúa hasta despertar). */
public class Dormir extends Habilidad {
    public Dormir(String nombre, int costoMP) { super(nombre, costoMP); }

    @Override
    public void ejecutar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " hace dormir a " + objetivo.getNombre());
        objetivo.aplicarEstado(EstadoAlterado.DORMIDO);
    }
}
