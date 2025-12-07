package mvc.view.gui;

import mvc.model.persistencia.*;
import mvc.model.exceptions.ExcepcionGuardadoPartida;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * VentanaGestionPartidas - Interfaz para gestionar partidas guardadas
 */
public class VentanaGestionPartidas extends JDialog {
    private JList<String> listaPartidas;
    private DefaultListModel<String> modeloLista;
    private JButton btnCargar, btnEliminar, btnActualizar;
    private Image fondo;
    private VentanaPrincipalCompleta ventanaPadre;
    
    public VentanaGestionPartidas(JFrame padre) {
        super(padre, "💾 Gestión de Partidas Guardadas", true);
        this.ventanaPadre = (VentanaPrincipalCompleta) padre;
        
        setSize(600, 500);
        setLocationRelativeTo(padre);
        setResizable(false);
        
        // Cargar fondo
        try {
            java.net.URL ruta = getClass().getResource("/imagenes/fondo_azul.png");
            if (ruta != null) {
                fondo = new ImageIcon(ruta).getImage();
            }
        } catch (Exception e) {
            // Fondo no disponible
        }
        
        crearInterfaz();
        cargarListaPartidas();
    }
    
    private void crearInterfaz() {
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
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titulo = new JLabel("💾 PARTIDAS GUARDADAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 20));
        titulo.setForeground(Color.YELLOW);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Lista de partidas
        modeloLista = new DefaultListModel<>();
        listaPartidas = new JList<>(modeloLista);
        listaPartidas.setFont(new Font("Monospaced", Font.PLAIN, 14));
        listaPartidas.setBackground(new Color(20, 20, 40));
        listaPartidas.setForeground(Color.WHITE);
        listaPartidas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(listaPartidas);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 10));
        panelBotones.setOpaque(false);
        
        btnCargar = crearBoton("📂 CARGAR");
        btnEliminar = crearBoton("🗑️ ELIMINAR");
        btnActualizar = crearBoton("🔄 ACTUALIZAR");
        
        btnCargar.addActionListener(e -> cargarPartida());
        btnEliminar.addActionListener(e -> eliminarPartida());
        btnActualizar.addActionListener(e -> cargarListaPartidas());
        
        panelBotones.add(btnCargar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        
        // Botón cerrar
        JButton btnCerrar = crearBoton("↩️ CERRAR");
        btnCerrar.addActionListener(e -> dispose());
        
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setOpaque(false);
        panelInferior.add(btnCerrar);
        
        panelPrincipal.add(titulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        JPanel panelSur = new JPanel(new BorderLayout(10, 10));
        panelSur.setOpaque(false);
        panelSur.add(panelBotones, BorderLayout.CENTER);
        panelSur.add(panelInferior, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void cargarListaPartidas() {
        modeloLista.clear();
        List<String> partidas = GestorPersistencia.listarPartidas();
        
        if (partidas.isEmpty()) {
            modeloLista.addElement("(No hay partidas guardadas)");
            btnCargar.setEnabled(false);
            btnEliminar.setEnabled(false);
        } else {
            for (String partida : partidas) {
                modeloLista.addElement("💾 " + partida);
            }
            btnCargar.setEnabled(true);
            btnEliminar.setEnabled(true);
        }
    }
    
    private void cargarPartida() {
        String seleccion = listaPartidas.getSelectedValue();
        
        if (seleccion == null || seleccion.contains("No hay")) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona una partida para cargar",
                "Sin selección",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        String nombrePartida = seleccion.replace("💾 ", "");
        
        try {
            EstadoBatalla estado = GestorPersistencia.cargarPartida(nombrePartida);
            
            dispose();
            ventanaPadre.dispose();
            
            VentanaCombateCompleta ventanaCombate = new VentanaCombateCompleta(estado);
            ventanaCombate.setVisible(true);
            
            JOptionPane.showMessageDialog(
                ventanaCombate,
                "✅ Partida cargada exitosamente\nContinuando desde turno " + estado.turnoActual,
                "Carga Exitosa",
                JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (ExcepcionGuardadoPartida e) {
            JOptionPane.showMessageDialog(
                this,
                "Error al cargar partida:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void eliminarPartida() {
        String seleccion = listaPartidas.getSelectedValue();
        
        if (seleccion == null || seleccion.contains("No hay")) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona una partida para eliminar",
                "Sin selección",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        String nombrePartida = seleccion.replace("💾 ", "");
        
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de eliminar la partida '" + nombrePartida + "'?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (GestorPersistencia.eliminarPartida(nombrePartida)) {
                JOptionPane.showMessageDialog(
                    this,
                    "✅ Partida eliminada exitosamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
                );
                cargarListaPartidas();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "❌ Error al eliminar la partida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        boton.setOpaque(true);
        boton.setBackground(new Color(30, 60, 130));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Monospaced", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
        
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(45, 90, 170));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(30, 60, 130));
            }
        });
        
        return boton;
    }
}