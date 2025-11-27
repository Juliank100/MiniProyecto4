package config;

import java.awt.*;
import java.io.*;
import java.util.Properties;

/**
 * ConfiguracionJuego - Gestiona las preferencias del jugador
 * (volumen, resolución, dificultad, etc.)
 */
public class ConfiguracionJuego {
    private static ConfiguracionJuego instancia;
    private Properties propiedades;
    private static final String ARCHIVO_CONFIG = "config.properties";
    
    // Valores por defecto
    private int volumenMusica = 70;
    private int volumenEfectos = 80;
    private boolean pantallaCompleta = false;
    private String dificultad = "Normal"; // Fácil, Normal, Difícil
    private boolean mostrarTutorial = true;
    private int anchoVentana = 1200;
    private int altoVentana = 800;
    
    // Colores del tema
    private Color colorPrimario = new Color(30, 30, 80);
    private Color colorSecundario = new Color(50, 50, 120);
    private Color colorTexto = Color.WHITE;
    
    private ConfiguracionJuego() {
        propiedades = new Properties();
        cargarConfiguracion();
    }
    
    public static ConfiguracionJuego obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionJuego();
        }
        return instancia;
    }
    
    /**
     * Carga la configuración desde el archivo
     */
    private void cargarConfiguracion() {
        File archivo = new File(ARCHIVO_CONFIG);
        
        if (!archivo.exists()) {
            System.out.println("📝 Creando configuración por defecto...");
            guardarConfiguracion();
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(archivo)) {
            propiedades.load(fis);
            
            volumenMusica = Integer.parseInt(propiedades.getProperty("volumen.musica", "70"));
            volumenEfectos = Integer.parseInt(propiedades.getProperty("volumen.efectos", "80"));
            pantallaCompleta = Boolean.parseBoolean(propiedades.getProperty("pantalla.completa", "false"));
            dificultad = propiedades.getProperty("dificultad", "Normal");
            mostrarTutorial = Boolean.parseBoolean(propiedades.getProperty("mostrar.tutorial", "true"));
            anchoVentana = Integer.parseInt(propiedades.getProperty("ventana.ancho", "1200"));
            altoVentana = Integer.parseInt(propiedades.getProperty("ventana.alto", "800"));
            
            System.out.println("✅ Configuración cargada correctamente");
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("⚠️ Error al cargar configuración: " + e.getMessage());
            System.err.println("Usando valores por defecto...");
        }
    }
    
    /**
     * Guarda la configuración actual en el archivo
     */
    public void guardarConfiguracion() {
        propiedades.setProperty("volumen.musica", String.valueOf(volumenMusica));
        propiedades.setProperty("volumen.efectos", String.valueOf(volumenEfectos));
        propiedades.setProperty("pantalla.completa", String.valueOf(pantallaCompleta));
        propiedades.setProperty("dificultad", dificultad);
        propiedades.setProperty("mostrar.tutorial", String.valueOf(mostrarTutorial));
        propiedades.setProperty("ventana.ancho", String.valueOf(anchoVentana));
        propiedades.setProperty("ventana.alto", String.valueOf(altoVentana));
        
        try (FileOutputStream fos = new FileOutputStream(ARCHIVO_CONFIG)) {
            propiedades.store(fos, "Configuración de Dragon Quest VIII - Simulador de Combate");
            System.out.println("💾 Configuración guardada");
        } catch (IOException e) {
            System.err.println("❌ Error al guardar configuración: " + e.getMessage());
        }
    }
    
    // ==================== GETTERS Y SETTERS ====================
    
    public int getVolumenMusica() {
        return volumenMusica;
    }
    
    public void setVolumenMusica(int volumen) {
        this.volumenMusica = Math.max(0, Math.min(100, volumen));
    }
    
    public int getVolumenEfectos() {
        return volumenEfectos;
    }
    
    public void setVolumenEfectos(int volumen) {
        this.volumenEfectos = Math.max(0, Math.min(100, volumen));
    }
    
    public boolean isPantallaCompleta() {
        return pantallaCompleta;
    }
    
    public void setPantallaCompleta(boolean pantallaCompleta) {
        this.pantallaCompleta = pantallaCompleta;
    }
    
    public String getDificultad() {
        return dificultad;
    }
    
    public void setDificultad(String dificultad) {
        if (dificultad.equals("Fácil") || dificultad.equals("Normal") || dificultad.equals("Difícil")) {
            this.dificultad = dificultad;
        }
    }
    
    public boolean isMostrarTutorial() {
        return mostrarTutorial;
    }
    
    public void setMostrarTutorial(boolean mostrar) {
        this.mostrarTutorial = mostrar;
    }
    
    public Dimension getDimensionVentana() {
        return new Dimension(anchoVentana, altoVentana);
    }
    
    public void setDimensionVentana(int ancho, int alto) {
        this.anchoVentana = ancho;
        this.altoVentana = alto;
    }
    
    public Color getColorPrimario() {
        return colorPrimario;
    }
    
    public Color getColorSecundario() {
        return colorSecundario;
    }
    
    public Color getColorTexto() {
        return colorTexto;
    }
    
    /**
     * Obtiene el multiplicador de dificultad para enemigos
     */
    public double getMultiplicadorDificultad() {
        return switch (dificultad) {
            case "Fácil" -> 0.75;
            case "Difícil" -> 1.5;
            default -> 1.0;
        };
    }
    
    /**
     * Restablece todos los valores a los predeterminados
     */
    public void restaurarPorDefecto() {
        volumenMusica = 70;
        volumenEfectos = 80;
        pantallaCompleta = false;
        dificultad = "Normal";
        mostrarTutorial = true;
        anchoVentana = 1200;
        altoVentana = 800;
        
        guardarConfiguracion();
        System.out.println("🔄 Configuración restaurada a valores por defecto");
    }
    
    @Override
    public String toString() {
        return "ConfiguracionJuego{" +
                "volumenMusica=" + volumenMusica +
                ", volumenEfectos=" + volumenEfectos +
                ", pantallaCompleta=" + pantallaCompleta +
                ", dificultad='" + dificultad + '\'' +
                ", mostrarTutorial=" + mostrarTutorial +
                ", dimensiones=" + anchoVentana + "x" + altoVentana +
                '}';
    }
}

