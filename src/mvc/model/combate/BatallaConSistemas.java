// ===== Archivo: src/mvc/model/combate/BatallaConSistemas.java =====
package mvc.model.combate;

import java.util.*;

import mvc.model.habilidades.*;
import mvc.model.items.*;
import mvc.model.personajes.*;
import mvc.model.historia.*;
import mvc.model.persistencia.*;
import mvc.model.exceptions.*;

/**
 * Batalla con todos los sistemas integrados:
 * - Inventarios individuales
 * - Sistema deshacer/rehacer
 * - Historial de acciones
 * - Persistencia (guardar/cargar)
 * - Manejo de excepciones
 */
public class BatallaConSistemas {
    private List<Personaje> heroes;
    private List<Personaje> enemigos;
    private Scanner sc;
    private Map<String, InventarioPersonal> inventariosHeroes; // Cada héroe tiene su inventario
    private MiniBoss miniBoss;
    private HistorialCombate historial;
    private int turno;
    
    public BatallaConSistemas(Scanner sc) {
        this.sc = sc;
        heroes = new ArrayList<>();
        enemigos = new ArrayList<>();
        inventariosHeroes = new HashMap<>();
        historial = new HistorialCombate();
        turno = 1;
        inicializar();
    }
    
    /**
     * Constructor para cargar partida guardada
     */
    public BatallaConSistemas(Scanner sc, EstadoBatalla estado) throws ExcepcionGuardadoPartida {
        this.sc = sc;
        this.historial = new HistorialCombate();
        this.turno = estado.turnoActual;
        
        cargarDesdeEstado(estado);
        
        System.out.println("\n✅ Batalla restaurada desde partida guardada");
        System.out.println("   Turno actual: " + turno);
    }
    
    /**
     * Inicializa personajes e inventarios individuales
     */
    private void inicializar() {
        // ========================================
        // HÉROES CON INVENTARIOS INDIVIDUALES
        // ========================================
        
        Heroe heroe = new Heroe("Héroe", 100, 50, 20, 10, 15);
        heroe.agregarHabilidad(new DanioMagico("Bola de Fuego", 10, 30));
        heroe.agregarHabilidad(new Curacion("Curar", 8, 25));
        heroe.agregarHabilidad(new DanioMagico("Gigaslash", 15, 40));
        
        Heroe yangus = new Heroe("Yangus", 120, 30, 25, 15, 10);
        yangus.agregarHabilidad(new GolpeCritico("Hachazo Brutal", 5));
        yangus.agregarHabilidad(new Aturdimiento("Golpe Aturdidor", 8));
        yangus.agregarHabilidad(new Curacion("Primeros Auxilios", 10, 20));

        Heroe jessica = new Heroe("Jessica", 80, 70, 18, 8, 20);
        jessica.agregarHabilidad(new DanioMagico("Rayo", 12, 35));
        jessica.agregarHabilidad(new Veneno("Toxina", 8));
        jessica.agregarHabilidad(new DanioMagico("Kafrizzle", 20, 50));
        jessica.agregarHabilidad(new Dormir("Hipnosis", 10));

        Heroe angelo = new Heroe("Angelo", 90, 60, 22, 12, 18);
        angelo.agregarHabilidad(new Curacion("Bendición", 10, 30));
        angelo.agregarHabilidad(new Paralisis("Toque Sagrado", 10));
        angelo.agregarHabilidad(new CuracionGrupal("Curación Divina", 20, 25));
        angelo.agregarHabilidad(new RemoverEstado("Purificación", 8));

        heroes.add(heroe);
        heroes.add(yangus);
        heroes.add(jessica);
        heroes.add(angelo);

        // Inicializar inventarios individuales
        try {
            inicializarInventarios();
        } catch (ExcepcionInventarioLleno e) {
            System.err.println("⚠️ Error al inicializar inventarios: " + e.getMessage());
        }

        // ========================================
        // ENEMIGOS (incluye Mini Jefe)
        // ========================================
        
        Enemigo fantasma = new Enemigo("Fantasma", 85, 40, 18, 8, 21, "estratégico");
        fantasma.agregarHabilidad(new Dormir("Pesadilla", 5));
        fantasma.agregarHabilidad(new Paralisis("Toque Espectral", 5));
        
        Enemigo slimeMetalico = new Enemigo("Slime Metálico", 50, 30, 15, 25, 30, "evasivo");
        slimeMetalico.agregarHabilidad(new Veneno("Baba Tóxica", 3));
        
        Enemigo orcoGuerrero = new Enemigo("Orco Guerrero", 95, 35, 22, 12, 16, "agresivo");
        orcoGuerrero.agregarHabilidad(new GolpeCritico("Hachazo Salvaje", 8));
        orcoGuerrero.agregarHabilidad(new Aturdimiento("Golpe Atronador", 6));

        miniBoss = new MiniBoss("Dragón Oscuro", 120, 40, 25, 12, 18, "agresivo");
        miniBoss.agregarHabilidad(new DanioMagico("Aliento de Fuego", 0, 35));
        miniBoss.agregarHabilidad(new DanioMagico("Llamarada Infernal", 15, 50));
        miniBoss.agregarHabilidad(new Aturdimiento("Rugido Aterrador", 10));

        enemigos.add(fantasma);
        enemigos.add(slimeMetalico);
        enemigos.add(orcoGuerrero);
        enemigos.add(miniBoss);
    }
    
    /**
     * Inicializa inventarios individuales de cada héroe
     */
    private void inicializarInventarios() throws ExcepcionInventarioLleno {
        for (Personaje p : heroes) {
            if (p instanceof Heroe) {
                InventarioPersonal inventario = new InventarioPersonal(p.getNombre());
                
                // Distribuir ítems según la clase del héroe
                switch (p.getNombre()) {
                    case "Héroe" -> {
                        inventario.agregarItem(new PocionCuracion("Poción media", 60), 2);
                        inventario.agregarItem(new HierbaMedicinal(), 3);
                        inventario.agregarItem(new AguaBendita(), 1);
                        inventario.agregarItem(new PlumaMundo(), 1);
                        inventario.agregarItem(new PocionMagia("Éter", 20), 2);
                    }
                    case "Yangus" -> {
                        inventario.agregarItem(new PocionCuracion("Poción grande", 100), 1);
                        inventario.agregarItem(new HierbaMedicinal(), 4);
                        inventario.agregarItem(new Antidoto("Antídoto"), 2);
                        inventario.agregarItem(new AlaQuimera(), 1);
                        inventario.agregarItem(new PocionMagia("Éter", 20), 2);
                    }
                    case "Jessica" -> {
                        inventario.agregarItem(new PocionMagia("Éter", 20), 3);
                        inventario.agregarItem(new SemillaMagica(), 2);
                        inventario.agregarItem(new HierbaMedicinal(), 2);
                        inventario.agregarItem(new AlaQuimera(), 1);
                        inventario.agregarItem(new PocionCuracion("Poción media", 60), 2);
                    }
                    case "Angelo" -> {
                        inventario.agregarItem(new PocionCuracion("Poción pequeña", 30), 3);
                        inventario.agregarItem(new AguaBendita(), 2);
                        inventario.agregarItem(new Antidoto("Antídoto"), 1);
                        inventario.agregarItem(new PlumaMundo(), 1);
                        inventario.agregarItem(new PocionMagia("Éter", 20), 3);
                    }
                }
                
                inventariosHeroes.put(p.getNombre(), inventario);
                System.out.println("🎒 Inventario de " + p.getNombre() + " inicializado: " + inventario);
            }
        }
    }
    
    /**
     * Bucle principal de combate con sistema completo
     */
    public void iniciarCombate() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║       ¡COMIENZA LA BATALLA EN EL REINO DE TRODAIN!           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println("\n⚡ COMANDOS ESPECIALES:");
        System.out.println("   - Durante tu turno, escribe 'guardar' para guardar la partida");
        System.out.println("   - Escribe 'deshacer' para deshacer la última acción");
        System.out.println("   - Escribe 'rehacer' para rehacer una acción deshecha");
        System.out.println();

        while (true) {
            // Mostrar estado visual
            System.out.println("\n┌───────────────────────────────────────────────────────────────┐");
            System.out.println("│                 TURNO " + turno + " - ESTADO DEL COMBATE              │");
            System.out.println("└───────────────────────────────────────────────────────────────┘");
            
            System.out.println("\n▸ HÉROES:");
            for (Personaje h : heroes) h.mostrarEstado();
            
            System.out.println("\n▸ ENEMIGOS:");
            for (Personaje e : enemigos) e.mostrarEstado();
            System.out.println("═══════════════════════════════════════════════════════════════\n");

            // Comprobar final de combate
            if (todosMuertos(enemigos)) {
                mostrarVictoria();
                break;
            }
            if (todosMuertos(heroes)) {
                mostrarDerrota();
                break;
            }

            // Construir lista de turno y ordenar por velocidad
            List<Personaje> orden = new ArrayList<>();
            orden.addAll(heroes);
            orden.addAll(enemigos);
            orden.sort((a, b) -> {
                if (b.getVelocidad() != a.getVelocidad()) 
                    return b.getVelocidad() - a.getVelocidad();
                if (a instanceof MiniBoss) return -1;
                if (b instanceof MiniBoss) return 1;
                if (a instanceof Heroe && b instanceof Enemigo) return -1;
                if (a instanceof Enemigo && b instanceof Heroe) return 1;
                return 0;
            });

            // Ejecutar turnos
            for (Personaje p : orden) {
                if (!p.estaVivo()) continue;

                try {
                    if (p instanceof Heroe) {
                        tomarTurnoHeroeConSistema((Heroe) p);
                    } else {
                        tomarTurnoEnemigo(p);
                    }
                } catch (ExcepcionJuego e) {
                    System.err.println("⚠️ Error en turno: " + e.getMessage());
                }
                
                if (todosMuertos(enemigos) || todosMuertos(heroes)) break;
            }
            
            turno++;
        }
    }
    
    /**
     * Turno de héroe con sistema completo (inventario personal, excepciones, historial)
     */
    private void tomarTurnoHeroeConSistema(Heroe heroe) throws ExcepcionJuego {
        if (!heroe.estaVivo()) {
            throw new ExcepcionPersonajeMuerto(heroe.getNombre());
        }

        boolean puede = heroe.procesarEstadosAntesDeActuar();
        if (!puede) return;

        System.out.println("\n▸ Turno de " + heroe.getNombre() + " (HP: " + heroe.getHpActual() + 
                         " MP: " + heroe.getMpActual() + ")");
        System.out.println("1. Atacar");
        System.out.println("2. Usar habilidad");
        System.out.println("3. Usar ítem (inventario personal)");
        System.out.println("4. Ver inventario");
        System.out.println("5. Guardar partida");
        System.out.println("6. Deshacer última acción");
        System.out.println("7. Rehacer acción");
        System.out.print("Elige acción: ");
        
        int opcion = sc.nextInt();
        
        switch (opcion) {
            case 1 -> atacarConHistorial(heroe, enemigos);
            case 2 -> usarHabilidadConHistorial(heroe, enemigos);
            case 3 -> usarItemPersonal(heroe);
            case 4 -> mostrarInventarioPersonal(heroe);
            case 5 -> guardarPartida();
            case 6 -> deshacerAccion();
            case 7 -> rehacerAccion();
            default -> System.out.println("Opción inválida.");
        }
    }
    
    /**
     * Atacar registrando en el historial
     */
    private void atacarConHistorial(Heroe heroe, List<Personaje> enemigos) {
        List<Personaje> vivos = obtenerVivos(enemigos);
        if (vivos.isEmpty()) return;
        
        System.out.println("Seleccione objetivo:");
        mostrarObjetivos(vivos);
        
        int eleccion = sc.nextInt();
        if (eleccion < 1 || eleccion > vivos.size()) {
            System.out.println("Elección inválida.");
            return;
        }
        
        Personaje objetivo = vivos.get(eleccion - 1);
        int hpAntes = objetivo.getHpActual();
        
        System.out.println(heroe.getNombre() + " ataca a " + objetivo.getNombre());
        objetivo.recibirDaño(heroe.getAtaque());
        
        int hpDespues = objetivo.getHpActual();
        
        // Registrar acción en el historial
        AccionCombate accion = new AccionCombate(
            AccionCombate.TipoAccion.ATAQUE,
            heroe.getNombre(),
            objetivo.getNombre(),
            hpAntes,
            hpDespues,
            heroe.getNombre() + " atacó a " + objetivo.getNombre() + 
            " (" + (hpAntes - hpDespues) + " daño)"
        );
        historial.registrarAccion(accion);
    }
    
    /**
     * Usar habilidad registrando en el historial
     */
    private void usarHabilidadConHistorial(Heroe heroe, List<Personaje> enemigos) 
            throws ExcepcionMPInsuficiente {
        
        if (heroe.getHabilidades().isEmpty()) {
            System.out.println("No tienes habilidades.");
            return;
        }
        
        System.out.println("Elige una habilidad:");
        List<Habilidad> habilidades = heroe.getHabilidades();
        for (int i = 0; i < habilidades.size(); i++) {
            Habilidad h = habilidades.get(i);
            System.out.println((i + 1) + ". " + h.getNombre() + " (Costo MP: " + h.getCostoMP() + ")");
        }
        
        int eleccion = sc.nextInt();
        if (eleccion < 1 || eleccion > habilidades.size()) {
            System.out.println("Elección inválida.");
            return;
        }
        
        Habilidad habilidad = habilidades.get(eleccion - 1);
        
        if (heroe.getMpActual() < habilidad.getCostoMP()) {
            throw new ExcepcionMPInsuficiente(habilidad.getCostoMP(), heroe.getMpActual());
        }
        
        List<Personaje> vivos = obtenerVivos(enemigos);
        if (vivos.isEmpty()) return;
        
        System.out.println("Selecciona objetivo:");
        mostrarObjetivos(vivos);
        
        int objetivoIdx = sc.nextInt();
        if (objetivoIdx < 1 || objetivoIdx > vivos.size()) {
            System.out.println("Elección inválida.");
            return;
        }
        
        Personaje objetivo = vivos.get(objetivoIdx - 1);
        int hpAntes = objetivo.getHpActual();
        
        heroe.consumirMP(habilidad.getCostoMP());
        habilidad.ejecutar(heroe, objetivo);
        
        // Registrar en historial
        AccionCombate accion = new AccionCombate(
            AccionCombate.TipoAccion.HABILIDAD,
            heroe.getNombre(),
            objetivo.getNombre(),
            hpAntes,
            objetivo.getHpActual(),
            heroe.getNombre() + " usó " + habilidad.getNombre() + " sobre " + objetivo.getNombre()
        );
        historial.registrarAccion(accion);
    }
    
    /**
     * Usar ítem del inventario personal
     */
    private void usarItemPersonal(Heroe heroe) throws ExcepcionJuego {
        InventarioPersonal inventario = inventariosHeroes.get(heroe.getNombre());
        
        if (inventario.estaVacio()) {
            System.out.println("⚠️ Tu inventario está vacío.");
            return;
        }
        
        System.out.println("\n🎒 INVENTARIO DE " + heroe.getNombre().toUpperCase());
        List<Item> items = inventario.listarItems();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            int cantidad = inventario.getCantidad(item.getNombre());
            System.out.println((i + 1) + ". " + item.getNombre() + " x" + cantidad);
        }
        
        System.out.print("Elige ítem: ");
        int idx = sc.nextInt();
        if (idx < 1 || idx > items.size()) {
            System.out.println("Selección inválida.");
            return;
        }
        
        Item elegido = items.get(idx - 1);
        
        // Decidir objetivo (héroe o enemigo)
        System.out.println("¿Usar en...?");
        System.out.println("1. Aliado");
        System.out.println("2. Enemigo");
        int tipo = sc.nextInt();
        
        Personaje objetivo;
        if (tipo == 1) {
            List<Personaje> vivosAliados = obtenerVivos(heroes);
            mostrarObjetivos(vivosAliados);
            int sel = sc.nextInt();
            if (sel < 1 || sel > vivosAliados.size()) return;
            objetivo = vivosAliados.get(sel - 1);
        } else {
            List<Personaje> vivosEnemigos = obtenerVivos(enemigos);
            mostrarObjetivos(vivosEnemigos);
            int sel = sc.nextInt();
            if (sel < 1 || sel > vivosEnemigos.size()) return;
            objetivo = vivosEnemigos.get(sel - 1);
        }
        
        int hpAntes = objetivo.getHpActual();
        boolean usado = inventario.usarItem(elegido.getNombre(), heroe, objetivo);
        
        if (usado) {
            System.out.println("✅ " + elegido.getNombre() + " usado sobre " + objetivo.getNombre());
            
            // Registrar en historial
            AccionCombate accion = new AccionCombate(
                AccionCombate.TipoAccion.ITEM,
                heroe.getNombre(),
                objetivo.getNombre(),
                hpAntes,
                objetivo.getHpActual(),
                heroe.getNombre() + " usó " + elegido.getNombre() + " sobre " + objetivo.getNombre()
            );
            historial.registrarAccion(accion);
        }
    }
    
    /**
     * Muestra el inventario personal del héroe
     */
    private void mostrarInventarioPersonal(Heroe heroe) {
        InventarioPersonal inventario = inventariosHeroes.get(heroe.getNombre());
        
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   🎒 INVENTARIO DE " + heroe.getNombre().toUpperCase());
        System.out.println("╠════════════════════════════════════════════╣");
        
        if (inventario.estaVacio()) {
            System.out.println("║   (Vacío)                                  ║");
        } else {
            List<Item> items = inventario.listarItems();
            for (Item item : items) {
                int cantidad = inventario.getCantidad(item.getNombre());
                System.out.printf("║   • %-30s x%d   ║\n", item.getNombre(), cantidad);
            }
        }
        
        System.out.println("║                                            ║");
        System.out.printf("║   Espacio: %d/%d                           ║\n", 
            5 - inventario.getEspacioDisponible(), 5);
        System.out.println("╚════════════════════════════════════════════╝\n");
    }
    
    /**
     * Guarda el estado actual de la batalla
     */
    private void guardarPartida() {
        System.out.print("\n💾 Nombre de la partida: ");
        String nombre = sc.next();
        
        try {
            EstadoBatalla estado = crearEstadoActual();
            GestorPersistencia.guardarPartida(nombre, estado);
            System.out.println("✅ Partida guardada exitosamente");
        } catch (ExcepcionGuardadoPartida e) {
            System.err.println("❌ Error al guardar: " + e.getMessage());
        }
    }
    
    /**
     * Crea el objeto EstadoBatalla con el estado actual
     */
    private EstadoBatalla crearEstadoActual() {
        EstadoBatalla estado = new EstadoBatalla();
        estado.turnoActual = turno;
        
        // Guardar héroes
        for (Personaje h : heroes) {
            EstadoBatalla.DatosPersonaje datos = new EstadoBatalla.DatosPersonaje();
            datos.nombre = h.getNombre();
            datos.tipo = "Heroe";
            datos.hpActual = h.getHpActual();
            datos.hpMax = h.getHpMax();
            datos.mpActual = h.getMpActual();
            datos.mpMax = h.getMpMax();
            datos.ataque = h.getAtaque();
            datos.defensa = h.getDefensa();
            datos.velocidad = h.getVelocidad();
            datos.estado = h.getEstado().toString();
            datos.estadoDuracion = h.getEstadoDuracion();
            
            for (Habilidad hab : h.getHabilidades()) {
                datos.habilidades.add(hab.getNombre());
            }
            
            estado.heroes.add(datos);
        }
        
        // Guardar enemigos
        for (Personaje e : enemigos) {
            EstadoBatalla.DatosPersonaje datos = new EstadoBatalla.DatosPersonaje();
            datos.nombre = e.getNombre();
            datos.tipo = e instanceof MiniBoss ? "MiniBoss" : "Enemigo";
            datos.hpActual = e.getHpActual();
            datos.hpMax = e.getHpMax();
            datos.mpActual = e.getMpActual();
            datos.mpMax = e.getMpMax();
            datos.ataque = e.getAtaque();
            datos.defensa = e.getDefensa();
            datos.velocidad = e.getVelocidad();
            datos.estado = e.getEstado().toString();
            datos.estadoDuracion = e.getEstadoDuracion();
            
            if (e instanceof Enemigo) {
                // Comportamiento se guarda usando reflection o cast
                datos.comportamiento = "agresivo"; // Por simplicidad
            }
            
            estado.enemigos.add(datos);
        }
        
        // Guardar inventarios
        for (Map.Entry<String, InventarioPersonal> entry : inventariosHeroes.entrySet()) {
            estado.inventariosHeroes.put(entry.getKey(), 
                entry.getValue().obtenerItemsParaGuardar());
        }
        
        return estado;
    }
    
    /**
     * Carga batalla desde estado guardado
     */
    private void cargarDesdeEstado(EstadoBatalla estado) throws ExcepcionGuardadoPartida {
        heroes = new ArrayList<>();
        enemigos = new ArrayList<>();
        inventariosHeroes = new HashMap<>();
        
        // Cargar héroes (simplificado - necesitarías reconstruir habilidades)
        for (EstadoBatalla.DatosPersonaje datos : estado.heroes) {
            Heroe heroe = new Heroe(datos.nombre, datos.hpMax, datos.mpMax, 
                                   datos.ataque, datos.defensa, datos.velocidad);
            heroe.setHpActual(datos.hpActual);
            heroe.setMpActual(datos.mpActual);
            // Restaurar estado, habilidades, etc.
            heroes.add(heroe);
        }
        
        // Similar para enemigos...
        // Y cargar inventarios desde estado.inventariosHeroes
        
        System.out.println("✅ Estado de batalla cargado");
    }
    
    /**
     * Deshacer última acción
     */
    private void deshacerAccion() {
        List<Personaje> todos = new ArrayList<>();
        todos.addAll(heroes);
        todos.addAll(enemigos);
        
        AccionCombate accion = historial.deshacer(todos);
        if (accion != null) {
            System.out.println("✅ Acción deshecha: " + accion.getDescripcion());
        }
    }
    
    /**
     * Rehacer acción
     */
    private void rehacerAccion() {
        List<Personaje> todos = new ArrayList<>();
        todos.addAll(heroes);
        todos.addAll(enemigos);
        
        AccionCombate accion = historial.rehacer(todos);
        if (accion != null) {
            System.out.println("✅ Acción rehecha: " + accion.getDescripcion());
        }
    }
    
    /**
     * Turno de enemigo (sin cambios)
     */
    private void tomarTurnoEnemigo(Personaje enemigo) {
        System.out.println("\n▸ Turno de " + enemigo.getNombre() + " (Enemigo)");
        enemigo.tomarTurno(enemigos, heroes, sc);
    }
    
    // Métodos auxiliares
    private boolean todosMuertos(List<Personaje> lista) {
        return lista.stream().noneMatch(Personaje::estaVivo);
    }
    
    private List<Personaje> obtenerVivos(List<Personaje> lista) {
        return lista.stream().filter(Personaje::estaVivo).toList();
    }
    
    private void mostrarObjetivos(List<Personaje> vivos) {
        for (int i = 0; i < vivos.size(); i++) {
            System.out.println((i + 1) + ". " + vivos.get(i).getNombre() + 
                             " (HP: " + vivos.get(i).getHpActual() + ")");
        }
    }
    
    private void mostrarVictoria() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                          ¡VICTORIA!                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println("\n⭐ Los héroes han derrotado al " + miniBoss.getNombre() + "!");
        System.out.println("   Experiencia ganada: " + (50 * enemigos.size()));
        System.out.println("   Oro ganado: " + (100 * enemigos.size()));
    }
    
    private void mostrarDerrota() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                          DERROTA...                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}