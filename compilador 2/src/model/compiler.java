/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import model.tables;
import model.words;
import model.ConnectionDB;
import model.varList;
import view.Main;

/**
 *
 * @author coffeeleak
 */
public class compiler {

    tables t = new tables();
    words w = new words();
    ConnectionDB db = new ConnectionDB();
    String file, text, letters = "";
    StringBuffer sb;
    JFileChooser fc;
    BufferedReader read;
    int option, rowCounter = 1, errorCounter = 1;
    int lexico[][] = new int[31][31];
    int sintax[][] = new int[49][86];
    int product[][] = new int[129][11];
    char characters[];
    Stack<Integer> tokenStack = new Stack<Integer>();
    Stack<String> nomActual = new Stack<String>();
    int amb, var, cons, func, proce, arr, para, pararr;
    int intCounter, realCounter, expCounter, stringCounter, charCounter, boolCounter, fileCounter;
    ArrayList<varList> vars = new ArrayList<varList>();

    public void loadMatrix() {
        File archivo1 = new File("/Users/coffeeleak/NetBeansProjects/compilador/src/records/lexicalMatrix.xls");
        try {
            POIFSFileSystem fs1 = new POIFSFileSystem(new FileInputStream(archivo1));
            HSSFWorkbook wb1 = new HSSFWorkbook(fs1);
            HSSFSheet sheet1 = wb1.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell1;
            int rows1 = sheet1.getPhysicalNumberOfRows();
            int cols1 = 31;
            lexico = new int[rows1][cols1];
            for (int r = 1; r < rows1; r++) {
                row = sheet1.getRow(r);
                if (row != null) {
                    for (int c = 1; c < cols1; c++) {
                        cell1 = row.getCell((short) c);
                        if (cell1 != null) {
                            lexico[r - 1][c - 1] = (int) cell1.getNumericCellValue();
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void loadSintaxMatrix() {
        File archivo1 = new File("/Users/coffeeleak/NetBeansProjects/compilador/src/records/MatrizPredictiva.xls");
        try {
            POIFSFileSystem fs1 = new POIFSFileSystem(new FileInputStream(archivo1));
            HSSFWorkbook wb1 = new HSSFWorkbook(fs1);
            HSSFSheet sheet1 = wb1.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell1;
            int rows1 = sheet1.getPhysicalNumberOfRows();
            int cols1 = 88;
            sintax = new int[rows1][cols1];
            for (int r = 1; r < rows1; r++) {
                row = sheet1.getRow(r);
                if (row != null) {
                    for (int c = 1; c < cols1; c++) {
                        cell1 = row.getCell((short) c);
                        if (cell1 != null) {
                            sintax[r - 1][c - 1] = (int) cell1.getNumericCellValue();
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void loadProductionsMatrix() {
        File archivo1 = new File("/Users/coffeeleak/NetBeansProjects/compilador/src/records/Producciones.xls");
        try {
            POIFSFileSystem fs1 = new POIFSFileSystem(new FileInputStream(archivo1));
            HSSFWorkbook wb1 = new HSSFWorkbook(fs1);
            HSSFSheet sheet1 = wb1.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell1;
            int rows1 = sheet1.getPhysicalNumberOfRows();
            int cols1 = 11;
            product = new int[rows1][cols1];
            for (int r = 1; r < rows1; r++) {
                row = sheet1.getRow(r);
                if (row != null) {
                    for (int c = 1; c < cols1; c++) {
                        cell1 = row.getCell((short) c);
                        if (cell1 != null) {
                            product[r - 1][c - 1] = (int) cell1.getNumericCellValue();
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String leerArchivo(JTable error, JTable token, JTextArea code) {
        fc = new JFileChooser();
        sb = new StringBuffer();
        option = fc.showOpenDialog(this.fc);
        if (option == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile().getPath();
            try {
                read = new BufferedReader(new FileReader(file));
                while ((text = read.readLine()) != null) {
                    code.append(text + "\n");
                    letters += text + "\n";
                }
                read.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        return letters;
    }

    public void lexico(JTable errors, JTable tokens) {
        if (!letters.equals("")) {
            Stack<Integer> sintaxStack = new Stack<Integer>();
            Stack<String> lexemeStack = new Stack<String>();
            int col = 0, ren = 0, value, rwToken;
            String lexeme = "", reserved = "";
            characters = letters.toCharArray();
            for (int i = 0; i < characters.length; i++) {
                switch (characters[i]) {
                    case '@':
                        col = 3;
                        break;
                    case '_':
                        col = 4;
                        break;
                    case '"':
                        col = 5;
                        break;
                    case 'e':
                        if (Character.isDigit(characters[i + 1]) || characters[i + 1] == '+' || characters[i + 1] == '-') {
                            col = 6;
                        } else {
                            col = 1;
                            reserved += characters[i];
                        }
                        break;
                    case '\'':
                        col = 7;
                        break;
                    case '+':
                        col = 8;
                        break;
                    case '-':
                        col = 9;
                        break;
                    case '*':
                        col = 10;
                        break;
                    case '/':
                        col = 11;
                        break;
                    case '!':
                        col = 12;
                        break;
                    case '&':
                        col = 13;
                        break;
                    case '|':
                        col = 14;
                        break;
                    case '<':
                        col = 15;
                        break;
                    case '>':
                        col = 16;
                        break;
                    case ';':
                        col = 17;
                        break;
                    case ',':
                        col = 18;
                        break;
                    case '.':
                        col = 19;
                        break;
                    case ':':
                        col = 20;
                        break;
                    case '(':
                        col = 21;
                        break;
                    case ')':
                        col = 22;
                        break;
                    case '[':
                        col = 23;
                        break;
                    case ']':
                        col = 24;
                        break;
                    case '=':
                        col = 25;
                        break;
                    case '^':
                        col = 26;
                        break;
                    case '#':
                        col = 27;
                        break;
                    case ' ':
                        col = 28;
                        break;
                    case '\n':
                        col = 29;
                        break;
                    case '\t':
                        col = 30;
                        break;
                    default:
                        if (Character.isLetter(characters[i])) {
                            if (Character.isUpperCase(characters[i])) {
                                col = 0;
                            } else if (Character.isLowerCase(characters[i])) {
                                col = 1;
                                reserved += characters[i];
                            }
                        } else if (Character.isDigit(characters[i])) {
                            col = 2;
                        } else {
                            col = 6;
                        }
                }
                lexeme += characters[i];
                value = lexico[ren][col];
                if (value > 0) {
                    ren = value;
                } else if (value == -100) {
                    t.fillErrors(errors, errorCounter, "Caracter invalido", lexeme, "Lexico", rowCounter);
                    ren = 0;
                    lexeme = "";
                    reserved = "";
                    errorCounter++;
                } else if (value < 0) {
                    //if para verificar tokens que necesiten retroceso
                    if (value == -1 || value == -55 || value == -57 || value == -58 || value == -63 || value == -61 || value == -63 || value == -60 || value == -62 || value == -66 || value == -69 || value == -83 || value == -74) {
                        i--;
                        rwToken = w.reservedWords(reserved);
                        if (rwToken != -1) {
                            //Es una palabra reservada
                            tokenStack.push(rwToken);
                            lexemeStack.push(reserved);
                            t.fillTokens(tokens, rwToken, reserved, rowCounter);
                            ren = 0;
                            lexeme = "";
                            reserved = "";
                        } else if (Character.isLowerCase(lexeme.charAt(0))) {
                            //Es un id que no empieza con mayuscula
                            lexeme = lexeme.substring(0, lexeme.length() - 1);
                            t.fillErrors(errors, errorCounter, "Los id deben empezar con mayuscula", lexeme, "Lexico", rowCounter);
                            ren = 0;
                            lexeme = "";
                            reserved = "";
                            errorCounter++;
                        } else {
                            //Es un id
                            tokenStack.push(value);
                            lexeme = lexeme.substring(0, lexeme.length() - 1);
                            lexemeStack.push(lexeme.trim());
                            t.fillTokens(tokens, value, lexeme, rowCounter);
                            ren = 0;
                            lexeme = "";
                            reserved = "";
                        }
                        //else para tokens que sean directos
                    } else {
                        if (value == -90) {
                            //Si es un salto de linea incrementa el renglon
                            tokenStack.push(value);
                            lexemeStack.push("\n");
                            rowCounter++;
                        } else {
                            //Si no es un token directo
                            if (value==-2||value==-3) {
                                
                            } else { 
                                tokenStack.push(value);
                                lexemeStack.push(lexeme.trim());
                                t.fillTokens(tokens, value, lexeme, rowCounter);
                            }
                            ren = 0;
                            lexeme = "";
                            reserved = "";
                        }
                    }
                }
            }
            sintaxStack = inverte(tokenStack);
            lexemeStack = inverteStringStack(lexemeStack);
            rowCounter = 1;
            sintaxis(errors, tokens, sintaxStack, lexemeStack, letters);
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese un archivo o limpie y vuelva a intentar");
        }
    }

    public Stack inverte(Stack token) {
        Stack<Integer> tem = new Stack<Integer>();
        int val;
        tem.push(-800);
        while (!token.isEmpty()) {
            val = Integer.valueOf(String.valueOf(token.peek()));
            tem.push(val);
            token.pop();
        }
        return tem;
    }

    public Stack inverteStringStack(Stack lexeme) {
        Stack<String> tem = new Stack<String>();
        String val;
        tem.push("$");
        while (!lexeme.isEmpty()) {
            val = String.valueOf(lexeme.peek());
            tem.push(val);
            lexeme.pop();
        }
        return tem;
    }

    public void reset() {
        tokenStack = new Stack<Integer>();
        letters = "";
        rowCounter = 1;
    }

    public void sintaxis(JTable errors, JTable tokens, Stack token, Stack lexeme, String code) {
        //Inicializar la primera produccion
        String lexem;
        Queue<Integer> variables = new LinkedList<Integer>();
        Queue<String> variablesS = new LinkedList<String>();
        boolean declaration = false;
        boolean check = false;
        int tokenValue, productionValue, ambitCounter = 0;
        Stack<Integer> productions = new Stack<Integer>();
        Stack<Integer> ambitStack = new Stack<Integer>();
        productions.push(-800);
        for (int i = 9; i > -1; i--) {
            if (product[0][i] == -9000) {

            } else {
                productions.push(product[0][i]);
            }
        }
        ambitStack.push(ambitCounter);
        //Inicia sintaxis
        do {
            tokenValue = Integer.parseInt(String.valueOf(token.peek()));
            productionValue = Integer.parseInt(String.valueOf(productions.peek()));
            //Si es salto de linea se elimina
            if (tokenValue == -90) {
                token.pop();
                lexeme.pop();
                rowCounter++;
                variables.add(rowCounter);
                variablesS.add("salto");
            //Si es -1001 entramos en zona de declaracion
            } else if (productionValue == -1001) {
                if(declaration == false) {
                    declaration = true;
                    productions.pop();
                    System.out.println("Declaracion activada");
                } else {
                    declaration = false;
                    productions.pop();
                    System.out.println(variables);
                    System.out.println(variablesS);
                    insertInAmbit(variables, variablesS, ambitStack, errors);
                    System.out.println("Declaracion desactivada");
                    deleteQueue(variables);
                    deleteQueue(variablesS);
                }
            //Si es -1002 entramos en zona de verificacion y la uso para cuando debe terminar un ambito 
            } else if (productionValue == -1002) {
                if(check == false) {
                    check = true;
                    productions.pop();
                    int[] backup = new int[ambitStack.size()];
                    int back;
                    for(int i=0;i<backup.length;i++){
                        back = Integer.parseInt(String.valueOf(ambitStack.peek()));
                        backup[i] = back;
                        ambitStack.pop();
                    }
                    for(int i=backup.length;i>0;i--){
                        ambitStack.push(backup[i-1]);
                    }
                    //Aqui es para terminar el ambito que este activo o que ya haya sido empezado
                    int a = Integer.parseInt(String.valueOf(ambitStack.peek()));
                    db.insertTypeCounter(a, intCounter, realCounter, expCounter, stringCounter, charCounter, boolCounter, fileCounter, -1002);
                    db.insertAmbitCounter(amb, var, cons, func, proce, arr, para, pararr, ambitStack, -1002);
                    for(int i=backup.length;i>0;i--){
                        ambitStack.push(backup[i-1]);
                    }
                    System.out.println("HOLLAAAAAAAA!!!!!!!!!!!");
                    System.out.println(ambitStack);
                    var = 0;
                    cons = 0;
                    func = 0;
                    proce = 0;
                    arr = 0;
                    para = 0;
                    pararr = 0;
                    intCounter = 0;
                    realCounter = 0;
                    expCounter = 0;
                    stringCounter = 0;
                    charCounter = 0;
                    boolCounter = 0;
                    fileCounter = 0;
                } else {
                    check = false;
                    productions.pop();
                    System.out.println("Verificacion desactivada");
                }
            //Si es -1003 sumamos un ambito y lo metemos a la pila  
            } else if (productionValue == -1003) {
                ambitCounter++;
                ambitStack.push(ambitCounter);
                productions.pop();
                System.out.println(ambitStack);
            //Si es -1004 restamos un ambito a la pila  
            } else if (productionValue == -1004) {
                ambitStack.pop();
                nomActual.pop();
                productions.pop();
                System.out.println(ambitStack);
            //Si es -1005 hay que aumentar la dimension del arreglo
            //Solo esta puesta por si acaso ahora
            } else if (productionValue == -1005) {
                variables.add(-1005);
                variablesS.add("@");
                productions.pop();
            //Alto para las funciones
            } else if (productionValue == -1006) {
                variables.add(-1006);
                variablesS.add("@");
                productions.pop();
            } else if (productionValue == -1007) {
                variables.add(-1007);
                variablesS.add("@");
                productions.pop();
            //Si las dos cimas tienen el token final se eliminan
            } else if (tokenValue == -800 && productionValue == -800) {
                token.pop();
                productions.pop();
                break;
                //Verifica si ambas cimas son negativas
            } else if (tokenValue < 0 && productionValue < 0) {
                //Si ambas cimas son iguales se eliminan
                if (tokenValue == productionValue) {
                    if(declaration||check)
                        System.out.println("Se elimino " + tokenValue + "\t" + lexeme.peek());
                    String lexemValue = String.valueOf(lexeme.peek());
                    //Sacamos el valor de la pila de los token y su lexema para enviarlos al ambito
                    if (declaration) {
                        variables.add(tokenValue);
                        variablesS.add(lexemValue);
                    } else if(check) {
                        if (tokenValue == -1) {
                            System.out.println("Recibi esto: "+lexemValue+" pila: "+ambitStack);
                            searchInAmbit(lexemValue, ambitStack, errors);
                        }
                    }
                    token.pop();
                    productions.pop();
                    lexeme.pop();
                    //System.out.println("Cadena " + token);
                    //System.out.println("Producciones " + productions);
                //Si ambas cimas son negativas pero diferentes se produce un error de fuerza bruta
                } else {
                    lexem = String.valueOf(lexeme.peek());
                    t.fillErrors(errors, errorCounter, "Fuerza bruta", lexem, "Sintaxis", rowCounter);
                    errorCounter++;
                    break;
                }
            //Si la cima de producciones produce otra cosa
            } else if (productionValue >= 0 && tokenValue < 0) {//Si la cima de produccion es positiva se desapila y obtiene nueva produccion
                int prod = searchValues(productions, token);
                //Verifica si el valor esta entre los rangos de error
                if (prod <= -500 && prod >= -532) {
                    //Error de sincronizacion
                    if (prod == -500) {
                        lexem = String.valueOf(lexeme.peek());
                        t.fillErrors(errors, errorCounter, "Error de sincronizacion", lexem, "Sintaxis", rowCounter);
                        errorCounter++;
                        //System.out.println("Error " + token.peek());
                        //System.out.println("Cadena " + token);
                        //System.out.println("Producciones " + productions);
                        token.pop();
                        lexeme.pop();
                    //Error de sintaxis
                    } else {
                        lexem = String.valueOf(lexeme.peek());
                        String error = getError(prod);
                        t.fillErrors(errors, errorCounter, error, lexem, "Sintaxis", rowCounter);
                        errorCounter++;
                        //System.out.println("Error " + token.peek());
                        //System.out.println("Cadena " + token);
                        //System.out.println("Producciones " + productions);
                        token.pop();
                        lexeme.pop();
                    }
                }
                //Si la produccion es positiva se obtiene una nueva
                if (prod >= 0) {
                    productions.pop();
                    for (int i = 9; i > -1; i--) {
                        if (product[prod][i] == -9000) {

                        } else {
                            productions.push(product[prod][i]);
                        }
                    }
                    //System.out.println(tokens);
                    //System.out.println("Produccion nueva: \n"+producciones);
                }
                //Si la produccion es -99 es un epsilon
                if (productions.peek() == -99) {
                    productions.pop();
                }
            }
        } while (!token.empty() && !productions.empty());
        if (!token.empty() || !productions.empty()) {
            System.out.println("Cadena Invalida");
            System.out.println("Cadena: "+token);
            System.out.println("Produc: "+productions);
        } else {
            System.out.println("Cadena Valida");
            System.out.println("Cadena: "+token);
            System.out.println("Produc: "+productions);
        }
        System.out.println("Variables en la lista de variables: ");
//        for(varList vl: vars) {
//            System.out.println("Id: "+vl.id+" linea: "+vl.line+" ambito: "+vl.ambit+" nombre: "+vl.nomActual);
//        }
        reset();
    }
    
    public void insertInAmbit(Queue variables, Queue variablesS, Stack ambitStack, JTable errors) {
        Queue<String> identificadores = new LinkedList<String>();
        Queue<Integer> parametros = new LinkedList<Integer>();
        Queue<String> parametrosS = new LinkedList<String>();
        String id = "", type = "", clas = "", tarr = "", tparr = "";
        int ambit = 0, noparr = 0, dimarr = 1, counter = rowCounter;
        String query;
        int value = Integer.parseInt(String.valueOf(variables.peek()));
        do {
            if(variables.isEmpty())
                value = Integer.parseInt(String.valueOf(parametros.peek()));
            else
                value = Integer.parseInt(String.valueOf(variables.peek()));
            switch (value) {
                //id's
                case -1:
                    if (clas.equals("parametro")) {
                        identificadores.add(String.valueOf(parametrosS.peek()));
                    } else {
                        identificadores.add(String.valueOf(variablesS.peek()));
                    }
                    break;
                //variables, procedure, function y constantes
                case -5: clas="variable";break;
                case -9: clas="constante";break;
                case -7: clas="function";break;
                case -8: clas="procedure";break;
                //tipos de variables
                case -12: type="char";break;
                case -13: type="exp";break;
                case -14: type="integer";break;
                case -15: type="real";break;
                case -16: type="string";break;
                case -17: type="bool";break;
                case -18: type="file";break;
                //tipos de constantes
                case -53: type="bool";break;
                case -54: type="bool";break;
                case -55: type="integer";break;
                case -56: type="string";break;
                case -57: type="real";break;
                case -58: type="exp";break;
                case -59: type="char";break;
                //punto y coma
                case -75:
                    if(!clas.equals("parametro")) {
                        do {
                            System.out.println(identificadores);
                            id = String.valueOf(identificadores.peek());
                            ambit = Integer.parseInt(String.valueOf(ambitStack.peek()));
                            String nombre = "";
                            if(tarr.equals("")) {
                                dimarr = 0;
                                if(ambit == 0){
                                    query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+id+"')";
                                    nombre = id;
                                } else {
                                    nombre = String.valueOf(nomActual.peek());
                                    nombre += String.valueOf(ambitStack.peek());
                                    nombre += clas.charAt(0);
                                    nombre += id;
                                    query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+nombre+"')";
                                }
                            } else {
                                arr++;
                                if(ambit == 0){
                                    query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+id+"')";
                                    nombre = id;
                                } else {
                                    nombre = String.valueOf(nomActual.peek());
                                    nombre += String.valueOf(ambitStack.peek());
                                    nombre += clas.charAt(0);
                                    nombre += id;
                                    query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+nombre+"')";
                                }                            }
                            db.insertData(query, id, ambitStack, errors, errorCounter, counter, clas);
                            identificadores.poll();
                            if(clas.equals("variable")) {
                                var++;
                            } else if(clas.equals("constante")) {
                                cons++;
                            }
                            switch(type){
                                case "integer":intCounter++;break;
                                case "real":realCounter++;break;
                                case "exp":expCounter++;break;
                                case "string":stringCounter++;break;
                                case "char":charCounter++;break;
                                case "bool":boolCounter++;break;
                                case "file":fileCounter++;break;
                            }
                            varList newVar = new varList(id,counter,ambit,nombre);
                            vars.add(newVar);
                        } while(!identificadores.isEmpty());
                        id = "";
                        dimarr = 1;
                        tarr = "";
                        noparr = 0;
                        tparr = "";
                    }
                    break;
                //Parentesis abierto arreglo
                case -79:
                    if(clas.equals("variable")||clas.equals("constante")) {
                        tarr+="(";
                        variables.poll();
                        variablesS.poll();
                        int val = Integer.parseInt(String.valueOf(variables.peek()));
                        while(val!=-80){
                            if(val==-1005) {
                                dimarr++;
                                variables.poll();
                                variablesS.poll();
                                val = Integer.parseInt(String.valueOf(variables.peek()));
                            } else {
                                tarr+=String.valueOf(variablesS.peek());
                                variables.poll();
                                variablesS.poll();
                                val = Integer.parseInt(String.valueOf(variables.peek()));
                            }
                        }
                        tarr+=")";
                    } else if(clas.equals("function")||clas.equals("procedure")) {
                        variables.poll();
                        variablesS.poll();
                        int val = Integer.parseInt(String.valueOf(variables.peek()));
                        String valS = String.valueOf(variablesS.peek());
                        parametros.add(0);
                        parametrosS.add("0");
                        while(val!=-1006) {
                            parametros.add(val);
                            parametrosS.add(valS);
                            variables.poll();
                            variablesS.poll();
                            val = Integer.parseInt(String.valueOf(variables.peek()));
                            valS = String.valueOf(variablesS.peek());
                        }
                        variables.poll();
                        variablesS.poll();
                        do {
                            val = Integer.parseInt(String.valueOf(variables.peek()));
                            valS = String.valueOf(variablesS.peek());
                            switch(val) {
                                //tipos de variables
                                case -12: type="char";break;
                                case -13: type="exp";break;
                                case -14: type="integer";break;
                                case -15: type="real";break;
                                case -16: type="string";break;
                                case -17: type="bool";break;
                                case -18: type="file";break;
                                //Arreglo
                                case -79:
                                    tarr+="(";
                                    variables.poll();
                                    variablesS.poll();
                                    int valor = Integer.parseInt(String.valueOf(variables.peek()));
                                    while(valor!=-80){
                                        if(valor==-1005) {
                                            dimarr++;
                                            variables.poll();
                                            variablesS.poll();
                                            valor = Integer.parseInt(String.valueOf(variables.peek()));
                                        } else {
                                            tarr+=String.valueOf(variablesS.peek());
                                            variables.poll();
                                            variablesS.poll();
                                            valor = Integer.parseInt(String.valueOf(variables.peek()));
                                        }
                                    }
                                    tarr+=")";
                                    break;
                                default:
                                    counter = val - 1;
                                    break;
                            }
                            variables.poll();
                            variablesS.poll();
                        } while(!variables.isEmpty());
                        System.out.println(parametros);
                        System.out.println(parametrosS);
                        id = String.valueOf(identificadores.peek());
                        identificadores.poll();
                        int saveAmbit = Integer.parseInt(String.valueOf(ambitStack.peek()));
                        ambitStack.pop();
                        ambit = Integer.parseInt(String.valueOf(ambitStack.peek()));
                        ambitStack.push(saveAmbit);
                        nomActual.push(id);
                        String nombre = "";
                        if(tarr.equals("")) {
                            dimarr = 0;
                            if(ambit == 0){
                                query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+id+"')";
                                nombre = id;
                            } else {
                                nombre = String.valueOf(nomActual.peek());
                                nombre += String.valueOf(ambitStack.peek());
                                nombre += clas.charAt(0);
                                nombre += id;
                                query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+nombre+"')";
                            }
                        } else {
                            arr++;
                            if(ambit == 0){
                                query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+id+"')";
                                nombre = id;
                            } else {
                                nombre = String.valueOf(nomActual.peek());
                                nombre += String.valueOf(ambitStack.peek());
                                nombre += clas.charAt(0);
                                nombre += id;
                                query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+nombre+"')";
                            }
                        }
                        if(clas.equals("function")){
                            func++;
                        } else if(clas.equals("procedure")){
                            proce++;
                        }
                        switch(type){
                            case "integer":intCounter++;break;
                            case "real":realCounter++;break;
                            case "exp":expCounter++;break;
                            case "string":stringCounter++;break;
                            case "char":charCounter++;break;
                            case "bool":boolCounter++;break;
                            case "file":fileCounter++;break;
                        }
                        db.insertData(query, id, ambitStack, errors, errorCounter, counter, clas);
                        //Aqui es para guardar parcialmente los datos de los tipos
                        int a = Integer.parseInt(String.valueOf(ambitStack.peek()));
                        db.insertTypeCounter(a, intCounter, realCounter, expCounter, stringCounter, charCounter, boolCounter, fileCounter, -7);
                        db.insertAmbitCounter(amb, var, cons, func, proce, arr, para, pararr, ambitStack, -7);
                        var = 0;
                        cons = 0;
                        func = 0;
                        proce = 0;
                        arr = 0;
                        para = 0;
                        pararr = 0;
                        intCounter = 0;
                        realCounter = 0;
                        expCounter = 0;
                        stringCounter = 0;
                        charCounter = 0;
                        boolCounter = 0;
                        fileCounter = 0;
                        ambitStack.push(saveAmbit);
                        varList newVar = new varList(id,counter,ambit,nombre);
                        vars.add(newVar);
                        clas = "parametro";
                        tparr = id;
                        id = "";
                        tarr = "";
                        dimarr = 1;
                    } else if(clas.equals("parametro")) {
                        tarr+="(";
                        parametros.poll();
                        parametrosS.poll();
                        int val = Integer.parseInt(String.valueOf(parametros.peek()));
                        while(val!=-80){
                            if(val==-1005) {
                                dimarr++;
                                parametros.poll();
                                parametrosS.poll();
                                val = Integer.parseInt(String.valueOf(parametros.peek()));
                            } else {
                                tarr+=String.valueOf(parametrosS.peek());
                                parametros.poll();
                                parametrosS.poll();
                                val = Integer.parseInt(String.valueOf(parametros.peek()));
                            }
                        }
                        tarr+=")";
                        System.out.println(tarr);
                    }
                    break;
                case -1007:
                    if(!identificadores.isEmpty()){
                        do {
                            System.out.println(identificadores);
                            id = String.valueOf(identificadores.peek());
                            ambit = Integer.parseInt(String.valueOf(ambitStack.peek()));
                            noparr++;
                            String nombre = "";
                            if(tarr.equals("")) {
                                dimarr = 0;
                                if(ambit == 0){
                                    query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+id+"')";
                                    nombre = id;
                                } else {
                                    nombre = String.valueOf(nomActual.peek());
                                    nombre += String.valueOf(ambitStack.peek());
                                    nombre += clas.charAt(0);
                                    nombre += id;
                                    query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+nombre+"')";
                                }
                            } else {
                                pararr++;
                                if(ambit == 0){
                                    query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+id+"')";
                                    nombre = id;
                                } else {
                                    nombre = String.valueOf(nomActual.peek());
                                    nombre += String.valueOf(ambitStack.peek());
                                    nombre += clas.charAt(0);
                                    nombre += id;
                                    query = "INSERT INTO ambito(id, tipo, clase, ambito, tarr, dimarr, nopar, tparr, nomact) VALUES ('"+id+"','"+type+"','"+clas+"',"+ambit+",'"+tarr+"',"+dimarr+","+noparr+",'"+tparr+"','"+nombre+"')";
                                }
                            }
                            switch(type){
                                case "integer":intCounter++;break;
                                case "real":realCounter++;break;
                                case "exp":expCounter++;break;
                                case "string":stringCounter++;break;
                                case "char":charCounter++;break;
                                case "bool":boolCounter++;break;
                                case "file":fileCounter++;break;
                            }
                            para++;
                            db.insertData(query, id, ambitStack, errors, errorCounter, counter, clas);
                            identificadores.poll();
                            varList newVar = new varList(id,counter,ambit,nombre);
                            vars.add(newVar);
                        } while(!identificadores.isEmpty());
                        id = "";
                        dimarr = 1;
                        tarr = "";
                    }
                    break;
                default:
                    if(!variablesS.isEmpty()) {
                        if(variablesS.peek().equals("salto")) {
                            counter = Integer.parseInt(String.valueOf(variables.peek()));
                        }
                    }
                    break;
            }
            if(!variables.isEmpty()) {
                variables.poll();
                variablesS.poll();
            } else {
                parametros.poll();
                parametrosS.poll();
            }
        } while(!variables.isEmpty() || !parametros.isEmpty());
        if(clas.equals("parametro")&&noparr>0) {
            int saveAmbit = Integer.parseInt(String.valueOf(ambitStack.peek()));
            ambitStack.pop();
            ambit = Integer.parseInt(String.valueOf(ambitStack.peek()));
            ambitStack.push(saveAmbit);
            db.updateNopar(noparr, tparr, ambit);
        }
    }
    
    public void searchInAmbit(String id, Stack ambit, JTable errors) {
        int[] backup = new int[ambit.size()];
        int back;
        for(int i=0;i<backup.length;i++){
            back = Integer.parseInt(String.valueOf(ambit.peek()));
            backup[i] = back;
            ambit.pop();
        }
        for(int i=backup.length;i>0;i--){
            ambit.push(backup[i-1]);
        }
        if(!db.searchVariables(id, ambit)) {
            t.fillErrors(errors, errorCounter, "Variable no declarada", id, "Ambito", rowCounter);
            errorCounter++;
        }
        System.out.println("Devuelta del metodo");
        System.out.println(ambit);
        for(int i=backup.length;i>0;i--){
            ambit.push(backup[i-1]);
        }
        System.out.println("Devuleta de meter los datos");
        System.out.println(ambit);
    }

    public int searchValues(Stack productions, Stack token) {
        int valcol = 0, valren = 0, valor;
        valren = Integer.parseInt(String.valueOf(productions.peek()));
        valcol = getColumn(Integer.parseInt(String.valueOf(token.peek())));
        valor = sintax[valren][valcol];
        return valor;
    }

    public int getColumn(int val) {
        int col = 0;
        switch (val) {
            case -1:
                col = 0;
                break;
            case -4:
                col = 1;
                break;
            case -5:
                col = 2;
                break;
            case -6:
                col = 3;
                break;
            case -7:
                col = 4;
                break;
            case -8:
                col = 5;
                break;
            case -9:
                col = 6;
                break;
            case -10:
                col = 7;
                break;
            case -11:
                col = 8;
                break;
            case -12:
                col = 9;
                break;
            case -13:
                col = 10;
                break;
            case -14:
                col = 11;
                break;
            case -15:
                col = 12;
                break;
            case -16:
                col = 13;
                break;
            case -17:
                col = 14;
                break;
            case -18:
                col = 15;
                break;
            case -19:
                col = 16;
                break;
            case -20:
                col = 17;
                break;
            case -21:
                col = 18;
                break;
            case -22:
                col = 19;
                break;
            case -23:
                col = 20;
                break;
            case -24:
                col = 21;
                break;
            case -25:
                col = 22;
                break;
            case -26:
                col = 23;
                break;
            case -27:
                col = 24;
                break;
            case -28:
                col = 25;
                break;
            case -29:
                col = 26;
                break;
            case -30:
                col = 27;
                break;
            case -31:
                col = 28;
                break;
            case -32:
                col = 29;
                break;
            case -33:
                col = 30;
                break;
            case -34:
                col = 31;
                break;
            case -35:
                col = 32;
                break;
            case -36:
                col = 33;
                break;
            case -37:
                col = 34;
                break;
            case -38:
                col = 35;
                break;
            case -40:
                col = 36;
                break;
            case -41:
                col = 37;
                break;
            case -42:
                col = 38;
                break;
            case -43:
                col = 39;
                break;
            case -44:
                col = 40;
                break;
            case -45:
                col = 41;
                break;
            case -46:
                col = 42;
                break;
            case -47:
                col = 43;
                break;
            case -48:
                col = 44;
                break;
            case -49:
                col = 45;
                break;
            case -50:
                col = 46;
                break;
            case -51:
                col = 47;
                break;
            case -52:
                col = 48;
                break;
            case -53:
                col = 49;
                break;
            case -54:
                col = 50;
                break;
            case -55:
                col = 51;
                break;
            case -56:
                col = 52;
                break;
            case -57:
                col = 53;
                break;
            case -58:
                col = 54;
                break;
            case -59:
                col = 55;
                break;
            case -60:
                col = 56;
                break;
            case -61:
                col = 57;
                break;
            case -62:
                col = 58;
                break;
            case -63:
                col = 59;
                break;
            case -64:
                col = 60;
                break;
            case -65:
                col = 61;
                break;
            case -66:
                col = 62;
                break;
            case -67:
                col = 63;
                break;
            case -68:
                col = 64;
                break;
            case -69:
                col = 65;
                break;
            case -70:
                col = 66;
                break;
            case -71:
                col = 67;
                break;
            case -72:
                col = 68;
                break;
            case -73:
                col = 69;
                break;
            case -74:
                col = 70;
                break;
            case -75:
                col = 71;
                break;
            case -76:
                col = 72;
                break;
            case -77:
                col = 73;
                break;
            case -78:
                col = 74;
                break;
            case -79:
                col = 75;
                break;
            case -80:
                col = 76;
                break;
            case -81:
                col = 77;
                break;
            case -82:
                col = 78;
                break;
            case -83:
                col = 79;
                break;
            case -84:
                col = 80;
                break;
            case -85:
                col = 81;
                break;
            case -86:
                col = 82;
                break;
            case -87:
                col = 83;
                break;
            case -88:
                col = 84;
                break;
            case -89:
                col = 85;
                break;
            case -800:
                col = 86;
                break;
        }
        return col;
    }

    public String getError(int noError) {
        String error = "";
        switch (noError) {
            case -501:
                error = "Se esperaba program";
                break;//Error PROGRAM
            case -502:
                error = "Se esperaba var function procedure use o begin";
                break;//Error BLOQUE
            case -503:
                error = "Se esperaba var";
                break;//Error A1, A4
            case -504:
                error = "Se esperaba function o procedure";
                break;//Error A2
            case -505:
                error = "Se esperaba use";
                break;//Error A3
            case -506:
                error = "Se esperaba ;";
                break;//Error A5, C2
            case -507:
                error = "Se esperaba forward begin var function procedure o use";
                break;//Error A9
            case -508:
                error = "Se esperaba id";
                break;//Error A6, A7, A8, C1, DECLARACION VARIABLES
            case -509:
                error = "Se esperaba char exp integer real string bool file";
                break;//Error TIPO
            case -510:
                error = "Se esperaba (";
                break;//Error B1, LISTA PARAMETROS
            case -511:
                error = "Se esperaba ,";
                break;//Error B2, D1, I2, I6, L1, M4
            case -512:
                error = "Se esperaba real cadena carácter entero exponencial true false id ++ -- ( ! limpiarpantalla sqrt sqr exp strcmp strcat strcpy strlen strins toupper tolower open close scanf printf asc o chr";
                break;//Error ELEVACION, TERMINO PASCAL, FACTOR PASCAL
            case -513:
                error = "Se esperaba ^";
                break;//Error E1
            case -514:
                error = "Se esperaba * / # o &&";
                break;//Error F1
            case -515:
                error = "Se esperaba + - real cadena carácter entero exponencial true false id ++ -- ( ! limpiarpantalla sqrt sqr exp strcmp strcat strcpy strlen strins toupper tolower open close scanf printf asc o chr";
                break;//Error SIMPLE EXP PASCAL, EXP PASCAL
            case -516:
                error = "Se esperaba + o -";
                break;//Error G1
            case -517:
                error = "Se esperaba || + o -";
                break;//Error G2
            case -518:
                error = "Se esperaba < <= == != >= o >";
                break;//Error H1
            case -519:
                error = "Se esperaba write read if repeat for while begin case retur + - real cadena carácter entero exponencial true false id ++ -- ( ! limpiarpantalla sqrt sqr exp strcmp strcat strcpy strlen strins toupper tolower open close scanf printf asc o chr";
                break;//Error ESTATUTOS
            case -520:
                error = "Se esperaba (";
                break;//Error I1
            case -521:
                error = "Se esperaba [";
                break;//Error I3, ARREGLO, J2
            case -522:
                error = "Se esperaba else";
                break;//Error I4
            case -523:
                error = "Se esperaba write read if repeat for while begin case retur + - real cadena carácter entero exponencial true false id ++ -- ( ! limpiarpantalla sqrt sqr exp strcmp strcat strcpy strlen strins toupper tolower open close scanf printf asc chr o ;";
                break;//Error I5
            case -524:
                error = "Se esperaba else o romper";
                break;//Error I7
            case -525:
                error = "Se esperaba + - real cadena carácter entero exponencial true false id ++ -- ( ! limpiarpantalla sqrt sqr exp strcmp strcat strcpy strlen strins toupper tolower open close scanf printf asc chr o todo";
                break;//Error J1
            case -526:
                error = "Se esperaba real cadena carácter entero exponencial true o false";
                break;//Error CONSTANTE S/SIGNO
            case -527:
                error = "Se esperaba = += /= *= o -=";
                break;//Error ASIGNACION, M3
            case -528:
                error = "Se esperaba + - real cadena carácter entero exponencial true o false";
                break;//Error DECLARACION CONSTANTES
            case -529:
                error = "Se esperaba Valor entero o decimal";
                break;//Error K1
            case -530:
                error = "Se esperaba limpiarpantalla sqrt sqr exp strcmp strcat strcpy strlen strins toupper tolower open close scanf printf asc o chr";
                break;//Error FUNCIONES
            case -531:
                error = "Se esperaba ++ -- [ = += /= *= -= o (";
                break;//Error M1
            case -532:
                error = "Se esperaba + - real cadena carácter entero exponencial true false id ++ -- ( ! limpiarpantalla sqrt sqr exp strcmp strcat strcpy strlen strins toupper tolower open close scanf printf asc chr , o )";
                break;//Error M2
        }
        return error;
    }

    public Queue deleteQueue(Queue lista) {
        while(!lista.isEmpty()) {
            lista.poll();
        }
        return lista;
    }

    public void fillVarListTable(JTable table) { 
        DefaultTableModel model=(DefaultTableModel)table.getModel();
        for(varList vl: vars) {
            String[] vlTable = {"","",""};
            vlTable[0] = vl.id;
            vlTable[1] = String.valueOf(vl.ambit);
            vlTable[2] = vl.nomActual;
            model.addRow(vlTable);
        }
    }
}

