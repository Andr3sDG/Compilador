/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Stack;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.tables;

/**
 *
 * @author coffeeleak
 */
public class ConnectionDB {
    
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    tables t = new tables();
    boolean check;
    
    public void insertData(String query, String id, Stack ambitStack, JTable tabla, int noError, int noLinea, String clas) {
        try {
            if(searchData(id, ambitStack, clas)) {
                System.out.println(query);
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
                pst = conn.prepareStatement(query);
                pst.executeUpdate();
            } else {
                t.fillErrors(tabla, noError, "Variable duplicada", id, "Ambito", noLinea);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public boolean searchData(String id, Stack ambitStack, String clas) {
        try {
            int ambit = Integer.parseInt(String.valueOf(ambitStack.peek()));
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
            if(clas.equals("function")||clas.equals("procedure")) {
                ambitStack.pop();
                ambit = Integer.parseInt(String.valueOf(ambitStack.peek()));
            }
            pst = conn.prepareStatement("SELECT id, ambito FROM ambito WHERE id='"+id+"' AND ambito='"+ambit+"'");
            rs = pst.executeQuery();
            if(rs.next()) {
                System.out.println("Hay mas de uno");
                check = false;
            } else {
                System.out.println("Yo no wache nada");
                check = true;
            }
            //Falta el resultset
        } catch (Exception e) {
            System.out.println(e);
        }
        return check;
    }
    
    public boolean searchVariables(String id, Stack ambitStack) {
        boolean proceed = false;
        String query;
        int ambit;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
            do {
                ambit = Integer.parseInt(String.valueOf(ambitStack.peek()));
                query = "SELECT id, ambito FROM ambito WHERE id='"+id+"' AND ambito='"+ambit+"'";
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
                if(rs.next()) {
                    proceed = true;
                    System.out.println("Variable encontrada: "+id+" en ambito: "+ambit);
                    break;
                } else {
                    ambitStack.pop();
                    System.out.println("Buscando en el ambito: "+ambitStack.peek());
                }
            } while(!ambitStack.isEmpty());
        } catch (Exception e) {
            System.out.println(e);
        }
        while(!ambitStack.isEmpty()) {
            ambitStack.pop();
        }
        return proceed;
    }
    
    public void showData(JTable table) {
        try {
            String query = "SELECT * FROM ambito";
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            DefaultTableModel tm = (DefaultTableModel)table.getModel();
            tm.setRowCount(0);
            while (rs.next()) {
                Object data[] = {rs.getString("id"),rs.getString("tipo"),rs.getString("clase"),rs.getInt("ambito"),rs.getString("tarr"),rs.getInt("dimarr"),rs.getInt("nopar"),rs.getString("tparr"),rs.getString("nomact")};
                tm.addRow(data);
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    public void showData2(JTable table) {
        try {
            String query = "SELECT * FROM contadores";
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            DefaultTableModel tm = (DefaultTableModel)table.getModel();
            tm.setRowCount(0);
            while (rs.next()) {
                Object data[] = {rs.getInt("ambito"),rs.getInt("variables"),rs.getInt("constantes"),rs.getInt("funciones"),rs.getInt("procedures"),rs.getInt("arreglos"),rs.getInt("parametros"),rs.getInt("pararr"),rs.getString("transe")};
                tm.addRow(data);
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    public void showData3(JTable table) {
        try {
            String query = "SELECT * FROM tipos";
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            DefaultTableModel tm = (DefaultTableModel)table.getModel();
            tm.setRowCount(0);
            while (rs.next()) {
                Object data[] = {rs.getInt("ambito"),rs.getInt("entero"),rs.getInt("decreal"),rs.getInt("exp"),rs.getInt("string"),rs.getInt("cha"),rs.getInt("booleano"),rs.getInt("file")};
                tm.addRow(data);
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    public void truncateTable() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
            pst = conn.prepareStatement("TRUNCATE ambito");
            pst.executeUpdate();
            pst = conn.prepareStatement("TRUNCATE contadores");
            pst.executeUpdate();
            pst = conn.prepareStatement("TRUNCATE tipos");
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void insertAmbitCounter(int amb, int var, int cons, int func, int proce, int arr, int para, int pararr, Stack ambitStack, int tokenValue) {
        String query;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
            //Significa que hay que guardar los datos del ambito actual para despues seguir
            if(tokenValue == -7){
                String transe = ambitStack.toString();
                amb = Integer.parseInt(String.valueOf(ambitStack.peek()));
                query = "INSERT INTO contadores(ambito, variables, constantes, funciones, procedures, arreglos, parametros, pararr, transe) VALUES ('"+amb+"','"+var+"','"+cons+"','"+func+"','"+proce+"','"+arr+"','"+para+"','"+pararr+"','"+transe+"')";
                pst = conn.prepareStatement(query);
                pst.executeUpdate();
            //Significa que hay que obtener los datos de los ambitos que esten en la pila,
            //Sumarlos a las variables y despues hacer el update en la tabla
            } else if (tokenValue == -1002) {
                int guard = Integer.parseInt(String.valueOf(ambitStack.peek()));
                amb = Integer.parseInt(String.valueOf(ambitStack.peek()));
                System.out.println("ENTREE AQUUUUIIII!!");
                query = "SELECT ambito FROM contadores WHERE ambito='"+amb+"'";
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
                if(!rs.next()){
                    String transe = ambitStack.toString();
                    query = "INSERT INTO contadores(ambito, variables, constantes, funciones, procedures, arreglos, parametros, pararr, transe) VALUES ('"+amb+"','"+var+"','"+cons+"','"+func+"','"+proce+"','"+arr+"','"+para+"','"+pararr+"','"+transe+"')";
                    pst = conn.prepareStatement(query);
                    pst.executeUpdate();
                    do {
                        ambitStack.pop();
                        amb = Integer.parseInt(String.valueOf(ambitStack.peek()));
                        pst = conn.prepareStatement("SELECT variables, constantes, funciones, procedures, arreglos, parametros, pararr FROM contadores WHERE ambito='"+amb+"'");
                        rs = pst.executeQuery();
                        if(rs.next()){
                            var += rs.getInt("variables");
                            cons += rs.getInt("constantes");
                            func += rs.getInt("funciones");
                            proce += rs.getInt("procedures");
                            arr += rs.getInt("arreglos");
                            para += rs.getInt("parametros");
                            pararr += rs.getInt("pararr");
                        }
                    } while(amb != 0);
                    pst = conn.prepareStatement("UPDATE contadores SET variables="+var+", constantes="+cons+", funciones="+func+", procedures="+proce+", arreglos="+arr+", parametros="+para+", pararr="+pararr+" WHERE ambito="+guard+"");
                    pst.executeUpdate();
                } else {
                    do {
                        amb = Integer.parseInt(String.valueOf(ambitStack.peek()));
                        if(guard == amb){
                            amb = Integer.parseInt(String.valueOf(ambitStack.peek()));
                            pst = conn.prepareStatement("SELECT variables, constantes, funciones, procedures, arreglos, parametros, pararr FROM contadores WHERE ambito='"+amb+"'");
                            ambitStack.pop();
                        } else {
                            pst = conn.prepareStatement("SELECT variables, constantes, funciones, procedures, arreglos, parametros, pararr FROM contadores WHERE ambito='"+amb+"'");
                            ambitStack.pop();
                        }
                        rs = pst.executeQuery();
                        if(rs.next()){
                            var += rs.getInt("variables");
                            cons += rs.getInt("constantes");
                            func += rs.getInt("funciones");
                            proce += rs.getInt("procedures");
                            arr += rs.getInt("arreglos");
                            para += rs.getInt("parametros");
                            pararr += rs.getInt("pararr");
                        }
                    } while(amb != 0);
                    pst = conn.prepareStatement("UPDATE contadores SET variables="+var+", constantes="+cons+", funciones="+func+", procedures="+proce+", arreglos="+arr+", parametros="+para+", pararr="+pararr+" WHERE ambito="+guard+"");
                    pst.executeUpdate();
                }
                while(!ambitStack.isEmpty())
                    ambitStack.pop();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void insertTypeCounter(int ambit, int intCounter, int realCounter, int expCounter, int stringCounter, int charCounter, int boolCounter, int fileCounter, int tokenValue) {
        String query;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
            if(tokenValue == -7) {
                query = "INSERT INTO tipos(ambito, entero, decreal, exp, string, cha, booleano, file) VALUES ("+ambit+","+intCounter+","+realCounter+","+expCounter+","+stringCounter+","+charCounter+","+boolCounter+","+fileCounter+")";
                pst = conn.prepareStatement(query);
                pst.executeUpdate();
            } else if(tokenValue == -1002) {
                query = "SELECT ambito FROM tipos WHERE ambito='"+ambit+"'";
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
                if(!rs.next()){
                    query = "INSERT INTO tipos(ambito, entero, decreal, exp, string, cha, booleano, file) VALUES ("+ambit+","+intCounter+","+realCounter+","+expCounter+","+stringCounter+","+charCounter+","+boolCounter+","+fileCounter+")";
                    pst = conn.prepareStatement(query);
                    pst.executeUpdate();
                } else {
                    query = "SELECT entero, decreal, exp, string, cha, booleano, file FROM tipos WHERE ambito="+ambit+"";
                    pst = conn.prepareStatement(query);
                    rs = pst.executeQuery();
                    if(rs.next()) {
                        intCounter += rs.getInt("entero");
                        realCounter += rs.getInt("decreal");
                        expCounter += rs.getInt("exp");
                        stringCounter += rs.getInt("string");
                        charCounter += rs.getInt("cha");
                        boolCounter += rs.getInt("booleano");
                        fileCounter += rs.getInt("file");
                    }
                    query = "UPDATE tipos SET entero="+intCounter+", decreal="+realCounter+", exp="+expCounter+", string="+stringCounter+", cha="+charCounter+", booleano="+boolCounter+", file="+fileCounter+" WHERE ambito="+ambit+"";
                    pst = conn.prepareStatement(query);
                    pst.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void updateNopar(int nopar, String id, int amb) {
        System.out.println("Parametros: "+nopar+" Funcion: "+id+" ambito: "+amb);
        String query = "UPDATE ambito SET nopar="+nopar+" WHERE id='"+id+"' AND ambito="+amb+"";
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Compilador","root","");
            pst = conn.prepareStatement(query);
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
