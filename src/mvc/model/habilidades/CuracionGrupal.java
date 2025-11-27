package mvc.model.habilidades;

import mvc.model.personajes.Personaje;

/**
 * CuracionGrupal - Cura a todo el grupo de aliados
 * Nota: Necesita acceso a la lista de aliados
 */
public class CuracionGrupal extends Habilidad {
    private int poder;
    
    public CuracionGrupal(String nombre, int costoMP, int poder) {
        super(nombre, costoMP);
        this.poder = poder;
    }

    @Override
    public void ejecutar(Personaje usuario, Personaje objetivo) {
        // En este caso, objetivo no se usa directamente
        // Idealmente necesitaríamos la lista de aliados
        System.out.println(usuario.getNombre() + " invoca " + nombre);
        System.out.println("¡Una luz divina envuelve al grupo!");
        
        // Por simplicidad, curamos solo al usuario y mostramos mensaje
        // En una implementación completa, pasaríamos la lista de aliados
        usuario.curar(poder);
        System.out.println("(Nota: En combate real, curaría a todos los aliados)");
    }
}
