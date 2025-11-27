package mvc.model.habilidades;

import mvc.model.estados.EstadoAlterado;
import mvc.model.personajes.Personaje;

/**
 * RemoverEstado - Elimina estados alterados negativos
 */
public class RemoverEstado extends Habilidad {
    
    public RemoverEstado(String nombre, int costoMP) {
        super(nombre, costoMP);
    }

    @Override
    public void ejecutar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " usa " + nombre + " sobre " + objetivo.getNombre());
        
        // Remover cualquier estado negativo usando los métodos públicos
        if (objetivo.getEstado() != EstadoAlterado.NORMAL) {
            EstadoAlterado estadoAnterior = objetivo.getEstado();
            objetivo.setEstado(EstadoAlterado.NORMAL);
            objetivo.setEstadoDuracion(0);
            System.out.println(objetivo.getNombre() + " se libera del estado " + estadoAnterior);
        } else {
            System.out.println(objetivo.getNombre() + " no tiene estados alterados que remover.");
        }
    }
}