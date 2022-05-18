package de.dhbw.mosbach.se.si.app3;

public class App {
    
    public static void main(String[] args) {
        System.out.println("app3 is working properly!");

        var d1 = 1.0;
        var d2 = 0.1e80;

        var erg = Math.pow(0.00000001, 2.0) * Math.pow(d1 / d2, 2.0);

        System.out.println(erg);
    }
}
