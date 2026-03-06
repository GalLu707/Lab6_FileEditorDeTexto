/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author hermi
 */
public class WordExportar {
    
    public static void guardar(JTextPane editor, File archivoDestino) throws Exception{
        Objects.requireNonNull(editor, "editor == null");
        Objects.requireNonNull(archivoDestino,"archivoDestino == null");
        
        File padre = archivoDestino.getParentFile();
        if(padre != null && !padre.exists()){
            padre.mkdirs();
        }
        
        StyledDocument documento = editor.getStyledDocument();
        
        try(ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(archivoDestino))){
            escribirEnZip(zip,"[Content_Types].xml",xmlContentTypes());
            escribirEnZip(zip,"_rels/.rels",xmlRelsRaiz());
            escribirEnZip(zip,"docProps/app.xml",xmlApp());
            escribirEnZip(zip,"docProps/core.xml",xmlCore());
            escribirEnZip(zip,"word/_rels/document.xml.rels",xmlRelsDocumento());
            escribirEnZip(zip, "word/document.xml", xmlDocumentoWord(documento));
        }
    }
    
    private static void escribirEnZip(ZipOutputStream zip,String ruta,String contenido) throws Exception {
            zip.putNextEntry(new ZipEntry(ruta));
            zip.write(contenido.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
    }
    
    private static String xmlContentTypes() {
        return """
               <?xml version="1.0" encoding="UTF-8"?>
               <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                 <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                 <Default Extension="xml" ContentType="application/xml"/>
                 <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
                 <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
                 <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
               </Types>
               """;
    }
    
    private static String xmlRelsRaiz() {
        return """
               <?xml version="1.0" encoding="UTF-8"?>
               <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                 <Relationship Id="rId1"
                   Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument"
                   Target="word/document.xml"/>
               </Relationships>
               """;
    }
    
    private static String xmlApp() {
        return """
               <?xml version="1.0" encoding="UTF-8"?>
               <Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"
                           xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
                 <Application>Editor</Application>
               </Properties>
               """;
    }
    
    private static String xmlCore() {
        return """
               <?xml version="1.0" encoding="UTF-8"?>
               <cp:coreProperties
                   xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
                   xmlns:dc="http://purl.org/dc/elements/1.1/"
                   xmlns:dcterms="http://purl.org/dc/terms/"
                   xmlns:dcmitype="http://purl.org/dc/dcmitype/"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                 <dc:title>Documento</dc:title>
               </cp:coreProperties>
               """;
    }
    
    private static String xmlRelsDocumento() {
        return """
               <?xml version="1.0" encoding="UTF-8"?>
               <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"/>
               """;
    }
    
    private static String xmlDocumentoWord(StyledDocument documento) throws Exception{
        StringBuilder salida = new StringBuilder(8192);
        
        salida.append("""
            <?xml version="1.0" encoding="UTF-8"?>
            <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                <w:body>      
            """);
        
        Element raiz = documento.getDefaultRootElement();
        int totalParrafos = raiz.getElementCount();
        
        for(int iParrafo = 0; iParrafo < totalParrafos; iParrafo++){
            Element parrafo = raiz.getElement(iParrafo);
            
            StringBuilder runsParrafo = new StringBuilder();
            boolean escribioTabla = false;
            
            AttributeSet attrParrafo = parrafo.getAttributes();
            String xmlParrafoInicio = "<w:p>" + xmlPropiedadesParrafo(attrParrafo);
            String xmlParrafoFin = "</w:p>";
            
            int totalRuns = parrafo.getElementCount();
            for(int iRun=0;iRun<totalRuns;iRun++){
                Element hoja=parrafo.getElement(iRun);
                AttributeSet atributos = hoja.getAttributes();
                
                Component componente= StyleConstants.getComponent(atributos);
                if(componente != null){
                    if(runsParrafo.length() > 0){
                        salida.append(xmlParrafoInicio).append(runsParrafo).append(xmlParrafoFin);
                        runsParrafo.setLength(0);
                    }
                    
                    JTable tabla= extraerTablaDeComponente(componente);
                    if(tabla!=null){
                        String[][] celdas=extraerCeldasDeTabla(tabla);
                        salida.append(construirTblaOOXML(celdas,null));
                        escribioTabla=true;
                    }
                    continue;
                }
                
                int inicio=Math.max(0,hoja.getStartOffset());
                int fin=Math.min(documento.getLength(),hoja.getEndOffset());
                
                if(fin>inicio){
                    String trozo=documento.getText(inicio, fin-inicio);
                    if("\n".equals(trozo)){
                        trozo="";
                    }
                    if(!trozo.isEmpty()){
                        runsParrafo.append(xmlRun(trozo,atributos));
                        
                    }
                }
            }
            
            if (runsParrafo.length() > 0 || !escribioTabla) {
                salida.append(xmlParrafoInicio).append(runsParrafo).append(xmlParrafoFin);
            }
        }
        
        salida.append("""
              <w:sectPr/>
              </w:body>
            </w:document>
            """);
        
        return salida.toString();
    }
    
    private static String xmlPropiedadesParrafo(AttributeSet atributos){
        StringBuilder sb=new StringBuilder();
        int alineacion=StyleConstants.getAlignment(atributos);
        
        sb.append("<>w:pPr");
        
        if(alineacion==StyleConstants.ALIGN_CENTER){
            sb.append("w:jc w:val=\"center\"/>");
        }else if(alineacion == StyleConstants.ALIGN_CENTER){
            sb.append("w:jc w:val=\"right\"/>");
        }else{
            sb.append("<w:jc w:val=\"left\"/>");
        }
        
        sb.append("</w:pPr>");
        return sb.toString();
    }
    
    private static String xmlRun(String texto, AttributeSet atributos){
        StringBuilder sb = new StringBuilder();
        sb.append("<w:r><w:rPr>");
        
        String fuente= StyleConstants.getFontFamily(atributos);
        int tamano = StyleConstants.getFontSize(atributos);
        Color color = StyleConstants.getForeground(atributos);
        
        if(fuente != null && !fuente.isEmpty()){
            String f=escaparXml(fuente);
            sb.append("<w:rFonts w:ascii=\"").append(f).append("\" w:hAnsi=\"").append(f).append("\"/>");
        }
        
        if(tamano>0){
            sb.append("<w:sz w:val=\"").append(tamano*2).append("\"/>");
        }
        
        if(color!=null){
            sb.append("<w:color w:val=\"").append(aHex(color)).append("\"/>");
        }
        
        if(StyleConstants.isBold(atributos)){
            sb.append("<w:b/>");
        }
        
        if(StyleConstants.isItalic(atributos)){
            sb.append("<w:i/>");
        }
        
        if(StyleConstants.isUnderline((atributos))){
            sb.append("<w:u w:val=\"single\"/>");
        }
        
        sb.append("</w:rPr>");
        sb.append("<w:t xml:space=\"preserve\">").append(escaparXml(texto)).append("</w:t>");
        sb.append("</w:r>");

        return sb.toString();
    }
    
    private static String escaparXml(String s){
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;");
    }
    
    private static String aHex(Color c){
        return String.format("%02x%02x%02x",c.getRed(),c.getGreen(),c.getBlue());
    }
    
    private static JTable extraerTablaDeComponente(Component componente){
        if(componente instanceof JTable tabla){
            return tabla;
        }
        
        if(componente instanceof JScrollPane panelConScroll){
            Component vista= panelConScroll.getViewport().getView();
            if(vista instanceof JTable tablaVista){
                return tablaVista;
            }
        }
        return null;
    }
    
    private static String[][] extraerCeldasDeTabla(JTable tabla){
        TableModel modelo= tabla.getModel();
        int filas= modelo.getRowCount();
        int columnas= modelo.getColumnCount();
        
        String[][] celdas= new String[filas][columnas];
        
        for(int r=0;r<filas;r++){
            for(int c=0;c<columnas;c++){
                Object valor = modelo.getValueAt(r,c);
                celdas[r][c]= valor==null ? "":valor.toString();
            }
        }
        return celdas;
    }
    
    private static String construirTblaOOXML(String[][] celdas, int[] anchosTwips){
        int filas= celdas.length;
        int columnas= filas == 0 ? 0 : celdas[0].length;
        
        StringBuilder sb = new StringBuilder(2048);
        sb.append("<w:tbl xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">");

        sb.append("<w:tblPr><w:tblBorders>")
                .append(xmlBorde("top"))
                .append(xmlBorde("left"))
                .append(xmlBorde("bottom"))
                .append(xmlBorde("right"))
                .append(xmlBorde("insideH"))
                .append(xmlBorde("insideV"))
                .append("</w:tblBorders></w:tblPr>");

        sb.append("<w:tblGrid>");
        if(columnas>0){
            if(anchosTwips==null){
                int anchoPorColumna= 2400;
                for(int i=0; i<columnas; i++){
                    sb.append("<w:gridCol w:w=\"").append(anchoPorColumna).append("\"/>");
                }
            }else{
                for(int ancho: anchosTwips){
                    sb.append("<w:gridCol w:w=\"").append(ancho).append("\"/>");
                }
            }
        }
        sb.append("</w:tblGrid>");
        
        for(int r=0;r<filas;r++){
            sb.append("<w:tr>");
            for (int c=0; c<columnas;c++) {
                String texto = celdas[r][c] == null ? "" : escaparXml(celdas[r][c]);
                sb.append("<w:tc><w:tcPr/>")
                        .append("<w:p><w:r><w:t xml:space=\"preserve\">")
                        .append(texto)
                        .append("</w:t></w:r></w:p></w:tc>");
            }
            sb.append("</w:tr>");
        }
        sb.append("</w:tbl>");
        return sb.toString();
    }
    
    private static String xmlBorde(String lado) {
        return "<w:" + lado + " w:val=\"single\" w:sz=\"8\" w:space=\"0\" w:color=\"000000\"/>";
    }
}
