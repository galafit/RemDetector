package rem.saccade;

import data.DataList;
import data.DataSeries;
import data.Scaling;
import filters.FilterDerivativeRem;

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
 * MAX_PEAK_VELOCITY_DEGREES  = 700 °/s
 * <br><br>
 * (See: <a href="https://www.liverpool.ac.uk/~pcknox/teaching/Eymovs/params.htm">The parameters of eye movement</a>
 * and <a href = "https://www.researchgate.net/post/What_is_the_average_duration_for_saccades_with_36_and_54_of_amplitude">
 *     average duration for saccades with 36º and 54º of amplitude</a>
 * Here is the link to download a book from Roger Carpenter
 * http://libgen.io/ads.php?md5=22AB8EC2F36192D85A1F5288187D0888
 * http://gen.lib.rus.ec/book/index.php?md5=22AB8EC2F36192D85A1F5288187D0888
 * Torrent: http://download1.libgen.io/ads.php?md5=22AB8EC2F36192D85A1F5288187D0888)
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
 * But we need take into account that besides the saccades there are
 * also <b>smooth pursuit eye movements</b> that allows the eyes to closely follow a moving object and
 * that has duration about 133ms and latency 125ms and that can precede or follow saccades.
 * (See https://physoc.onlinelibrary.wiley.com/doi/pdf/10.1113/jphysiol.1965.sp007718)
 * Other authors write about Glissades eye movements.
 * It was found that a big main saccade is frequently followed by a second smaller corrective movement
 * that brings the eye closer to the target o Glissades.
 * (Glissades - slow drifting eye movements occasionally seen at the end of saccadic eye movements).
 * Glissades duration 30-280ms.
 * (See http://sysengr.engr.arizona.edu/publishedPapers/Glissades.pdf)
 *
 * So  real full eye movement can have duration till 500ms (saccade + glissade or saccade + pursuit eye movement)
 * <br>
 * SACCADE_DURATION_MAX_MS = 500 ms
 *
 * <p/>
 * !!! The movement of the eyes usually undershoots the target and requires another small saccade to reach it.
 * Overshooting of the target is uncommon in normal subjects.!!!
 * (http://www.bem.fi/book/28/28.htm)
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
 * <li>Overlong fixations: 4900ms</li>
 * <li>Short fixations: 50ms</li>
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
 *
 */

public class SaccadeDetector implements DataSeries {
    private static final int SACCADE_WIDTH_MIN_MS = 40; // [ms] (milliseconds)
    private static final int SACCADE_WIDTH_MAX_MS = 320;
    private static final int SACCADE_PEAK_VELOCITY_MAX = 700; // [°/s]
    private static final int AVERAGING_TIME_MS = 40;

    private static final int SACCADES_DISTANCE_MAX_MS = 5000;
    private static final int SACCADES_DISTANCE_NORMAL_MS = 600;
    private static final int SACCADES_DISTANCE_MIN_MS = 50;

    private static final int SENSITIVITY = 14; // [µV/°]
    private static final double SACCADE_MAX_PHYSICAL_VALUE =
            SACCADE_PEAK_VELOCITY_MAX * SENSITIVITY * AVERAGING_TIME_MS / 1000; // [ µV ]

    private static final int THRESHOLD_INTERVAL_MS = 2000;
    private static final int START_DEAD_INTERVAL_MS = 10000; // no saccade detection

    private static final int THRESHOLD_NOISE_RATIO = 6; // Threshold to noise ratio

    private final int saccadeMaxDigitalValue;
    private final int saccadeWidthMinPoints;
    private final int saccadeWidthMaxPoints;
    private final int saccadeDistanceMaxPoints;
    private final int saccadeDistanceMinPoints;
    private final int startDeadIntervalPoints;

    private DataSeries velocityData;
    private EnergyNoiseDetector noiseDetector;

    // during disconnections signal is a constant and its derivative (velocity) == 0
    private LinearNoiseDetector zeroDetector;
    private static final int ZERO_POINTS_NUMBER = 3;

    private int currentIndex = -1;
    private int zeroLastIndex = -1;
    private int nonRemActivityLastIndex = -1;


    private boolean isThresholdsCachingEnabled;
    private DataList thresholdList = new DataList();

    /**
     * we calculate  global threshold on the base of noise
     * not at the currentIndex (where we take the Value to compare with the threshold)
     * but "thresholdLagPoints"  before
     * Value almost never grows at once/immediately (it took 1-4 points as a rule)
     * so we need a shift/gap between the  threshold and the currentIndex
     */
    private int thresholdLagPoints = 2;



    private Saccade currentPeak; // to become saccade peak should be approved

    // saccades appear in group always. Single saccade will not be approved
    private List<Saccade> saccadeGroup = new ArrayList<>(10);
    private static final int SACCADE_MIN_NUMBER_IN_GROUP = 3;

    private List<Saccade> saccadeList = new ArrayList<>(); // approved saccades
    private HashMap<Integer, Integer> saccadeValues = new HashMap<>();

    public SaccadeDetector(DataSeries eogData) {
        this(eogData, false);
    }

    public SaccadeDetector(DataSeries eogData, boolean isThresholdsCachingEnabled) {
        velocityData = new FilterDerivativeRem(eogData);
        double gain = 1;
        if (eogData.getScaling() != null) {
            gain = eogData.getScaling().getDataGain();
        }
        saccadeMaxDigitalValue = (int) (SACCADE_MAX_PHYSICAL_VALUE / gain);
        double dataIntervalMs = eogData.getScaling().getSamplingInterval() * 1000;
        saccadeWidthMinPoints = (int) Math.round(SACCADE_WIDTH_MIN_MS / dataIntervalMs);
        saccadeWidthMaxPoints = (int) Math.round(SACCADE_WIDTH_MAX_MS / dataIntervalMs);
        saccadeDistanceMaxPoints = (int) Math.round(SACCADES_DISTANCE_MAX_MS / dataIntervalMs);
        saccadeDistanceMinPoints = (int) Math.round(SACCADES_DISTANCE_MIN_MS / dataIntervalMs);
        startDeadIntervalPoints = (int) Math.round(START_DEAD_INTERVAL_MS / dataIntervalMs);

        int thresholdIntervalPoints = (int) Math.round(THRESHOLD_INTERVAL_MS / dataIntervalMs);
        if(thresholdIntervalPoints < 1) {
            thresholdIntervalPoints = 1;
        }
        noiseDetector = new EnergyNoiseDetector(new FilterDerivativeRem(eogData), thresholdIntervalPoints);
        zeroDetector = new LinearNoiseDetector(velocityData, ZERO_POINTS_NUMBER);
        this.isThresholdsCachingEnabled = isThresholdsCachingEnabled;
    }


    private int getThreshold() {
        // wait "thresholdLagPoints" before start noiseDetector
        if (currentIndex < thresholdLagPoints) {
            return 0;
        }

        boolean isPreviousSacadeClose = false;
        if (saccadeGroup.size() > 0) {
            Saccade previousSaccade = saccadeGroup.get(saccadeGroup.size() - 1);

            if (currentIndex - previousSaccade.getEndIndex() < 2 * thresholdLagPoints) {
                isPreviousSacadeClose = true;
            }
        }

        if (currentPeak != null || isPreviousSacadeClose || isDisconnected()) {
            return noiseDetector.skipNext() * THRESHOLD_NOISE_RATIO;
        } else {
            return noiseDetector.calculateNext() * THRESHOLD_NOISE_RATIO;
        }
    }


    /**
     * To detect saccades we use a variation of Velocity/Acceleration-Threshold Algorithm
     * that separates fixation and saccade points based on their point-to-point velocities/acceleration.
     * The velocity profiles of saccadic eye
     * movements show essentially two distributions of velocities:
     * low velocities for fixations (i.e., <100 deg/sec), and high
     * velocities (i.e., >300 deg/sec) for saccades
     * <p/>
     * Our algorithm:
     * 1) instead of fixed threshold use adaptive threshold calculated from the signal data itself during some period
     * 2) combine acceleration and velocity data: the threshold we calculated from the acceleration data
     * and then use it to detect velocity peaks.
     * (As acceleration actually involves data from more points: velocity = x2 - x1 and
     * acceleration = x3 + x1 - 2*x2, acceleration threshold is more sensitive to noise and
     * permits cut it off more effective, while in REM where "noise" decrease
     * Acceleration and Velocity thresholds are almost equal)
     * 3) instead of  instantaneous velocity and acceleration we use the averaged (for 40ms) ones.
     * That also reduce random noise and emphasize saccades
     * 4) Threshold is calculated on the base of energy, summarizing the squares of the values (instead of absolute values)
     * (Parseval's theorem:  the sum (or integral) of the square of a function is equal to the sum (or integral)
     * of the square of its Fourier transform. So from a physical point of view, more adequately work
     * with squares values (energy) )
     */
    // TODO handle situation velocity_abs > threshold but velocity change sign
    public void detectOnNextData() {
        currentIndex++;

        // check if there are signal problems ()
        if(zeroDetector.calculateNext() == 0) {
            zeroLastIndex = currentIndex;
        }

        int threshold = getThreshold();

        if(isThresholdsCachingEnabled) {
            thresholdList.add(threshold);
        }

        if (isSaccadeDetectionDisabled()) {
            return;
        }

        int velocity = Math.abs(velocityData.get(currentIndex));
        if (currentPeak == null) {
            if (velocity > threshold) { // saccade start
                currentPeak = new Saccade(currentIndex, velocity);
            }
        } else {
            if (velocity > threshold) {
                currentPeak.addPoint(velocity);
            } else { // saccade end
                int saccadeDistance = 0;
                if(saccadeGroup.size() > 0) {
                    Saccade lastSaccadeInGroup = saccadeGroup.get(saccadeGroup.size() - 1);
                    saccadeDistance = currentPeak.getStartIndex() - lastSaccadeInGroup.getEndIndex();

                }
                if(saccadeDistance > saccadeDistanceMaxPoints) {
                    saccadeGroup.clear();
                }
                saccadeGroup.add(currentPeak);

                if(saccadeGroup.size() == SACCADE_MIN_NUMBER_IN_GROUP) {
                    if(isSaccadeGroupOk(saccadeGroup)) {
                        // approve all saccades in group
                        for (int i = 0; i < saccadeGroup.size(); i++) {
                            approveSaccade(saccadeGroup.get(i));
                        }
                    } else {
                        saccadeGroup.clear();
                        // mark that as nonRemActivity
                        nonRemActivityLastIndex = currentIndex;
                    }
                } else if(saccadeGroup.size() > SACCADE_MIN_NUMBER_IN_GROUP) {
                    // approve saccade without any verification
                    approveSaccade(currentPeak);
                }
                currentPeak = null;
            }
        }
    }
    // connection problems and signal failure
    // (when dataVelocity = 0 during more then 2-3 points)
    private boolean isDisconnected() {
        if(currentIndex == zeroLastIndex) {
           return true;
        }
        return false;
    }

    private boolean isSaccadeDetectionDisabled() {
        if (currentIndex < startDeadIntervalPoints) {
            return true;
        }
        // noise buffer data must be completely updated
        if((currentIndex - zeroLastIndex) < noiseDetector.getBufferSize()) {
            return true;
        }
        if((currentIndex - nonRemActivityLastIndex) < noiseDetector.getBufferSize()){
            return true;
        }
        return false;
    }

    private void approveSaccade(Saccade saccade) {
        saccadeList.add(saccade);
        for (int i = saccade.getStartIndex(); i <= saccade.getEndIndex(); i++) {
            saccadeValues.put(i, saccade.getMaxValue());
        }
    }

    private boolean isSaccadeGroupOk(List<Saccade> saccadeGroup) {
        int avgSaccadeWidth = 0;
        int avgSaccadeDistance = 0;
        int groupSize = saccadeGroup.size();
        for (int i = 0; i < groupSize; i++) {
            Saccade saccade_i = saccadeGroup.get(i);
            if(saccade_i.getMaxValue() > SACCADE_PEAK_VELOCITY_MAX) {
              //  return false;
            }
            avgSaccadeWidth += saccade_i.getWidth();
            if(i > 0) {
                avgSaccadeDistance += saccade_i.getStartIndex() - saccadeGroup.get(i - 1).getEndIndex();
            }
        }
        avgSaccadeWidth = avgSaccadeWidth / groupSize;
        avgSaccadeDistance = avgSaccadeDistance / (groupSize - 1);

        if(avgSaccadeWidth < saccadeWidthMinPoints * 2) {
            return false;
        }

        if(avgSaccadeWidth > saccadeWidthMaxPoints ) {
            return false;
        }

        if(avgSaccadeDistance < saccadeDistanceMinPoints * 2) {
            return false;
        }
        return true;

    }

    private void update() {
        for (int i = currentIndex; i < velocityData.size() - 1; i++) {
            detectOnNextData();
        }
    }



    /**
     *
     * @return
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
                return velocityData.getScaling();
            }
        };
    }

    @Override
    public int size() {
        update();
        return velocityData.size();
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
        return velocityData.getScaling();
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
