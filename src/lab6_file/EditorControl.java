/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;

import java.awt.Color;
import java.io.File;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Nathan
 */
public class EditorControl {
    
    private final JTextPane editor;
    private final Archivo archivo;
    private String fuente="Arial"; 
    private int tamanio=16;
    private Color color=Color.BLACK;

    public EditorControl(JTextPane editor, Archivo archivo) {
        this.editor = editor;
        this.archivo = archivo;
    }
    
    public void setFuente(String fuente){
        if(fuente != null && !fuente.isBlank())
            this.fuente=fuente;
    }
    
    public void setTamanio(int tamanio){
        if(tamanio>0)
            this.tamanio=tamanio;
    }
    
    public void setColor(Color color){
        if(color!=null)
            this.color=color;
    }
    
    public void aplicar(){
        int inicio=editor.getSelectionStart();
        int fin=editor.getSelectionEnd();
        SimpleAttributeSet attrs=new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, fuente);
        StyleConstants.setFontSize(attrs, tamanio);
        StyleConstants.setForeground(attrs, color);
        
        if(inicio!=fin){
            editor.getStyledDocument().setCharacterAttributes(inicio, fin-inicio, attrs, false);
        }else{
            editor.setCharacterAttributes(attrs, false);
        }
    }
    
    public void abrir()throws Exception{
        archivo.abrirEnEditor(editor);
    }
    
    public void guardar()throws Exception{
        archivo.guardarDesdeEditor(editor);
    }
    
    public void setArchivo(File file){
        archivo.setArchivo(file);
    }
}
