package mvc.view.gui;

import mvc.model.gremio.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * VentanaGremio - Interfaz gráfica para el sistema de turnos del gremio
 */
public class VentanaGremio extends JDialog {
    private ColaTurnosGremio colaTurnos;
    private JTextArea textArea;
    private JButton btnAgregar, btnAtender, btnVer, btnEstadisticas;
    private Image fondo;
    
    public VentanaGremio(JFrame padre, ColaTurnosGremio cola) {
        super(padre, "🏛️ Gremio de Aventureros - Sistema de Turnos", true);
        this.colaTurnos = cola;
        
        setSize(700, 600);
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
        JLabel titulo = new JLabel("🏛️ SISTEMA DE TURNOS DEL GREMIO", SwingConstants.CENTER);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 20));
        titulo.setForeground(Color.YELLOW);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Área de texto para mostrar información
        textArea = new JTextArea(15, 50);
        textArea.setEditable(false);
        textArea.setBackground(new Color(20, 20, 40));
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setOpaque(false);
        
        btnAgregar = crearBoton("➕ AGREGAR SOLICITUD");
        btnAtender = crearBoton("🎯 ATENDER SIGUIENTE");
        btnVer = crearBoton("👁️ VER PENDIENTES");
        btnEstadisticas = crearBoton("📊 ESTADÍSTICAS");
        
        btnAgregar.addActionListener(e -> agregarSolicitud());
        btnAtender.addActionListener(e -> atenderSiguiente());
        btnVer.addActionListener(e -> verPendientes());
        btnEstadisticas.addActionListener(e -> verEstadisticas());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnAtender);
        panelBotones.add(btnVer);
        panelBotones.add(btnEstadisticas);
        
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
        
        // Mostrar estado inicial
        verPendientes();
    }
    
    private void agregarSolicitud() {
        // Panel para entrada de datos
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JTextField txtNombre = new JTextField();
        
        JComboBox<String> comboTipo = new JComboBox<>();
        for (SolicitudGremio.TipoSolicitud tipo : SolicitudGremio.TipoSolicitud.values()) {
            comboTipo.addItem(tipo.getDescripcion());
        }
        
        JComboBox<String> comboPrioridad = new JComboBox<>();
        for (SolicitudGremio.PrioridadSolicitud prioridad : SolicitudGremio.PrioridadSolicitud.values()) {
            comboPrioridad.addItem(prioridad.getIcono());
        }
        
        panel.add(new JLabel("Nombre del aventurero:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Tipo de solicitud:"));
        panel.add(comboTipo);
        panel.add(new JLabel("Prioridad:"));
        panel.add(comboPrioridad);
        
        int resultado = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Nueva Solicitud",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (resultado == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes ingresar un nombre", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            SolicitudGremio.TipoSolicitud tipo = SolicitudGremio.TipoSolicitud.values()[comboTipo.getSelectedIndex()];
            SolicitudGremio.PrioridadSolicitud prioridad = SolicitudGremio.PrioridadSolicitud.values()[comboPrioridad.getSelectedIndex()];
            
            colaTurnos.agregarSolicitud(nombre, tipo, prioridad);
            verPendientes();
            
            JOptionPane.showMessageDialog(
                this,
                "✅ Solicitud agregada exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void atenderSiguiente() {
        SolicitudGremio solicitud = colaTurnos.atenderSiguiente();
        
        if (solicitud != null) {
            textArea.setText("═══════════════════════════════════════\n");
            textArea.append("🎯 SOLICITUD ATENDIDA\n");
            textArea.append("═══════════════════════════════════════\n\n");
            textArea.append(solicitud.toString());
            textArea.append("\n\n✅ Atención completada");
            
            JOptionPane.showMessageDialog(
                this,
                "Solicitud atendida:\n" + solicitud.toString(),
                "Atención Completada",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void verPendientes() {
        List<SolicitudGremio> pendientes = colaTurnos.listarPendientes();
        
        textArea.setText("═══════════════════════════════════════\n");
        textArea.append("📋 SOLICITUDES PENDIENTES\n");
        textArea.append("═══════════════════════════════════════\n\n");
        
        if (pendientes.isEmpty()) {
            textArea.append("✅ No hay solicitudes pendientes\n");
        } else {
            textArea.append("Total: " + pendientes.size() + " solicitud(es)\n\n");
            for (SolicitudGremio sol : pendientes) {
                textArea.append(sol.toString() + "\n");
            }
        }
    }
    
    private void verEstadisticas() {
        textArea.setText(colaTurnos.obtenerEstadisticas());
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
