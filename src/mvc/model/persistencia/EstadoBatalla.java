package mvc.model.persistencia;

import java.util.*;

/**
 * Representa el estado completo de una batalla para guardar/cargar
 */
public class EstadoBatalla {
    // Información general
    public int turnoActual;
    public String fechaGuardado;
    
    // Estado de los héroes
    public List<DatosPersonaje> heroes;
    
    // Estado de los enemigos
    public List<DatosPersonaje> enemigos;
    
    // Inventarios individuales de cada héroe
    public Map<String, Map<String, Integer>> inventariosHeroes;
    
    public EstadoBatalla() {
        heroes = new ArrayList<>();
        enemigos = new ArrayList<>();
        inventariosHeroes = new HashMap<>();
    }
    
    /**
     * Clase interna para datos de personajes
     */
    public static class DatosPersonaje {
        public String nombre;
        public String tipo; // "Heroe", "Enemigo", "MiniBoss"
        public int hpActual;
        public int hpMax;
        public int mpActual;
        public int mpMax;
        public int ataque;
        public int defensa;
        public int velocidad;
        public String estado;
        public int estadoDuracion;
        public List<String> habilidades;
        public String comportamiento; // Para enemigos
        
        public DatosPersonaje() {
            habilidades = new ArrayList<>();
        }
    }
}