package classifier;

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
import java.util.List;
import java.util.Random;
import weka.core.Debug;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/**
 * The Classifier classifies Objects as either A or B objects based on the average difference between the marked value of
 * an object and the highest difference between two neighbouring values in the objects surroundings using random forests.
 * @author Christoph Thomas, Luca Thurm
 */
public class Classifier  {

    /**
     * The range which is examined in each direction from the marked point of a classifier.DataObject when
     * searching for the difference between the average value of the surroundings and the marked value
     * (tuning-parameter for avgHDiff-method).
     */
    private int avgHDiffRange;

    /**
     * The range which is examined in each direction from the marked point of a classifier.DataObject when searching for the
     * highest difference between two neighbouring values (tuning-parameter for stsHDiff-method).
     */
    private int stsHDiffRange;

    /**
     * A 2D-Array containing the Data with the objects to classify
     */
    private double[][] data;

    /**
     * Creates a new Instance of the classifier.Classifier-Type which classifies DataObjects as either A or B objects based on the
     * average value in their surroundings and the highest difference between two neighbouring values in the objects
     * surroundings.
     * @param dataPath The path to a .csv file containing the data.
     * @param avgHDiffRange The range which is examined in each direction from the marked point of a classifier.DataObject when
     *                      searching for the difference between the average value of the surroundings and the marked
     *                      value(tuning-parameter for avgHDiff-method).
     * @param stsHDiffRange The range which is examined in each direction from the marked point of a classifier.DataObject when
     *                      searching for the highest difference between two neighbouring values (tuning-parameter for
     *                      stsHDiff-method).
     * @throws IOException if the path to the file does not exist.
     */
    public Classifier(String dataPath, int avgHDiffRange, int stsHDiffRange) throws IOException {
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
        this.avgHDiffRange = avgHDiffRange;
        this.stsHDiffRange = stsHDiffRange;
    }

    /**
     * Reads DataObjects from a .csv file with two columns containing double values and returns them as an Array
     * @param path the path to the .csv file which contains the objects
     * @return an Array of DataObjects build from the data in the .csv file
     * @throws IOException if the file does not exist
     */
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

    /**
     * Determines the highest difference between two neighbouring values in an DataOjects surroundings.
     * @param obj the classifier.DataObject which is examined.
     * @param range The distance which determines the size of the area which is examined. The examined area will have a
     *              size of (range+1)^2.
     * @return the highest difference between two neighbouring values in the surroundings of the classifier.DataObject.
     */
    private double stsHDiff(DataObject obj, int range) {

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

    /**
     * Determines the Difference between a marked value and the average value of its surroundings
     * @param obj the marked value which is examined.
     * @param range The distance which determines the size of the area which is examined. The examined area will have a
     *              size of (range+1)^2.
     * @return the highest difference between two neighbouring values in the surroundings of the classifier.DataObject.
     */
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


    /**
     * Classifies DataObjects from .csv files with two columns containing integer values marking the Objects position
     * (column, row) as either B or A objects and prints the results to standard out. The first two files contain the training data for this classifier and the
     * last two contain the Objects which are classified. The Classification is based on a random forest and takes the
     * average difference between the marked value of each object and its surroundings, as well as the highest
     * difference between two neighbouring values in eachh objects surroundings into consideration.
     * @param aTrainPath A path to a .csv file containing A-objects, used to train the random forest.
     * @param bTrainPath A path to a .csv file containing B-objects, used to train the random forest.
     * @param aTestPath A path to a .ccsv file containing A-objects to classify.
     * @param bTestPath A path to a .csv file containing B-objects to classify
     * @throws IOException If one of the paths does not exist.
     * @throws Exception If the data isn't well formed.
     */
    public void classify(String aTrainPath, String bTrainPath, String aTestPath, String bTestPath) throws IOException, Exception {
        // Lists for the training objects
        DataObject[] aObjects = readObjects(aTrainPath);
        DataObject[] bObjects = readObjects(bTrainPath);
        DataObject[] a1bjects = readObjects(aTestPath);
        DataObject[] b1bjects = readObjects(bTestPath);

        // the length of a0 and b0 combined will become the length of the training-data array
        // the training array holds arrays with the stsDifference and the averageHeightDifference for each object in A0 and B0
        int trainSetLen = aObjects.length + bObjects.length;
        double[][] values_train = new double[trainSetLen][2];
        for (int i = 0; i < aObjects.length; i++) {
            values_train[i][0] = avgHDiff(aObjects[i], avgHDiffRange);
            values_train[i][1] = stsHDiff(aObjects[i], stsHDiffRange);
        }
        for (int i = aObjects.length; i < trainSetLen; i++) {
            values_train[i][0] = avgHDiff(bObjects[i - aObjects.length], avgHDiffRange);
            values_train[i][1] = stsHDiff(bObjects[i - aObjects.length], stsHDiffRange);
        }

        // the test array holds arrays with the same characteristics for each object in A1 and B1 the data from a0 and
        int testSetLen  = a1bjects.length + b1bjects.length;
        double[][] values_test = new double[testSetLen][2];
        for (int i = 0; i < a1bjects.length; i++) {
            values_test[i][0] = avgHDiff(a1bjects[i], avgHDiffRange);
            values_test[i][1] = stsHDiff(a1bjects[i], stsHDiffRange);
        }
        for (int i = a1bjects.length; i < testSetLen; i++) {
            values_test[i][0] = avgHDiff(b1bjects[i - a1bjects.length], avgHDiffRange);
            values_test[i][1] = stsHDiff(b1bjects[i - a1bjects.length], stsHDiffRange);
        }

        // the classes for the classification
        List<String> targets = new ArrayList<>();
        targets.add("A");
        targets.add("B");

        // the attribute's names
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Average Height Difference"));
        attributes.add(new Attribute("Highest Difference between Neighbours"));


        // create the instances
        Instances dataRaw_train = new Instances("TrainInstance", attributes, 0);
        Instances dataRaw_test = new Instances("TestInstance", attributes, 0);


        // add attribute values (the arrays in values_train and values_test)
        for (double[] aValues_train : values_train) {
            dataRaw_train.add(new DenseInstance(1.0, aValues_train));
        }
        for (double[] aValues_test : values_test) {
            dataRaw_test.add(new DenseInstance(1.0, aValues_test));
        }


        // insert target attribute (the classes)
        dataRaw_train.insertAttributeAt(new Attribute("target", targets), dataRaw_train.numAttributes());
        dataRaw_test.insertAttributeAt(new Attribute("target", targets), dataRaw_test.numAttributes());


        // set target value to A for A-objects and to B for B-objects
        for (int i = 0 ; i < aObjects.length ; i++) {
            dataRaw_train.instance(i).setValue(dataRaw_train.numAttributes()-1, "A");
        }
        for (int i = aObjects.length; i < values_train.length; i++) {
            dataRaw_train.instance(i).setValue(dataRaw_train.numAttributes()-1, "B");
        }
        for (int i = 0 ; i < a1bjects.length ; i++) {
            dataRaw_test.instance(i).setValue(dataRaw_test.numAttributes()-1, "A");
        }
        for (int i = a1bjects.length; i < values_test.length; i++) {
            dataRaw_test.instance(i).setValue(dataRaw_test.numAttributes()-1, "B");
        }


        // set class index to target attribute (last attribute of the Instances)
        dataRaw_train.setClassIndex(dataRaw_train.numAttributes() - 1);
        dataRaw_test.setClassIndex(dataRaw_train.numAttributes() - 1);

        //randomize and normalize the objects
        dataRaw_train.randomize(new Debug.Random(1));
        dataRaw_test.randomize(new Debug.Random(1));
        Filter normalizer = new Normalize();
        normalizer.setInputFormat(dataRaw_train);

        Instances data_train = Filter.useFilter(dataRaw_train, normalizer);

        normalizer.setInputFormat(dataRaw_test);
        Instances data_test = Filter.useFilter(dataRaw_test, normalizer);

        int folds = 5;

        Evaluation eval = new Evaluation(data_test);

        // build random forest classifier
        RandomForest clf = new RandomForest();
        clf.buildClassifier(data_train);

        // classify the training data
        eval.crossValidateModel(clf, data_test, folds, new Random(1));

        // print the results
        System.out.println(eval.toMatrixString());
        System.out.println(eval.toSummaryString("\nResults\n========\n", false));
        System.out.println("Results for A");
        System.out.println("precision= " + eval.precision(0));
        System.out.println("recall= " + eval.recall(0));
        System.out.println("Results for B");
        System.out.println("precision= " + eval.precision(1));
        System.out.println("recall= " + eval.recall(1));
    }
}
