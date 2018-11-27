import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String args[]) {
        try {
            Classifier c = new Classifier("C:\\Users\\Luca\\Studium\\Intelligente Systeme\\Praktikum\\Aufgabe 2\\data.csv");

            System.out.println("A0:");
            Map<Character, List<DataObject>> a0Results = c.classify("C:\\Users\\Luca\\Studium\\Intelligente Systeme\\Praktikum\\Aufgabe 2\\A0.csv", 5, 10);
            System.out.println("A-Objects: " + a0Results.get('A').size() + "(" + (double)a0Results.get('A').size() / (a0Results.get('A').size() + a0Results.get('B').size()) * 100 + "%)");
            System.out.println("B-Objects: " + a0Results.get('B').size() + "(" + (double)a0Results.get('B').size() / (a0Results.get('A').size() + a0Results.get('B').size()) * 100 + "%)");


            System.out.println("\nB0:");
            Map<Character, List<DataObject>> b0Results = c.classify("C:\\Users\\Luca\\Studium\\Intelligente Systeme\\Praktikum\\Aufgabe 2\\B0.csv", 5, 10);
            System.out.println("A-Objects: " + b0Results.get('A').size() + "(" + (double)b0Results.get('A').size() / (b0Results.get('A').size() + b0Results.get('B').size()) * 100 + "%)");
            System.out.println("B-Objects: " + b0Results.get('B').size() + "(" + (double)b0Results.get('B').size() / (b0Results.get('A').size() + b0Results.get('B').size()) * 100+ "%)");

            System.out.println("\nA1");
            Map<Character, List<DataObject>> a1Results = c.classify("C:\\Users\\Luca\\Studium\\Intelligente Systeme\\Praktikum\\Aufgabe 2\\A1.csv", 5, 10);
            System.out.println("A-Objects: " + a1Results.get('A').size() + "(" + (double)a0Results.get('A').size() / (a0Results.get('A').size() + a0Results.get('B').size()) * 100 + "%)");
            System.out.println("B-Objects: " + a1Results.get('B').size() + "(" + (double)a0Results.get('B').size() / (a0Results.get('A').size() + a0Results.get('B').size()) * 100 + "%)");


            System.out.println("\nB1:");
            Map<Character, List<DataObject>> b1Results = c.classify("C:\\Users\\Luca\\Studium\\Intelligente Systeme\\Praktikum\\Aufgabe 2\\B1.csv", 5, 10);
            System.out.println("A-Objects: " + b1Results.get('A').size() + "(" + (double)b1Results.get('A').size() / (b1Results.get('A').size() + b1Results.get('B').size()) * 100 + "%)");
            System.out.println("B-Objects: " + b1Results.get('B').size() + "(" + (double)b1Results.get('B').size() / (b1Results.get('A').size() + b1Results.get('B').size()) * 100+ "%)");

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
