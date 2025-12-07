package mvc.model.registro;

import mvc.model.personajes.Heroe;
import java.util.*;

/**
 * Registro de aventureros disponibles para misiones
 * 
 * ESTRUCTURA DE DATOS UTILIZADA: TreeMap
 * JUSTIFICACIÓN:
 * - Los aventureros se ordenan alfabéticamente por nombre
 * - O(log n) para búsqueda, inserción y eliminación
 * - Mantiene orden natural (ordenamiento automático)
 * - Permite navegación eficiente (firstKey, lastKey, etc.)
 * - Ideal para mostrar listados ordenados
 */
public class RegistroAventureros {
    private final TreeMap<String, Heroe> aventureros;
    
    public RegistroAventureros() {
        this.aventureros = new TreeMap<>(); // Orden alfabético automático
    }
    
    /**
     * Registra un nuevo aventurero
     */
    public void registrarAventurero(Heroe heroe) {
        String nombre = heroe.getNombre();
        
        if (aventureros.containsKey(nombre)) {
            System.out.println("⚠️ Ya existe un aventurero llamado " + nombre);
            return;
        }
        
        aventureros.put(nombre, heroe);
        System.out.println("✅ Aventurero registrado: " + nombre);
    }
    
    /**
     * Busca un aventurero por nombre (O(log n))
     */
    public Heroe buscarAventurero(String nombre) {
        return aventureros.get(nombre);
    }
    
    /**
     * Elimina un aventurero del registro
     */
    public boolean eliminarAventurero(String nombre) {
        if (aventureros.remove(nombre) != null) {
            System.out.println("🗑️ Aventurero eliminado: " + nombre);
            return true;
        }
        return false;
    }
    
    /**
     * Lista todos los aventureros en orden alfabético
     */
    public List<Heroe> listarAventurerosOrdenados() {
        return new ArrayList<>(aventureros.values());
    }
    
    /**
     * Obtiene aventureros por rango de nombres (subfunción de TreeMap)
     */
    public List<Heroe> buscarPorRango(String nombreInicio, String nombreFin) {
        SortedMap<String, Heroe> subMapa = aventureros.subMap(nombreInicio, nombreFin);
        return new ArrayList<>(subMapa.values());
    }
    
    /**
     * Obtiene los primeros N aventureros alfabéticamente
     */
    public List<Heroe> obtenerPrimeros(int cantidad) {
        List<Heroe> resultado = new ArrayList<>();
        int contador = 0;
        
        for (Heroe heroe : aventureros.values()) {
            if (contador >= cantidad) break;
            resultado.add(heroe);
            contador++;
        }
        
        return resultado;
    }
    
    /**
     * Obtiene estadísticas del registro
     */
    public String obtenerEstadisticas() {
        if (aventureros.isEmpty()) {
            return "📋 No hay aventureros registrados";
        }
        
        int totalHP = 0;
        int totalMP = 0;
        int totalAtaque = 0;
        
        for (Heroe heroe : aventureros.values()) {
            totalHP += heroe.getHpMax();
            totalMP += heroe.getMpMax();
            totalAtaque += heroe.getAtaque();
        }
        
        int cantidad = aventureros.size();
        
        return String.format("""
            📋 REGISTRO DE AVENTUREROS
            ═══════════════════════════
            Total registrados: %d
            Promedio HP: %d
            Promedio MP: %d
            Promedio Ataque: %d
            Primer aventurero: %s
            Último aventurero: %s
            """, cantidad, totalHP/cantidad, totalMP/cantidad, totalAtaque/cantidad,
            aventureros.firstKey(), aventureros.lastKey());
    }
    
    public int getCantidadAventureros() {
        return aventureros.size();
    }
    
    public boolean estaVacio() {
        return aventureros.isEmpty();
    }
}