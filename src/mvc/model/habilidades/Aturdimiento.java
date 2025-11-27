package mvc.model.habilidades;

import mvc.model.estados.EstadoAlterado;

/**
 * Aturdimiento - Habilidad que aplica el estado PARALIZADO al objetivo
 * Representa un golpe que aturde temporalmente al enemigo
 */
public class Aturdimiento extends Habilidad {
    
    public Aturdimiento(String nombre, int costoMP) {
        super(nombre, costoMP);
    }

    @Override
    public void ejecutar(mvc.model.personajes.Personaje usuario, mvc.model.personajes.Personaje objetivo) {
        System.out.println(usuario.getNombre() + " ejecuta " + nombre + " sobre " + objetivo.getNombre());
        System.out.println("¡El impacto aturde al objetivo!");
        objetivo.aplicarEstado(EstadoAlterado.PARALIZADO);
    }
}