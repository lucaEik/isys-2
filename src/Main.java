
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main {

    public static void main(String args[]) throws IOException, Exception {

        Classifier c = new Classifier("C:\\Users\\Luca\\Desktop\\data\\data.csv");
        c.trainNew("C:\\Users\\Luca\\Desktop\\data\\A0.csv", "C:\\Users\\Luca\\Desktop\\data\\B0.csv");


        System.out.println("Results A1: ");
        Map<String, List<DataObject>> results = c.classify("C:\\Users\\Luca\\Desktop\\data\\A1.csv");
        System.out.println("A: " + results.get("A").size());
        System.out.println("B: " + results.get("B").size());

        System.out.println("Results B1:");
        results = c.classify("C:\\Users\\Luca\\Desktop\\data\\B1.csv");
        System.out.println("A: " + results.get("A").size());
        System.out.println("B: " + results.get("B").size());

        /*

        String data = System.getProperty("user.dir") + "/data/data.csv";
        String A0 = System.getProperty("user.dir") + "/data/A0.csv";
        String B0 = System.getProperty("user.dir") + "/data/B0.csv";
        //String A1 = System.getProperty("user.dir") + "/A1.csv";
        //String B1 = System.getProperty("user.dir") + "/B1.csv";
        Classifier c = new Classifier(data);

        List<DataObject> A_obj = c.dataPoints(A0);
        List<DataObject> B_obj = c.dataPoints(B0);


        // array with attributes
        ArrayList<ArrayList<Double>> A_x = c.nearestKNeightbours(10,2, A_obj);
        ArrayList<ArrayList<Double>> B_x = c.nearestKNeightbours(10,2, B_obj);

        ArrayList<ArrayList<Double>> A_B_Array = c.concatenateAB(A_x, B_x);

        // convert 2d list to 2d array
        Double[][] doubleArray = new Double[A_B_Array.size()][];
        for (int i = 0; i < A_B_Array.size(); i++) {
            List<Double> row = A_B_Array.get(i);
            doubleArray[i] = row.toArray(new Double[row.size()]);
        }

        // classes:
        List<String> targets = new LinkedList<>();
        targets.add("B");
        targets.add("A");

        System.out.println("Anzahl X-Merkmale: " + doubleArray[0].length );



        // add attribute names to attribute list
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < doubleArray[0].length; i++) {
            attributes.add(new Attribute(String.valueOf(i)));
        }

        // target attribute
        Attribute classAttr = new Attribute("target", targets);

        Instances dataRaw = new Instances("Datainstance", attributes, 0);

        for(Double[] row : doubleArray) {
                dataRaw.add(new DenseInstance(1.0, ArrayUtils.toPrimitive(row)));
        }


        dataRaw.insertAttributeAt(classAttr, dataRaw.numAttributes());


        // set target value for A-objects
        for (int i = 0 ; i < A_obj.size(); i++) {
            dataRaw.instance(i).setValue(dataRaw.numAttributes()-1, "A");
        }

        // set target values for B-objects
        for (int i = A_obj.size(); i < A_B_Array.size(); i++) {
            dataRaw.instance(i).setValue(dataRaw.numAttributes()-1, "B");
        }



        dataRaw.setClassIndex(dataRaw.numAttributes()-1);

        int trainSize = (int) Math.round(dataRaw.numInstances() * 0.8);
        int testSize = dataRaw.numInstances() - trainSize;

        dataRaw.randomize(new Debug.Random(1));
        Filter normalizer = new Normalize();

        normalizer.setInputFormat(dataRaw);
        Instances normalized = Filter.useFilter(dataRaw, normalizer);

        int folds = 5;
        RandomForest clf = new RandomForest();



        Instances testData = new Instances(normalized, 0, testSize);
        Instances trainData = new Instances(normalized, testSize, trainSize);
        Evaluation eval = new Evaluation(testData);

        clf.buildClassifier(trainData);

        eval.crossValidateModel(clf, testData, folds, new Random(1));

        //print results
        System.out.println(eval.toMatrixString());
        System.out.println(eval.toSummaryString("\nResults\n========\n", false));
        System.out.println("Results for B");
        System.out.println("precision= " + eval.precision(0));
        System.out.println("recall= " + eval.recall(0));
        System.out.println("Results for A");
        System.out.println("precision= " + eval.precision(1));
        System.out.println("recall= " + eval.recall(1));

        */

    }
}
