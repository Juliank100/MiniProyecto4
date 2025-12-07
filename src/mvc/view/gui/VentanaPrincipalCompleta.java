// ===== Archivo: src/mvc/view/gui/VentanaPrincipalCompleta.java =====
package mvc.view.gui;

import config.ConfiguracionJuego;
import mvc.model.gremio.SistemaGremio;
import mvc.model.registro.*;
import mvc.model.personajes.Heroe;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * VentanaPrincipalCompleta - Menú principal con todas las funcionalidades integradas:
 * - Iniciar combate nuevo
 * - Cargar partida guardada
 * - Gestionar gremio
 * - Ver historial de batallas
 * - Ver registro de aventureros
 */
public class VentanaPrincipalCompleta extends JFrame {
    private JButton btnIniciar, btnCargar, btnGremio, btnHistorial, btnRegistro, btnOpciones, btnSalir;
    private JLabel titulo;
    private Clip clipMusica;
    private Image fondo;
    private ConfiguracionJuego config;
    private SistemaGremio gremio;

    public VentanaPrincipalCompleta() {
        config = ConfiguracionJuego.obtenerInstancia();
        
        // Inicializar sistema del gremio
        gremio = new SistemaGremio("Gremio de Trodain");
        inicializarAventureros();
        
        setTitle("Dragon Quest VIII - Simulador de Combate (Sistema Completo)");
        setSize(900, 700);
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

        // Panel personalizado con fondo
        JPanel panelFondo = new JPanel() {
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
        panelFondo.setLayout(new BorderLayout());

        Font fuente = new Font("Monospaced", Font.BOLD, 16);

        // Título del juego con efecto de sombra
        JPanel panelTitulo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                Font fuenteTitulo = new Font("Monospaced", Font.BOLD, 36);
                g2d.setFont(fuenteTitulo);
                
                String texto = "DRAGON QUEST VIII";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(texto)) / 2;
                int y = getHeight() / 2;
                
                g2d.setColor(Color.BLACK);
                g2d.drawString(texto, x + 3, y + 3);
                
                g2d.setColor(Color.YELLOW);
                g2d.drawString(texto, x, y);
            }
        };
        panelTitulo.setOpaque(false);
        panelTitulo.setPreferredSize(new Dimension(900, 120));

        titulo = new JLabel("~ Simulador de Combate - Sistema Completo v2.0 ~", SwingConstants.CENTER);
        titulo.setForeground(Color.CYAN);
        titulo.setFont(new Font("Monospaced", Font.ITALIC, 14));
        
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);
        panelSuperior.add(panelTitulo, BorderLayout.CENTER);
        panelSuperior.add(titulo, BorderLayout.SOUTH);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(30, 10, 15, 10));

        // Botones del menú con nuevas opciones
        btnIniciar = crearBoton("⚔️ INICIAR COMBATE", fuente);
        btnCargar = crearBoton("📂 CARGAR PARTIDA", fuente);
        btnGremio = crearBoton("🏛️ GREMIO DE AVENTUREROS", fuente);
        btnHistorial = crearBoton("📊 HISTORIAL DE BATALLAS", fuente);
        btnRegistro = crearBoton("📋 REGISTRO DE AVENTUREROS", fuente);
        btnOpciones = crearBoton("⚙️ OPCIONES", fuente);
        btnSalir = crearBoton("✖ SALIR", fuente);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new GridLayout(7, 1, 0, 12));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 120, 40, 120));
        panelBotones.add(btnIniciar);
        panelBotones.add(btnCargar);
        panelBotones.add(btnGremio);
        panelBotones.add(btnHistorial);
        panelBotones.add(btnRegistro);
        panelBotones.add(btnOpciones);
        panelBotones.add(btnSalir);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setOpaque(false);
        JLabel lblVersion = new JLabel("v2.0 | Sistema Completo con Estructuras de Datos Avanzadas");
        lblVersion.setForeground(new Color(150, 150, 150));
        lblVersion.setFont(new Font("Monospaced", Font.PLAIN, 11));
        panelInferior.add(lblVersion);

        panelFondo.add(panelSuperior, BorderLayout.NORTH);
        panelFondo.add(panelBotones, BorderLayout.CENTER);
        panelFondo.add(panelInferior, BorderLayout.SOUTH);
        add(panelFondo);

        // ========== ACCIONES DE LOS BOTONES ==========
        
        btnIniciar.addActionListener((ActionEvent e) -> iniciarCombate());
        btnCargar.addActionListener(e -> abrirGestionPartidas());
        btnGremio.addActionListener(e -> abrirGremio());
        btnHistorial.addActionListener(e -> mostrarHistorialBatallas());
        btnRegistro.addActionListener(e -> mostrarRegistroAventureros());
        btnOpciones.addActionListener(e -> abrirOpciones());
        btnSalir.addActionListener(e -> salir());

        reproducirMusica("/sonidos/musica_menu.wav");
        
        if (config.isMostrarTutorial()) {
            mostrarTutorialCompleto();
        }
    }
    
    /**
     * Inicializa aventureros en el registro del gremio
     */
    private void inicializarAventureros() {
        RegistroAventureros registro = gremio.getRegistroAventureros();
        
        registro.registrarAventurero(new Heroe("Héroe", 100, 50, 20, 10, 25));
        registro.registrarAventurero(new Heroe("Yangus", 120, 30, 25, 15, 22));
        registro.registrarAventurero(new Heroe("Jessica", 80, 70, 18, 8, 28));
        registro.registrarAventurero(new Heroe("Angelo", 90, 60, 22, 12, 24));
        registro.registrarAventurero(new Heroe("Red", 85, 45, 18, 12, 20));
        registro.registrarAventurero(new Heroe("Morrie", 95, 40, 23, 14, 19));
        
        System.out.println("✅ Sistema del gremio inicializado con " + registro.getCantidadAventureros() + " aventureros");
    }
    
    /**
     * Inicia un nuevo combate
     */
    private void iniciarCombate() {
        System.out.println("🎮 Iniciando nuevo combate...");
        detenerMusica();
        
        try {
            VentanaCombateCompleta ventanaCombate = new VentanaCombateCompleta();
            ventanaCombate.setVisible(true);
            dispose();
            System.out.println("✅ Ventana de combate iniciada");
        } catch (Exception ex) {
            System.err.println("❌ Error al iniciar combate:");
            ex.printStackTrace();
            
            JOptionPane.showMessageDialog(
                this,
                "Error al iniciar combate:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            
            reproducirMusica("/sonidos/musica_menu.wav");
        }
    }
    
    /**
     * Abre la ventana de gestión de partidas
     */
    private void abrirGestionPartidas() {
        System.out.println("📂 Abriendo gestión de partidas...");
        try {
            VentanaGestionPartidas ventana = new VentanaGestionPartidas(this);
            ventana.setVisible(true);
        } catch (Exception ex) {
            mostrarError("Error al abrir gestión de partidas", ex.getMessage());
        }
    }
    
    /**
     * Abre la ventana del gremio
     */
    private void abrirGremio() {
        System.out.println("🏛️ Abriendo sistema del gremio...");
        try {
            VentanaGremio ventana = new VentanaGremio(this, gremio.getColaTurnos());
            ventana.setVisible(true);
        } catch (Exception ex) {
            mostrarError("Error al abrir gremio", ex.getMessage());
        }
    }
    
    /**
     * Muestra el historial de batallas
     */
    private void mostrarHistorialBatallas() {
        System.out.println("📊 Mostrando historial de batallas...");
        
        HistorialBatallas historial = gremio.getHistorialBatallas();
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 HISTORIAL DE BATALLAS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        // Estadísticas
        HistorialBatallas.EstadisticasJugador stats = historial.obtenerEstadisticas();
        sb.append(stats.toString()).append("\n");
        
        // Últimas batallas
        sb.append("\n📜 ÚLTIMAS 10 BATALLAS:\n");
        sb.append("═══════════════════════════════════════════════════════\n");
        
        List<RegistroBatalla> ultimas = historial.obtenerUltimasBatallas(10);
        
        if (ultimas.isEmpty()) {
            sb.append("\n📭 No hay batallas registradas aún\n");
            sb.append("\n💡 Completa algunas batallas para ver tu historial aquí");
        } else {
            for (RegistroBatalla batalla : ultimas) {
                sb.append(batalla.toString()).append("\n");
            }
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 450));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Historial de Batallas",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Muestra el registro de aventureros
     */
    private void mostrarRegistroAventureros() {
        System.out.println("📋 Mostrando registro de aventureros...");
        
        RegistroAventureros registro = gremio.getRegistroAventureros();
        
        StringBuilder sb = new StringBuilder();
        sb.append("📋 REGISTRO DE AVENTUREROS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        sb.append(registro.obtenerEstadisticas()).append("\n");
        
        sb.append("\n👥 AVENTUREROS REGISTRADOS (Orden Alfabético):\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        List<Heroe> aventureros = registro.listarAventurerosOrdenados();
        
        for (Heroe heroe : aventureros) {
            sb.append(String.format("⚔️  %-15s | HP: %3d | MP: %3d | ATK: %2d | DEF: %2d | VEL: %2d\n",
                heroe.getNombre(), heroe.getHpMax(), heroe.getMpMax(),
                heroe.getAtaque(), heroe.getDefensa(), heroe.getVelocidad()));
        }
        
        sb.append("\n💡 Estructura: TreeMap (orden alfabético automático O(log n))");
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 450));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Registro de Aventureros",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Abre las opciones
     */
    private void abrirOpciones() {
        System.out.println("⚙️ Abriendo opciones...");
        try {
            VentanaOpciones ventanaOpciones = new VentanaOpciones(this);
            ventanaOpciones.setVisible(true);
        } catch (Exception ex) {
            mostrarError("Error al abrir opciones", ex.getMessage());
        }
    }
    
    /**
     * Sale del juego
     */
    private void salir() {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de que deseas salir?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (respuesta == JOptionPane.YES_OPTION) {
            detenerMusica();
            System.exit(0);
        }
    }
    
    /**
     * Muestra el tutorial completo
     */
    private void mostrarTutorialCompleto() {
        SwingUtilities.invokeLater(() -> {
            String mensaje = """
                ¡Bienvenido a Dragon Quest VIII - Sistema Completo!
                
                🆕 NUEVAS FUNCIONALIDADES:
                
                🎒 INVENTARIOS INDIVIDUALES
                • Cada héroe tiene su propio inventario (5 ítems máximo)
                • Nuevos ítems: Hierba Medicinal, Agua Bendita, Semilla Mágica, etc.
                • Estructura: LinkedHashMap (búsqueda O(1))
                
                ↩️ DESHACER/REHACER
                • Sistema de deshacer acciones durante el combate
                • Historial de hasta 10 acciones
                • Estructura: Stack (LIFO) con Deque
                
                💾 GUARDAR/CARGAR
                • Guarda tu progreso en cualquier momento
                • Archivos en formato texto (.dq8save)
                • Carga partidas desde el menú principal
                
                🏛️ GREMIO DE AVENTUREROS
                • Sistema de turnos con prioridades
                • Estructura: PriorityQueue (atención por urgencia)
                • Registros ordenados alfabéticamente
                
                📊 HISTORIAL Y ESTADÍSTICAS
                • Registro automático de batallas completadas
                • Estadísticas detalladas (victorias, derrotas, EXP, oro)
                • Estructura: Queue (historial cronológico)
                
                ⚡ CONTROLES:
                • Usa los botones del menú para navegar
                • En combate: botones de acción + inventario personal
                • Barra de herramientas: Deshacer, Rehacer, Guardar
                
                ¡Buena suerte, aventurero!
                """;
            
            JTextArea textArea = new JTextArea(mensaje);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(550, 450));
            
            JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Tutorial - Sistema Completo",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    private JButton crearBoton(String texto, Font fuente) {
        JButton boton = new JButton(texto);
        boton.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        Color colorFondo = new Color(30, 60, 130);
        Color colorHover = new Color(45, 90, 170);
        Color colorTexto = Color.WHITE;
        Color colorBorde = new Color(255, 215, 0);

        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBackground(colorFondo);
        boton.setForeground(colorTexto);
        boton.setFont(fuente);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorBorde, 3),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorHover);
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.YELLOW, 4),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorBorde, 3),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        return boton;
    }

    private void reproducirMusica(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) return;
            
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            clipMusica = AudioSystem.getClip();
            clipMusica.open(audioInput);
            
            ajustarVolumenMusica();
            
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
            clipMusica.start();
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("⚠️ Error al reproducir música");
        }
    }
    
    public void ajustarVolumenMusica() {
        if (clipMusica != null && clipMusica.isOpen()) {
            try {
                FloatControl volumen = (FloatControl) clipMusica.getControl(FloatControl.Type.MASTER_GAIN);
                float db = (float) (Math.log(config.getVolumenMusica() / 100.0) / Math.log(10.0) * 20.0);
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
    }
    
    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            titulo,
            JOptionPane.ERROR_MESSAGE
        );
        System.err.println("❌ " + titulo + ": " + mensaje);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo establecer Look & Feel");
        }
        
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipalCompleta().setVisible(true);
        });
    }
}