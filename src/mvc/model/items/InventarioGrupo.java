package mvc.model.items;

import java.util.*;

/**
 * InventarioGrupo - inventario compartido entre los héroes.
 * - Gestiona cantidades de ítems y su uso.
 * - Provee listarItemsUnicos() para mostrar ítems disponibles.
 */
public class InventarioGrupo {
    private final Map<String, Pair<Item, Integer>> mapa; // nombre -> (item, cantidad)

    public InventarioGrupo() {
        mapa = new LinkedHashMap<>();
    }

    /** Agrega cantidad de un ítem (por nombre). */
    public void agregarItem(Item item, int cantidad) {
        if (mapa.containsKey(item.getNombre())) {
            Pair<Item, Integer> p = mapa.get(item.getNombre());
            mapa.put(item.getNombre(), new Pair<>(p.first, p.second + cantidad));
        } else {
            mapa.put(item.getNombre(), new Pair<>(item, cantidad));
        }
    }

    /** Lista de objetos únicos (sin repetidos) para mostrar al jugador. */
    public List<Item> listarItemsUnicos() {
        List<Item> res = new ArrayList<>();
        for (Pair<Item, Integer> p : mapa.values()) res.add(p.first);
        return res;
    }

    /** Retorna la cantidad disponible de un item dado (por referencia a objeto). */
    public int getCantidad(Item item) {
        Pair<Item, Integer> p = mapa.get(item.getNombre());
        return p == null ? 0 : p.second;
    }

    /** Usa un ítem: ejecuta item.usar(usuario, objetivo) y si devuelve true disminuye la cantidad. */
    public boolean usarItem(Item item, java.lang.Object usuarioObj, java.lang.Object objetivoObj) {
        // Cast seguro (en tiempo de ejecución puede lanzar excepción si se usa mal)
        if (!mapa.containsKey(item.getNombre())) return false;
        Pair<Item, Integer> p = mapa.get(item.getNombre());
        boolean efecto;
        try {
            efecto = p.first.usar((mvc.model.personajes.Personaje) usuarioObj, (mvc.model.personajes.Personaje) objetivoObj);
        } catch (ClassCastException ex) {
            System.out.println("Error: el usuario u objetivo no son Personaje.");
            return false;
        }

        if (efecto) {
            int nueva = p.second - 1;
            if (nueva <= 0) mapa.remove(item.getNombre());
            else mapa.put(item.getNombre(), new Pair<>(p.first, nueva));
            return true;
        } else {
            return false;
        }
    }

    public boolean estaVacio() { return mapa.isEmpty(); }

    /** Clase auxiliar simple para pares (no usar Pair de librerías externas). */
    private static class Pair<F, S> {
        public final F first;
        public final S second;
        public Pair(F f, S s) { first = f; second = s; }
    }
}
