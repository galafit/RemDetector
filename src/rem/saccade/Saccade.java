package rem.saccade;

public class Saccade {
    private int startIndex;
    private int numberOfPoints;
    private int maxValue;
    private int sumValue;


    public Saccade(int index, int value) {
        startIndex = index;
        numberOfPoints++;
        sumValue = value;
        maxValue = value;
    }

    public void addPoint(int value) {
        if(Math.abs(maxValue) < Math.abs(value)) {
            maxValue = value;
        }
        sumValue += value;
        numberOfPoints++;
    }

    public int getWidth() {
        return numberOfPoints;
    }

    public int getAverageValue() {
        return sumValue / getWidth();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return startIndex + numberOfPoints;
    }
}