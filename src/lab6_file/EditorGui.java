/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;

import java.awt.BorderLayout;
import java.awt.Color;
import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import java.util.List;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

/**
 *
 * @author USER
 */
public class EditorGui extends BaseFrame {
    private final MenuPrincipal owner;
    private JPanel panelPrincipal;
    private JPanel PanelNorte;
    private JPanel panelCentro;
    private JPanel panelSur;
    private JComboBox<String> cboFuetes;
    private JComboBox<String> cbotamano;
    private JButton btnColor;
    private JButton btnNegrita;
    private JButton btnCursiva;
    private JButton btnSubrayado;
    private JButton btnIzquierda;
    private JButton btnCentro;
    private JButton btnDerecha;
    private JButton btnTabla;
    private JButton btnGuardar;
    private JButton btnGuardarComo;
    private JButton btnRegresar;
    
    private JTextPane areaTexto;
    private File ArchivoActual= null;
    private JPanel panelRecientes;
    private final List<Color> coloresRecientes = new ArrayList<>();
    private static final int MAX_RECIENTES=8;
    private static final int TAM_SL=22;
    private final List<JTable> tablas = new ArrayList<>();
    
    public EditorGui(){
        this(null, null);
    }
    
    public EditorGui(MenuPrincipal owner, File fileToOpen){
        super("Editor de texto", 980, 720);
        this.owner= owner;
        initComponents();
        if(fileToOpen != null){
            cargarDocxEnEditor(fileToOpen);
            ArchivoActual= fileToOpen;
        }
        setVisible(true);
        
        
    }
    public void initComponents(){
        panelPrincipal = new JPanel(new BorderLayout());
        setContentPane(panelPrincipal);
        construirPanelNorte();
        construirPanelCentro();
        construirPanelCentro();
        construirPanelSur();
    }
    private void construirPanelNorte(){
        PanelNorte= new JPanel(new BorderLayout());
        PanelNorte.setBorder(new EmptyBorder(8,8,8,8));
        panelPrincipal.add(PanelNorte, BorderLayout.NORTH);
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        JLabel lblFuente = new JLabel("Fuente: ");
        barra.add(lblFuente);
        cboFuetes= crearComboFuentes();
        cboFuetes.setMaximumSize(new Dimension(220, 28));
        barra.addSeparator(new Dimension(10,0));
        JLabel blTam = new JLabel("tamaño: ");
        barra.add(blTam);
        cbotamano = crearComboTamanio();
        cbotamano.setMaximumSize(new Dimension(80,28));
        barra.add(cbotamano);
        barra.addSeparator(new Dimension(10, 0));
        btnNegrita = new JButton("B");
        btnNegrita.setToolTipText("Negrita");
        btnNegrita.addActionListener(e -> aplicarNegrita());
        barra.add(btnNegrita);
        btnCursiva = new JButton("I");
        btnCursiva.setToolTipText("Cursiva");
        btnCursiva.addActionListener(e -> aplicarCursiva());
        barra.add(btnCursiva);
        btnSubrayado = new JButton("U");
        btnSubrayado.setToolTipText("Subrayado");
        btnSubrayado.addActionListener(e -> aplicarSubrayado());
        barra.add(btnSubrayado);
        barra.addSeparator(new Dimension(10, 0));
        btnIzquierda = new JButton("Izq");
        btnIzquierda.setToolTipText("Alinear a la izquierda");
        btnIzquierda.addActionListener(e -> aplicarAlineacion(StyleConstants.ALIGN_LEFT));
        barra.add(btnIzquierda);
        btnCentro = new JButton("Centro");
        btnCentro.setToolTipText("Centrar");
        btnCentro.addActionListener(e -> aplicarAlineacion(StyleConstants.ALIGN_CENTER));
        barra.add(btnCentro);
        btnDerecha = new JButton("Der");
        btnDerecha.setToolTipText("Alinear a la derecha");
        btnDerecha.addActionListener(e -> aplicarAlineacion(StyleConstants.ALIGN_RIGHT));
        barra.add(btnDerecha);
        barra.addSeparator(new Dimension(10, 0));
        btnColor = new JButton("Color");
        btnColor.addActionListener(e -> elegirColor());
        barra.add(btnColor);
        barra.addSeparator(new Dimension(10, 0));
        btnTabla = new JButton("Tabla");
        btnTabla.addActionListener(e -> {
            DialogoTabla dlg = new DialogoTabla(this);
            dlg.setVisible(true);
            if (!dlg.isOk()) {
                return;
            }
            insertarTablaEnTexto(dlg.getFilas(), dlg.getCols());
        });
        barra.add(btnTabla);
        PanelNorte.add(barra, BorderLayout.NORTH);
        JPanel panelUsados = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelUsados.setBorder(BorderFactory.createTitledBorder("Colores usados"));
        panelRecientes = new JPanel(null);
        panelRecientes.setPreferredSize(new Dimension(260, 26));
        panelRecientes.setOpaque(false);
        panelUsados.add(panelRecientes);
        PanelNorte.add(panelUsados, BorderLayout.SOUTH);
        renderRecientes();
     
        
        
    }
     private void construirPanelCentro() {
        panelCentro = new JPanel(new BorderLayout());
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
        areaTexto = new JTextPane(new DefaultStyledDocument());
        areaTexto.setEditorKit(new StyledEditorKit());
        areaTexto.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelCentro.add(scroll, BorderLayout.CENTER);
    }
    private void construirPanelSur() {
        panelSur = new JPanel();
        btnGuardar = new JButton("Guardar");
        btnGuardarComo = new JButton("Guardar como");
        btnRegresar = new JButton("Regresar");
        panelSur.add(btnGuardar);
        panelSur.add(btnGuardarComo);
        panelSur.add(btnRegresar);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
        btnGuardar.addActionListener(e -> guardarArchivoActual());
        btnGuardarComo.addActionListener(e -> guardarDocxConChooser());
        btnRegresar.addActionListener(e -> {
            if (owner != null) {
                owner.setVisible(true);
            }
            dispose();
        });
    }
    private JComboBox <String> crearComboFuentes(){
        
        String[] familias = getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> combo = new JComboBox<>(familias);
        combo.setMaximumRowCount(12);
        combo.addActionListener(e->{
            String Familia = (String) combo.getSelectedItem();
            if(Familia != null){
               aplicarFuente(Familia);
               
            }
        });
        return combo;
        
        
    }
    
    private JComboBox<String> crearComboTamanio(){
          String[] tamanios = {"8", "10", "12", "14", "16", "18", "20", "24", "28", "32", "36", "48", "72"};
        JComboBox<String> combo = new JComboBox<>(tamanios);
        combo.setEditable(true);
        combo.addActionListener(e -> {
            Object seleccionado = combo.getSelectedItem();
            if (seleccionado != null) {
                try {
                    int tam = Integer.parseInt(seleccionado.toString().trim());
                    aplicarTamanio(tam);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ingrese un número válido.");
                }
            }
        });
        return combo;
        
    }
    
    private void aplicarFuente(String familia){
         SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, familia);
        aplicarAtributosCaracter(attrs);
    }
    
    private void aplicarTamanio(int tam){
         if (tam <= 0) {
            return;
        }
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrs, tam);
        aplicarAtributosCaracter(attrs);
    }
    private void aplicarColor(Color color) {
        if (color == null) {
            return;
        }
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, color);
        aplicarAtributosCaracter(attrs);
    }
    
    
    private void aplicarNegrita(){
     SimpleAttributeSet attrs = new SimpleAttributeSet();
        boolean actual = StyleConstants.isBold(areaTexto.getCharacterAttributes());
        StyleConstants.setBold(attrs, !actual);
        aplicarAtributosCaracter(attrs);
    }
    private void aplicarCursiva(){
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        boolean actual = StyleConstants.isItalic(areaTexto.getCharacterAttributes());
        StyleConstants.setItalic(attrs, !actual);
        aplicarAtributosCaracter(attrs);
    }
    private void aplicarSubrayado(){
         SimpleAttributeSet attrs = new SimpleAttributeSet();
        boolean actual = StyleConstants.isUnderline(areaTexto.getCharacterAttributes());
        StyleConstants.setUnderline(attrs, !actual);
        aplicarAtributosCaracter(attrs);
    }
    private void aplicarAlineacion(int alineacion){
        StyledDocument doc = areaTexto.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attrs, alineacion);
        int inicio = areaTexto.getSelectionStart();
        int fin = areaTexto.getSelectionEnd();
        Element raiz = doc.getDefaultRootElement();
        int pInicio = raiz.getElementIndex(inicio);
        int pFin = raiz.getElementIndex(fin);
        for (int i = pInicio; i <= pFin; i++) {
            Element parrafo = raiz.getElement(i);
            int offset = parrafo.getStartOffset();
            int largo = parrafo.getEndOffset() - offset;
            doc.setParagraphAttributes(offset, largo, attrs, false);
        }
    }
    private void aplicarAtributosCaracter(SimpleAttributeSet attrs){
         int inicio = areaTexto.getSelectionStart();
        int fin = areaTexto.getSelectionEnd();
        if (inicio != fin) {
            areaTexto.getStyledDocument().setCharacterAttributes(inicio, fin - inicio, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
        areaTexto.requestFocusInWindow();
    }
    
    
    private void elegirColor(){
        JColorChooser selectorColor = new JColorChooser(areaTexto.getForeground());
        selectorColor.setPreviewPanel(new JPanel());
        JDialog dialogColor = JColorChooser.createDialog(
                this,
                "Elegir color",
                true,
                selectorColor,
                ev -> {
                    Color colorElegido = selectorColor.getColor();
                    if (colorElegido != null) {
                        aplicarColor(colorElegido);
                        pushColorReciente(colorElegido);
                    }
                },
                null
        );
        dialogColor.setVisible(true);
    }
    private void pushColorReciente(Color c) {
        if (c == null) {
            return;
        }
        for (int i = 0; i < coloresRecientes.size(); i++) {
            if (coloresRecientes.get(i).equals(c)) {
                coloresRecientes.remove(i);
                break;
            }
        }
        coloresRecientes.add(0, c);
        while (coloresRecientes.size() > MAX_RECIENTES) {
            coloresRecientes.remove(coloresRecientes.size() - 1);
        }
        renderRecientes();
    }
    
    private void renderRecientes(){
         panelRecientes.removeAll();
        final int espaciado = 6;
        int x = 0;
        for (Color c : coloresRecientes) {
            JButton btn = crearSlot(c);
            btn.setBounds(x, 0, TAM_SL, TAM_SL);
            panelRecientes.add(btn);
            x += TAM_SL + espaciado;
        }
        if (coloresRecientes.isEmpty()) {
            JButton btn = crearSlot(null);
            btn.setBounds(0, 0, TAM_SL, TAM_SL);
            panelRecientes.add(btn);
        }
        panelRecientes.revalidate();
        panelRecientes.repaint();
    }
    
    private JButton crearSlot(Color c){
         JButton btn = new JButton();
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(70, 70, 70)));
        btn.setOpaque(true);
        if (c == null) {
            btn.setEnabled(false);
            btn.setBackground(new Color(235, 235, 235));
            btn.setToolTipText("Sin colores usados aún");
        } else {
            btn.setEnabled(true);
            btn.setBackground(c);
            btn.setToolTipText("#" + toHex(c));
            btn.addActionListener(e -> {
                aplicarColor(c);
                pushColorReciente(c);
            });
        }
        return btn;
    }
    private static String toHex(Color c) {
        return String.format("%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }
    
    private void insertarTablaEnTexto(int filas, int cols){
       DefaultTableModel modelo = new DefaultTableModel(filas, cols);
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int anchoColumna = 100;
        int altoEncabezado = tabla.getTableHeader().getPreferredSize().height;
        for (int c = 0; c < cols; c++) {
            tabla.getColumnModel().getColumn(c).setPreferredWidth(anchoColumna);
        }
        int anchoPreferido = cols * anchoColumna;
        int altoPreferido = altoEncabezado + filas * tabla.getRowHeight() + 2;
        JScrollPane sp = new JScrollPane(tabla);
        sp.setPreferredSize(new Dimension(anchoPreferido, altoPreferido));
        sp.setBorder(new LineBorder(new Color(160, 160, 160)));
        try {
            StyledDocument doc = areaTexto.getStyledDocument();
            int pos = areaTexto.getCaretPosition();
            doc.insertString(pos, "\n", null);
            pos++;
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setComponent(attrs, sp);
            doc.insertString(pos, " ", attrs);
            pos++;
            doc.insertString(pos, "\n", null);
            tablas.add(tabla);
            areaTexto.requestFocusInWindow();
        } catch (BadLocationException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo insertar la tabla: " + ex.getMessage());
        }   
        
    }
    
    private void cargarDocxEnEditor(File f) {
        try {
            Wordimportar.cargar(areaTexto, f);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo leer el DOCX: " + ex.getMessage());
        }
    }
    private void guardarArchivoActual() {
        try {
            if (ArchivoActual == null) {
                guardarDocxConChooser();
                return;
            }
            WordExportar.guardar(areaTexto, ArchivoActual);
            JOptionPane.showMessageDialog(this, "Archivo guardado.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }
    
      private void guardarDocxConChooser() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Guardar como");
            fc.setFileFilter(new FileNameExtensionFilter("Documento Word (*.docx)", "docx"));
            if (ArchivoActual != null) {
                fc.setSelectedFile(ArchivoActual);
            }
            int r = fc.showSaveDialog(this);
            if (r != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File seleccionado = fc.getSelectedFile();
            String ruta = seleccionado.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".docx")) {
                ruta += ".docx";
            }
            ArchivoActual = new File(ruta);
            WordExportar.guardar(areaTexto, ArchivoActual);
            JOptionPane.showMessageDialog(this, "Guardado correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }
      
      
      
      
    
}
