package mvc.model.items;

import mvc.model.personajes.Personaje;

/**
 * PocionMagia - Ítem que restaura puntos de magia (MP) al objetivo
 */
public class PocionMagia extends Item {
    private int cantidad;

    public PocionMagia(String nombre, int cantidad) {
        super(nombre);
        this.cantidad = cantidad;
    }

    @Override
    public boolean usar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " usa " + nombre + " sobre " + objetivo.getNombre());
        
        // Restaurar MP usando métodos públicos
        int mpAnterior = objetivo.getMpActual();
        int mpMax = objetivo.getMpMax();
        int nuevoMp = Math.min(mpMax, mpAnterior + cantidad);
        int mpRestaurado = nuevoMp - mpAnterior;
        
        if (mpRestaurado > 0) {
            objetivo.restaurarMP(cantidad); // Usa el método público de Personaje
            return true; // Se consume el ítem
        } else {
            System.out.println(objetivo.getNombre() + " ya tiene el MP al máximo. No hace efecto.");
            return false; // No se consume si no tuvo efecto
        }
    }
}