package rem.saccade;

import data.DataSeries;
import data.Scaling;

/**
 * /**
 * Calculate noise as an average energy of inputData for given period.
 * We summarizing squared values (instead of absolute values) of inputData
 * and then finally get the square root of the result sum
 * <p>
 * (Parseval's theorem:  the sum (or integral) of the square of a function is equal
 * to the sum (or integral) of the square of its Fourier transform)
 * So from a physical point of view, more adequately work with squares values (energy)
 */

public class EnergyNoiseDetector {
    private DataSeries inputData;
    private int counter = 0;
    private CircularIntFifoBuffer fifoBuffer;
    private int bufferSize;
    private long sum;

    public EnergyNoiseDetector(DataSeries inputData, int bufferSize) {
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

    public int getBufferSize() {
        return bufferSize;
    }

    public int calculateNext() {
        int value = Math.abs(inputData.get(counter++));
        fifoBuffer.add(value);
        sum += value * value;

        int result = (int) Math.sqrt(sum / fifoBuffer.size()); //(int) (Math.pow(sum / fifoBuffer.size(), 1.0 / 4));

        if(fifoBuffer.size() == bufferSize) {
            sum -= fifoBuffer.get() * fifoBuffer.get();
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
            return (int) Math.sqrt(sum / fifoBuffer.size());
        }
        return 0;
    }


    public Scaling getScaling() {
        return inputData.getScaling();
    }
}
