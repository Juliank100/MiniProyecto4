package mvc.view.gui;

import mvc.model.estados.EstadoAlterado;
import mvc.model.habilidades.*;
import mvc.model.items.*;
import mvc.model.personajes.*;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;
import java.util.List;

public class VentanaCombate extends JFrame {
    // Componentes principales
    private JPanel panelHeroes, panelEnemigos, panelAcciones;
    private JTextArea logCombate;
    private JScrollPane scrollLog;
    private List<PanelPersonaje> panelesHeroes;
    private List<PanelPersonaje> panelesEnemigos;
    private JLabel lblTurno;
    
    // Lógica de combate
    private List<Personaje> heroes;
    private List<Personaje> enemigos;
    private InventarioGrupo inventario;
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
    
    public VentanaCombate() {
        setTitle("Combate - Dragon Quest VIII");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Cargar fondo desde mvc/view/imagenes/
        try {
            URL ruta = getClass().getResource("/mvc/view/imagenes/fondo_azul.png");
            if (ruta != null) {
                fondo = new ImageIcon(ruta).getImage();
                System.out.println("✅ Fondo de combate cargado");
            } else {
                System.err.println("⚠️ Fondo no encontrado en /mvc/view/imagenes/fondo_azul.png");
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
        inventario = new InventarioGrupo();
        panelesHeroes = new ArrayList<>();
        panelesEnemigos = new ArrayList<>();
        
        // Inicializar personajes e inventario
        inicializarCombate();
        
        // Crear interfaz
        crearInterfaz();
        
        // Configurar redirección de System.out al log
        ConsolaRedirect.configurarRedireccion(logCombate);
        
        // Reproducir música de batalla desde mvc/view/sonidos/
        reproducirMusica("/mvc/view/sonidos/musica_batalla.wav");
        
        // Iniciar primer turno
        SwingUtilities.invokeLater(this::iniciarSiguienteTurno);
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
        
        // Panel superior: combatientes
        JPanel panelCombate = new JPanel(new BorderLayout(20, 10));
        panelCombate.setOpaque(false);
        panelCombate.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
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
        logCombate = new JTextArea(6, 40);
        logCombate.setEditable(false);
        logCombate.setBackground(new Color(20, 20, 40));
        logCombate.setForeground(Color.WHITE);
        logCombate.setFont(new Font("Monospaced", Font.PLAIN, 11));
        scrollLog = new JScrollPane(logCombate);
        scrollLog.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        scrollLog.setPreferredSize(new Dimension(400, 120));
        
        // Panel de acciones
        panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelAcciones.setOpaque(false);
        
        panelInferior.add(scrollLog, BorderLayout.CENTER);
        panelInferior.add(panelAcciones, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelCombate, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        add(panelPrincipal);
        
        agregarLog("╔═══════════════════════════════════════════════════════════════╗");
        agregarLog("║     ¡COMIENZA LA BATALLA EN EL REINO DE TRODAIN!            ║");
        agregarLog("╚═══════════════════════════════════════════════════════════════╝");
    }
    
    private void inicializarCombate() {
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
        
        // Crear enemigos (3 normales + 1 MINI JEFE)
        Enemigo fantasma = new Enemigo("Fantasma", 85, 40, 18, 8, 15, "estratégico");
        fantasma.agregarHabilidad(new Dormir("Pesadilla", 5));
        
        MiniBoss dragonOscuro = new MiniBoss("Dragón Oscuro", 120, 40, 25, 12, 12, "agresivo");
        dragonOscuro.agregarHabilidad(new DanioMagico("Aliento de Fuego", 0, 35));
        dragonOscuro.agregarHabilidad(new DanioMagico("Llamarada Infernal", 15, 50));
        
        Enemigo slimeMetalico = new Enemigo("Slime Metálico", 50, 30, 15, 25, 18, "evasivo");
        slimeMetalico.agregarHabilidad(new Veneno("Baba Tóxica", 3));

        Enemigo orcoGuerrero = new Enemigo("Orco Guerrero", 95, 35, 22, 12, 14, "agresivo");
        orcoGuerrero.agregarHabilidad(new GolpeCritico("Hachazo Salvaje", 8));
        orcoGuerrero.agregarHabilidad(new Aturdimiento("Golpe Atronador", 6));
        
        enemigos.add(fantasma);
        enemigos.add(slimeMetalico);
        enemigos.add(orcoGuerrero);
        enemigos.add(dragonOscuro);
        
        // Inicializar inventario
        inventario.agregarItem(new PocionCuracion("Poción pequeña", 30), 5);
        inventario.agregarItem(new PocionCuracion("Poción media", 60), 3);
        inventario.agregarItem(new Antidoto("Antídoto"), 3);
        inventario.agregarItem(new PocionMagia("Éter", 20), 2);
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
    
    private void mostrarMenuHeroe(Heroe heroe, Runnable siguiente) {
        panelAcciones.removeAll();
        agregarLog("\n▸ Turno de " + heroe.getNombre() + " (HP: " + heroe.getHpActual() + " MP: " + heroe.getMpActual() + ")");
        
        JButton btnAtacar = crearBotonAccion("⚔️ ATACAR");
        JButton btnHabilidad = crearBotonAccion("✨ HABILIDAD");
        JButton btnItem = crearBotonAccion("🎒 ÍTEM");
        
        btnAtacar.addActionListener(e -> mostrarSeleccionObjetivo(heroe, "atacar", siguiente));
        btnHabilidad.addActionListener(e -> mostrarMenuHabilidades(heroe, siguiente));
        btnItem.addActionListener(e -> mostrarMenuItems(heroe, siguiente));
        
        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnHabilidad);
        panelAcciones.add(btnItem);
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }
    
    private void mostrarMenuHabilidades(Heroe heroe, Runnable siguiente) {
        if (heroe.getHabilidades().isEmpty()) {
            System.out.println("⚠️ No tienes habilidades.");
            mostrarMenuHeroe(heroe, siguiente);
            return;
        }
        
        panelAcciones.removeAll();
        
        for (Habilidad h : heroe.getHabilidades()) {
            String textoBoton = h.getNombre() + " (MP: " + h.costoMP + ")";
            JButton btnHabilidad = crearBotonAccion(textoBoton);
            
            if (heroe.getMpActual() < h.costoMP) {
                btnHabilidad.setEnabled(false);
                btnHabilidad.setBackground(new Color(60, 60, 60));
            }
            
            btnHabilidad.addActionListener(e -> {
                mostrarSeleccionObjetivo(heroe, "habilidad_" + h.getNombre(), siguiente);
            });
            panelAcciones.add(btnHabilidad);
        }
        
        JButton btnVolver = crearBotonAccion("↩️ VOLVER");
        btnVolver.addActionListener(e -> mostrarMenuHeroe(heroe, siguiente));
        panelAcciones.add(btnVolver);
        
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }
    
    private void mostrarMenuItems(Heroe heroe, Runnable siguiente) {
        if (inventario.estaVacio()) {
            agregarLog("El inventario está vacío.");
            mostrarMenuHeroe(heroe, siguiente);
            return;
        }
        
        panelAcciones.removeAll();
        
        List<Item> items = inventario.listarItemsUnicos();
        for (Item item : items) {
            int cantidad = inventario.getCantidad(item);
            JButton btnItem = crearBotonAccion(item.getNombre() + " x" + cantidad);
            btnItem.addActionListener(e -> {
                mostrarSeleccionObjetivoItem(heroe, item, siguiente);
            });
            panelAcciones.add(btnItem);
        }
        
        JButton btnVolver = crearBotonAccion("↩️ VOLVER");
        btnVolver.addActionListener(e -> mostrarMenuHeroe(heroe, siguiente));
        panelAcciones.add(btnVolver);
        
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }
    
    private void mostrarSeleccionObjetivo(Heroe heroe, String accion, Runnable siguiente) {
        panelAcciones.removeAll();
        agregarLog("Selecciona objetivo:");
        
        List<Personaje> vivosEnemigos = new ArrayList<>();
        for (Personaje e : enemigos) if (e.estaVivo()) vivosEnemigos.add(e);
        
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
    
    private void mostrarSeleccionObjetivoItem(Heroe heroe, Item item, Runnable siguiente) {
        panelAcciones.removeAll();
        agregarLog("¿Usar en héroe o enemigo?");
        
        JButton btnHeroe = crearBotonAccion("👤 HÉROE");
        JButton btnEnemigo = crearBotonAccion("👾 ENEMIGO");
        
        btnHeroe.addActionListener(e -> {
            panelAcciones.removeAll();
            for (Personaje h : heroes) {
                if (!h.estaVivo()) continue;
                JButton btn = crearBotonAccion(h.getNombre() + " (HP: " + h.getHpActual() + ")");
                btn.addActionListener(ev -> {
                    inventario.usarItem(item, heroe, h);
                    actualizarPaneles();
                    panelAcciones.removeAll();
                    panelAcciones.revalidate();
                    panelAcciones.repaint();
                    
                    Timer timer = new Timer(1500, t -> siguiente.run());
                    timer.setRepeats(false);
                    timer.start();
                });
                panelAcciones.add(btn);
            }
            panelAcciones.revalidate();
            panelAcciones.repaint();
        });
        
        btnEnemigo.addActionListener(e -> {
            panelAcciones.removeAll();
            for (Personaje en : enemigos) {
                if (!en.estaVivo()) continue;
                JButton btn = crearBotonAccion(en.getNombre() + " (HP: " + en.getHpActual() + ")");
                btn.addActionListener(ev -> {
                    inventario.usarItem(item, heroe, en);
                    actualizarPaneles();
                    panelAcciones.removeAll();
                    panelAcciones.revalidate();
                    panelAcciones.repaint();
                    
                    Timer timer = new Timer(1500, t -> siguiente.run());
                    timer.setRepeats(false);
                    timer.start();
                });
                panelAcciones.add(btn);
            }
            panelAcciones.revalidate();
            panelAcciones.repaint();
        });
        
        panelAcciones.add(btnHeroe);
        panelAcciones.add(btnEnemigo);
        panelAcciones.revalidate();
        panelAcciones.repaint();
    }
    
    private void ejecutarAccionHeroe(Heroe heroe, String accion, Personaje objetivo) {
        if (accion.equals("atacar")) {
            System.out.println(heroe.getNombre() + " ataca a " + objetivo.getNombre());
            objetivo.recibirDaño(heroe.getAtaque());
        } else if (accion.startsWith("habilidad_")) {
            String nombreHabilidad = accion.substring(10);
            for (Habilidad h : heroe.getHabilidades()) {
                if (h.getNombre().equals(nombreHabilidad)) {
                    if (heroe.consumirMP(h.costoMP)) {
                        h.ejecutar(heroe, objetivo);
                    } else {
                        System.out.println("⚠️ " + heroe.getNombre() + " no tiene suficiente MP!");
                    }
                    break;
                }
            }
        }
    }
    
    private void ejecutarTurnoEnemigo(Enemigo enemigo) {
        agregarLog("\n▸ Turno de " + enemigo.getNombre() + " (Enemigo)");
        try {
            enemigo.tomarTurno(enemigos, heroes, scannerEnemigos);
        } catch (Exception e) {
            agregarLog("⚠️ Error en turno de enemigo, atacando básicamente...");
            if (!heroes.isEmpty()) {
                Personaje objetivo = heroes.get(0);
                if (objetivo.estaVivo()) {
                    int daño = enemigo.getAtaque();
                    objetivo.recibirDaño(daño);
                    agregarLog(enemigo.getNombre() + " ataca a " + objetivo.getNombre() + " por " + daño + " de daño.");
                }
            }
        }
        actualizarPaneles();
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
            agregarLog("║         Los héroes han salvado el reino de Trodain.         ║");
            agregarLog("╚═══════════════════════════════════════════════════════════════╝");
        } else {
            agregarLog("\n╔═══════════════════════════════════════════════════════════════╗");
            agregarLog("║                     DERROTA...                               ║");
            agregarLog("║      El reino de Trodain cae ante las fuerzas del mal.      ║");
            agregarLog("╚═══════════════════════════════════════════════════════════════╝");
        }
        
        JButton btnVolver = crearBotonAccion("↩️ VOLVER AL MENÚ");
        btnVolver.addActionListener(e -> {
            dispose();
            new VentanaPrincipal().setVisible(true);
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
        logCombate.append(texto + "\n");
        logCombate.setCaretPosition(logCombate.getDocument().getLength());
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
    
    private void reproducirMusica(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.out.println("⚠️ Música de batalla no encontrada en: " + ruta);
                System.out.println("   Verifica que el archivo exista en: src/mvc/view/sonidos/musica_batalla.wav");
                return;
            }
            
            System.out.println("✅ Cargando música de batalla desde: " + url.getPath());
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            clipMusica = AudioSystem.getClip();
            clipMusica.open(audioInput);
            
            ajustarVolumenMusica();
            
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
            clipMusica.start();
            System.out.println("🎵 Música de batalla reproduciéndose");
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo reproducir música de batalla: " + e.getMessage());
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
                System.out.println("⚠️ No se pudo ajustar el volumen");
            }
        }
    }
    
    private void detenerMusica() {
        if (clipMusica != null && clipMusica.isRunning()) {
            clipMusica.stop();
            clipMusica.close();
            System.out.println("🔇 Música de batalla detenida");
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
            barraHP.setPreferredSize(new Dimension(200, 20));
            
            lblHP = new JLabel("HP: " + p.getHpActual() + "/" + p.getHpMax());
            lblHP.setForeground(Color.WHITE);
            lblHP.setFont(new Font("Monospaced", Font.PLAIN, 10));
            
            barraMP = new JProgressBar(0, p.getMpMax());
            barraMP.setValue(p.getMpActual());
            barraMP.setStringPainted(true);
            barraMP.setString(p.getMpActual() + "/" + p.getMpMax());
            barraMP.setForeground(Color.CYAN);
            barraMP.setBackground(Color.DARK_GRAY);
            barraMP.setPreferredSize(new Dimension(200, 20));
            
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
            
            // Cambiar color de barra HP según la salud
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