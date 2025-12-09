// ===== Archivo: src/mvc/view/gui/VentanaCombateCompleta.java =====
package mvc.view.gui;

import mvc.model.estados.EstadoAlterado;
import mvc.model.habilidades.*;
import mvc.model.items.*;
import mvc.model.personajes.*;
import mvc.model.historia.*;
import mvc.model.persistencia.*;
import mvc.model.exceptions.*;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * VentanaCombateCompleta - Interfaz gráfica con todas las nuevas funcionalidades:
 * - Inventarios individuales por héroe
 * - Sistema deshacer/rehacer
 * - Guardar/cargar partida
 * - Manejo de excepciones visual
 */
public class VentanaCombateCompleta extends JFrame {
    // Componentes principales
    private JPanel panelHeroes, panelEnemigos, panelAcciones;
    private JTextArea logCombate;
    private JScrollPane scrollLog;
    private List<PanelPersonaje> panelesHeroes;
    private List<PanelPersonaje> panelesEnemigos;
    private JLabel lblTurno;
    private JButton btnDeshacer, btnRehacer, btnGuardar;
    
    // Lógica de combate con nuevos sistemas
    private List<Personaje> heroes;
    private List<Personaje> enemigos;
    private Map<String, InventarioPersonal> inventariosHeroes; // NUEVO: Inventarios individuales
    private HistorialCombate historial; // NUEVO: Sistema deshacer/rehacer
    private Personaje personajeActual;
    private int turno = 1;
    
    // Audio
    private Clip clipMusica;
    
    // Fondo
    private Image fondo;
    
    // System.out original para restaurar
    private PrintStream salidaOriginal;
    
    // Scanner para enemigos
    private Scanner scannerEnemigos;
    
    public VentanaCombateCompleta() {
        setTitle("Combate - Dragon Quest VIII (Sistema Completo)");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Cargar fondo
        try {
            URL ruta = getClass().getResource("/imagenes/fondo_azul.png");
            if (ruta != null) {
                fondo = new ImageIcon(ruta).getImage();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al cargar fondo: " + e.getMessage());
        }
        
        // Guardar salida original
        salidaOriginal = System.out;
        
        // Crear scanner para enemigos
        scannerEnemigos = new Scanner("dummy");
        
        // Inicializar estructuras de datos
        heroes = new ArrayList<>();
        enemigos = new ArrayList<>();
        inventariosHeroes = new HashMap<>();
        panelesHeroes = new ArrayList<>();
        panelesEnemigos = new ArrayList<>();
        historial = new HistorialCombate(); // NUEVO
        
        // Inicializar personajes e inventarios
        try {
            inicializarCombate();
        } catch (ExcepcionInventarioLleno e) {
            mostrarError("Error al inicializar inventarios", e.getMessage());
        }
        
        // Crear interfaz
        crearInterfaz();
        
        // Configurar redirección de System.out al log DESPUÉS de crear la interfaz
        if (logCombate != null) {
            ConsolaRedirect.configurarRedireccion(logCombate);
        }
        
        // Reproducir música de batalla
        reproducirMusica("/sonidos/musica_batalla.wav");
        
        agregarLog("╔═══════════════════════════════════════════════════════════════╗");
        agregarLog("║     ¡COMIENZA LA BATALLA EN EL REINO DE TRODAIN!            ║");
        agregarLog("║              Sistema Completo con Todas las Funcionalidades  ║");
        agregarLog("╚═══════════════════════════════════════════════════════════════╝");
        
        // Iniciar primer turno
        SwingUtilities.invokeLater(this::iniciarSiguienteTurno);
    }
    
    /**
     * Constructor para cargar partida guardada
     */
    public VentanaCombateCompleta(EstadoBatalla estado) {
        // Primero llamamos al constructor por defecto para inicializar todo
        setTitle("Combate - Dragon Quest VIII (Sistema Completo)");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Cargar fondo
        try {
            URL ruta = getClass().getResource("/imagenes/fondo_azul.png");
            if (ruta != null) {
                fondo = new ImageIcon(ruta).getImage();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al cargar fondo: " + e.getMessage());
        }
        
        // Guardar salida original
        salidaOriginal = System.out;
        
        // Crear scanner para enemigos
        scannerEnemigos = new Scanner("dummy");
        
        // Inicializar estructuras de datos
        heroes = new ArrayList<>();
        enemigos = new ArrayList<>();
        inventariosHeroes = new HashMap<>();
        panelesHeroes = new ArrayList<>();
        panelesEnemigos = new ArrayList<>();
        historial = new HistorialCombate();
        
        try {
            // Cargar estado desde partida guardada
            cargarDesdeEstadoCompleto(estado);
            turno = estado.turnoActual;
            
            // Crear interfaz
            crearInterfaz();
            
            // Configurar redirección de System.out al log
            ConsolaRedirect.configurarRedireccion(logCombate);
            
            // Actualizar UI
            actualizarLabelTurno();
            actualizarPaneles();
            agregarLog("✅ Partida cargada - Continuando desde turno " + turno);
            
            // Reproducir música de batalla
            reproducirMusica("/sonidos/musica_batalla.wav");
            
            // Continuar con el siguiente turno
            SwingUtilities.invokeLater(this::iniciarSiguienteTurno);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar partida:\n" + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void crearInterfaz() {
        // Panel principal con fondo
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) {
                    for (int x = 0; x < getWidth(); x += fondo.getWidth(null)) {
                        for (int y = 0; y < getHeight(); y += fondo.getHeight(null)) {
                            g.drawImage(fondo, x, y, this);
                        }
                    }
                } else {
                    g.setColor(new Color(30, 30, 80));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        
        // ========== PANEL SUPERIOR: BARRA DE HERRAMIENTAS ==========
        JPanel panelHerramientas = crearPanelHerramientas();
        
        // Panel superior: combatientes
        JPanel panelCombate = new JPanel(new BorderLayout(20, 10));
        panelCombate.setOpaque(false);
        panelCombate.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Panel de héroes (izquierda)
        panelHeroes = new JPanel(new GridLayout(4, 1, 5, 10));
        panelHeroes.setOpaque(false);
        panelHeroes.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.CYAN, 2),
            "⚔️ HÉROES",
            0, 0, new Font("Monospaced", Font.BOLD, 16), Color.CYAN
        ));
        
        for (Personaje h : heroes) {
            PanelPersonaje panel = new PanelPersonaje(h, true);
            panelesHeroes.add(panel);
            panelHeroes.add(panel);
        }
        
        // Panel de enemigos (derecha)
        panelEnemigos = new JPanel(new GridLayout(4, 1, 5, 10));
        panelEnemigos.setOpaque(false);
        panelEnemigos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.RED, 2),
            "👾 ENEMIGOS",
            0, 0, new Font("Monospaced", Font.BOLD, 16), Color.RED
        ));
        
        for (Personaje e : enemigos) {
            PanelPersonaje panel = new PanelPersonaje(e, false);
            panelesEnemigos.add(panel);
            panelEnemigos.add(panel);
        }
        
        panelCombate.add(panelHeroes, BorderLayout.WEST);
        panelCombate.add(panelEnemigos, BorderLayout.EAST);
        
        // Panel central: información de turno
        lblTurno = new JLabel("TURNO " + turno, SwingConstants.CENTER);
        lblTurno.setFont(new Font("Monospaced", Font.BOLD, 24));
        lblTurno.setForeground(Color.YELLOW);
        panelCombate.add(lblTurno, BorderLayout.CENTER);
        
        // Panel inferior: log y acciones
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setOpaque(false);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Log de combate
        logCombate = new JTextArea(8, 40);
        logCombate.setEditable(false);
        logCombate.setBackground(new Color(20, 20, 40));
        logCombate.setForeground(Color.WHITE);
        logCombate.setFont(new Font("Monospaced", Font.PLAIN, 11));
        scrollLog = new JScrollPane(logCombate);
        scrollLog.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        // Panel de acciones
        panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelAcciones.setOpaque(false);
        
        panelInferior.add(scrollLog, BorderLayout.CENTER);
        panelInferior.add(panelAcciones, BorderLayout.SOUTH);
        
        // Ensamblar todo
        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);
        panelContenido.add(panelHerramientas, BorderLayout.NORTH);
        panelContenido.add(panelCombate, BorderLayout.CENTER);
        panelContenido.add(panelInferior, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        add(panelPrincipal);
        
        // NO mostrar mensaje inicial aquí, se hará después de configurar redirección
    }
    
    /**
     * NUEVO: Crea el panel de herramientas con botones especiales
     */
    private JPanel crearPanelHerramientas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        
        // Botón Deshacer
        btnDeshacer = crearBotonHerramienta("↩️ DESHACER");
        btnDeshacer.setToolTipText("Deshacer última acción (Ctrl+Z)");
        btnDeshacer.setEnabled(false);
        btnDeshacer.addActionListener(e -> deshacerAccion());
        
        // Botón Rehacer
        btnRehacer = crearBotonHerramienta("↪️ REHACER");
        btnRehacer.setToolTipText("Rehacer acción deshecha (Ctrl+Y)");
        btnRehacer.setEnabled(false);
        btnRehacer.addActionListener(e -> rehacerAccion());
        
        // Botón Guardar
        btnGuardar = crearBotonHerramienta("💾 GUARDAR");
        btnGuardar.setToolTipText("Guardar partida actual");
        btnGuardar.addActionListener(e -> mostrarDialogoGuardar());
        
        // Botón Ver Historial
        JButton btnHistorial = crearBotonHerramienta("📜 HISTORIAL");
        btnHistorial.setToolTipText("Ver historial de acciones");
        btnHistorial.addActionListener(e -> mostrarHistorialAcciones());
        
        panel.add(btnDeshacer);
        panel.add(btnRehacer);
        panel.add(btnGuardar);
        panel.add(btnHistorial);
        
        // Separador
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Info de estado
        JLabel lblInfo = new JLabel("💡 Usa los botones para gestionar el combate");
        lblInfo.setForeground(Color.CYAN);
        lblInfo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        panel.add(lblInfo);
        
        return panel;
    }
    
    private JButton crearBotonHerramienta(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        boton.setOpaque(true);
        boton.setBackground(new Color(40, 70, 120));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Monospaced", Font.BOLD, 11));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 150, 255), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (boton.isEnabled()) {
                    boton.setBackground(new Color(60, 100, 160));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (boton.isEnabled()) {
                    boton.setBackground(new Color(40, 70, 120));
                }
            }
        });
        
        return boton;
    }
    
    /**
     * Inicializa personajes con inventarios individuales
     */
    private void inicializarCombate() throws ExcepcionInventarioLleno {
        // Crear héroes
        Heroe heroe = new Heroe("Héroe", 100, 50, 20, 10, 25);
        heroe.agregarHabilidad(new DanioMagico("Bola de Fuego", 10, 30));
        heroe.agregarHabilidad(new Curacion("Curar", 8, 25));
        
        Heroe yangus = new Heroe("Yangus", 120, 30, 25, 15, 22);
        yangus.agregarHabilidad(new GolpeCritico("Hachazo Brutal", 5));
        yangus.agregarHabilidad(new Aturdimiento("Golpe Aturdidor", 8));
        
        Heroe jessica = new Heroe("Jessica", 80, 70, 18, 8, 28);
        jessica.agregarHabilidad(new DanioMagico("Rayo", 12, 35));
        jessica.agregarHabilidad(new Veneno("Toxina", 8));
        
        Heroe angelo = new Heroe("Angelo", 90, 60, 22, 12, 24);
        angelo.agregarHabilidad(new Curacion("Bendición", 10, 30));
        angelo.agregarHabilidad(new Paralisis("Toque Sagrado", 10));
        
        heroes.add(heroe);
        heroes.add(yangus);
        heroes.add(jessica);
        heroes.add(angelo);
        
        // NUEVO: Inicializar inventarios individuales
        inicializarInventarios();
        
        // Crear enemigos
        Enemigo fantasma = new Enemigo("Fantasma", 85, 40, 18, 8, 15, "estratégico");
        fantasma.agregarHabilidad(new Dormir("Pesadilla", 5));
        
        MiniBoss dragonOscuro = new MiniBoss("Dragón Oscuro", 120, 40, 25, 12, 12, "agresivo");
        dragonOscuro.agregarHabilidad(new DanioMagico("Aliento de Fuego", 0, 35));
        dragonOscuro.agregarHabilidad(new DanioMagico("Llamarada Infernal", 15, 50));
        
        Enemigo slimeMetalico = new Enemigo("Slime Metálico", 50, 30, 15, 25, 18, "evasivo");
        slimeMetalico.agregarHabilidad(new Veneno("Baba Tóxica", 3));

        Enemigo orcoGuerrero = new Enemigo("Orco Guerrero", 95, 35, 22, 12, 14, "agresivo");
        orcoGuerrero.agregarHabilidad(new GolpeCritico("Hachazo Salvaje", 8));
        
        enemigos.add(fantasma);
        enemigos.add(slimeMetalico);
        enemigos.add(orcoGuerrero);
        enemigos.add(dragonOscuro);
    }
    
    /**
     * NUEVO: Inicializa inventarios individuales con distribución específica
     */
    private void inicializarInventarios() throws ExcepcionInventarioLleno {
        for (Personaje p : heroes) {
            InventarioPersonal inventario = new InventarioPersonal(p.getNombre());
            
            switch (p.getNombre()) {
                case "Héroe" -> {
                    inventario.agregarItem(new PocionCuracion("Poción media", 60), 2);
                    inventario.agregarItem(new HierbaMedicinal(), 2);
                    inventario.agregarItem(new AguaBendita(), 1);
                }
                case "Yangus" -> {
                    inventario.agregarItem(new PocionCuracion("Poción grande", 100), 1);
                    inventario.agregarItem(new HierbaMedicinal(), 3);
                    inventario.agregarItem(new Antidoto("Antídoto"), 1);
                }
                case "Jessica" -> {
                    inventario.agregarItem(new PocionMagia("Éter", 20), 2);
                    inventario.agregarItem(new SemillaMagica(), 2);
                    inventario.agregarItem(new HierbaMedicinal(), 1);
                }
                case "Angelo" -> {
                    inventario.agregarItem(new PocionCuracion("Poción pequeña", 30), 2);
                    inventario.agregarItem(new AguaBendita(), 1);
                    inventario.agregarItem(new PlumaMundo(), 1);
                }
            }
            
            inventariosHeroes.put(p.getNombre(), inventario);
            agregarLog("🎒 " + inventario.toString());
        }
    }
    
    private void iniciarSiguienteTurno() {
        if (verificarFinCombate()) {
            return;
        }
        
        // Ordenar por velocidad
        List<Personaje> orden = new ArrayList<>();
        orden.addAll(heroes);
        orden.addAll(enemigos);
        orden.sort((a, b) -> {
            if (b.getVelocidad() != a.getVelocidad()) return b.getVelocidad() - a.getVelocidad();
            if (a instanceof Heroe && b instanceof Enemigo) return -1;
            if (a instanceof Enemigo && b instanceof Heroe) return 1;
            return 0;
        });
        
        ejecutarTurnos(orden, 0);
    }
    
    private void ejecutarTurnos(List<Personaje> orden, int indice) {
        if (verificarFinCombate()) {
            return;
        }
        
        if (indice >= orden.size()) {
            turno++;
            actualizarPaneles();
            actualizarLabelTurno();
            actualizarBotonesHistorial();
            agregarLog("\n═══════════════════════════════════════════════════════════════");
            agregarLog("TURNO " + turno);
            agregarLog("═══════════════════════════════════════════════════════════════\n");
            
            Timer timer = new Timer(1000, e -> iniciarSiguienteTurno());
            timer.setRepeats(false);
            timer.start();
            return;
        }
        
        personajeActual = orden.get(indice);
        
        if (!personajeActual.estaVivo()) {
            ejecutarTurnos(orden, indice + 1);
            return;
        }
        
        boolean puedeActuar = personajeActual.procesarEstadosAntesDeActuar();
        actualizarPaneles();
        
        if (!puedeActuar) {
            Timer timer = new Timer(1500, e -> ejecutarTurnos(orden, indice + 1));
            timer.setRepeats(false);
            timer.start();
            return;
        }
        
        if (personajeActual instanceof Heroe) {
            mostrarMenuHeroe((Heroe) personajeActual, () -> ejecutarTurnos(orden, indice + 1));
        } else {
            ejecutarTurnoEnemigo((Enemigo) personajeActual);
            Timer timer = new Timer(2000, e -> ejecutarTurnos(orden, indice + 1));
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    /**
     * ACTUALIZADO: Menú de héroe con opción de inventario personal
     */
    private void mostrarMenuHeroe(Heroe heroe, Runnable siguiente) {
        panelAcciones.removeAll();
        agregarLog("\n▸ Turno de " + heroe.getNombre() + " (HP: " + heroe.getHpActual() + " MP: " + heroe.getMpActual() + ")");
        
        JButton btnAtacar = crearBotonAccion("⚔️ ATACAR");
        JButton btnHabilidad = crearBotonAccion("✨ HABILIDAD");
        JButton btnItem = crearBotonAccion("🎒 ÍTEM");
        JButton btnVerInventario = crearBotonAccion("👁️ VER INVENTARIO");
        
        btnAtacar.addActionListener(e -> mostrarSeleccionObjetivo(heroe, "atacar", siguiente));
        btnHabilidad.addActionListener(e -> mostrarMenuHabilidades(heroe, siguiente));
        btnItem.addActionListener(e -> mostrarMenuItemsPersonales(heroe, siguiente));
        btnVerInventario.addActionListener(e -> {
            mostrarInventarioDetallado(heroe);
            mostrarMenuHeroe(heroe, siguiente);
        });
        
        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnHabilidad);
        panelAcciones.add(btnItem);
        panelAcciones.add(btnVerInventario);
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }
    
    /**
     * NUEVO: Muestra el menú de ítems del inventario personal
     */
    private void mostrarMenuItemsPersonales(Heroe heroe, Runnable siguiente) {
        InventarioPersonal inventario = inventariosHeroes.get(heroe.getNombre());
        
        if (inventario.estaVacio()) {
            JOptionPane.showMessageDialog(this, 
                "Tu inventario está vacío", 
                "Sin ítems", 
                JOptionPane.INFORMATION_MESSAGE);
            mostrarMenuHeroe(heroe, siguiente);
            return;
        }
        
        panelAcciones.removeAll();
        
        List<Item> items = inventario.listarItems();
        for (Item item : items) {
            int cantidad = inventario.getCantidad(item.getNombre());
            JButton btnItem = crearBotonAccion(item.getNombre() + " x" + cantidad);
            btnItem.addActionListener(e -> {
                usarItemPersonal(heroe, item, inventario, siguiente);
            });
            panelAcciones.add(btnItem);
        }
        
        JButton btnVolver = crearBotonAccion("↩️ VOLVER");
        btnVolver.addActionListener(e -> mostrarMenuHeroe(heroe, siguiente));
        panelAcciones.add(btnVolver);
        
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }
    
    /**
     * NUEVO: Usa un ítem del inventario personal con manejo de excepciones
     */
    private void usarItemPersonal(Heroe heroe, Item item, InventarioPersonal inventario, Runnable siguiente) {
        // Preguntar si usar en aliado o enemigo
        String[] opciones = {"Aliado", "Enemigo", "Cancelar"};
        int seleccion = JOptionPane.showOptionDialog(
            this,
            "¿Sobre quién usar " + item.getNombre() + "?",
            "Seleccionar objetivo",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        
        if (seleccion == 2 || seleccion == -1) {
            mostrarMenuItemsPersonales(heroe, siguiente);
            return;
        }
        
        // Mostrar objetivos
        List<Personaje> objetivos = seleccion == 0 ? obtenerVivos(heroes) : obtenerVivos(enemigos);
        
        Object[] nombresObjetivos = objetivos.stream()
            .map(p -> p.getNombre() + " (HP: " + p.getHpActual() + "/" + p.getHpMax() + ")")
            .toArray();
        
        String objetivoSeleccionado = (String) JOptionPane.showInputDialog(
            this,
            "Selecciona el objetivo:",
            "Usar " + item.getNombre(),
            JOptionPane.QUESTION_MESSAGE,
            null,
            nombresObjetivos,
            nombresObjetivos[0]
        );
        
        if (objetivoSeleccionado == null) {
            mostrarMenuItemsPersonales(heroe, siguiente);
            return;
        }
        
        // Encontrar el personaje objetivo
        int indiceObjetivo = Arrays.asList(nombresObjetivos).indexOf(objetivoSeleccionado);
        Personaje objetivo = objetivos.get(indiceObjetivo);
        
        // Usar el ítem con manejo de excepciones
        try {
            int hpAntes = objetivo.getHpActual();
            boolean usado = inventario.usarItem(item.getNombre(), heroe, objetivo);
            
            if (usado) {
                // Registrar en historial
                AccionCombate accion = new AccionCombate(
                    AccionCombate.TipoAccion.ITEM,
                    heroe.getNombre(),
                    objetivo.getNombre(),
                    hpAntes,
                    objetivo.getHpActual(),
                    heroe.getNombre() + " usó " + item.getNombre() + " sobre " + objetivo.getNombre()
                );
                historial.registrarAccion(accion);
                actualizarBotonesHistorial();
                
                agregarLog("✅ " + item.getNombre() + " usado exitosamente");
            }
            
        } catch (ExcepcionItemNoEncontrado e) {
            mostrarError("Ítem no encontrado", e.getMessage());
        } catch (ExcepcionJuego e) {
            mostrarError("Error al usar ítem", e.getMessage());
        }
        
        actualizarPaneles();
        panelAcciones.removeAll();
        panelAcciones.revalidate();
        panelAcciones.repaint();
        
        Timer timer = new Timer(1500, ev -> siguiente.run());
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * NUEVO: Muestra el inventario detallado del héroe
     */
    private void mostrarInventarioDetallado(Heroe heroe) {
        InventarioPersonal inventario = inventariosHeroes.get(heroe.getNombre());
        
        StringBuilder sb = new StringBuilder();
        sb.append("🎒 INVENTARIO DE ").append(heroe.getNombre().toUpperCase()).append("\n\n");
        
        if (inventario.estaVacio()) {
            sb.append("(Vacío)\n");
        } else {
            List<Item> items = inventario.listarItems();
            for (Item item : items) {
                int cantidad = inventario.getCantidad(item.getNombre());
                sb.append("• ").append(item.getNombre()).append(" x").append(cantidad).append("\n");
            }
        }
        
        sb.append("\nEspacio usado: ").append(5 - inventario.getEspacioDisponible()).append("/5");
        
        JOptionPane.showMessageDialog(
            this,
            sb.toString(),
            "Inventario - " + heroe.getNombre(),
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void mostrarMenuHabilidades(Heroe heroe, Runnable siguiente) {
        if (heroe.getHabilidades().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tienes habilidades", "Sin habilidades", JOptionPane.INFORMATION_MESSAGE);
            mostrarMenuHeroe(heroe, siguiente);
            return;
        }
        
        panelAcciones.removeAll();
        
        for (Habilidad h : heroe.getHabilidades()) {
            String textoBoton = h.getNombre() + " (MP: " + h.getCostoMP() + ")";
            JButton btnHabilidad = crearBotonAccion(textoBoton);
            
            if (heroe.getMpActual() < h.getCostoMP()) {
                btnHabilidad.setEnabled(false);
                btnHabilidad.setBackground(new Color(60, 60, 60));
            }
            
            btnHabilidad.addActionListener(e -> {
                try {
                    usarHabilidadConHistorial(heroe, h, siguiente);
                } catch (ExcepcionMPInsuficiente ex) {
                    mostrarError("MP Insuficiente", ex.getMessage());
                    mostrarMenuHeroe(heroe, siguiente);
                }
            });
            panelAcciones.add(btnHabilidad);
        }
        
        JButton btnVolver = crearBotonAccion("↩️ VOLVER");
        btnVolver.addActionListener(e -> mostrarMenuHeroe(heroe, siguiente));
        panelAcciones.add(btnVolver);
        
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }
    
    /**
     * NUEVO: Usa habilidad registrando en historial
     */
    private void usarHabilidadConHistorial(Heroe heroe, Habilidad habilidad, Runnable siguiente) 
            throws ExcepcionMPInsuficiente {
        
        if (heroe.getMpActual() < habilidad.getCostoMP()) {
            throw new ExcepcionMPInsuficiente(habilidad.getCostoMP(), heroe.getMpActual());
        }
        
        List<Personaje> vivos = obtenerVivos(enemigos);
        
        Object[] nombresObjetivos = vivos.stream()
            .map(p -> p.getNombre() + " (HP: " + p.getHpActual() + ")")
            .toArray();
        
        String objetivoSeleccionado = (String) JOptionPane.showInputDialog(
            this,
            "Selecciona objetivo para " + habilidad.getNombre(),
            "Usar Habilidad",
            JOptionPane.QUESTION_MESSAGE,
            null,
            nombresObjetivos,
            nombresObjetivos[0]
        );
        
        if (objetivoSeleccionado == null) {
            mostrarMenuHabilidades(heroe, siguiente);
            return;
        }
        
        int indiceObjetivo = Arrays.asList(nombresObjetivos).indexOf(objetivoSeleccionado);
        Personaje objetivo = vivos.get(indiceObjetivo);
        
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
        actualizarBotonesHistorial();
        
        actualizarPaneles();
        panelAcciones.removeAll();
        panelAcciones.revalidate();
        panelAcciones.repaint();
        
        Timer timer = new Timer(1500, ev -> siguiente.run());
        timer.setRepeats(false);
        timer.start();
    }
    
    private void mostrarSeleccionObjetivo(Heroe heroe, String accion, Runnable siguiente) {
        panelAcciones.removeAll();
        agregarLog("Selecciona objetivo:");
        
        List<Personaje> vivosEnemigos = obtenerVivos(enemigos);
        
        if (vivosEnemigos.isEmpty()) {
            siguiente.run();
            return;
        }
        
        for (Personaje enemigo : vivosEnemigos) {
            JButton btnObjetivo = crearBotonAccion(enemigo.getNombre() + " (HP: " + enemigo.getHpActual() + ")");
            btnObjetivo.addActionListener(e -> {
                ejecutarAccionHeroe(heroe, accion, enemigo);
                actualizarPaneles();
                panelAcciones.removeAll();
                panelAcciones.revalidate();
                panelAcciones.repaint();
                
                Timer timer = new Timer(1500, ev -> siguiente.run());
                timer.setRepeats(false);
                timer.start();
            });
            panelAcciones.add(btnObjetivo);
        }
        
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }
    
    /**
     * ACTUALIZADO: Ejecuta acción registrando en historial
     */
    private void ejecutarAccionHeroe(Heroe heroe, String accion, Personaje objetivo) {
        if (accion.equals("atacar")) {
            int hpAntes = objetivo.getHpActual();
            
            System.out.println(heroe.getNombre() + " ataca a " + objetivo.getNombre());
            objetivo.recibirDaño(heroe.getAtaque());
            
            // Registrar en historial
            AccionCombate accionCombate = new AccionCombate(
                AccionCombate.TipoAccion.ATAQUE,
                heroe.getNombre(),
                objetivo.getNombre(),
                hpAntes,
                objetivo.getHpActual(),
                heroe.getNombre() + " atacó a " + objetivo.getNombre()
            );
            historial.registrarAccion(accionCombate);
            actualizarBotonesHistorial();
        }
    }
    
    private void ejecutarTurnoEnemigo(Enemigo enemigo) {
        agregarLog("\n▸ Turno de " + enemigo.getNombre() + " (Enemigo)");
        try {
            enemigo.tomarTurno(enemigos, heroes, scannerEnemigos);
        } catch (Exception e) {
            agregarLog("⚠️ Error en turno de enemigo");
        }
        actualizarPaneles();
    }
    
    /**
     * NUEVO: Deshace la última acción
     */
    private void deshacerAccion() {
        List<Personaje> todos = new ArrayList<>();
        todos.addAll(heroes);
        todos.addAll(enemigos);
        
        AccionCombate accion = historial.deshacer(todos);
        if (accion != null) {
            agregarLog("↩️ DESHECHO: " + accion.getDescripcion());
            actualizarPaneles();
            actualizarBotonesHistorial();
            
            JOptionPane.showMessageDialog(
                this,
                "Acción deshecha:\n" + accion.getDescripcion(),
                "Deshacer",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * NUEVO: Rehace la última acción deshecha
     */
    private void rehacerAccion() {
        List<Personaje> todos = new ArrayList<>();
        todos.addAll(heroes);
        todos.addAll(enemigos);
        
        AccionCombate accion = historial.rehacer(todos);
        if (accion != null) {
            agregarLog("↪️ REHECHO: " + accion.getDescripcion());
            actualizarPaneles();
            actualizarBotonesHistorial();
            
            JOptionPane.showMessageDialog(
                this,
                "Acción rehecha:\n" + accion.getDescripcion(),
                "Rehacer",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * NUEVO: Actualiza el estado de los botones deshacer/rehacer
     */
    private void actualizarBotonesHistorial() {
        btnDeshacer.setEnabled(historial.puedeDeshacer());
        btnRehacer.setEnabled(historial.puedeRehacer());
    }
    
    /**
     * NUEVO: Muestra el historial de acciones
     */
    private void mostrarHistorialAcciones() {
        List<String> historiaList = historial.obtenerHistorial();
        
        if (historiaList.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No hay acciones en el historial",
                "Historial Vacío",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("📜 HISTORIAL DE ACCIONES\n");
        sb.append("═══════════════════════════════════════\n\n");
        
        for (String accion : historiaList) {
            sb.append(accion).append("\n");
        }
        
        sb.append("\nTotal de acciones: ").append(historiaList.size());
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Historial de Acciones",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * NUEVO: Muestra diálogo para guardar partida
     */
    private void mostrarDialogoGuardar() {
        String nombrePartida = JOptionPane.showInputDialog(
            this,
            "Nombre de la partida:",
            "Guardar Partida",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (nombrePartida != null && !nombrePartida.trim().isEmpty()) {
            try {
                EstadoBatalla estado = crearEstadoActual();
                GestorPersistencia.guardarPartida(nombrePartida, estado);
                
                JOptionPane.showMessageDialog(
                    this,
                    "✅ Partida guardada exitosamente\n\nArchivo: " + nombrePartida + ".dq8save",
                    "Guardado Exitoso",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                agregarLog("💾 Partida guardada: " + nombrePartida);
                
            } catch (ExcepcionGuardadoPartida e) {
                mostrarError("Error al guardar", e.getMessage());
            }
        }
    }
    
    /**
     * NUEVO: Crea el estado actual para guardar
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
     * NUEVO: Carga batalla desde estado guardado (VERSIÓN COMPLETA)
     */
    private void cargarDesdeEstadoCompleto(EstadoBatalla estado) throws Exception {
        agregarLog("📂 Cargando estado desde partida guardada...");
        
        // Recrear héroes con sus datos
        for (EstadoBatalla.DatosPersonaje datos : estado.heroes) {
            Heroe heroe = new Heroe(datos.nombre, datos.hpMax, datos.mpMax, 
                                   datos.ataque, datos.defensa, datos.velocidad);
            heroe.setHpActual(datos.hpActual);
            heroe.setMpActual(datos.mpActual);
            
            // Restaurar estado
            try {
                heroe.setEstado(EstadoAlterado.valueOf(datos.estado));
            } catch (Exception e) {
                heroe.setEstado(EstadoAlterado.NORMAL);
            }
            heroe.setEstadoDuracion(datos.estadoDuracion);
            
            // Agregar habilidades básicas (simplificado)
            agregarHabilidadesBasicas(heroe);
            
            heroes.add(heroe);
            
            // Inicializar inventario
            InventarioPersonal inventario = new InventarioPersonal(datos.nombre);
            inventariosHeroes.put(datos.nombre, inventario);
            
            // Cargar ítems del inventario si existen
            if (estado.inventariosHeroes.containsKey(datos.nombre)) {
                Map<String, Integer> items = estado.inventariosHeroes.get(datos.nombre);
                for (Map.Entry<String, Integer> item : items.entrySet()) {
                    try {
                        Item itemObj = GestorPersistencia.crearItemDesdeNombre(item.getKey());
                        inventario.agregarItem(itemObj, item.getValue());
                    } catch (Exception e) {
                        System.err.println("⚠️ No se pudo cargar ítem: " + item.getKey());
                    }
                }
            }
        }
        
        // Recrear enemigos
        for (EstadoBatalla.DatosPersonaje datos : estado.enemigos) {
            Personaje enemigo;
            
            if ("MiniBoss".equals(datos.tipo)) {
                enemigo = new MiniBoss(datos.nombre, datos.hpMax, datos.mpMax,
                                      datos.ataque, datos.defensa, datos.velocidad, 
                                      datos.comportamiento != null ? datos.comportamiento : "agresivo");
            } else {
                enemigo = new Enemigo(datos.nombre, datos.hpMax, datos.mpMax,
                                     datos.ataque, datos.defensa, datos.velocidad,
                                     datos.comportamiento != null ? datos.comportamiento : "agresivo");
            }
            
            enemigo.setHpActual(datos.hpActual);
            enemigo.setMpActual(datos.mpActual);
            
            try {
                enemigo.setEstado(EstadoAlterado.valueOf(datos.estado));
            } catch (Exception e) {
                enemigo.setEstado(EstadoAlterado.NORMAL);
            }
            enemigo.setEstadoDuracion(datos.estadoDuracion);
            
            // Agregar habilidades básicas
            agregarHabilidadesBasicas(enemigo);
            
            enemigos.add(enemigo);
        }
        
        agregarLog("✅ Estado cargado: " + heroes.size() + " héroes, " + enemigos.size() + " enemigos");
    }
    
    /**
     * Agrega habilidades básicas a un personaje (helper para carga)
     */
    private void agregarHabilidadesBasicas(Personaje personaje) {
        if (personaje instanceof Heroe) {
            switch (personaje.getNombre()) {
                case "Héroe" -> {
                    personaje.agregarHabilidad(new DanioMagico("Bola de Fuego", 10, 30));
                    personaje.agregarHabilidad(new Curacion("Curar", 8, 25));
                }
                case "Yangus" -> {
                    personaje.agregarHabilidad(new GolpeCritico("Hachazo Brutal", 5));
                    personaje.agregarHabilidad(new Aturdimiento("Golpe Aturdidor", 8));
                }
                case "Jessica" -> {
                    personaje.agregarHabilidad(new DanioMagico("Rayo", 12, 35));
                    personaje.agregarHabilidad(new Veneno("Toxina", 8));
                }
                case "Angelo" -> {
                    personaje.agregarHabilidad(new Curacion("Bendición", 10, 30));
                    personaje.agregarHabilidad(new Paralisis("Toque Sagrado", 10));
                }
            }
        } else {
            // Habilidades básicas para enemigos
            personaje.agregarHabilidad(new DanioMagico("Ataque Especial", 5, 20));
        }
    }
    
    /**
     * NUEVO: Muestra diálogo de error
     */
    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            titulo,
            JOptionPane.ERROR_MESSAGE
        );
        agregarLog("❌ ERROR: " + mensaje);
    }
    
    private boolean verificarFinCombate() {
        boolean heroesVivos = heroes.stream().anyMatch(Personaje::estaVivo);
        boolean enemigosVivos = enemigos.stream().anyMatch(Personaje::estaVivo);
        
        if (!enemigosVivos) {
            mostrarFinCombate(true);
            return true;
        }
        if (!heroesVivos) {
            mostrarFinCombate(false);
            return true;
        }
        return false;
    }
    
    private void mostrarFinCombate(boolean victoria) {
        panelAcciones.removeAll();
        detenerMusica();
        
        if (victoria) {
            agregarLog("\n╔═══════════════════════════════════════════════════════════════╗");
            agregarLog("║                      ¡VICTORIA!                              ║");
            agregarLog("╚═══════════════════════════════════════════════════════════════╝");
        } else {
            agregarLog("\n╔═══════════════════════════════════════════════════════════════╗");
            agregarLog("║                     DERROTA...                               ║");
            agregarLog("╚═══════════════════════════════════════════════════════════════╝");
        }
        
        JButton btnVolver = crearBotonAccion("↩️ VOLVER AL MENÚ");
        btnVolver.addActionListener(e -> {
            dispose();
            new VentanaPrincipalCompleta().setVisible(true);
        });
        
        panelAcciones.add(btnVolver);
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }
    
    private void actualizarPaneles() {
        for (int i = 0; i < heroes.size(); i++) {
            panelesHeroes.get(i).actualizar();
        }
        for (int i = 0; i < enemigos.size(); i++) {
            panelesEnemigos.get(i).actualizar();
        }
    }
    
    private void actualizarLabelTurno() {
        if (lblTurno != null) {
            lblTurno.setText("TURNO " + turno);
        }
    }
    
    private void agregarLog(String texto) {
        if (logCombate != null) {
            logCombate.append(texto + "\n");
            logCombate.setCaretPosition(logCombate.getDocument().getLength());
        } else {
            // Fallback a consola si logCombate no está inicializado
            System.out.println(texto);
        }
    }
    
    private JButton crearBotonAccion(String texto) {
        JButton boton = new JButton(texto);
        
        boton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBackground(new Color(30, 60, 130));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Monospaced", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        boton.setPreferredSize(new Dimension(180, 40));
        
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            Color colorOriginal = boton.getBackground();
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(45, 90, 170));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (boton.isEnabled()) {
                    boton.setBackground(colorOriginal);
                }
            }
        });
        
        return boton;
    }
    
    private List<Personaje> obtenerVivos(List<Personaje> lista) {
        return lista.stream().filter(Personaje::estaVivo).toList();
    }
    
    private void reproducirMusica(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                return;
            }
            
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            clipMusica = AudioSystem.getClip();
            clipMusica.open(audioInput);
            
            ajustarVolumenMusica();
            
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
            clipMusica.start();
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo reproducir música");
        }
    }
    
    public void ajustarVolumenMusica() {
        if (clipMusica != null && clipMusica.isOpen()) {
            try {
                FloatControl volumen = (FloatControl) clipMusica.getControl(FloatControl.Type.MASTER_GAIN);
                config.ConfiguracionJuego configuracion = config.ConfiguracionJuego.obtenerInstancia();
                float db = (float) (Math.log(configuracion.getVolumenMusica() / 100.0) / Math.log(10.0) * 20.0);
                db = Math.max(-80.0f, Math.min(6.0f, db));
                volumen.setValue(db);
            } catch (IllegalArgumentException e) {
                // Ignorar
            }
        }
    }
    
    private void detenerMusica() {
        if (clipMusica != null && clipMusica.isRunning()) {
            clipMusica.stop();
            clipMusica.close();
        }
        if (scannerEnemigos != null) {
            scannerEnemigos.close();
        }
        ConsolaRedirect.restaurarSalida(salidaOriginal);
    }
    
    // Clase interna para representar visualmente a cada personaje
    class PanelPersonaje extends JPanel {
        private Personaje personaje;
        private JLabel lblNombre, lblHP, lblMP, lblEstado;
        private JProgressBar barraHP, barraMP;
        
        public PanelPersonaje(Personaje p, boolean esHeroe) {
            this.personaje = p;
            
            setLayout(new GridLayout(5, 1, 2, 2));
            setBackground(new Color(20, 20, 40, 200));
            setBorder(BorderFactory.createLineBorder(esHeroe ? Color.CYAN : Color.RED, 2));
            
            String icono = esHeroe ? "⚔️" : (p instanceof MiniBoss ? "👹" : "👾");
            lblNombre = new JLabel(icono + " " + p.getNombre());
            lblNombre.setForeground(Color.WHITE);
            lblNombre.setFont(new Font("Monospaced", Font.BOLD, 13));
            
            barraHP = new JProgressBar(0, p.getHpMax());
            barraHP.setValue(p.getHpActual());
            barraHP.setStringPainted(true);
            barraHP.setString(p.getHpActual() + "/" + p.getHpMax());
            barraHP.setForeground(Color.GREEN);
            barraHP.setBackground(Color.DARK_GRAY);
            
            lblHP = new JLabel("HP: " + p.getHpActual() + "/" + p.getHpMax());
            lblHP.setForeground(Color.WHITE);
            lblHP.setFont(new Font("Monospaced", Font.PLAIN, 10));
            
            barraMP = new JProgressBar(0, p.getMpMax());
            barraMP.setValue(p.getMpActual());
            barraMP.setStringPainted(true);
            barraMP.setString(p.getMpActual() + "/" + p.getMpMax());
            barraMP.setForeground(Color.CYAN);
            barraMP.setBackground(Color.DARK_GRAY);
            
            lblMP = new JLabel("MP: " + p.getMpActual() + "/" + p.getMpMax());
            lblMP.setForeground(Color.WHITE);
            lblMP.setFont(new Font("Monospaced", Font.PLAIN, 10));
            
            String estadoTexto = p.getEstado() == EstadoAlterado.NORMAL ? "Normal" : p.getEstado().toString();
            lblEstado = new JLabel("Estado: " + estadoTexto);
            lblEstado.setForeground(Color.YELLOW);
            lblEstado.setFont(new Font("Monospaced", Font.PLAIN, 11));
            
            add(lblNombre);
            add(barraHP);
            add(lblHP);
            add(barraMP);
            add(lblEstado);
        }
        
        public void actualizar() {
            barraHP.setValue(personaje.getHpActual());
            barraHP.setString(personaje.getHpActual() + "/" + personaje.getHpMax());
            lblHP.setText("HP: " + personaje.getHpActual() + "/" + personaje.getHpMax());
            
            barraMP.setValue(personaje.getMpActual());
            barraMP.setString(personaje.getMpActual() + "/" + personaje.getMpMax());
            lblMP.setText("MP: " + personaje.getMpActual() + "/" + personaje.getMpMax());
            
            String estadoTexto = personaje.getEstado() == EstadoAlterado.NORMAL ? "Normal" : personaje.getEstado().toString();
            lblEstado.setText("Estado: " + estadoTexto);
            
            double porcentaje = (double) personaje.getHpActual() / personaje.getHpMax();
            if (porcentaje > 0.5) {
                barraHP.setForeground(Color.GREEN);
            } else if (porcentaje > 0.25) {
                barraHP.setForeground(Color.YELLOW);
            } else {
                barraHP.setForeground(Color.RED);
            }
            
            if (!personaje.estaVivo()) {
                setBackground(new Color(40, 0, 0, 150));
                lblNombre.setText("💀 " + personaje.getNombre() + " (K.O.)");
            }
        }
    }
}