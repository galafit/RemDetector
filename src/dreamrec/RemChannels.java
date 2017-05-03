package dreamrec;

public class RemChannels {
    private int accelerometerX = -1;
    private int accelerometerY = -1;
    private int accelerometerZ = -1;
    private int eog1 = -1;
    private int eog2 = -1;
    private boolean[] remActiveChannels;


    private static final String EOG = "EOG";
    private static final String EOG_1 = "EOG1";
    private static final String EOG_2 = "EOG2";
    private static final String ACCELEROMETER_X = "Accelerometer X";
    private static final String ACCELEROMETER_Y = "Accelerometer Y";
    private static final String ACCELEROMETER_Z = "Accelerometer Z";
    private static final String ACCELEROMETER_1 = "Accelerometer 1";
    private static final String ACCELEROMETER_2 = "Accelerometer 2";
    private static final String ACCELEROMETER_3 = "Accelerometer 3";


    public RemChannels(String[] signalsLabels) throws ApplicationException{
        remActiveChannels = new boolean[signalsLabels.length];
        for (int i = 0; i < signalsLabels.length; i++) {
            if (signalsLabels[i].equals(EOG)) {
                eog1 = i;
                remActiveChannels[i] = true;
            }
            if (signalsLabels[i].equals(EOG_1)) {
                if(eog1 < 0) {
                    eog1 = i;
                }
                else {
                    eog2 = i;
                }
                remActiveChannels[i] = true;
            }
            if (signalsLabels[i].equals(EOG_2)) {
                if(eog1 < 0) {
                    eog1 = i;
                }
                else {
                    eog2 = i;
                }
                remActiveChannels[i] = true;
            }
            if (signalsLabels[i].equals(ACCELEROMETER_X) || signalsLabels[i].equals(ACCELEROMETER_1)) {
                accelerometerX = i;
                remActiveChannels[i] = true;
            }
            if (signalsLabels[i].equals(ACCELEROMETER_Y) || signalsLabels[i].equals(ACCELEROMETER_2)) {
                accelerometerY = i;
                remActiveChannels[i] = true;
            }
            if (signalsLabels[i].equals(ACCELEROMETER_Z) || signalsLabels[i].equals(ACCELEROMETER_3)) {
                accelerometerZ = i;
                remActiveChannels[i] = true;
            }
        }


        if(accelerometerX < 0) {
            throw new ApplicationException("Accelerometer 1, Accelerometer 2 and Accelerometer 3 channels must be specified!");
        }
        if(accelerometerY < 0) {
            throw new ApplicationException("Accelerometer 1, Accelerometer 2 and Accelerometer 3 channels must be specified!");

        }
        if(accelerometerZ < 0) {
            throw new ApplicationException("Accelerometer 1, Accelerometer 2 and Accelerometer 3 channels must be specified!");

        }
        if(eog1 < 0) {
            throw new ApplicationException("EOG channel must be specified!");
        }

    }



    public int getAccelerometerX() {
        return accelerometerX;
    }

    public int getAccelerometerY() {
        return accelerometerY;
    }

    public int getAccelerometerZ() {
        return accelerometerZ;
    }

    public int getEog1() {
        return eog1;
    }

    public int getEog2() {
        return eog2;
    }

    public boolean[] getRemActiveChannels() {
        return remActiveChannels;
    }

}
