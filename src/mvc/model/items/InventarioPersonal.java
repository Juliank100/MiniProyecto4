package mvc.model.items;

import mvc.model.exceptions.*;
import java.util.*;

/**
 * InventarioPersonal - Cada héroe tiene su propio inventario con capacidad limitada
 * 
 * ESTRUCTURA DE DATOS UTILIZADA: LinkedHashMap
 * JUSTIFICACIÓN:
 * - Mantiene el orden de inserción (importante para mostrar ítems consistentemente)
 * - O(1) para búsqueda, inserción y eliminación por clave
 * - Permite acceso rápido por nombre del ítem
 * - Iteración eficiente en orden de inserción
 */
public class InventarioPersonal {
    private final Map<String, ItemStack> items; // LinkedHashMap para orden de inserción
    private final int CAPACIDAD_MAXIMA = 5; // Cada héroe puede llevar máximo 5 ítems
    private final String dueño;
    
    public InventarioPersonal(String nombreDueño) {
        this.items = new LinkedHashMap<>(); // Orden de inserción preservado
        this.dueño = nombreDueño;
    }
    
    /**
     * Agrega un ítem al inventario
     * @throws ExcepcionInventarioLleno si el inventario está lleno
     */
    public void agregarItem(Item item, int cantidad) throws ExcepcionInventarioLleno {
        String nombre = item.getNombre();
        
        if (items.containsKey(nombre)) {
            // Ya existe, solo aumentar cantidad
            ItemStack stack = items.get(nombre);
            stack.cantidad += cantidad;
        } else {
            // Nuevo ítem, verificar espacio
            if (items.size() >= CAPACIDAD_MAXIMA) {
                throw new ExcepcionInventarioLleno(CAPACIDAD_MAXIMA);
            }
            items.put(nombre, new ItemStack(item, cantidad));
        }
    }
    
    /**
     * Usa un ítem del inventario
     * @throws ExcepcionItemNoEncontrado si el ítem no existe
     */
    public boolean usarItem(String nombreItem, Object usuario, Object objetivo) 
            throws ExcepcionItemNoEncontrado, mvc.model.exceptions.ExcepcionAccionInvalida {
        
        if (!items.containsKey(nombreItem)) {
            throw new ExcepcionItemNoEncontrado(nombreItem);
        }
        
        ItemStack stack = items.get(nombreItem);
        
        try {
            boolean usado = stack.item.usar(
                (mvc.model.personajes.Personaje) usuario,
                (mvc.model.personajes.Personaje) objetivo
            );
            
            if (usado) {
                stack.cantidad--;
                if (stack.cantidad <= 0) {
                    items.remove(nombreItem);
                    System.out.println("⚠️ " + dueño + " ha usado el último " + nombreItem);
                }
            }
            
            return usado;
        } catch (ClassCastException e) {
            throw new mvc.model.exceptions.ExcepcionAccionInvalida(
                "Error al usar ítem: tipos de personaje inválidos"
            );
        }
    }
    
    /**
     * Obtiene la cantidad de un ítem específico
     */
    public int getCantidad(String nombreItem) {
        ItemStack stack = items.get(nombreItem);
        return stack != null ? stack.cantidad : 0;
    }
    
    /**
     * Lista todos los ítems disponibles (sin duplicados)
     */
    public List<Item> listarItems() {
        List<Item> resultado = new ArrayList<>();
        for (ItemStack stack : items.values()) {
            resultado.add(stack.item);
        }
        return resultado;
    }
    
    /**
     * Verifica si el inventario está vacío
     */
    public boolean estaVacio() {
        return items.isEmpty();
    }
    
    /**
     * Verifica si el inventario está lleno
     */
    public boolean estaLleno() {
        return items.size() >= CAPACIDAD_MAXIMA;
    }
    
    /**
     * Obtiene el espacio disponible
     */
    public int getEspacioDisponible() {
        return CAPACIDAD_MAXIMA - items.size();
    }
    
    /**
     * Transfiere un ítem a otro inventario
     * @throws ExcepcionItemNoEncontrado si el ítem no existe
     * @throws ExcepcionInventarioLleno si el inventario destino está lleno
     */
    public void transferirItem(String nombreItem, int cantidad, InventarioPersonal destino) 
            throws ExcepcionItemNoEncontrado, ExcepcionInventarioLleno {
        
        if (!items.containsKey(nombreItem)) {
            throw new ExcepcionItemNoEncontrado(nombreItem);
        }
        
        ItemStack stack = items.get(nombreItem);
        
        if (stack.cantidad < cantidad) {
            cantidad = stack.cantidad; // Transferir todo lo disponible
        }
        
        // Agregar al destino
        destino.agregarItem(stack.item, cantidad);
        
        // Remover del origen
        stack.cantidad -= cantidad;
        if (stack.cantidad <= 0) {
            items.remove(nombreItem);
        }
    }
    
    /**
     * Obtiene un mapa con todos los ítems y sus cantidades (para guardar)
     */
    public Map<String, Integer> obtenerItemsParaGuardar() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().cantidad);
        }
        return resultado;
    }
    
    @Override
    public String toString() {
        if (items.isEmpty()) {
            return dueño + ": Inventario vacío";
        }
        
        StringBuilder sb = new StringBuilder(dueño + ": ");
        for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
            sb.append(entry.getKey())
              .append(" x")
              .append(entry.getValue().cantidad)
              .append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
    
    // Clase interna para almacenar ítem + cantidad
    private static class ItemStack {
        Item item;
        int cantidad;
        
        ItemStack(Item item, int cantidad) {
            this.item = item;
            this.cantidad = cantidad;
        }
    }
}
