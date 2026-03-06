/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;

import java.io.File;
import java.io.IOException;
import javax.swing.JTextPane;

/**
 *
 * @author HP
 */
public class Archivo {
    private File archivo;
    
    public Archivo(){
    }
    
    public Archivo(File archivo){
        this.archivo = asegurarExtensionDocx(archivo);
    }
    
    public void crearArchivo(String nombre)throws IOException{
        File f = asegurarExtensionDocx(new File(nombre));
        if (!f.exists()) {
            File padre = f.getParentFile();
            if(padre != null && !padre.exists()){
                padre.mkdirs();
            }
            f.createNewFile();
        }
        this.archivo = f;
    }
    
    public void setArchivo(String nombreORuta){
        this.archivo = asegurarExtensionDocx(new File(nombreORuta));
    }
    
    public void setArchivo(File archivo){
        this.archivo = asegurarExtensionDocx(archivo);
    }
    
    public File getArchivo(){
        return archivo;
    }
    
    public boolean existe(){
        return archivo != null && archivo.exists();
    }
    
    public void abrirEnEditor(JTextPane editor)throws Exception{
        validarLectura();
        Wordimportar.cargar(editor, archivo);
    }
    
    public void guardarDesdeEditor(JTextPane editor)throws Exception{
        validarEscritura();
        //Wordexportar.guardar(editor, archivo);
    }
    
    private File asegurarExtensionDocx(File archivo){
        if(archivo == null){
            throw new IllegalArgumentException("El archivo no puede ser null");
        }
        
        String ruta = archivo.getAbsolutePath();
        if(!ruta.toLowerCase().endsWith(".docx")){
            ruta += ".docx";
        }
        return new File(ruta);
    }
    
    public void validarLectura()throws IOException{
        if(archivo == null){
            throw new IllegalArgumentException("No hay archivo seleccionado");
        }
        if(!archivo.exists()){
            throw new IOException("El archiv no existe: "+archivo.getAbsolutePath());
        }
        if(!archivo.isFile()){
            throw new IOException("La ruta no es un archivo valido");
        }
        if(!archivo.canRead()){
            throw new IOException("No se puede leer el archivo");
        }
    }
    
    private void validarEscritura()throws IOException{
        if(archivo == null){
            throw new IllegalStateException("No hay archivo seleccionado");
        }
        
        File padre = archivo.getParentFile();
        if(padre != null && !padre.exists()){
            padre.mkdirs();
        }
        
        if(archivo.exists() && !archivo.canWrite()){
            throw new IOException("No se puede escribir en el archivo");
        }
    }
}
