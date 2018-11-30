import org.apache.commons.lang.ArrayUtils;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Classifier  {

    private int avgHDiffRange = 105;
    private int stsHDiffRange = 40;

    private double[][] data;

    private RandomForest classifier;

    public Classifier(String dataPath) throws IOException {
        // csv file with data:
        File file = new File(dataPath);
        BufferedReader bf = new BufferedReader(new FileReader(file));

        // read the data from the csv file:
        List<List<Double>> dataList = new ArrayList<>();
        String line;
        String[] splitStrings;
        while ((line = bf.readLine()) != null) {
            splitStrings = line.split(",");
            ArrayList<Double> row = new ArrayList<>();
            for (int i = 0; i < splitStrings.length; i++) {
                row.add(Double.valueOf(splitStrings[i]));
            }
            dataList.add(row);
        }

        Double[][] doubs = new Double[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            doubs[i] = dataList.get(i).toArray(new Double[dataList.get(i).size()]);
        }

        double[][] data = new double[doubs.length][];
        for (int i = 0; i < doubs.length; i++) {
            data[i] = ArrayUtils.toPrimitive(doubs[i]);
        }
        this.data = data;
    }

    public DataObject[] readObjects(String path) throws IOException {
        File file = new File(path);
        BufferedReader bf = new BufferedReader(new FileReader(file));

        String line;
        String[] split;

        // List for the objects from the csv file
        List<DataObject> objects = new ArrayList<>();

        // read the data from file
        while ((line = bf.readLine()) != null) {
            split = line.split(",");
            objects.add(new DataObject(Integer.valueOf(split[1]), Integer.valueOf(split[0])));
        }

        return objects.toArray(new DataObject[objects.size()]);
    }

    private double[] nearestKNeighbours(DataObject obj, int range, int steps) {    //TODO: maybe add step size
        // TODO fix the "out of range problem"
        double[] neighbours = new double[(int)Math.pow(2 * (range + 1), 2)];
        int index = 0;
        for (int row = -range; row <= range; row+=steps) {
            for (int col = -range; col <= range; col+=steps, index++) {
                if (obj.getRow() + row >= 0 && obj.getCol() + col >= 0 && obj.getRow() + row < data.length && obj.getCol() + col < data[0].length) {
                    neighbours[index] = data[obj.getRow() + row][obj.getCol() + col];
                } else {
                    neighbours[index] = data[obj.getRow()][obj.getCol()];   // TODO: object markers value is added to prevent OOB-exception
                }
            }
        }

        return neighbours;
    }

    private double avgHeightDifference(DataObject obj, int range) {
        // TODO fix the "out of range problem"
        double avgHeight = 0;
        int counter = 0;
        for (int row = -range; row <= range; row++) {
            for (int col = -range; col <= range; col++, counter++) {
                if (obj.getRow() + row >= 0 && obj.getCol() + col >= 0 && obj.getRow() + row < data.length && obj.getCol() + col < data[0].length) {
                    avgHeight += data[obj.getRow() + row][obj.getCol() + col];
                }
            }
        }

        return Math.abs(data[obj.getRow()][obj.getCol()] - (avgHeight / counter));
    }

    private double highestSTSDiff(DataObject obj, int range) {

        ArrayList<DataObject> objects = new ArrayList<>();
        double heightDiff = 0.0;

        for (int row = -range; row <= range; row++) {
            for (int col = -range; col <= range; col++) {
                if (obj.getRow() + row >= 0 && obj.getCol() + col >= 0 && obj.getRow() + row < data.length && obj.getCol() + col < data[0].length) {
                    objects.add(new DataObject(obj.getRow() + row, obj.getCol() + col));
                }
            }
        }

        for (DataObject object : objects) {
            if (object.getRow() - 1 >= 0){
                double diff = Math.abs(data[object.getRow()][object.getCol()] - data[object.getRow() - 1][object.getCol()]);
                if (diff > heightDiff) {
                    heightDiff = diff;
                }
            }
            if (object.getRow() + 1 < data.length){
                double diff = Math.abs(data[object.getRow()][object.getCol()] - data[object.getRow() + 1][object.getCol()]);
                if (diff > heightDiff) {
                    heightDiff = diff;
                }
            }
            if (object.getCol() - 1 >= 0){
                double diff = Math.abs(data[object.getRow()][object.getCol()] - data[object.getRow()][object.getCol() - 1]);
                if (diff > heightDiff) {
                    heightDiff = diff;
                }
            }
            if (object.getCol() + 1 < data[0].length){
                double diff = Math.abs(data[object.getRow()][object.getCol()] - data[object.getRow()][object.getCol() + 1]);
                if (diff > heightDiff) {
                    heightDiff = diff;
                }
            }
        }

        return heightDiff;
    }

    private double avgHDiff(DataObject obj, int range) {
        double avgDiff = 0.0;
        int count = 0;
        for (int row = -range; row < range; row++) {
            for (int col = -range; col < range; col++) {
                if (obj.getRow() + row >= 0 && obj.getRow() + row < data.length && obj.getCol() + col >= 0 && obj.getCol() + col < data[0].length) {
                    avgDiff += data[obj.getRow() + row][obj.getCol() + col];
                    count++;
                }
            }
        }
        return Math.abs(data[obj.getRow()][obj.getCol()] - avgDiff / count);
    }

    /*
    private double rise(DataObject obj, int range) {
        // TODO fix the "out of range problem"

        if (obj.getRow() + range >= 0 && obj.getCol() + range >= 0 && obj.getRow() + range < data.length && obj.getCol() + range < data[0].length)
            return 0;

        double north, south, east, west;
        north = Math.abs(data[obj.getRow()][obj.getCol()] - data[obj.getRow() - range][obj.getCol()]) / range;
        south = Math.abs(data[obj.getRow()][obj.getCol()] - data[obj.getRow() + range][obj.getCol()]) / range;
        east = Math.abs(data[obj.getRow()][obj.getCol()] - data[obj.getRow()][obj.getCol() + range]) / range;
        west = Math.abs(data[obj.getRow()][obj.getCol()] - data[obj.getRow()][obj.getCol() - range]) / range;

        return (north + east + south + west) / 4;
    }
    */

    public void trainNew(String aObjectsPath, String bObjectsPath) throws Exception{
        // Lists for the training objects
        DataObject[] aObjects = readObjects(aObjectsPath);
        DataObject[] bObjects = readObjects(bObjectsPath);

        // height differences
        int bothLen = aObjects.length + bObjects.length;
        double[][] values = new double[bothLen][2];
        for (int i = 0; i < aObjects.length; i++) {
            values[i][0] = avgHDiff(aObjects[i], avgHDiffRange);
            values[i][1] = highestSTSDiff(aObjects[i], stsHDiffRange);
        }
        for (int i = aObjects.length; i < bothLen; i++) {
            values[i][0] = avgHDiff(bObjects[i - aObjects.length], avgHDiffRange);
            values[i][1] = highestSTSDiff(bObjects[i - aObjects.length], stsHDiffRange);
        }

        // classes
        List<String> targets = new ArrayList<>();
        targets.add("A");
        targets.add("B");

        // add attribute names
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("avgHeightDiff"));
        attributes.add(new Attribute("highestSTSDiff"));


        // create instances with attributes etc.
        Instances dataRaw = new Instances("TrainInstance", attributes, 0);

        // add attribute values
        for (int i = 0; i < values.length; i++) {
            dataRaw.add(new DenseInstance(1.0, values[i]));
        }

        // insert target attribute
        dataRaw.insertAttributeAt(new Attribute("target", targets), dataRaw.numAttributes());

        // set target value for A-objects
        for (int i = 0 ; i < aObjects.length ; i++) {
            dataRaw.instance(i).setValue(dataRaw.numAttributes()-1, "A");
        }

        // set target values for B-objects
        for (int i = aObjects.length; i < values.length; i++) {
            dataRaw.instance(i).setValue(dataRaw.numAttributes()-1, "B");
        }

        // set class index to target attribute (last attribute of the Instances)
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);

        dataRaw.randomize(new Random());

        int trainSize = (int) Math.round(dataRaw.numInstances() * 0.8);
        int testSize = dataRaw.numInstances() - trainSize;
        Instances testData = new Instances(dataRaw, 0, testSize);
        Instances trainData = new Instances(dataRaw, testSize, trainSize);

        int folds = 5;

        Evaluation eval = new Evaluation(testData);

        // build random forest classifier
        RandomForest clf = new RandomForest();
        clf.buildClassifier(trainData);

        eval.crossValidateModel(clf, testData, folds, new Random());

        System.out.println(eval.toMatrixString());
        System.out.println(eval.toSummaryString("\nResults\n========\n", false));
        System.out.println("Results for A");
        System.out.println("precision= " + eval.precision(0));
        System.out.println("recall= " + eval.recall(0));
        System.out.println("Results for B");
        System.out.println("precision= " + eval.precision(1));
        System.out.println("recall= " + eval.recall(1));

        // save the random forest in the classifier field
        this.classifier = clf;

}
/*
    public void train(String aObjectsPath, String bObjectsPath) throws Exception {

        // Lists for the training objects
        DataObject[] aObjects = readObjects(aObjectsPath);
        DataObject[] bObjects = readObjects(bObjectsPath);

        System.out.println(aObjects.length);
        System.out.println(bObjects.length);

        // neighbours
        int neighboursLen = aObjects.length + bObjects.length;
        double[][] neighbours = new double[neighboursLen][];
        for (int i = 0; i < aObjects.length; i++) {
            neighbours[i] = nearestKNeighbours(aObjects[i], 5, 1);   //TODO range
        }
        for (int i = aObjects.length; i < neighboursLen; i++) {
            neighbours[i] = nearestKNeighbours(bObjects[i - aObjects.length], 5, 1);   //TODO range
        }

        //classes
        List<String> targets = new ArrayList<>();
        targets.add("A");
        targets.add("B");

        //add attribute names
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < neighbours[0].length; i++) {
            attributes.add(new Attribute(String.valueOf(i)));
        }

        // create instances with attributes etc.
        Instances dataRaw = new Instances("TrainInstance", attributes, 0);

        // add attribute values
        for (int i = 0; i < neighbours.length; i++) {
            dataRaw.add(new DenseInstance(1.0, neighbours[i]));
        }

        // insert target attribute
        dataRaw.insertAttributeAt(new Attribute("target", targets), dataRaw.numAttributes());

        // set target value for A-objects
        for (int i = 0 ; i < aObjects.length ; i++) {
            dataRaw.instance(i).setValue(dataRaw.numAttributes()-1, "A");
        }

        // set target values for B-objects
        for (int i = aObjects.length; i < neighbours.length; i++) {
            dataRaw.instance(i).setValue(dataRaw.numAttributes()-1, "B");
        }

        // set class index to target attribute (last attribute of the Instances)
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);

        dataRaw.randomize(new Random(1));
        Filter normalizer = new Normalize();
        normalizer.setInputFormat(dataRaw);
        Instances normalized = Filter.useFilter(dataRaw, normalizer);

        int trainSize = (int) Math.round(dataRaw.numInstances() * 0.8);
        int testSize = dataRaw.numInstances() - trainSize;
        Instances testData = new Instances(normalized, 0, testSize);
        Instances trainData = new Instances(normalized, testSize, trainSize);

        int folds = 5;

        Evaluation eval = new Evaluation(testData);

        // build random forest classifier
        RandomForest clf = new RandomForest();
        clf.buildClassifier(trainData);

        eval.crossValidateModel(clf, testData, folds, new Random(1));

        System.out.println(eval.toMatrixString());
        System.out.println(eval.toSummaryString("\nResults\n========\n", false));
        System.out.println("Results for B");
        System.out.println("precision= " + eval.precision(0));
        System.out.println("recall= " + eval.recall(0));
        System.out.println("Results for A");
        System.out.println("precision= " + eval.precision(1));
        System.out.println("recall= " + eval.recall(1));

        /*
        dataRaw.randomize(new Debug.Random(1));
        Filter normalizer = new Normalize();

        normalizer.setInputFormat(dataRaw);
        Instances normalized = Filter.useFilter(dataRaw, normalizer);

        int folds = 5;
        RandomForest clf = new RandomForest();

        // save the random forest in the classifier field
        this.classifier = clf;
    }
*/
    public Map<String, List<DataObject>> classify(String objectsPath) throws Exception {
        // analog zu train()
        DataObject[] objects = readObjects(objectsPath);
        double[][] values = new double[objects.length][2];
        for (int i = 0; i < objects.length; i++) {
            values[i][0] = avgHeightDifference(objects[i], avgHDiffRange);
            values[i][1] = highestSTSDiff(objects[i], stsHDiffRange);
        }
        List<String> targets = new ArrayList<>();
        targets.add("A");
        targets.add("B");

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("avgHeightDiff"));

        Instances dataRaw = new Instances("DataInstance", attributes, 0);
        for (int i = 0; i < values.length; i++) {
            dataRaw.add(new DenseInstance(1.0, values[i]));
        }

        // insert target attribute
        dataRaw.insertAttributeAt(new Attribute("target", targets), dataRaw.numAttributes());

        // set class index to target attribute
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);

        Map<String, List<DataObject>> results = new HashMap<>();
        results.put("A", new ArrayList<>());
        results.put("B", new ArrayList<>());

        for (int i = 0; i < dataRaw.numInstances(); i++) {
            if (this.classifier.classifyInstance(dataRaw.instance(i)) == 1.0) {
                results.get("B").add(objects[i]);
            } else if (this.classifier.classifyInstance(dataRaw.instance(i)) == 0.0) {
                results.get("A").add(objects[i]);
            }
        }

        return results;
    }

    /*
    public List<ArrayList<Double>> getData(){
        return this.data;
    }
    */

    /*
    public List<DataObject> dataPoints(String path) throws  IOException {

        File file = new File(path);
        BufferedReader bf = new BufferedReader(new FileReader(file));

        String line;
        String[] split;

        // List for the objects from the csv file
        List<DataObject> objects = new ArrayList<>();

        // read the data from file
        while ((line = bf.readLine()) != null) {
            split = line.split(",");
            objects.add(new DataObject(Integer.valueOf(split[1]), Integer.valueOf(split[0])));
        }
        return objects;
    }
    */
    /*
    public ArrayList<ArrayList<Double>> nearestKNeightbours(int range,int steps, List<DataObject> datapoints) {
        int min_range = -range;
        int max_range = range;
        ArrayList<Double> rangelist = new ArrayList<>();
        ArrayList<ArrayList<Double>> ret = new ArrayList<>();

        for (int i = 0; i < datapoints.size(); i++) {
            int x_index = datapoints.get(i).getRow();
            int y_index = datapoints.get(i).getCol();
            for (int j = min_range; j <= max_range; j+= steps) {
                for (int z = min_range; z <= max_range; z+= steps) {
                    if (x_index + j < 0 || x_index + j >= 4943 || y_index + z < 0 || y_index + z >= 3000 ) {
                        rangelist.add(0.0);
                    }
                    else {
                        rangelist.add(this.data.get(x_index + j).get(y_index + z));
                    }
                }
            }
            //  rangelist.remove(this.data.get(x_index).get(y_index));
            ret.add(rangelist);
            rangelist = new ArrayList<>();
        }
        return ret;
    }
    */
    /*
    public ArrayList<ArrayList<Double>> concatenateAB(ArrayList<ArrayList<Double>>A, ArrayList<ArrayList<Double>> B) {
        for (ArrayList<Double> list :  B) {
            A.add(list);
        }
        return A;
    }
    */
}
