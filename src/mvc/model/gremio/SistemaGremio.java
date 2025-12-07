package mvc.model.gremio;

import mvc.model.registro.RegistroAventureros;
import mvc.model.registro.HistorialBatallas;

/**
 * Sistema completo del Gremio de Aventureros
 * Integra turnos, registro de aventureros e historial de batallas
 */
public class SistemaGremio {
    private final ColaTurnosGremio colaTurnos;
    private final RegistroAventureros registroAventureros;
    private final HistorialBatallas historialBatallas;
    private final String nombreGremio;
    
    public SistemaGremio(String nombreGremio) {
        this.nombreGremio = nombreGremio;
        this.colaTurnos = new ColaTurnosGremio();
        this.registroAventureros = new RegistroAventureros();
        this.historialBatallas = new HistorialBatallas();
        
        System.out.println("\n🏛️ GREMIO DE AVENTUREROS: " + nombreGremio);
        System.out.println("Sistema iniciado correctamente\n");
    }
    
    /**
     * Menú principal del gremio (para consola)
     */
    public void mostrarMenuGremio() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   🏛️  " + nombreGremio + "           ");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. 📝 Agregar solicitud");
        System.out.println("2. 🎯 Atender siguiente solicitud");
        System.out.println("3. 👁️  Ver solicitudes pendientes");
        System.out.println("4. 📊 Ver estadísticas");
        System.out.println("5. 📋 Ver historial de batallas");
        System.out.println("6. ↩️  Volver");
    }
    
    // Getters para los subsistemas
    public ColaTurnosGremio getColaTurnos() {
        return colaTurnos;
    }
    
    public RegistroAventureros getRegistroAventureros() {
        return registroAventureros;
    }
    
    public HistorialBatallas getHistorialBatallas() {
        return historialBatallas;
    }
    
    public String getNombreGremio() {
        return nombreGremio;
    }
}