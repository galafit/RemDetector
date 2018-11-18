package rem.saccade;

import data.DataList;
import data.DataSeries;
import data.Scaling;
import filters.FilterDerivativeRem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
 * SACCADE AMPLITUDES: ranges of 1 - 100°/s.
 * The amplitude of a saccade is the angular distance the eye travels during the movement.
 * Under natural conditions most saccades (83%) are smaller than 15°,
 * 99% of all eye movements smaller then 30° and are within 15 degrees of primary position.
 * In  the  EOG  technique  it is  difficult to measure  saccades  less then  1-2  degree
 * since  the  noise inherent.
 *
 * <br>See: <a href="http://www.cis.rit.edu/pelz/lab/papers/malinov_epelboim_et_al_saccades_vergence_look-tap.pdf">
 *     Characteristics of saccades</a>
 *
 * <p/>
 * !! Saccades larger than about 20° is accompanied by a head movement !!
 *
 *
 * <p/>
 * SACCADE PEAK VELOCITY: 50 - 1000 degrees/s
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
 *
 * (See: <a href="https://www.liverpool.ac.uk/~pcknox/teaching/Eymovs/params.htm">The parameters of eye movement</a>
 * and <a href = "https://www.researchgate.net/post/What_is_the_average_duration_for_saccades_with_36_and_54_of_amplitude">
 *     average duration for saccades with 36º and 54º of amplitude</a>
 * <br>
 * PEAK_VELOCITY_MAX = 1000 °/s
 *
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
 *
 * Simplest methods to detect saccade is a Velocity-Threshold Algorithm.
 * The velocity profiles of  eye movements show essentially two distributions of velocities:
 * low velocities for smooth pursuit(slower tracking) movements, glissades and fixations (i.e., < 50 deg/sec),
 * and high velocities (i.e., >100 deg/sec) for saccades.
 *
 * Matlab program marks saccades by a velocity threshold of 75°/s.
 * Sometimes acceleration is used instead of velocity - Acceleration-Threshold Algorithm or its combination.
 * The EyeLink software in its cognitive configuration uses velocity, acceleration, and motion
 * thresholds of 30º/sec, 8,000º/sec2, and 0.15º, respectively
 *
 * <p/>
 * We use a variant of improved Velocity-Threshold Algorithm with ADAPTIVE threshold that is calculated from
 * the signal data itself during some period (noise). Based on these works:
 * <br>1) <a href="https://link.springer.com/content/pdf/10.3758/BRM.42.1.188.pdf">
 *     An adaptive algorithm for fixation, saccade, and glissade detection in eyetracking data</a>
 * <br>2) <a href="https://link.springer.com/content/pdf/10.3758/BRM.42.3.701.pdf">
 *     An improved algorithm for automatic detection of saccades in eye movement data and for calculating saccade parameters</a>
 *
 *
 * <p/>
 * For simplicity we calculate the threshold not on the base of signal standard deviation SD
 * but on the base of noise energy, summarizing the squares of EOG signal values
 * during the given time interval.
 * (Parseval's theorem:  the sum (or integral) of the square of a function is equal to the sum (or integral)
 * of the square of its Fourier transform. So from a physical point of view, it is rather adequate to work
 * with squares values (energy)
 * <p/>
 * Also instead of instantaneous velocity we use the averaged one (40-60ms) .
 * That reduces random noise and emphasize saccades
 *
 */

public class SacadeDetector {
    public static final int SACCADE_DURATION_MIN_MS = 40; // [ms] (milliseconds)
    public static final int SACCADE_DURATION_MAX_MS = 800;
    public static final int PEAK_VELOCITY_MAX = 1000; // [degrees/s]

    public static final int SACCADES_DISTANCE_MAX_MS = 6000;
    public static final int SACCADES_DISTANCE_MIN_MS = 200;

    public static final int SENSITIVITY_MAX = 15; // [µV/degree]


    private static final int DEFAULT_START_DISABLED_INTERVAL_MS = 10 * 1000; // no detection

    private int disabledPoints;
    private int disablingIndex;

    private int saccadeDurationMinPoints;
    private int saccadeDurationMaxPoints;
    private int saccadeMaxDigitalValue;

    private DataSeries eogDerivative;
    private int eogDerivativeIntervalMs = 40;


    // during disconnections signal is a constant and its derivative == 0
    private LinearNoiseDetector zeroDetector;
    private int zeroPointsNumber = 3; // to detect disconnections
    private int disconnectionIndex = -100;


    /**
     * we calculate  threshold on the base of noise but
     * not at the currentIndex (where we take the dataValue to compare with the threshold)
     * but "noiseLagPoints"  before
     * Value almost never grows at once/immediately (it took 1-4 points as a rule)
     * so we need a shift/gap between the  threshold and the currentIndex
     */
    private EnergyNoiseDetector noiseDetector;
    private int noiseLagPoints;
    private int noiseAveragingIntervalMs = 400;

    private int thresholdNoiseRatio = 6; // Threshold to noise ratio

    private boolean isThresholdsCachingEnabled;
    private DataList thresholdList = new DataList();

    private SaccadeInfo previouseSaccadeInfo;
    private SaccadeInfo currentSaccadeInfo;

    private int currentIndex = -1;
    private SaccadeListener saccadeListener = new NullSaccadeListener();

    boolean isPeakUnderDetection;
    int startSaccadeDetectionIndex;


    public SacadeDetector(DataSeries eogData) {
        this(eogData, false);
    }

    public SacadeDetector(DataSeries eogData, boolean isThresholdsCachingEnabled) {
        eogDerivative = new FilterDerivativeRem(eogData, eogDerivativeIntervalMs);

        int saccadeMaxPhysValue = PEAK_VELOCITY_MAX * SENSITIVITY_MAX * eogDerivativeIntervalMs / 1000;
        saccadeMaxDigitalValue = (int) (saccadeMaxPhysValue / eogData.getScaling().getDataGain());

        saccadeDurationMinPoints = timeIntervalToPoints(SACCADE_DURATION_MIN_MS);
        saccadeDurationMaxPoints = timeIntervalToPoints(SACCADE_DURATION_MAX_MS);
        disabledPoints = timeIntervalToPoints(DEFAULT_START_DISABLED_INTERVAL_MS);

        noiseLagPoints = 10;

        int noiseAveragingIntervalPoints = timeIntervalToPoints(noiseAveragingIntervalMs);
        if(noiseAveragingIntervalPoints < 1) {
            noiseAveragingIntervalPoints = 1;
        }
        noiseDetector = new EnergyNoiseDetector(eogDerivative, noiseAveragingIntervalPoints);
        zeroDetector = new LinearNoiseDetector(eogDerivative, zeroPointsNumber);
        this.isThresholdsCachingEnabled = isThresholdsCachingEnabled;
        saccadeListener = new NullSaccadeListener();

    }

    public void setSaccadeListener(SaccadeListener saccadeListener) {
        if(saccadeListener != null) {
            this.saccadeListener = saccadeListener;
        }
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
        // wait "noiseLagPoints" before startTime noiseDetector
        if (currentIndex < noiseLagPoints) {
            return 0;
        }

        boolean isSaccadeClose = false;
        //  we skip noise on the points belonging to saccade and (+-)noiseLagPoints around  saccade
        if(currentSaccadeInfo != null ) {
            isSaccadeClose = true;
        } else if (previouseSaccadeInfo != null) {
            if (currentIndex - previouseSaccadeInfo.getEndIndex() < 2 * noiseLagPoints) {
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


        if(isThresholdsCachingEnabled) {
            thresholdList.add(threshold);
        }

        if (isDetectionDisabled()) {
            return;
        }

        int dataValue = eogDerivative.get(currentIndex);
        int dataValueAbs = Math.abs(dataValue);
        if (currentSaccadeInfo == null) {
            if (dataValueAbs > threshold) { // saccade peak detection begin
                currentSaccadeInfo = new SaccadeInfo(noise);
                currentSaccadeInfo.addToPeak(dataValue, currentIndex);
                isPeakUnderDetection = true;
                startSaccadeDetectionIndex = currentIndex;
            }
        } else {
            if(isPeakUnderDetection) {
                if (dataValueAbs > threshold) {
                    try {
                        currentSaccadeInfo.addToPeak(dataValue, currentIndex);
                    } catch (IllegalArgumentException ex) {
                        noiseDetector.restoreLastSkippedValues(currentIndex - startSaccadeDetectionIndex);
                        currentSaccadeInfo = null;
                    }
                } else { // saccade peak detection finished
                    isPeakUnderDetection = false;
                    // find saccade startTime
                    int startIndex = currentSaccadeInfo.getPeakIndex() - 1;
                    while (!currentSaccadeInfo.addToStart(eogDerivative.get(startIndex), startIndex)) {
                        startIndex--;
                    }

                    // find saccade end (has sense in the case of joined big saccade with 2 peaks)
                    /*for (int endIndex = currentSaccadeInfo.getPeakIndex() + 1; endIndex <= currentIndex; endIndex++) {
                        if(currentSaccadeInfo.addToEnd(eogDerivative.get(endIndex), endIndex)) {
                            handleSaccade(currentSaccadeInfo);
                            currentSaccadeInfo = null;
                        }
                    }*/
                }
            } else {
                if(currentSaccadeInfo.addToEnd(dataValue, currentIndex)) { // saccade detection finished
                   handleSaccade(currentSaccadeInfo);
                   currentSaccadeInfo = null;
                }
            }
        }
    }

    private void handleSaccade(SaccadeInfo saccadeInfo) {
        if(saccadeInfo.getWidth() > saccadeDurationMaxPoints || saccadeInfo.getWidth() < saccadeDurationMinPoints) {
            noiseDetector.restoreLastSkippedValues(currentIndex - startSaccadeDetectionIndex);
        } else { // if saccade duration is ok
            previouseSaccadeInfo = saccadeInfo;
            // symmetrize saccade
            SaccadeInfo resultantSaccadeInfo = saccadeInfo.symmetrize();
            long saccadeStart = indexToTime(resultantSaccadeInfo.getStartIndex());
            long saccadeWidth = pointsToTimeInterval(resultantSaccadeInfo.getWidth());
            int saccadeValue = resultantSaccadeInfo.getPeakValue();
            notifyListener(new Saccade(saccadeStart, saccadeWidth, saccadeValue));
        }
    }

    private void notifyListener(Saccade saccade) {
        saccadeListener.onSaccadeDetected(saccade);
    }

    private int timeIntervalToPoints(long timeInterval) {
        return (int) Math.round(timeInterval / (eogDerivative.getScaling().getSamplingInterval() * 1000));
    }

    private long pointsToTimeInterval(int points) {
        return (long)(eogDerivative.getScaling().getSamplingInterval() * points * 1000);
    }

    private long indexToTime(int index) {
        return (long)(eogDerivative.getScaling().getStart() + eogDerivative.getScaling().getSamplingInterval() * index * 1000);
    }

    private String indexToFormattedTime(int index) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        return formatter.format(indexToTime(index));
    }

    // connection problems and signal failure
    // (when dataVelocity = 0 during more then 2-3 points)
    private boolean isDisconnected() {
        if(currentIndex - disconnectionIndex <= zeroPointsNumber) {
            return true;
        }
        return false;
    }


    private boolean isDetectionDisabled() {
        if (currentIndex - disablingIndex < disabledPoints) {
            return true;
        }
        // noise buffer data must be completely updated
        if((currentIndex - disconnectionIndex) < noiseDetector.getBufferSize()) {
            return true;
        }
        return false;
    }


    public void detect() {
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


    class SaccadeInfo {
        private int startIndex = -1;
        private int endIndex = -1;
        private int peakIndex = -1;

        private int startValue;
        private int endValue;
        private int peakValue;

        private int energy;
        private int noiseLevel;

        public SaccadeInfo(int noiseLevel) {
            this.noiseLevel = noiseLevel;
        }


        public SaccadeInfo symmetrize() {
            SaccadeInfo saccadeInfoNew = new SaccadeInfo(noiseLevel);
            saccadeInfoNew.energy = energy;
            saccadeInfoNew.startValue = startValue;
            saccadeInfoNew.endValue = endValue;
            saccadeInfoNew.peakValue = peakValue;
            saccadeInfoNew.startIndex = peakIndex;
            saccadeInfoNew.startIndex = startIndex;
            saccadeInfoNew.endIndex = endIndex;

            int delta = Math.min(peakIndex - startIndex, endIndex - peakIndex);
            if(delta > 0) {
                saccadeInfoNew.startIndex = peakIndex - delta;
                saccadeInfoNew.endIndex = peakIndex + delta;
            }
            return saccadeInfoNew;
        }

        public void addToPeak(int value, int index) throws IllegalArgumentException {
            if(!isEqualSign(peakValue, value)) {
                String msg = "All saccade values must have the same sign";
                throw new IllegalArgumentException(msg);
            }
            if(Math.abs(peakValue) > saccadeMaxDigitalValue) {
                String msg = "Saccade value > saccade max value";
                throw new IllegalArgumentException(msg);
            }

            if(Math.abs(peakValue) < Math.abs(value)) {
                peakValue = value;
                startValue = value;
                endValue = value;

                peakIndex = index;
                startIndex = peakIndex;
                endIndex = peakIndex;
            }
            energy += value * value;
        }

        public int getPeakIndex() {
            return peakIndex;
        }

        public boolean addToStart(int value, int index) throws IllegalStateException, IllegalArgumentException{
            checkPeakDetected();
            if(index >= startIndex) {
                String msg = "Index = "+index + " Expected < already detected startIndex: " + startIndex;
                throw new IllegalArgumentException(msg);
            }
            if(!isEqualSign(value, startValue)) {
                return true;
            }
            if(Math.abs(value) >= Math.abs(startValue)) {
                return true;
            }
            if(Math.abs(value) <= noiseLevel) {
                return true;
            }
            startValue = value;
            startIndex = index;
            return false;
        }

        public boolean addToEnd(int value, int index) throws IllegalStateException, IllegalArgumentException{
            checkPeakDetected();
            if(index <= endIndex) {
                String msg = "Index = "+index + " Expected > already detected endIndex: " + endIndex;
                throw new IllegalArgumentException(msg);
            }
            if(!isEqualSign(value, endValue)) {
                return true;
            }

            if(Math.abs(value) >= Math.abs(endValue)) {
                return true;
            }
            if(Math.abs(value) <= noiseLevel) {
                return true;
            }
            endValue = value;
            endIndex = index;
            return false;
        }


        private void checkPeakDetected() {
            if(peakIndex < 0) {
                String msg = "Peak value must be detected first";
                throw new IllegalStateException(msg);
            }
        }

        /**
         * Saccade duration - number of points belonging to that saccade
         * @throws IllegalStateException if saccade startTime or end was not detected
         */
        public int getWidth() throws IllegalStateException {
            if(startIndex < 0) {
                String msg = "Start index is not detected";
                throw new IllegalStateException(msg);
            }
            if(endIndex < 0) {
                String msg = "End index is not detected";
                throw new IllegalStateException(msg);
            }

            return endIndex - startIndex + 1;
        }

        public int getPeakValue() {
            return peakValue;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public int getEnergy() {
            return energy;
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
}
