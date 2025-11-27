package mvc.model.habilidades;

import mvc.model.personajes.Personaje;

/**
 * Clase abstracta que define la interfaz de una Habilidad.
 * Implementaciones concretas realizan efectos (daño, curación, estados).
 */
public abstract class Habilidad {
    protected String nombre;
    public int costoMP; // ⭐ Ahora es público para acceso directo

    public Habilidad(String nombre, int costoMP) {
        this.nombre = nombre;
        this.costoMP = costoMP;
    }

    /**
     * Ejecuta la habilidad sobre un objetivo.
     * Implementaciones deciden si el objetivo es aliado o enemigo.
     */
    public abstract void ejecutar(Personaje usuario, Personaje objetivo);

    public String getNombre() { 
        return nombre; 
    }
    
    /**
     * ⭐ NUEVO: Getter para el costo de MP
     */
    public int getCostoMP() { 
        return costoMP; 
    }
}