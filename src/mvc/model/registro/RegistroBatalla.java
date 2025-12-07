package mvc.model.registro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un registro completo de una batalla
 */
public class RegistroBatalla {
    private final int id;
    private final LocalDateTime fecha;
    private final boolean victoria;
    private final int turnosTranscurridos;
    private final String[] nombresHeroes;
    private final String[] nombresEnemigos;
    private final int experienciaGanada;
    private final int oroGanado;
    
    public RegistroBatalla(int id, boolean victoria, int turnos, 
                          String[] heroes, String[] enemigos, int exp, int oro) {
        this.id = id;
        this.fecha = LocalDateTime.now();
        this.victoria = victoria;
        this.turnosTranscurridos = turnos;
        this.nombresHeroes = heroes;
        this.nombresEnemigos = enemigos;
        this.experienciaGanada = exp;
        this.oroGanado = oro;
    }
    
    // Constructor para cargar desde archivo
    public RegistroBatalla(int id, LocalDateTime fecha, boolean victoria, int turnos,
                          String[] heroes, String[] enemigos, int exp, int oro) {
        this.id = id;
        this.fecha = fecha;
        this.victoria = victoria;
        this.turnosTranscurridos = turnos;
        this.nombresHeroes = heroes;
        this.nombresEnemigos = enemigos;
        this.experienciaGanada = exp;
        this.oroGanado = oro;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String resultado = victoria ? "VICTORIA ✅" : "DERROTA ❌";
        
        return String.format("[#%d] %s | %s | Turnos: %d | Exp: %d | Oro: %d",
            id, fecha.format(formatter), resultado, turnosTranscurridos, 
            experienciaGanada, oroGanado);
    }
    
    public String toFormatoGuardado() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return String.format("%d|%s|%b|%d|%s|%s|%d|%d",
            id, fecha.format(formatter), victoria, turnosTranscurridos,
            String.join(",", nombresHeroes),
            String.join(",", nombresEnemigos),
            experienciaGanada, oroGanado);
    }
    
    // Getters
    public int getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean isVictoria() { return victoria; }
    public int getTurnosTranscurridos() { return turnosTranscurridos; }
    public int getExperienciaGanada() { return experienciaGanada; }
    public int getOroGanado() { return oroGanado; }
}
