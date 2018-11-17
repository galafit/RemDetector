package rem.saccade;


class Saccade {
    private int startIndex;
    private int peakIndex;

    private int numberOfPoints;
    private int peakValue;
    private int energy;
    private int noiseLevel;


    public Saccade(int value, int index, int noiseLevel) {
        startIndex = index;
        peakValue = value;
        this.noiseLevel = noiseLevel;
        energy += value * value;
        numberOfPoints++;
    }



    public void addData(int value) throws IllegalArgumentException{
        if(!isEqualSign(peakValue, value)) {
            String msg = "All saccade values must have the same sign";
           // System.out.println(msg);
           // throw new IllegalArgumentException(msg);
        }

        if(Math.abs(peakValue) < Math.abs(value)) {
            peakValue = value;
        }
        energy += value * value;
        numberOfPoints++;
    }

    public int getWidth() {
        return numberOfPoints;
    }

    public int getPeakValue() {
        return peakValue;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return startIndex + numberOfPoints - 1;
    }

    public int getRatioToNoise() {
        return (int)Math.sqrt(energy / (noiseLevel * noiseLevel));
    }

    private boolean isEqualSign(int a, int b) {
        if ((a >= 0) && (b >= 0)) {
            return true;
        }

        if ((a <= 0) && (b <= 0)) {
            return true;
        }

        return false;
    }

}