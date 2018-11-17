package rem.saccade;

import data.DataList;
import data.DataSeries;
import data.Scaling;
import filters.FilterDerivativeRem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * * *********************************************************
 * *                  SACCADE AYE MOVEMENTS                  *
 * ***********************************************************
 *
 * Humans and many animals do not look at a scene in fixed steadiness and
 * eye moves not with a smooth, steady movements, but instead, in a series of little rapid jumps (Saccades)
 * separated by pauses - brief periods of relative stability or slow phase movements (Fixations)
 * <p/>
 * !!! On average, a second humans make 2–3 saccades a second.!!!
 *
 * <p/>
 * So SACCADE is quick, simultaneous movement of both eyes between two phases of fixation
 * (from one fixation point to another)
 * The parameters commonly employed in the analysis of saccadic performance are
 * <b>amplitude</b>, the <b>maximum angular velocity</b>, <b>duration</b>, and <b>latency</b>.
 *
 * <p/>
 * SACCADE AMPLITUDES: ranges of 1 - 30°/s.
 * The amplitude of a saccade is the angular distance the eye travels during the movement.
 * Under natural conditions most saccades (83%) are smaller than 15°,
 * 99% of all eye movements smaller then 30° and are within 15 degrees of primary position.
 * In  the  EOG  technique  it is  difficult to measure  saccades  less then  1-2  degree
 * since  the  noise inherent.
 * <br>(See "Characteristics  of  saccades  and  vergence  in  two  kinds  of  sequential looking  tasks"
 * http://www.cis.rit.edu/pelz/lab/papers/malinov_epelboim_et_al_saccades_vergence_look-tap.pdf)
 *
 * <p/>
 * !! Saccades larger than about 20° is accompanied by a head movement !!
 *
 * <p/>
 * SACCADE PEAK VELOCITY: 400 - 600 degrees/s
 * Peak Velocity  is the highest velocity reached during the saccade.
 * For amplitudes up to 15 or 20°, the velocity of a saccade linearly depends on the amplitude -
 * <b>saccadic main sequence</b>. But rather different equations are given by different authors:
 * <ul>
 *     <li>DURATION[ms] = 21ms + 2.2 * AMPLITUDE[degrees]</li>
 *     <li>DURATION[ms] = 37ms + 2.7 * AMPLITUDE[degrees]</li>
 *     <li>DURATION[ms] = 38ms + 2 * AMPLITUDE[degrees]</li>
 * </ul>
 *
 * For amplitudes larger than 20°, the peak velocity starts to plateau
 * toward the asymptotic maximum (700° - 900°).
 * Actually for amplitudes more than 30 ° peak velocity is almost independent of the saccade size.
 * For instance, a 10° amplitude is associated with a velocity of 300°/s, and 30° is associated with 500-600°/s
 * Thus although big saccades ( 40-60°) exist and its max speed can reach 700°/s
 * the major (99% )of saccades are less then 30°.
 * Besides we measure the averaged values (during 20-40 ms) so the average velocity peak is at
 * least 20-30 percent less then the instantaneous velocity peak.
 * <br>
 * PEAK_VELOCITY_MAX  = 700 °/s
 * <br><br>
 * (See: <a href="https://www.liverpool.ac.uk/~pcknox/teaching/Eymovs/params.htm">The parameters of eye movement</a>
 * and <a href = "https://www.researchgate.net/post/What_is_the_average_duration_for_saccades_with_36_and_54_of_amplitude">
 *     average duration for saccades with 36º and 54º of amplitude</a>
 * Here is the link to download a book from Roger Carpenter
 * http://libgen.io/ads.php?md5=22AB8EC2F36192D85A1F5288187D0888
 *
 * <p/>
 * SACCADE DURATION:  20-200ms (Most of them 30-80ms).
 * Saccade duration depends on their amplitude.
 * For example:  2.5° - 37ms, 5° - 45ms,  10° - 55ms... (±10ms).
 * In language reading - 20–30 ms is typical.
 * As was said with EOG we can measure only saccades bigger then 1-2 degree
 * it is logical to assume that
 * <br>
 * SACCADE_DURATION_MIN = 37-40 ms
 *
 * <p/>
 * But we need take into account that  it was found that a big main saccade
 * is frequently followed by a second smaller corrective saccade or
 * slow drifting eye movement (Glissade) that brings the eye closer to the target
 * (See http://sysengr.engr.arizona.edu/publishedPapers/Glissades.pdf)
 *
 * !! The movement of the eyes usually undershoots the target and requires another
 * small saccade in the same direction to reach it.
 * Overshooting of the target is uncommon in normal subjects.!!!
 * (http://www.bem.fi/book/28/28.htm)
 *
 * This corrective saccades can occur almost immediately and
 * merging with the main saccade so the resultant eye movement can have duration till
 * 300-400ms.
 *
 * Another thing: "eye calibration" (max movement Right-Left) also take time about 400-500ms.
 * So summarizing all these cases we can accept:
 * <br>
 * SACCADE_DURATION_MAX_MS = 500 ms
 *
 *
 * <p/>
 * SACCADE LATENCY: 100-350 ms (normally 200ms)
 * Latency - this is the time taken from the appearance of a target
 * to the beginning of a saccade in response to that target.
 * The latency for most medium amplitude saccades (5°-10°) is usually around 200ms.
 * However, it can be as low as 100ms (see ), or as high as 350ms.
 * In respoce to to an unexpected stimulus or when the target moves suddenly or appear
 * there is a delay of about 200 ms before the eye begins to move to the new target position.
 *
 * <p/>
 * FIXATIONS: 50ms - 6s (normally 200-600ms)
 * Humans (and other animals with a fovea) typically alternate Saccades and visual Fixations.
 * (The notable exception is Smooth Pursuit -  eye movements that allow the eyes to closely follow a moving object)
 * So almost always before and after every saccade should be a short period of relative tranquility.
 * Fixations differ in their length but tend to be for about 200-600ms,
 * although much longer and shorter fixations can occur:
 * <ul>
 * <li>Normal fixations: 150-900ms</li>
 * <li>Overlong fixations: 5-6 seconds</li>
 * <li>Short fixations: 50-100ms</li>
 * </ul>
 * Thus:
 * <br>FIXATION_NORMAL = 600 - 1000 ms
 * <br>FIXATION_MAX = 5-6 sec
 * <br>FIXATION_MIN = 50 - 100 ms (can be seen in the case of subsequent corrective saccade)
 *
 * <p/>
 * <br>Small saccade: 3-5°,  duration 30-40ms, peak velocity 150-250 deg/sec
 * <br>Big saccade: 10-30°, duration 40-100ms, peak velocity 300-500 deg/sec
 * <br>Typical big saccade: 20°, duration 80ms, peak velocity 400 deg/sec
 * <br>VERY BIG saccade: 50-60°, duration 100ms, peak velocity 600-700 deg/sec
 * <br><br>
 * (see https://www.ncbi.nlm.nih.gov/pmc/articles/PMC1401908/?page=4,
 * https://en.wikipedia.org/wiki/Saccade#/media/File:Saccadic_main_sequence.svg)
 *<p/>
 *
 * *********************************************************
 * *                  DEVICE CALIBRATION                   *
 * *********************************************************
 * <p/>
 * MILLIVOLTS TO DEGREES CONVERSION FACTOR (SENSITIVITY): 10-30 µV/° (typically 10-20 µV/°)
 * The eye is a single dipole oriented from the retina to the cornea.
 * Such corneoretinal potentials are are in the range of: 0.4 - 1.0 mV (millivolts)
 * (Numbers can be slightly different from different autors:
 * http://www.bem.fi/book/28/28.htm and
 * https://books.google.es/books?id=uVGqDAAAQBAJ&pg=PA83&lpg=PA83&dq=corneoretinal+potentials++mV&source=bl&ots=9ceeZWKXdI&sig=Ukyg5oo0xaWSd7WgiSmlgbPUanI&hl=ru&sa=X&redir_esc=y#v=onepage&q=corneoretinal%20potentials%20%20mV&f=false)
 * <br>
 * The calibration of the signal may be achieved by having the patient look consecutively at two different
 * fixation points located a known angle apart and recording the concomitant EOGs.
 * Typical achievable accuracy is ±2° , and maximum rotation is ±70°.
 * However, linearity becomes progressively worse for angles beyond 30°.
 * <br>
 * Typical signal magnitudes range from 5-20 µV/°
 * (1° of eye movement evokes an average potential of 5–20 microvolts).
 * Ather autor gives range 10-30 µV/° (see https://www.mdpi.com/1424-8220/17/7/1505/pdf)
 *
 * <p/>
 * The calibration data obtained with our device:
 * eyes movement from max right to max left gives potential change:
 * 1) about +600 to -600 µV (coincides with http://noorqamariah.blogspot.com/2013/01/weekonealso.html)
 * (when electrodes located in the corners of the eyes)
 * 2) about +300 to -300 µV
 * (when electrodes located on the forehead above the eyes)
 * <br><br>
 * Maximum eye rotation is  ±70°.
 * For horizontal eye movements within the range of ±30 degrees,
 * the potential measured is assumed to be linear to the actual movement of the eye in the orbit.
 * But linearity becomes progressively worse for angles beyond 30°.
 * So max nonlinear ±70° eye rotation could be approximated by linear ±40(50)° rotation
 * And it gives us our signal magnitudes about  10 µV/° (microvolts per degree)
 * SENSITIVITY = 15 - 7 µV/°
 * SENSITIVITY_MAX = => 15µV/°
 * <p/>
 *
 * * *******************************************************
 * *                  DETECTOR ALGORITHM                   *
 * *********************************************************
 * <p/>
 * To detect saccades we use a variation of Velocity/Acceleration-Threshold Algorithm
 * that separates fixation and saccade points based on their point-to-point velocities/acceleration.
 * The velocity profiles of  eye movements show essentially two distributions of velocities:
 * low velocities for smooth pursuit(slower tracking) movements, glissades and fixations (i.e., < 50 deg/sec),
 * and high velocities (i.e., >100 deg/sec) for saccades
 * <p/>
 * Our algorithm:
 * <br>1) Instead of fixed threshold use adaptive threshold calculated from
 * the signal data itself during some period (noise)
 *
 * <br>2) Threshold is calculated on the base of energy, summarizing the squares of the values (instead of absolute values)
 * (Parseval's theorem:  the sum (or integral) of the square of a function is equal to the sum (or integral)
 * of the square of its Fourier transform. So from a physical point of view, more adequately work
 * with squares values (energy)
 *
 * <br>3) Instead of  instantaneous velocity and acceleration we use the averaged (for 40-60ms) ones.
 * That reduce random noise and emphasize saccades
 *
 */

public class SaccadeDetector implements DataSeries {
    private static final int SACCADE_DURATION_MIN_MS = 40; // [ms] (milliseconds)
    private static final int SACCADE_DURATION_MAX_MS = 800;
    private static final int PEAK_VELOCITY_MAX = 700; // (900) [degrees/s]

    private static final int SACCADES_DISTANCE_MAX_MS = 6000;
    private static final int SACCADES_DISTANCE_MIN_MS = 200;

    private static final int SENSITIVITY = 15; // [µV/degree]


    private SaccadeListener saccadeListener;
    private int zeroPointsNumber = 3; // to detect disconnections
    private int noiseAveragingIntervalMs = 400;

    private int startBlockedIntervalMs = 10 * 1000; // no detection

    private int thresholdNoiseRatio = 6; // Threshold to noise ratio

    private DataSeries eogDerivative;
    private int eogDerivativeIntervalMs = 40;

    private int saccadeMaxDigitalValue;
    private int saccadeDurationMinPoints;
    private int saccadeDurationMaxPoints;
    private int saccadeDistanceMaxPoints;
    private int saccadeDistanceMinPoints;
    private int startBlockedIntervalPoints;

    private EnergyNoiseDetector noiseDetector;

    // during disconnections signal is a constant and its derivative == 0
    private LinearNoiseDetector zeroDetector;

    /**
     * we calculate  threshold on the base of noise
     * not at the currentIndex (where we take the Value to compare with the threshold)
     * but "noiseLagPoints"  before
     * Value almost never grows at once/immediately (it took 1-4 points as a rule)
     * so we need a shift/gap between the  threshold and the currentIndex
     */
    private int noiseLagPoints;

    private Saccade currentSaccade;
    private SaccadeGroup currentSaccadeGroup;
    private int currentIndex = -1;

    private int disconnectionIndex = -1;
    private int nonSaccadicActivityIndex = -1;


    private boolean isThresholdsCachingEnabled;
    private DataList thresholdList = new DataList();

    private List<Saccade> saccadeList = new ArrayList<>();
    private HashMap<Integer, Integer> saccadeValues = new HashMap<>();

    public SaccadeDetector(DataSeries eogData) {
        this(eogData, false);
    }

    public SaccadeDetector(DataSeries eogData, boolean isThresholdsCachingEnabled) {
        double dataIntervalMs = eogData.getScaling().getSamplingInterval() * 1000;

        eogDerivative = new FilterDerivativeRem(eogData, eogDerivativeIntervalMs);

        int saccadeMaxPhysValue = PEAK_VELOCITY_MAX * SENSITIVITY * eogDerivativeIntervalMs / 1000;
        saccadeMaxDigitalValue = (int) (saccadeMaxPhysValue / eogData.getScaling().getDataGain());

        saccadeDurationMinPoints = (int) Math.round(SACCADE_DURATION_MIN_MS / dataIntervalMs);
        saccadeDurationMaxPoints = (int) Math.round(SACCADE_DURATION_MAX_MS / dataIntervalMs);
        saccadeDistanceMaxPoints = (int) Math.round(SACCADES_DISTANCE_MAX_MS / dataIntervalMs);
        saccadeDistanceMinPoints = (int) Math.round(SACCADES_DISTANCE_MIN_MS / dataIntervalMs);
        startBlockedIntervalPoints = (int) Math.round(startBlockedIntervalMs / dataIntervalMs);

        noiseLagPoints = 10;

        int noiseAveragingIntervalPoints = (int) Math.round(noiseAveragingIntervalMs / dataIntervalMs);
        if(noiseAveragingIntervalPoints < 1) {
            noiseAveragingIntervalPoints = 1;
        }
        noiseDetector = new EnergyNoiseDetector(eogDerivative, noiseAveragingIntervalPoints);
        zeroDetector = new LinearNoiseDetector(eogDerivative, zeroPointsNumber);
        this.isThresholdsCachingEnabled = isThresholdsCachingEnabled;
        saccadeListener = new NullSaccadeListener();


        int noiseDB1 = (int)(10 * Math.log10(6*6));
        int noiseDB2 = (int)(10 * Math.log10(8*8));
        int noiseDB3 = (int)(10 * Math.log10(9*9));
        System.out.println("db ratio "+ noiseDB1 + " "+noiseDB2+ "  "+noiseDB3);

        noiseDB1 = (int)(10 * Math.log10(10*10));
        noiseDB2 = (int)(10 * Math.log10(20*20));
        noiseDB3 = (int)(10 * Math.log10(30*30));
        System.out.println("db ratio "+ noiseDB1 + " "+noiseDB2+ "  "+noiseDB3);

    }

    public void setSaccadeListener(SaccadeListener saccadeListener) {
        if(saccadeListener != null) {
            this.saccadeListener = saccadeListener;
        }
        this.saccadeListener = new NullSaccadeListener();
    }

    public void removeSaccadeListener() {
        saccadeListener = new NullSaccadeListener();
    }


    /**
     * Noise/threshold is calculated with some delay (noiseLagPoints)
     * to avoid its growing when we are close to a saccade.
     * In the area: [noiseLagPoints before saccade | saccade | noiseLagPoints after saccade]
     * noise/threshold is fixed and do not change.
     */
    private int getNoiseOnNextData() {
        // wait "noiseLagPoints" before start noiseDetector
        if (currentIndex < noiseLagPoints) {
            return 0;
        }

        boolean isSaccadeClose = false;
        //  we skip noise on the points belonging to saccade and (+-)noiseLagPoints around  saccade
        if(currentSaccade != null ) {
            isSaccadeClose = true;
        } else if (currentSaccadeGroup != null) {
             if (currentIndex - currentSaccadeGroup.getEndIndex() < 2 * noiseLagPoints) {
                isSaccadeClose = true;
            }
        }

        if (isSaccadeClose || isDisconnected()) {
            return noiseDetector.skipNext();
        } else {
            return noiseDetector.calculateNext();
        }

    }


    public void detectOnNextData() {
        currentIndex++;

        // check if there are signal problems ()
        if(zeroDetector.calculateNext() == 0) {
            disconnectionIndex = currentIndex;
        }

        int noise = getNoiseOnNextData();
        int threshold = noise * thresholdNoiseRatio;

        if(noise == 0) {
            System.out.println(indexToDateTime(currentIndex) + " noise "+noise);
        }

        if(isThresholdsCachingEnabled) {
            thresholdList.add(threshold);
        }

        if (isSaccadeDetectionDisabled()) {
            return;
        }

        int dataValue = eogDerivative.get(currentIndex);
        int dataValueAbs = Math.abs(dataValue);
        if (currentSaccade == null) {
            if (dataValueAbs > threshold) { // saccade start
                currentSaccade = new Saccade(currentIndex, dataValue, noise);
            }
        } else {
            if (dataValueAbs > threshold) {
                try {
                    currentSaccade.addData(dataValue);
                } catch (IllegalArgumentException ex) {
                    noiseDetector.restoreLastSkippedValues(currentSaccade.getWidth());
                    currentSaccade = null;
                }
            } else { // saccade end
              //  System.out.println(isSaccadeOk(currentSaccade)+ "   "+indexToDateTime(currentSaccade.getStartIndex()) +" saccade  "+currentSaccade.getWidth()+ "    " +currentSaccade.getStartIndex());
                if (!isSaccadeOk(currentSaccade)) {
                    noiseDetector.restoreLastSkippedValues(currentSaccade.getWidth());
                    currentSaccade = null;
                } else {
                    if (currentSaccadeGroup == null || currentSaccadeGroup.isSaccadeOutOfGroupRange(currentSaccade)) {
                        currentSaccadeGroup = new SaccadeGroup(currentSaccade);
                        currentSaccade = null;
                    } else {
                        try {
                            currentSaccadeGroup.addSaccade(currentSaccade);
                            currentSaccade = null;
                        } catch (IllegalArgumentException ex) {
                            noiseDetector.restoreLastSkippedValues(currentSaccade.getWidth());
                            nonSaccadicActivityIndex = currentIndex;
                            currentSaccade = null;
                            currentSaccadeGroup = null;
                        }
                    }
                }

              //  System.out.println(" group "+ indexToDateTime(currentSaccadeGroup.getStartIndex()));
            }
        }
    }

    private String indexToDateTime(int index) {
        Scaling scaling = eogDerivative.getScaling();
        long time = (long)(scaling.getStart() + scaling.getSamplingInterval() * index * 1000);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        return formatter.format(time);
    }


    // connection problems and signal failure
    // (when dataVelocity = 0 during more then 2-3 points)
    private boolean isDisconnected() {
        if(currentIndex == disconnectionIndex) {
           return true;
        }
        return false;
    }

    private boolean isSaccadeDetectionDisabled() {
        if (currentIndex < startBlockedIntervalPoints) {
            return true;
        }
        // noise buffer data must be completely updated
        if((currentIndex - disconnectionIndex) < noiseDetector.getBufferSize()) {
            return true;
        }
        if((currentIndex - nonSaccadicActivityIndex) < noiseDetector.getBufferSize()){
            return true;
        }
        return false;
    }

    private void onSaccadesApproved(Saccade... saccades) {
        for (Saccade saccade : saccades) {
            for (int i = saccade.getStartIndex(); i <= saccade.getEndIndex(); i++) {
                saccadeList.add(saccade);
                saccadeValues.put(i, Math.abs(saccade.getPeakValue()));
            }
        }
        saccadeListener.onSaccadesDetected(saccades);
    }

    private void onSaccadeGroupApproved(SaccadeGroup saccadeGroup) {

    }

    private boolean isSaccadeOk(Saccade saccade) {

        if(saccade.getPeakValue() > saccadeMaxDigitalValue) {
            //return false;
        }
        if(saccade.getWidth() > saccadeDurationMaxPoints || saccade.getWidth() < saccadeDurationMinPoints) {
           // return false;
        }

        return true;
    }


    private void update() {
        for (int i = currentIndex; i < eogDerivative.size() - 1; i++) {
            detectOnNextData();
        }
    }


    /**
     * @throws IllegalStateException if isThresholdsCachingEnabled = false
     */
    public DataSeries getThresholds() throws IllegalStateException {
        if(!isThresholdsCachingEnabled) {
            String errMsg = "Thresholds are not available. isThresholdsCachingEnabled = "+isThresholdsCachingEnabled;
            throw new IllegalStateException(errMsg);
        }
        return new DataSeries() {
            @Override
            public int size() {
                update();
                return thresholdList.size();
            }

            @Override
            public int get(int index) {
                return thresholdList.get(index);
            }

            @Override
            public Scaling getScaling() {
                return eogDerivative.getScaling();
            }
        };
    }

    @Override
    public int size() {
        update();
        return eogDerivative.size();
    }

    @Override
    public int get(int index) {
        Integer saccadeValue = saccadeValues.get(index);
        if(saccadeValue != null ) {
            return saccadeValue;
        }
        return 0;
    }

    @Override
    public Scaling getScaling() {
        return eogDerivative.getScaling();
    }


    /**
     * Saccades always appear in group.
     * Single saccades are not taken into account
     */
    class SaccadeGroup {
        private ArrayList<Saccade> saccadeList = new ArrayList<>(10);
        private int saccadesInGroupMin = 3;
        private boolean isApproved = false;

        public SaccadeGroup(Saccade saccade) {
            saccadeList.add(saccade);
        }

        public int saccadesCount() {
            return saccadeList.size();
        }

        public int getStartIndex() {
            return saccadeList.get(0).getStartIndex();
        }

        public int getEndIndex() {
            return saccadeList.get(saccadeList.size() - 1).getEndIndex();
        }

        public int getWidth() {
            return getEndIndex() - getStartIndex() + 1;
        }

        public void addSaccade(Saccade saccade) throws IllegalArgumentException {
            saccadeList.add(saccade);
            if(saccadesCount() == saccadesInGroupMin) {
                approveGroup();
            } else {
                if(isApproved) {
                    onSaccadesApproved(saccade);
                }
            }
        }

        private void approveGroup() {
            isApproved = true;
            onSaccadeGroupApproved(this);
            onSaccadesApproved(saccadeList.toArray(new Saccade[saccadeList.size()]));
        }

        public boolean isSaccadeOutOfGroupRange(Saccade saccade) {
            if(saccade.getStartIndex() - getEndIndex() > saccadeDistanceMaxPoints) {
                return true;
            }
            return false;
        }
    }

    class NullSaccadeListener implements SaccadeListener {
        @Override
        public void onSaccadesDetected(Saccade[] saccades) {
            // do nothing!
        }
    }


}
