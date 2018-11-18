package rem.saccade;

import data.DataSeries;
import data.Scaling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Saccades always appear in group.
 * Single saccades are not taken into account
 */
public class SaccadeGroupDetector implements SaccadeListener {
    private static final int SACCADES_DISTANCE_MAX_MS = 6000;
    private static final int SACCADES_DISTANCE_MIN_MS = 200;

    private DataSeries eogData;
    private long dataIntervalMs;
    private long dataStartMs;

    private SacadeDetector sacadeDetector;

    private List<Saccade> saccadeList = new ArrayList<>();
    private HashMap<Integer, Integer> saccadeValues = new HashMap<>();

    private List<Saccade> saccadeGroup = new ArrayList<>();
    private List<SaccadeGroup> groupList = new ArrayList<>();
    private SaccadeGroup groupInfo;
    private int saccadesInGroupMin = 3;
    private boolean isGroupApproved = false;
    private SaccadeListener saccadeListener = new NullSaccadeListener();


    public SaccadeGroupDetector(DataSeries eogData) {
        this(eogData, false);
    }

    public SaccadeGroupDetector(DataSeries eogData, boolean isThresholdsCachingEnabled) {
        this.eogData = eogData;
        sacadeDetector = new SacadeDetector(eogData, isThresholdsCachingEnabled);
        sacadeDetector.setSaccadeListener(this);

        dataIntervalMs = Math.round(eogData.getScaling().getSamplingInterval() * 1000);
        dataStartMs = Math.round(eogData.getScaling().getStart());

        int noiseDB1 = (int) (10 * Math.log10(6 * 6));
        int noiseDB2 = (int) (10 * Math.log10(8 * 8));
        int noiseDB3 = (int) (10 * Math.log10(9 * 9));
        System.out.println("db ratio " + noiseDB1 + " " + noiseDB2 + "  " + noiseDB3);

        noiseDB1 = (int) (10 * Math.log10(10 * 10));
        noiseDB2 = (int) (10 * Math.log10(20 * 20));
        noiseDB3 = (int) (10 * Math.log10(30 * 30));
        System.out.println("db ratio " + noiseDB1 + " " + noiseDB2 + "  " + noiseDB3);

    }

    public void setSaccadeListener(SaccadeListener saccadeListener) {
        if (saccadeListener != null) {
            this.saccadeListener = saccadeListener;
        }
    }

    public void removeSaccadeListener() {
        saccadeListener = new NullSaccadeListener();
    }

    @Override
    public void onSaccadeDetected(Saccade saccade) {
        addSacadeToGroup(saccade);
    }

    private void addSacadeToGroup(Saccade saccade) {
        if (saccadeGroup.size() == 0 || saccade.getStartTime() - saccadeGroup.get(saccadeGroup.size() - 1).getEndTime() > SACCADES_DISTANCE_MAX_MS) {
            saccadeGroup.clear();
            saccadeGroup.add(saccade);
        } else {
            saccadeGroup.add(saccade);
            if (saccadeGroup.size() == saccadesInGroupMin) {
                approveGroup();
            } else if (isGroupApproved) {
                addSaccadeToMainList(saccade);
            }
        }
    }

    private void approveGroup() {
        isGroupApproved = true;
        for (Saccade saccade : saccadeGroup) {
            addSaccadeToMainList(saccade);
        }
    }

    private void notifyListener(Saccade saccade) {
        saccadeListener.onSaccadeDetected(saccade);
    }


    private void addSaccadeToMainList(Saccade saccade) {
        saccadeList.add(saccade);
        int startIndex = (int) ((saccade.getStartTime() - dataStartMs) / dataIntervalMs);
        int pointsNumber = (int) (saccade.getDuration() / dataIntervalMs);
        for (int i = 0; i < pointsNumber; i++) {
            saccadeValues.put(startIndex + i, saccade.getValue());
        }
        notifyListener(saccade);
    }

    public void update() {
        sacadeDetector.detect();
    }

    /**
     * @throws IllegalStateException if isThresholdsCachingEnabled = false
     */
    public DataSeries getThresholds() throws IllegalStateException {
        DataSeries thresholds = sacadeDetector.getThresholds();
        return new DataSeries() {
            @Override
            public int size() {
                update();
                return thresholds.size();
            }

            @Override
            public int get(int index) {
                return thresholds.get(index);
            }

            @Override
            public Scaling getScaling() {
                return thresholds.getScaling();
            }
        };
    }


    public DataSeries getSaccades() {
        return new DataSeries() {
            @Override
            public int size() {
                update();
                return eogData.size();
            }

            @Override
            public int get(int index) {
                Integer saccadeValue = saccadeValues.get(index);
                if (saccadeValue != null) {
                    return saccadeValue;
                }
                return 0;
            }

            @Override
            public Scaling getScaling() {
                return eogData.getScaling();
            }
        };
    }

    class SaccadeGroup {
        int firsSaccadeIndex;
        int succadesCount;

        public long getStartTime() {
            return saccadeList.get(firsSaccadeIndex).getStartTime();
        }

        public long getEndTime() {
            return saccadeList.get(firsSaccadeIndex + succadesCount).getEndTime();
        }
    }

}
