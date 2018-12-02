package classifier;

/**
 * A DataObject represents one cell of a 2 dimensional Array used to mark the Objects within the Classifiers data-array
 * @author Christoph Thomas, Luca Thurm
 */
public class DataObject {

    /**
     * The Row in which this DataObject is located
     */
    private int row;

    /**
     * The Column in which this data object is located
     */
    private int col;

    /**
     * Getter for this DataObjects row
     * @return this DataObjects row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Getter for this DataObjects column
     * @return this DataObjects column
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Creates a new DataObject-instance wich is located in the row row and the column col
     * @param row the row, in which this DataObject is located
     * @param col the column in which this DataObject is located
     */
    public DataObject(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
