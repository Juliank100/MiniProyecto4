package mvc.model.habilidades;

import mvc.model.personajes.Personaje;

/**
 * Curacion - habilidad que restaura HP del usuario.
 *
 * Actualmente está implementada para curar al usuario (no a aliados). Si quieres
 * curar a un aliado, cambia la lógica para aplicar la curación al 'objetivo'.
 */
public class Curacion extends Habilidad {
    private int poder;

    public Curacion(String nombre, int costoMP, int poder) {
        super(nombre, costoMP);
        this.poder = poder;
    }

    @Override
    public void ejecutar(Personaje usuario, Personaje objetivo) {
        // Diseñado para curar al usuario; si se quiere curar al objetivo, cambiar usuario.curar -> objetivo.curar
        System.out.println(usuario.getNombre() + " usa " + nombre);
        usuario.curar(poder);
    }
}
