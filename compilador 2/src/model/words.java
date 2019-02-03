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
public class words {
    
    public int reservedWords(String l)
    {
        int token = -1;
        switch(l)
        {
            case"program":token=-4;break;
            case"var":token=-5;break;
            case"forward":token=-6;break;
            case"function":token=-7;break;
            case"procedure":token=-8;break;
            case"use":token=-9;break;
            case"begin":token=-10;break;
            case"end":token=-11;break;
            case"char":token=-12;break;
            case"exp":token=-13;break;
            case"integer":token=-14;break;
            case"real":token=-15;break;
            case"string":token=-16;break;
            case"bool":token=-17;break;
            case"file":token=-18;break;
            case"write":token=-19;break;
            case"read":token=-20;break;
            case"if":token=-21;break;
            case"then":token=-22;break;
            case"else":token=-23;break;
            case"repeat":token=-24;break;
            case"until":token=-25;break;
            case"for":token=-26;break;
            case"to":token=-27;break;
            case"fnpara":token=-28;break;
            case"while":token=-29;break;
            case"do":token=-30;break;
            case"case":token=-31;break;
            case"of":token=-32;break;
            case"romper":token=-33;break;
            case"retur":token=-34;break;
            case"todo":token=-35;break;
            case"limpiarpantalla":token=-36;break;
            case"sqrt":token=-37;break;
            case"sqr":token=-38;break;
            case"strcmp":token=-40;break;
            case"strcat":token=-41;break;
            case"strcpy":token=-42;break;
            case"strlen":token=-43;break;
            case"strins":token=-44;break;
            case"toupper":token=-45;break;
            case"tolower":token=-46;break;
            case"open":token=-47;break;
            case"close":token=-48;break;
            case"scanf":token=-49;break;
            case"printf":token=-50;break;
            case"asc":token=-51;break;
            case"chr":token=-52;break;
            case"true":token=-53;break;
            case"false":token=-54;break;
        }
        return token;
    }
    
}
