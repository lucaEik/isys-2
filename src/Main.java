
import classifier.Classifier;

import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException, Exception {
        // paths to the data
        String data = "data/data.csv";
        String A0 = "data/A0.csv";
        String B0 = "data/B0.csv";
        String A1 = "data/A1.csv";
        String B1 = "data/B1.csv";

        //classify the objects
        Classifier c = new Classifier(data, 90, 25);
        c.classify(A0, B0, A1, B1);
    }
}
