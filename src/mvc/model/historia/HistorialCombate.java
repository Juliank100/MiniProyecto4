package mvc.model.historia;

import mvc.model.personajes.Personaje;
import java.util.*;

/**
 * HistorialCombate - Sistema de deshacer/rehacer acciones
 * 
 * ESTRUCTURA DE DATOS UTILIZADA: Stack (Deque como implementación)
 * JUSTIFICACIÓN:
 * - Stack es ideal para operaciones LIFO (Last In First Out)
 * - Deshacer requiere revertir la última acción (pop)
 * - Rehacer requiere volver a aplicar la última acción deshecha (push)
 * - O(1) para push, pop y peek
 * - Deque es preferible a Stack legacy por rendimiento
 */
public class HistorialCombate {
    private final Deque<AccionCombate> pillaDeshacer;  // Stack de acciones para deshacer
    private final Deque<AccionCombate> pillaRehacer;   // Stack de acciones para rehacer
    private final int CAPACIDAD_MAXIMA = 10; // Límite de memoria
    
    public HistorialCombate() {
        this.pillaDeshacer = new ArrayDeque<>();
        this.pillaRehacer = new ArrayDeque<>();
    }
    
    /**
     * Registra una nueva acción (limpia la pila de rehacer)
     */
    public void registrarAccion(AccionCombate accion) {
        // Al hacer una nueva acción, ya no se puede "rehacer" las antiguas
        pillaRehacer.clear();
        
        pillaDeshacer.push(accion);
        
        // Limitar tamaño de historial para evitar uso excesivo de memoria
        if (pillaDeshacer.size() > CAPACIDAD_MAXIMA) {
            // Remover la acción más antigua
            Deque<AccionCombate> temp = new ArrayDeque<>();
            while (pillaDeshacer.size() > 1) {
                temp.push(pillaDeshacer.pop());
            }
            pillaDeshacer.clear();
            while (!temp.isEmpty()) {
                pillaDeshacer.addLast(temp.pop());
            }
        }
        
        System.out.println("📝 Acción registrada: " + accion.getDescripcion());
    }
    
    /**
     * Deshace la última acción
     * @return La acción deshecha, o null si no hay nada que deshacer
     */
    public AccionCombate deshacer(List<Personaje> todosPersonajes) {
        if (pillaDeshacer.isEmpty()) {
            System.out.println("⚠️ No hay acciones para deshacer");
            return null;
        }
        
        AccionCombate accion = pillaDeshacer.pop();
        pillaRehacer.push(accion);
        
        // Aplicar el efecto inverso
        aplicarInverso(accion, todosPersonajes);
        
        System.out.println("↩️ DESHECHO: " + accion.getDescripcion());
        return accion;
    }
    
    /**
     * Rehace la última acción deshecha
     * @return La acción rehecha, o null si no hay nada que rehacer
     */
    public AccionCombate rehacer(List<Personaje> todosPersonajes) {
        if (pillaRehacer.isEmpty()) {
            System.out.println("⚠️ No hay acciones para rehacer");
            return null;
        }
        
        AccionCombate accion = pillaRehacer.pop();
        pillaDeshacer.push(accion);
        
        // Aplicar el efecto original
        aplicarDirecto(accion, todosPersonajes);
        
        System.out.println("↪️ REHECHO: " + accion.getDescripcion());
        return accion;
    }
    
    /**
     * Aplica el efecto inverso de una acción (para deshacer)
     */
    private void aplicarInverso(AccionCombate accion, List<Personaje> personajes) {
        Personaje objetivo = buscarPersonaje(accion.getNombreObjetivo(), personajes);
        if (objetivo == null) return;
        
        switch (accion.getTipo()) {
            case ATAQUE, HABILIDAD -> {
                // Restaurar HP anterior
                objetivo.setHpActual(accion.getValorAnterior());
            }
            case ITEM, CURAR_HP -> {
                // Volver al HP anterior (menor)
                objetivo.setHpActual(accion.getValorAnterior());
            }
            case CURAR_MP -> {
                // Volver al MP anterior
                objetivo.setMpActual(accion.getValorAnterior());
            }
            case CAMBIO_ESTADO -> {
                // Restaurar estado anterior
                objetivo.setEstado(accion.getEstadoAnterior());
            }
        }
    }
    
    /**
     * Aplica el efecto directo de una acción (para rehacer)
     */
    private void aplicarDirecto(AccionCombate accion, List<Personaje> personajes) {
        Personaje objetivo = buscarPersonaje(accion.getNombreObjetivo(), personajes);
        if (objetivo == null) return;
        
        switch (accion.getTipo()) {
            case ATAQUE, HABILIDAD, ITEM, CURAR_HP -> {
                objetivo.setHpActual(accion.getValorNuevo());
            }
            case CURAR_MP -> {
                objetivo.setMpActual(accion.getValorNuevo());
            }
            case CAMBIO_ESTADO -> {
                objetivo.setEstado(accion.getEstadoNuevo());
            }
        }
    }
    
    /**
     * Busca un personaje por nombre en la lista
     */
    private Personaje buscarPersonaje(String nombre, List<Personaje> personajes) {
        for (Personaje p : personajes) {
            if (p.getNombre().equals(nombre)) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * Obtiene el historial completo (para mostrar en UI)
     */
    public List<String> obtenerHistorial() {
        List<String> resultado = new ArrayList<>();
        for (AccionCombate accion : pillaDeshacer) {
            resultado.add(accion.toString());
        }
        Collections.reverse(resultado); // Más reciente primero
        return resultado;
    }
    
    /**
     * Limpia todo el historial
     */
    public void limpiar() {
        pillaDeshacer.clear();
        pillaRehacer.clear();
        System.out.println("🗑️ Historial limpiado");
    }
    
    public boolean puedeDeshacer() {
        return !pillaDeshacer.isEmpty();
    }
    
    public boolean puedeRehacer() {
        return !pillaRehacer.isEmpty();
    }
    
    public int getTamañoHistorial() {
        return pillaDeshacer.size();
    }
}