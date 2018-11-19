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

    private List<GroupInfo> groupInfoList = new ArrayList<>();
    private List<Saccade> saccadeList = new ArrayList<>();
    private HashMap<Integer, Integer> saccadeValues = new HashMap<>();

    private List<Saccade> currentGroup = new ArrayList<>();
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
        if (currentGroup.size() == 0 || saccade.getStartTime() - currentGroup.get(currentGroup.size() - 1).getEndTime() > SACCADES_DISTANCE_MAX_MS) {
            currentGroup.clear();
            currentGroup.add(saccade);
            isGroupApproved = false;
        } else {
            currentGroup.add(saccade);
            if (currentGroup.size() == saccadesInGroupMin) {
                approveGroup();
            } else if (isGroupApproved) {
                addSaccadeToMainList(saccade);
            }
        }
    }

    private void approveGroup() {
        isGroupApproved = true;
        groupInfoList.add(new GroupInfo(saccadeList.size()));
        for (Saccade saccade : currentGroup) {
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
        groupInfoList.get(groupInfoList.size() - 1).addSacade();
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

    class GroupInfo {
        int firstSaccadeIndex;
        int succadesCount;

        public GroupInfo(int firstSaccadeIndex) {
            this.firstSaccadeIndex = firstSaccadeIndex;
        }

        public void addSacade() {
            succadesCount++;
        }

        public long getStartTime() {
            return saccadeList.get(firstSaccadeIndex).getStartTime();
        }

        public long getEndTime() {
            return saccadeList.get(firstSaccadeIndex + succadesCount).getEndTime();
        }
    }

}
