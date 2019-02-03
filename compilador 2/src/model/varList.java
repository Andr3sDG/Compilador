/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author coffeeleak
 */
public class varList {
    String id;
    int line;
    int ambit;
    String nomActual;

    public varList(String id, int line, int ambit, String nomActual) {
        this.id = id;
        this.line = line;
        this.ambit = ambit;
        this.nomActual = nomActual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getAmbit() {
        return ambit;
    }

    public void setAmbit(int ambit) {
        this.ambit = ambit;
    }

    public String getNomActual() {
        return nomActual;
    }

    public void setNomActual(String nomActual) {
        this.nomActual = nomActual;
    }
    
}


