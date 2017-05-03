package functions;

import data.DataSeries;

/**
 * Created by gala on 03/05/17.
 */
public class Minus extends Function {
    protected DataSeries inputData1;

    public Minus(DataSeries inputData, DataSeries inputData1) {
        super(inputData);
        this.inputData1 = inputData1;
    }

    @Override
    public int get(int index) {
        return inputData.get(index) - inputData1.get(index);
    }
}
