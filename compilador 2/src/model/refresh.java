/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import model.compiler;

/**
 *
 * @author coffeeleak
 */
public class refresh {
    
    compiler c = new compiler();
    
    public void clean(JTextArea code, JTable error, JTable token) {
        DefaultTableModel e = (DefaultTableModel)error.getModel();
        DefaultTableModel t = (DefaultTableModel)token.getModel();
        code.setText(null);
        e.setRowCount(0);
        t.setRowCount(0);
        c.reset();
        JOptionPane.showMessageDialog(null, "Registros limpios");
    }
    
}
