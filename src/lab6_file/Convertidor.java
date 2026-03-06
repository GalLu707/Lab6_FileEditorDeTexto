/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;

import java.io.File;
import javax.swing.JTextPane;

/**
 *
 * @author HP
 */
public class Convertidor {

    private Convertidor() {
    }

    public static void docxARtf(File docxEntrada, File rtfSalida) throws Exception {
        if (docxEntrada == null) {
            throw new IllegalArgumentException("docxEntrada == null");
        }
        if (rtfSalida == null) {
            throw new IllegalArgumentException("rtfSalida == null");
        }

        JTextPane editor = new JTextPane();
        Wordimportar.cargar(editor, docxEntrada);

        javax.swing.text.rtf.RTFEditorKit kit = new javax.swing.text.rtf.RTFEditorKit();
        editor.setEditorKit(kit);

        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(asegurarRtf(rtfSalida))) {
            kit.write(fos, editor.getDocument(), 0, editor.getDocument().getLength());
        }
    }

    public static void rtfADocx(File rtfEntrada, File docxSalida) throws Exception {
        if (rtfEntrada == null) {
            throw new IllegalArgumentException("rtfEntrada == null");
        }
        if (docxSalida == null) {
            throw new IllegalArgumentException("docxSalida == null");
        }

        JTextPane editor = new JTextPane();
        javax.swing.text.rtf.RTFEditorKit kit = new javax.swing.text.rtf.RTFEditorKit();
        editor.setEditorKit(kit);

        try (java.io.FileInputStream fis = new java.io.FileInputStream(rtfEntrada)) {
            kit.read(fis, editor.getDocument(), 0);
        }
        
        WordExportar.guardar(editor, asegurarDocx(docxSalida));
    }
    
    private static File asegurarRtf(File archivo){
        String ruta = archivo.getAbsolutePath();
        if(!ruta.toLowerCase().endsWith(".rtf")){
            ruta += ".rtf";
        }
        return new File(ruta);
    }
    
    private static File asegurarDocx(File archivo){
        String ruta = archivo.getAbsolutePath();
        if(!ruta.toLowerCase().endsWith(".docx")){
            ruta += ".docx";
        }
        return new File(ruta);
    }
}
