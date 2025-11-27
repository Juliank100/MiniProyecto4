// ===== GolpeCritico.java =====
package mvc.model.habilidades;

import java.util.Random;

import mvc.model.personajes.Personaje;

/**
 * GolpeCritico - Golpe físico con chance de hacer daño crítico
 */
public class GolpeCritico extends Habilidad {
    private static final Random rand = new Random();
    
    public GolpeCritico(String nombre, int costoMP) {
        super(nombre, costoMP);
    }

    @Override
    public void ejecutar(Personaje usuario, Personaje objetivo) {
        System.out.println(usuario.getNombre() + " ejecuta " + nombre);
        
        // 40% de chance de crítico
        int damage;
        if (rand.nextInt(100) < 40) {
            damage = usuario.getAtaque() * 2; // Daño doble
            System.out.println("¡GOLPE CRÍTICO!");
        } else {
            damage = usuario.getAtaque() + 5; // Daño normal mejorado
        }
        
        objetivo.recibirDaño(damage);
    }
}
