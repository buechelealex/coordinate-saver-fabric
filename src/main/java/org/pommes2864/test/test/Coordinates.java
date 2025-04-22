package org.pommes2864.test.test;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;

public class Coordinates {
    private String Name;
    private int Xcoord;
    private int Ycoord;
    private int Zcoord;

    public Coordinates(String Name, int Xcoord, int Ycoord, int Zcoord){

        this.Name = Name;
        this.Xcoord = Xcoord;
        this.Ycoord = Ycoord;
        this.Zcoord = Zcoord;

    }

    public String getName() {
        return Name;
    }

    public int getXcoord() {
        return Xcoord;
    }

    public int getYcoord() {
        return Ycoord;
    }

    public int getZcoord() {
        return Zcoord;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        String template = "§6| %-15s| %6s | %6s | %6s |";
        formatter.format(template, getName(), getXcoord(), getYcoord(), getZcoord());

        return stringBuilder.toString();

        // return Name + " " + Xcoord + " " + Ycoord + " " + Zcoord;

    }


}
