import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Classifier  {


    private List<ArrayList<Double>> data = new ArrayList<>();

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

    public List<ArrayList<Double>> getData(){
        return this.data;
    }

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
                    if (x_index + j < 0 || x_index + j >= 4943 || y_index + z < 0 || y_index + z >= 3000 )
                    {rangelist.add(0.0);}
                    else {rangelist.add(this.data.get(x_index + j).get(y_index + z));}
                }
            }
            //  rangelist.remove(this.data.get(x_index).get(y_index));
            ret.add(rangelist);
            rangelist = new ArrayList<>();
        }
        return ret;
    }


    public ArrayList<ArrayList<Double>> concatenateAB(ArrayList<ArrayList<Double>>A, ArrayList<ArrayList<Double>> B) {
        for (ArrayList<Double> list :  B) {
            A.add(list);
        }
        return A;
    }

}
