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
public class SaccadeGroupDetector {
    private static final int SACCADES_INTERVAL_MAX = 6000;
    private static final int SACCADES_INTERVAL_MIN = 200;

    private static final int SACCADES_HALF_INTERVAL_MIN = SACCADES_INTERVAL_MIN / 2;
    private static final int SACCADES_INTERVAL_MAX_AND_HALF = SACCADES_INTERVAL_MAX * 3 / 2;

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
        dataIntervalMs = Math.round(eogData.getScaling().getSamplingInterval() * 1000);
        dataStartMs = Math.round(eogData.getScaling().getStart());

        sacadeDetector.setSaccadeListener(new SaccadeListener() {
            @Override
            public void onSaccadeDetected(Saccade saccade) {
                addSacadeToGroup(saccade);
            }
        });
    }

    public void setSaccadeListener(SaccadeListener saccadeListener) {
        if (saccadeListener != null) {
            this.saccadeListener = saccadeListener;
        }
    }

    public void removeSaccadeListener() {
        saccadeListener = new NullSaccadeListener();
    }

    public int groupsCount() {
        return groupInfoList.size();
    }

    /**
     * get group start time in milliseconds
     */
    public long getGroupStartTime(int groupNumber) {
        return groupInfoList.get(groupNumber).getStartTime();
    }

    /**
     * get group end time in milliseconds
     */
    public long getGroupEndTime(int groupNumber) {
        return groupInfoList.get(groupNumber).getEndTime();
    }


    public int saccadesCountInGroup(int groupNumber) {
        return groupInfoList.get(groupNumber).saccadesCount();
    }


    private void addSacadeToGroup(Saccade saccade) {
        // debug version without selection wrong groups
       /* if (currentGroup.size() == 0 || saccade.getStartTime() - currentGroup.get(currentGroup.size() - 1).getEndTime() > SACCADES_INTERVAL_MAX) {
            currentGroup.clear();
            currentGroup.add(saccade);
        } else {
            currentGroup.add(saccade);
            if(currentGroup.size() == saccadesInGroupMin) {
                approveGroup();
            }
            if(currentGroup.size() > saccadesInGroupMin){
                addSaccadeToMainList(saccade);
            }
        }*/

        if (currentGroup.size() == 0 || saccade.getStartTime() - currentGroup.get(currentGroup.size() - 1).getEndTime() > SACCADES_INTERVAL_MAX) {
            currentGroup.clear();
            currentGroup.add(saccade);
            isGroupApproved = false;
        } else {
            currentGroup.add(saccade);
            if (!checkIfLast4SaccadesOk()) {
                currentGroup.clear();
                sacadeDetector.disableDetection(sacadeDetector.getNoiseAveragingInterval());
                isGroupApproved = false;
            }
            if (!isGroupApproved) {
                if (currentGroup.size() >= saccadesInGroupMin) {
                    // supposed that if group  size >= 4 and every on adding every saccade
                    // checkIfLast3SaccadesOk() and checkIfLast4SaccadesOk() were true
                    // then group can be approved by default
                    if (currentGroup.size() < 4) {
                        if (checkIfGroupCanBeApproved()) {
                            approveGroup();
                        }
                    } else {
                        approveGroup();
                    }
                }
            }

            if (isGroupApproved) {
                addSaccadeToMainList(saccade);
            }
        }
    }

    /**
     * in the case of 3 close saccades (all saccade intervals < SACCADES_INTERVAL_MIN)
     * we will not approve the group and wait the 4-th saccade
     */
    private boolean checkIfGroupCanBeApproved() {
        return checkIfLastNSaccadesHasMinDistance(Math.min(currentGroup.size(), 4));
    }

    private boolean checkIfLast3SaccadesOk() {
        final int count = 3;

        if (currentGroup.size() < count) {
            return true;
        }

        int startIndex = currentGroup.size() - count;

        // check if all saccades are too close
        boolean isTooClose = true;
        for (int i = startIndex; i < startIndex + count - 1; i++) {
            if (currentGroup.get(i + 1).getStartTime() - currentGroup.get(i).getEndTime() > SACCADES_HALF_INTERVAL_MIN) {
                isTooClose = false;
                break;
            }
        }

        if (isTooClose) {
            // check sine "pattern" (plus-minus-plus or minus-plus-minus) [impossible for close saccades]
            // Groups of close saccades almost always have the same direction.
            // "The movement of the eyes usually undershoots the target and requires another
            // small saccade in the same direction to reach it.
            // Overshooting of the target is uncommon in normal subjects."

            int value1 = currentGroup.get(startIndex).getValue();
            int value2 = currentGroup.get(startIndex + 1).getValue();
            int value3 = currentGroup.get(startIndex + 2).getValue();


            if (!isEqualSign(value1, value2) && isEqualSign(value1, value3)) { // sine pattern
                return false;
            }
        }

        // check if the saccades are too far from each other
        if (currentGroup.get(startIndex + count - 1).getStartTime() - currentGroup.get(startIndex).getStartTime() < SACCADES_INTERVAL_MAX_AND_HALF) {
            return true;
        }

        return true;
    }

    private boolean checkIfLast4SaccadesOk() {
        if(currentGroup.size() < 3) {
            return true;
        }

        if(!checkIfLast3SaccadesOk()) {
            return false;
        }

        if(currentGroup.size() > 3) {
            return checkIfLastNSaccadesHasMinDistance(4);
        }

        return true;
    }

    private boolean checkIfLastNSaccadesHasMinDistance(int n) {
        int startIndex = currentGroup.size() - n;

        // check if at least one inter saccade interval >= SACCADES_INTERVAL_MIN
        for (int i = startIndex; i < startIndex + n - 1; i++) {
            if (currentGroup.get(i + 1).getStartTime() - currentGroup.get(i).getEndTime() >= SACCADES_INTERVAL_MIN) {
                return true;
            }
        }
        return false;
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


    private boolean isEqualSign(int a, int b) {
        if ((a >= 0) && (b >= 0)) {
            return true;
        }

        if ((a <= 0) && (b <= 0)) {
            return true;
        }

        return false;
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

        public int saccadesCount() {
            return succadesCount;
        }

        public long getStartTime() {
            return saccadeList.get(firstSaccadeIndex).getStartTime();
        }

        public long getEndTime() {
            return saccadeList.get(firstSaccadeIndex + succadesCount - 1).getEndTime();
        }
    }

}
