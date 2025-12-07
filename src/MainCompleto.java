// ===== Archivo: src/MainCompleto.java =====
import java.util.*;

import mvc.model.combate.BatallaConSistemas;
import mvc.model.gremio.*;
import mvc.model.persistencia.*;
import mvc.model.registro.*;
import mvc.model.exceptions.*;

/**
 * MainCompleto - Menú principal con todas las funcionalidades integradas
 * 
 * FUNCIONALIDADES:
 * 1. Iniciar nueva batalla
 * 2. Cargar partida guardada
 * 3. Gestión del gremio (turnos, aventureros)
 * 4. Ver historial de batallas
 * 5. Gestionar partidas guardadas
 */
public class MainCompleto {
    private static Scanner sc = new Scanner(System.in);
    private static SistemaGremio gremio;
    
    public static void main(String[] args) {
        // Inicializar sistema del gremio
        gremio = new SistemaGremio("Gremio de Trodain");
        
        // Registrar algunos aventureros de ejemplo
        inicializarAventureros();
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     🎮 DRAGON QUEST VIII - SIMULADOR DE COMBATE 🎮          ║");
        System.out.println("║                   Sistema Completo v2.0                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        
        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerOpcion();
            
            try {
                procesarOpcion(opcion);
            } catch (ExcepcionJuego e) {
                System.err.println("\n❌ ERROR: " + e.getMessage());
                System.err.println("Presiona ENTER para continuar...");
                sc.nextLine();
            }
            
        } while (opcion != 9);
        
        System.out.println("\n¡Hasta luego, aventurero! 👋");
        sc.close();
    }
    
    private static void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    MENÚ PRINCIPAL                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. ⚔️  Iniciar Nueva Batalla                             ║");
        System.out.println("║  2. 📂 Cargar Partida Guardada                            ║");
        System.out.println("║  3. 🏛️  Gestionar Gremio de Aventureros                   ║");
        System.out.println("║  4. 📊 Ver Historial de Batallas                          ║");
        System.out.println("║  5. 💾 Gestionar Partidas Guardadas                       ║");
        System.out.println("║  6. 📋 Ver Registro de Aventureros                        ║");
        System.out.println("║  7. ℹ️  Ayuda y Tutorial                                   ║");
        System.out.println("║  8. ⚙️  Configuración                                     ║");
        System.out.println("║  9. 🚪 Salir                                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("\nElige una opción: ");
    }
    
    private static void procesarOpcion(int opcion) throws ExcepcionJuego {
        switch (opcion) {
            case 1 -> iniciarNuevaBatalla();
            case 2 -> cargarPartidaGuardada();
            case 3 -> gestionarGremio();
            case 4 -> verHistorialBatallas();
            case 5 -> gestionarPartidas();
            case 6 -> verRegistroAventureros();
            case 7 -> mostrarAyuda();
            case 8 -> mostrarConfiguracion();
            case 9 -> System.out.println("\nCerrando juego...");
            default -> System.out.println("⚠️ Opción inválida. Intenta de nuevo.");
        }
    }
    
    // ========== OPCIÓN 1: INICIAR NUEVA BATALLA ==========
    private static void iniciarNuevaBatalla() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              ⚔️  INICIAR NUEVA BATALLA ⚔️                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\nPreparando combate...");
        System.out.println("• Reuniendo al grupo de héroes...");
        System.out.println("• Generando encuentro enemigo...");
        System.out.println("• Inicializando inventarios personales...");
        System.out.println();
        
        try {
            BatallaConSistemas batalla = new BatallaConSistemas(sc);
            batalla.iniciarCombate();
            
            // Registrar batalla en el historial
            System.out.print("\n¿Fue victoria? (s/n): ");
            String respuesta = sc.next();
            boolean victoria = respuesta.equalsIgnoreCase("s");
            
            gremio.getHistorialBatallas().registrarBatalla(
                victoria, 
                10, // turnosTranscurridos (simplificado)
                Arrays.asList("Héroe", "Yangus", "Jessica", "Angelo"),
                Arrays.asList("Fantasma", "Slime Metálico", "Orco Guerrero", "Dragón Oscuro")
            );
            
            System.out.println("\n✅ Batalla registrada en el historial del gremio");
            
        } catch (Exception e) {
            System.err.println("❌ Error al iniciar batalla: " + e.getMessage());
            e.printStackTrace();
        }
        
        esperarEnter();
    }
    
    // ========== OPCIÓN 2: CARGAR PARTIDA ==========
    private static void cargarPartidaGuardada() throws ExcepcionGuardadoPartida {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              📂 CARGAR PARTIDA GUARDADA                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        List<String> partidas = GestorPersistencia.listarPartidas();
        
        if (partidas.isEmpty()) {
            System.out.println("\n⚠️ No hay partidas guardadas disponibles.");
            esperarEnter();
            return;
        }
        
        System.out.println("\n📁 PARTIDAS DISPONIBLES:");
        for (int i = 0; i < partidas.size(); i++) {
            System.out.println((i + 1) + ". " + partidas.get(i));
        }
        
        System.out.print("\nSelecciona partida (0 para cancelar): ");
        int seleccion = leerOpcion();
        
        if (seleccion == 0 || seleccion > partidas.size()) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        String nombrePartida = partidas.get(seleccion - 1);
        System.out.println("\n📂 Cargando partida: " + nombrePartida + "...");
        
        EstadoBatalla estado = GestorPersistencia.cargarPartida(nombrePartida);
        BatallaConSistemas batalla = new BatallaConSistemas(sc, estado);
        
        System.out.println("✅ Partida cargada. Continuando combate...\n");
        batalla.iniciarCombate();
        
        esperarEnter();
    }
    
    // ========== OPCIÓN 3: GESTIONAR GREMIO ==========
    private static void gestionarGremio() {
        ColaTurnosGremio colaTurnos = gremio.getColaTurnos();
        
        int opcion;
        do {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║          🏛️  GREMIO DE AVENTUREROS - TRODAIN 🏛️           ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println("\n1. 📝 Agregar nueva solicitud");
            System.out.println("2. 🎯 Atender siguiente solicitud");
            System.out.println("3. 👁️  Ver solicitudes pendientes");
            System.out.println("4. 📊 Ver estadísticas del gremio");
            System.out.println("5. 📜 Ver historial de solicitudes atendidas");
            System.out.println("6. ❌ Cancelar solicitud");
            System.out.println("0. ↩️  Volver al menú principal");
            System.out.print("\nElige opción: ");
            
            opcion = leerOpcion();
            
            switch (opcion) {
                case 1 -> agregarSolicitudGremio(colaTurnos);
                case 2 -> atenderSiguienteSolicitud(colaTurnos);
                case 3 -> verSolicitudesPendientes(colaTurnos);
                case 4 -> verEstadisticasGremio(colaTurnos);
                case 5 -> verHistorialAtendidos(colaTurnos);
                case 6 -> cancelarSolicitud(colaTurnos);
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("⚠️ Opción inválida.");
            }
            
        } while (opcion != 0);
    }
    
    private static void agregarSolicitudGremio(ColaTurnosGremio cola) {
        System.out.println("\n➕ NUEVA SOLICITUD AL GREMIO");
        
        System.out.print("Nombre del aventurero: ");
        sc.nextLine(); // Limpiar buffer
        String nombre = sc.nextLine();
        
        System.out.println("\nTipo de solicitud:");
        SolicitudGremio.TipoSolicitud[] tipos = SolicitudGremio.TipoSolicitud.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + ". " + tipos[i].getDescripcion());
        }
        System.out.print("Selecciona tipo: ");
        int tipoIdx = leerOpcion() - 1;
        
        if (tipoIdx < 0 || tipoIdx >= tipos.length) {
            System.out.println("⚠️ Tipo inválido.");
            return;
        }
        
        System.out.println("\nPrioridad:");
        SolicitudGremio.PrioridadSolicitud[] prioridades = SolicitudGremio.PrioridadSolicitud.values();
        for (int i = 0; i < prioridades.length; i++) {
            System.out.println((i + 1) + ". " + prioridades[i].getIcono());
        }
        System.out.print("Selecciona prioridad: ");
        int prioridadIdx = leerOpcion() - 1;
        
        if (prioridadIdx < 0 || prioridadIdx >= prioridades.length) {
            System.out.println("⚠️ Prioridad inválida.");
            return;
        }
        
        cola.agregarSolicitud(nombre, tipos[tipoIdx], prioridades[prioridadIdx]);
        esperarEnter();
    }
    
    private static void atenderSiguienteSolicitud(ColaTurnosGremio cola) {
        SolicitudGremio solicitud = cola.atenderSiguiente();
        if (solicitud != null) {
            System.out.println("\n✅ Solicitud atendida exitosamente");
            System.out.println("   " + solicitud);
        }
        esperarEnter();
    }
    
    private static void verSolicitudesPendientes(ColaTurnosGremio cola) {
        System.out.println("\n📋 SOLICITUDES PENDIENTES (por prioridad):");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        List<SolicitudGremio> pendientes = cola.listarPendientes();
        
        if (pendientes.isEmpty()) {
            System.out.println("✅ No hay solicitudes pendientes");
        } else {
            for (SolicitudGremio sol : pendientes) {
                System.out.println(sol);
            }
        }
        
        esperarEnter();
    }
    
    private static void verEstadisticasGremio(ColaTurnosGremio cola) {
        System.out.println(cola.obtenerEstadisticas());
        esperarEnter();
    }
    
    private static void verHistorialAtendidos(ColaTurnosGremio cola) {
        System.out.println("\n📜 HISTORIAL DE SOLICITUDES ATENDIDAS:");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        List<SolicitudGremio> atendidos = cola.obtenerHistorialAtendidos();
        
        if (atendidos.isEmpty()) {
            System.out.println("📭 No se han atendido solicitudes aún");
        } else {
            for (SolicitudGremio sol : atendidos) {
                System.out.println("✅ " + sol);
            }
        }
        
        esperarEnter();
    }
    
    private static void cancelarSolicitud(ColaTurnosGremio cola) {
        System.out.print("\nNúmero de solicitud a cancelar: ");
        int numero = leerOpcion();
        
        if (cola.cancelarSolicitud(numero)) {
            System.out.println("✅ Solicitud cancelada");
        } else {
            System.out.println("⚠️ No se encontró la solicitud");
        }
        
        esperarEnter();
    }
    
    // ========== OPCIÓN 4: VER HISTORIAL DE BATALLAS ==========
    private static void verHistorialBatallas() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           📊 HISTORIAL DE BATALLAS COMPLETAS              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        HistorialBatallas historial = gremio.getHistorialBatallas();
        
        System.out.println(historial.obtenerEstadisticas());
        
        System.out.println("\n📜 ÚLTIMAS 10 BATALLAS:");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        List<RegistroBatalla> ultimas = historial.obtenerUltimasBatallas(10);
        
        if (ultimas.isEmpty()) {
            System.out.println("📭 No hay batallas registradas aún");
        } else {
            for (RegistroBatalla batalla : ultimas) {
                System.out.println(batalla);
            }
        }
        
        esperarEnter();
    }
    
    // ========== OPCIÓN 5: GESTIONAR PARTIDAS ==========
    private static void gestionarPartidas() throws ExcepcionGuardadoPartida {
        int opcion;
        do {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║           💾 GESTIÓN DE PARTIDAS GUARDADAS                ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println("\n1. 📋 Listar partidas guardadas");
            System.out.println("2. 🗑️  Eliminar partida");
            System.out.println("0. ↩️  Volver");
            System.out.print("\nElige opción: ");
            
            opcion = leerOpcion();
            
            switch (opcion) {
                case 1 -> listarPartidas();
                case 2 -> eliminarPartida();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("⚠️ Opción inválida.");
            }
            
        } while (opcion != 0);
    }
    
    private static void listarPartidas() {
        List<String> partidas = GestorPersistencia.listarPartidas();
        
        System.out.println("\n📁 PARTIDAS GUARDADAS:");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        if (partidas.isEmpty()) {
            System.out.println("📭 No hay partidas guardadas");
        } else {
            for (int i = 0; i < partidas.size(); i++) {
                System.out.println((i + 1) + ". 💾 " + partidas.get(i));
            }
        }
        
        esperarEnter();
    }
    
    private static void eliminarPartida() {
        List<String> partidas = GestorPersistencia.listarPartidas();
        
        if (partidas.isEmpty()) {
            System.out.println("\n⚠️ No hay partidas para eliminar.");
            esperarEnter();
            return;
        }
        
        System.out.println("\n📁 PARTIDAS DISPONIBLES:");
        for (int i = 0; i < partidas.size(); i++) {
            System.out.println((i + 1) + ". " + partidas.get(i));
        }
        
        System.out.print("\nSelecciona partida a eliminar (0 para cancelar): ");
        int seleccion = leerOpcion();
        
        if (seleccion == 0 || seleccion > partidas.size()) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        String nombrePartida = partidas.get(seleccion - 1);
        
        System.out.print("⚠️ ¿Confirmas eliminar '" + nombrePartida + "'? (s/n): ");
        String confirmacion = sc.next();
        
        if (confirmacion.equalsIgnoreCase("s")) {
            if (GestorPersistencia.eliminarPartida(nombrePartida)) {
                System.out.println("✅ Partida eliminada exitosamente");
            } else {
                System.out.println("❌ Error al eliminar partida");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
        
        esperarEnter();
    }
    
    // ========== OPCIÓN 6: VER REGISTRO DE AVENTUREROS ==========
    private static void verRegistroAventureros() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           📋 REGISTRO DE AVENTUREROS (A-Z)                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        RegistroAventureros registro = gremio.getRegistroAventureros();
        
        System.out.println(registro.obtenerEstadisticas());
        
        System.out.println("\n👥 AVENTUREROS REGISTRADOS:");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        List<mvc.model.personajes.Heroe> aventureros = registro.listarAventurerosOrdenados();
        
        if (aventureros.isEmpty()) {
            System.out.println("📭 No hay aventureros registrados");
        } else {
            for (mvc.model.personajes.Heroe heroe : aventureros) {
                System.out.printf("⚔️  %-15s | HP: %3d | MP: %3d | ATK: %2d | DEF: %2d | VEL: %2d\n",
                    heroe.getNombre(), heroe.getHpMax(), heroe.getMpMax(),
                    heroe.getAtaque(), heroe.getDefensa(), heroe.getVelocidad());
            }
        }
        
        esperarEnter();
    }
    
    // ========== OPCIÓN 7: AYUDA Y TUTORIAL ==========
    private static void mostrarAyuda() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              ℹ️  AYUDA Y TUTORIAL DEL JUEGO                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        System.out.println("""
            
            📖 GUÍA RÁPIDA:
            
            🎮 COMBATE:
            • Cada héroe tiene 5 espacios de inventario personal
            • Puedes deshacer/rehacer acciones durante la batalla
            • Guarda tu progreso en cualquier momento durante tu turno
            
            🏛️ GREMIO:
            • Las solicitudes se atienden por prioridad (Urgente > Alta > Normal > Baja)
            • Si hay empate en prioridad, se usa FIFO (primero en llegar)
            • El historial registra todas las batallas completadas
            
            💾 PARTIDAS:
            • Las partidas se guardan en formato texto en la carpeta 'partidas/'
            • Puedes cargar cualquier partida guardada para continuar
            
            🎒 INVENTARIO:
            • Cada héroe tiene inventario personal de 5 ítems
            • Los ítems incluyen: Pociones, Hierbas, Agua Bendita, Pluma del Mundo, etc.
            
            📊 ESTRUCTURAS DE DATOS USADAS:
            • LinkedHashMap: Inventarios (O(1) búsqueda, orden preservado)
            • Stack (Deque): Deshacer/Rehacer (LIFO)
            • Queue: Historial de batallas (FIFO)
            • PriorityQueue: Turnos del gremio (por prioridad)
            • TreeMap: Registro de aventureros (orden alfabético)
            
            🔧 COMANDOS ESPECIALES EN COMBATE:
            • 'guardar' - Guarda la partida actual
            • 'deshacer' - Deshace la última acción
            • 'rehacer' - Rehace una acción deshecha
            
            """);
        
        esperarEnter();
    }
    
    // ========== OPCIÓN 8: CONFIGURACIÓN ==========
    private static void mostrarConfiguracion() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              ⚙️  CONFIGURACIÓN DEL SISTEMA                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        System.out.println("""
            
            ⚙️ CONFIGURACIÓN ACTUAL:
            
            📂 Sistema de Archivos:
            • Carpeta de guardados: partidas/
            • Formato de archivo: .dq8save
            • Capacidad máxima de historial: 10 acciones
            
            🎒 Inventarios:
            • Capacidad por héroe: 5 ítems
            • Sistema: Inventario personal individual
            
            🏛️ Gremio:
            • Capacidad cola de turnos: Ilimitada
            • Sistema de prioridades: 4 niveles
            
            📊 Historial:
            • Batallas guardadas: 50 máximo
            • Sistema: Queue FIFO con límite
            
            ⚡ Rendimiento:
            • Búsquedas en inventario: O(1)
            • Deshacer/Rehacer: O(1)
            • Ordenamiento aventureros: O(log n)
            • Atención turnos gremio: O(log n)
            
            """);
        
        esperarEnter();
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private static void inicializarAventureros() {
        RegistroAventureros registro = gremio.getRegistroAventureros();
        
        // Registrar héroes principales
        registro.registrarAventurero(new mvc.model.personajes.Heroe("Héroe", 100, 50, 20, 10, 25));
        registro.registrarAventurero(new mvc.model.personajes.Heroe("Yangus", 120, 30, 25, 15, 22));
        registro.registrarAventurero(new mvc.model.personajes.Heroe("Jessica", 80, 70, 18, 8, 28));
        registro.registrarAventurero(new mvc.model.personajes.Heroe("Angelo", 90, 60, 22, 12, 24));
        
        // Aventureros adicionales del gremio
        registro.registrarAventurero(new mvc.model.personajes.Heroe("Red", 85, 45, 18, 12, 20));
        registro.registrarAventurero(new mvc.model.personajes.Heroe("Morrie", 95, 40, 23, 14, 19));
        
        System.out.println("✅ Aventureros inicializados en el gremio");
    }
    
    private static int leerOpcion() {
        try {
            return sc.nextInt();
        } catch (InputMismatchException e) {
            sc.nextLine(); // Limpiar buffer
            return -1;
        }
    }
    
    private static void esperarEnter() {
        System.out.print("\nPresiona ENTER para continuar...");
        try {
            System.in.read();
            sc.nextLine(); // Limpiar buffer
        } catch (Exception e) {
            // Ignorar
        }
    }
}