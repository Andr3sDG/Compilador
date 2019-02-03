/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import javax.swing.JTable;
import javax.swing.JTextArea;
import model.compiler;
import model.refresh;
import model.ConnectionDB;
import model.tables;

/**
 *
 * @author coffeeleak
 */
public class intermediary {
    
    compiler c = new compiler();
    refresh r = new refresh();
    ConnectionDB db = new ConnectionDB();
    tables t = new tables();
    
    
    public void read(JTable error, JTable token, JTextArea code) {
        c.leerArchivo(error, token, code);
    }
    
    public void compile(JTable error, JTable token) {
        c.lexico(error, token);
    }
    
    public void clean(JTextArea code, JTable error, JTable token) {
        r.clean(code, error, token);
    }
    
    public void load() {
        c.loadMatrix();
        c.loadSintaxMatrix();
        c.loadProductionsMatrix();
    }
    
    public void showTable(JTable table) {
        db.showData(table);
    }
    
    public void showTable2(JTable table) {
        db.showData2(table);
    }
    
    public void showTable3(JTable table) {
        db.showData3(table);
    }
    
    public void showTable4(JTable table) {
        c.fillVarListTable(table);
    } 
    
    public void closeWindow() {
        db.truncateTable();
    }
    
    public void exportAmbit(JTable table1, JTable table2, JTable table3) throws IOException {
        t.exportExcel(table1);
        t.exportExcel(table2);
        t.exportExcel(table3);
    }
    
    public void exportErrors(JTable table1) throws IOException {
        t.exportExcel(table1);
    }
    
    public void exportVarsList(JTable table1) throws IOException {
        t.exportExcel(table1);
    }
    
}
