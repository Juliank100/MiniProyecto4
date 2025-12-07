package mvc.model.persistencia;

import mvc.model.exceptions.ExcepcionGuardadoPartida;
import mvc.model.items.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * GestorPersistencia - Gestiona el guardado y carga de partidas
 * 
 * FORMATO DEL ARCHIVO:
 * - Texto plano con formato estructurado
 * - Fácil de leer y depurar
 * - Secciones claramente delimitadas
 */
public class GestorPersistencia {
    private static final String CARPETA_GUARDADOS = "partidas/";
    private static final String EXTENSION = ".dq8save";
    
    /**
     * Guarda el estado actual de la batalla
     */
    public static void guardarPartida(String nombrePartida, EstadoBatalla estado) 
            throws ExcepcionGuardadoPartida {
        
        // Crear carpeta si no existe
        File carpeta = new File(CARPETA_GUARDADOS);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        
        String rutaArchivo = CARPETA_GUARDADOS + nombrePartida + EXTENSION;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            
            // ENCABEZADO
            writer.println("=== DRAGON QUEST VIII - PARTIDA GUARDADA ===");
            writer.println("Fecha: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            ));
            writer.println("Turno: " + estado.turnoActual);
            writer.println();
            
            // SECCIÓN HÉROES
            writer.println("[HEROES]");
            writer.println("Total: " + estado.heroes.size());
            for (EstadoBatalla.DatosPersonaje heroe : estado.heroes) {
                escribirPersonaje(writer, heroe);
            }
            writer.println();
            
            // SECCIÓN ENEMIGOS
            writer.println("[ENEMIGOS]");
            writer.println("Total: " + estado.enemigos.size());
            for (EstadoBatalla.DatosPersonaje enemigo : estado.enemigos) {
                escribirPersonaje(writer, enemigo);
            }
            writer.println();
            
            // SECCIÓN INVENTARIOS
            writer.println("[INVENTARIOS]");
            for (Map.Entry<String, Map<String, Integer>> entry : estado.inventariosHeroes.entrySet()) {
                writer.println("Heroe: " + entry.getKey());
                Map<String, Integer> inventario = entry.getValue();
                for (Map.Entry<String, Integer> item : inventario.entrySet()) {
                    writer.println("  Item: " + item.getKey() + " | Cantidad: " + item.getValue());
                }
            }
            writer.println();
            
            writer.println("=== FIN DE PARTIDA ===");
            
            System.out.println("💾 Partida guardada exitosamente en: " + rutaArchivo);
            
        } catch (IOException e) {
            throw new ExcepcionGuardadoPartida("Error al guardar partida: " + e.getMessage(), e);
        }
    }
    
    /**
     * Escribe los datos de un personaje al archivo
     */
    private static void escribirPersonaje(PrintWriter writer, EstadoBatalla.DatosPersonaje p) {
        writer.println("---");
        writer.println("Nombre: " + p.nombre);
        writer.println("Tipo: " + p.tipo);
        writer.println("HP: " + p.hpActual + "/" + p.hpMax);
        writer.println("MP: " + p.mpActual + "/" + p.mpMax);
        writer.println("Ataque: " + p.ataque);
        writer.println("Defensa: " + p.defensa);
        writer.println("Velocidad: " + p.velocidad);
        writer.println("Estado: " + p.estado);
        writer.println("EstadoDuracion: " + p.estadoDuracion);
        
        if (p.comportamiento != null) {
            writer.println("Comportamiento: " + p.comportamiento);
        }
        
        if (!p.habilidades.isEmpty()) {
            writer.println("Habilidades: " + String.join(",", p.habilidades));
        }
    }
    
    /**
     * Carga una partida guardada
     */
    public static EstadoBatalla cargarPartida(String nombrePartida) 
            throws ExcepcionGuardadoPartida {
        
        String rutaArchivo = CARPETA_GUARDADOS + nombrePartida + EXTENSION;
        File archivo = new File(rutaArchivo);
        
        if (!archivo.exists()) {
            throw new ExcepcionGuardadoPartida("La partida '" + nombrePartida + "' no existe");
        }
        
        EstadoBatalla estado = new EstadoBatalla();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String seccionActual = "";
            EstadoBatalla.DatosPersonaje personajeActual = null;
            String heroeActualInventario = null;
            
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                
                if (linea.isEmpty() || linea.startsWith("===")) {
                    continue;
                }
                
                // Detectar secciones
                if (linea.startsWith("[")) {
                    seccionActual = linea;
                    continue;
                }
                
                // Leer datos según sección
                if (linea.startsWith("Turno:")) {
                    estado.turnoActual = Integer.parseInt(linea.split(":")[1].trim());
                }
                else if (seccionActual.equals("[HEROES]") || seccionActual.equals("[ENEMIGOS]")) {
                    if (linea.equals("---")) {
                        if (personajeActual != null) {
                            if (seccionActual.equals("[HEROES]")) {
                                estado.heroes.add(personajeActual);
                            } else {
                                estado.enemigos.add(personajeActual);
                            }
                        }
                        personajeActual = new EstadoBatalla.DatosPersonaje();
                    }
                    else if (personajeActual != null) {
                        leerLineaPersonaje(linea, personajeActual);
                    }
                }
                else if (seccionActual.equals("[INVENTARIOS]")) {
                    if (linea.startsWith("Heroe:")) {
                        heroeActualInventario = linea.split(":")[1].trim();
                        estado.inventariosHeroes.put(heroeActualInventario, new HashMap<>());
                    }
                    else if (linea.startsWith("Item:") && heroeActualInventario != null) {
                        String[] partes = linea.split("\\|");
                        String nombreItem = partes[0].split(":")[1].trim();
                        int cantidad = Integer.parseInt(partes[1].split(":")[1].trim());
                        estado.inventariosHeroes.get(heroeActualInventario).put(nombreItem, cantidad);
                    }
                }
            }
            
            // Agregar último personaje si existe
            if (personajeActual != null) {
                if (seccionActual.equals("[HEROES]")) {
                    estado.heroes.add(personajeActual);
                } else if (seccionActual.equals("[ENEMIGOS]")) {
                    estado.enemigos.add(personajeActual);
                }
            }
            
            System.out.println("📂 Partida cargada exitosamente desde: " + rutaArchivo);
            System.out.println("   Turno: " + estado.turnoActual);
            System.out.println("   Héroes: " + estado.heroes.size());
            System.out.println("   Enemigos: " + estado.enemigos.size());
            
            return estado;
            
        } catch (IOException e) {
            throw new ExcepcionGuardadoPartida("Error al cargar partida: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lee una línea de datos de personaje
     */
    private static void leerLineaPersonaje(String linea, EstadoBatalla.DatosPersonaje p) {
        String[] partes = linea.split(":", 2);
        if (partes.length < 2) return;
        
        String clave = partes[0].trim();
        String valor = partes[1].trim();
        
        switch (clave) {
            case "Nombre" -> p.nombre = valor;
            case "Tipo" -> p.tipo = valor;
            case "HP" -> {
                String[] hp = valor.split("/");
                p.hpActual = Integer.parseInt(hp[0]);
                p.hpMax = Integer.parseInt(hp[1]);
            }
            case "MP" -> {
                String[] mp = valor.split("/");
                p.mpActual = Integer.parseInt(mp[0]);
                p.mpMax = Integer.parseInt(mp[1]);
            }
            case "Ataque" -> p.ataque = Integer.parseInt(valor);
            case "Defensa" -> p.defensa = Integer.parseInt(valor);
            case "Velocidad" -> p.velocidad = Integer.parseInt(valor);
            case "Estado" -> p.estado = valor;
            case "EstadoDuracion" -> p.estadoDuracion = Integer.parseInt(valor);
            case "Comportamiento" -> p.comportamiento = valor;
            case "Habilidades" -> p.habilidades = Arrays.asList(valor.split(","));
        }
    }
    
    /**
     * Lista todas las partidas guardadas
     */
    public static List<String> listarPartidas() {
        List<String> partidas = new ArrayList<>();
        File carpeta = new File(CARPETA_GUARDADOS);
        
        if (!carpeta.exists()) {
            return partidas;
        }
        
        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(EXTENSION));
        
        if (archivos != null) {
            for (File archivo : archivos) {
                String nombre = archivo.getName().replace(EXTENSION, "");
                partidas.add(nombre);
            }
        }
        
        return partidas;
    }
    
    /**
     * Elimina una partida guardada
     */
    public static boolean eliminarPartida(String nombrePartida) {
        String rutaArchivo = CARPETA_GUARDADOS + nombrePartida + EXTENSION;
        File archivo = new File(rutaArchivo);
        
        if (archivo.exists() && archivo.delete()) {
            System.out.println("🗑️ Partida eliminada: " + nombrePartida);
            return true;
        }
        
        return false;
    }
    
    /**
     * Crea un ItemStack desde el nombre del ítem
     */
    public static Item crearItemDesdeNombre(String nombreItem) {
        return switch (nombreItem) {
            case "Poción pequeña" -> new PocionCuracion("Poción pequeña", 30);
            case "Poción media" -> new PocionCuracion("Poción media", 60);
            case "Poción grande" -> new PocionCuracion("Poción grande", 100);
            case "Antídoto" -> new Antidoto("Antídoto");
            case "Éter" -> new PocionMagia("Éter", 20);
            case "Hierba Medicinal" -> new HierbaMedicinal();
            case "Agua Bendita" -> new AguaBendita();
            case "Semilla Mágica" -> new SemillaMagica();
            case "Ala de Quimera" -> new AlaQuimera();
            case "Pluma del Mundo" -> new PlumaMundo();
            default -> new PocionCuracion(nombreItem, 30); // Fallback
        };
    }
}