package mvc.model.habilidades;

import mvc.model.personajes.Personaje;

/**
 * DanioMagico - habilidad que inflige daño fijo (ignora o usa ataque mágico).
 *
 * Ejemplo simple: resta puntos de vida al objetivo en base a 'poder'.
 * Nota: aquí no se resta la defensa del objetivo a menos que se haga en recibirDaño.
 */
public class DanioMagico extends Habilidad {
    private int poder;

    public DanioMagico(String nombre, int costoMP, int poder) {
        super(nombre, costoMP);
        this.poder = poder;
    }

    @Override
    public void ejecutar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " lanza " + nombre + " sobre " + objetivo.getNombre());
        objetivo.recibirDaño(poder);
    }
}
