import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Classifier {


    private List<List<Double>> data = new ArrayList<>();

    public Classifier(String dataPath) {
        try {
            // csv file with data:
            File file = new File(dataPath);
            BufferedReader bf = new BufferedReader(new FileReader(file));

            // read the data from the csv file:
            String line;
            String[] splitStrings;
            while ((line = bf.readLine()) != null) {
                splitStrings = line.split(",");
                ArrayList<Double> row = new ArrayList<>();
                for (int i = 0; i < splitStrings.length; i++) {
                    row.add(Double.valueOf(splitStrings[i]));
                }
                this.data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Character, List<DataObject>> classify(String objectsPath, int fieldSize, double avgDiffThreshold) throws IOException{
        //csv file with data:
        File file = new File(objectsPath);
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

        // as is going contain the objects classified as A, bs is going to ccntains the objects classified as B
        List<DataObject> as = new ArrayList<>();
        List<DataObject> bs = new ArrayList<>();

        double avg = 0;

        // if there's a "cliff" in the area around the data object, it is classified as B, if there's none, as A
        for (DataObject obj : objects) {
            double diff = highestStsDiff(obj, fieldSize);
            avg += diff;
            if (diff > avgDiffThreshold) {
                bs.add(obj);
            } else {
                as.add(obj);
            }
        }

        System.out.println(avg / objects.size());

        // the map for the results of this classification. The key 'A' accesses the objects classified as A
        // and the key 'B' the B-objects
        Map<Character, List<DataObject>> results = new HashMap<>();
        results.put('A', as);
        results.put('B', bs);

        return results;
    }

    private double highestStsDiff(DataObject obj, int fieldSize){
        //TODO: find a solution for out of bounds problem
        if (obj.getCol() - fieldSize - 1 < 0 || obj.getRow() - fieldSize - 1 < 0) return 1.9;
        if (obj.getCol() + fieldSize + 1 >= data.get(0).size() || obj.getRow() + fieldSize + 1 >= data.size()) return 1.9;

        // List for all the cells in the area around obj
        List<DataObject> objects = new ArrayList<>();

        // this variable will hold the highest difference between two neighbouring values in this sub-table
        double highestDiff = 0;

        for (int row = -fieldSize; row <= fieldSize; row++) {
            for (int col = -fieldSize; col <= fieldSize; col++) {
                objects.add(new DataObject(obj.getRow() + row, obj.getCol() + col));
            }
        }

        // check for each cell if the neighouring cells have a much lower or higher value
        for (DataObject o : objects) {
            double north = Math.abs(
                    data.get(o.getRow()).get(o.getCol())
                            - data.get(o.getRow() - 1).get(o.getCol())
            );
            double south = Math.abs(
                    data.get(o.getRow()).get(o.getCol())
                            - data.get(o.getRow() + 1).get(o.getCol())
            );
            double west = Math.abs(
                    data.get(o.getRow()).get(o.getCol())
                            - data.get(o.getRow()).get(o.getCol() - 1)
            );
            double east = Math.abs(
                    data.get(o.getRow()).get(o.getCol())
                            - data.get(o.getRow()).get(o.getCol() + 1)
            );

            // set highestDiff to that difference value if it is higher than the value currently assigned to highestDiff
            if (east > highestDiff) {
                highestDiff = east;
            }
            if (west > highestDiff) {
                highestDiff = west;
            }
            if (north > highestDiff) {
                highestDiff = north;
            }
            if (south > highestDiff) {
                highestDiff = south;
            }
        }
        // the return value is the highest difference between two neighbouring cells in the area
        return highestDiff;
    }

    /*
    //TODO: get rid of this
    public void printDataObject(DataObject obj, int fieldSize) {

        if (!(obj.getCol() - fieldSize < 0 || obj.getRow() - fieldSize < 0)) {

            double diff = 0;
            for (int row = -fieldSize; row <= fieldSize; row++) {
                for (int col = -fieldSize; col <= fieldSize; col++) {
                    System.out.print(data.get(obj.getRow() + row).get(obj.getCol() + col).intValue());
                    System.out.print(" ");
                    if (Math.abs(data.get(obj.getRow()).get(obj.getCol()) - data.get(obj.getRow() + row).get(obj.getCol() + col)) > diff) {
                        diff = Math.abs(data.get(obj.getRow()).get(obj.getCol()) - data.get(obj.getRow() + row).get(obj.getCol() + col));
                    }
                }
                System.out.print("\n");
            }
            System.out.println("DataPoint at " + obj.getRow() + ", " + obj.getCol());
            System.out.println("Value: " + data.get(obj.getRow()).get(obj.getCol()));
            System.out.println("highest Diff: " + diff);
            System.out.println("Highest sts diff:" + highestStsDiff(obj, fieldSize) + "\n");
        }
    }

    //NOT ACCURATE ENOUGH
    private double avgDiff(DataObject obj, int fieldSize) {
        //TODO: handle out of bounds exceptions

        if (obj.getCol() - fieldSize < 0 || obj.getRow() - fieldSize < 0) return 1.9;

        double diff = 0;
        int count = 0;
        for (int row = -fieldSize; row <= fieldSize; row++) {
            for (int col = -fieldSize; col <= fieldSize; col++) {
                count++;
                diff += Math.abs(data.get(obj.getRow()).get(obj.getCol()) - data.get(obj.getRow() + row).get(obj.getCol() + col));
            }
        }
        return diff / count;
    }

    */
}
