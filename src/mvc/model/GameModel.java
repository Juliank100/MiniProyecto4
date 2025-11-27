package mvc.model;

import mvc.model.combate.Batalla;
import mvc.model.personajes.Personaje;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * GameModel - adaptador mínimo que expone la lógica existente sin modificarla.
 * Envuelve la clase Batalla y expone métodos para leer estado.
 *
 * No cambia la lógica: delega llamadas a Batalla y a las clases del proyecto.
 */
public class GameModel {
    private Batalla batalla;

    public GameModel() {
        // no inicializamos Batalla aún; se crea al iniciar combate
    }

    /**
     * Inicia una batalla en modo consola usando el Scanner proporcionado.
     * Devuelve true si la batalla comenzó correctamente.
     */
    public boolean iniciarBatallaConsola(Scanner sc) {
        if (sc == null) throw new IllegalArgumentException("Scanner no puede ser null");
        this.batalla = new Batalla(sc);
        // No consumimos ni cambiamos la lógica interna; quien llame podrá usar Batalla directamente si lo desea.
        try {
            batalla.iniciarCombate();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Retorna una copia ligera de héroes (si Batalla lo permite).
     * Si Batalla no expone getters públicos, este método devuelve lista vacía.
     * (Se intenta acceder por reflexión solo si existen getters).
     */
    public List<Personaje> obtenerHeroes() {
        try {
            // Intentamos acceder a un método público 'getHeroes' si existe
            java.lang.reflect.Method m = batalla.getClass().getMethod("getHeroes");
            Object res = m.invoke(batalla);
            if (res instanceof List) {
                @SuppressWarnings("unchecked")
                List<Personaje> result = (List<Personaje>) res;
                return result;
            }
        } catch (Exception ignore) {}
        return new ArrayList<>();
    }

    /**
    public List<Personaje> obtenerEnemigos() {
        try {
            java.lang.reflect.Method m = batalla.getClass().getMethod("getEnemigos");
            Object res = m.invoke(batalla);
            if (res instanceof List) {
                @SuppressWarnings("unchecked")
                List<Personaje> result = (List<Personaje>) res;
                return result;
            }
        } catch (Exception ignore) {}
        return new ArrayList<>();
    }
        return new ArrayList<>();
    }

    /**
     * Si quieres exponer más funcionalidades (usar item, habilidad, etc.), agrégalas
     * como delegados a Batalla. Por ahora mantenemos un adaptador mínimo y seguro.
     */

    public Batalla getBatallaRaw() { return this.batalla; }
}
