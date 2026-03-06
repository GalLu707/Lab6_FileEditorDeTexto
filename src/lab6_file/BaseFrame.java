/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
/**
 *
 * @author HP
 */
public class BaseFrame extends JFrame{
    public BaseFrame(String titulo, int width, int height){
        super(titulo);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
    }
    
    protected JLabel crearLabel(String texto, int x, int y, int ancho, int alto, int estilo, float tamano){
        JLabel label = new JLabel(texto);
        label.setFont(label.getFont().deriveFont(estilo, tamano));
        label.setBounds(x, y, ancho, alto);
        return label;
    }
    
    protected JTextField crearTextField(int x, int y, int ancho, int alto){
        JTextField textField = new JTextField();
        textField.setBounds(x, y, ancho, alto);
        return textField;
    }
    
    protected JButton crearBoton(String texto, int x, int y, int ancho, int alto){
        JButton boton = new JButton(texto);
        boton.setBounds(x, y, ancho, alto);
        return boton;
    }
    
    protected <T> JComboBox<T> crearCombo(T[] items, int x, int y, int ancho, int alto){
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setBounds(x, y, ancho, alto);
        return combo;
    }
}
