package rem.saccade;

import data.DataSeries;
import data.Scaling;

/**
 * Created by galafit on 11/11/18.
 */
public class LinearNoiseDetector {
    private DataSeries inputData;
    private int counter = 0;
    private CircularIntFifoBuffer fifoBuffer;
    private int bufferSize;
    private long sum;

    public LinearNoiseDetector(DataSeries inputData, int bufferSize) {
        this.inputData = inputData;
        this.bufferSize = bufferSize;
        fifoBuffer = new CircularIntFifoBuffer(bufferSize);
    }

    public boolean isAvailable() {
        if (counter < inputData.size() - 1) {
            return true;
        }
        return false;
    }


    public int calculateNext() {
        int value = Math.abs(inputData.get(counter++));
        fifoBuffer.add(value);

        sum += value;

        int result = (int) (sum / fifoBuffer.size());

        if(fifoBuffer.size() == bufferSize) {
            sum -= fifoBuffer.get();
        }
        return result;
    }

    /**
     * Skip input data if we dont want calculate it as a noise
     * (to skip artifacts and so on)
     */
    public int skipNext() {
        counter++;
        if(fifoBuffer.size() > 0) {
            return (int) (sum / fifoBuffer.size());
        }
        return 0;
    }


    public Scaling getScaling() {
        return inputData.getScaling();
    }
}
