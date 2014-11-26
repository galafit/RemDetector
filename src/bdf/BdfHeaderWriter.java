package bdf;


import dreamrec.ExperimentInfo;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.crostec.ads.AdsUtils.adjustLength;

class BdfHeaderWriter {

    public static byte[] createBdfHeader(BdfConfig bdfConfig, ExperimentInfo experimentInfo) {
        Charset characterSet = Charset.forName("US-ASCII");
        StringBuilder bdfHeader = new StringBuilder();

        String identificationCode = "BIOSEMI";

        String localPatientIdentification =  experimentInfo.getLocalPatientIdentification();
        String localRecordingIdentification =   experimentInfo.getLocalRecordIdentification();
        long startTime = experimentInfo.getStartTime();
        int numberOfDataRecords = experimentInfo.getNumberOfDataRecords();

        String startDateOfRecording = new SimpleDateFormat("dd.MM.yy").format(new Date(startTime));
        String startTimeOfRecording = new SimpleDateFormat("HH.mm.ss").format(new Date(startTime));

        int numberOfSignals = bdfConfig.getNumberOfSignals();  // number of signals in data record = number of active channels
        int numberOfBytesInHeaderRecord = 256 * (1 + numberOfSignals);
        String versionOfDataFormat = "24BIT";

        String channelsDigitalMaximum = "8388607";
        String channelsDigitalMinimum = "-8388608";

        String accelerometerDigitalMaximum = "30800";
        String accelerometerDigitalMinimum = "-30800";
        String accelerometerPhysicalMaximum = "2";
        String accelerometerPhysicalMinimum = "-2";

        bdfHeader.append(adjustLength(identificationCode, 7));  //7 not 8 because first non ascii byte we will add later
        bdfHeader.append(adjustLength(localPatientIdentification, 80));
        bdfHeader.append(adjustLength(localRecordingIdentification, 80));
        bdfHeader.append(startDateOfRecording);
        bdfHeader.append(startTimeOfRecording);
        bdfHeader.append(adjustLength(Integer.toString(numberOfBytesInHeaderRecord), 8));
        bdfHeader.append(adjustLength(versionOfDataFormat, 44));
        bdfHeader.append(adjustLength(Integer.toString(numberOfDataRecords), 8));
        bdfHeader.append(adjustLength(String.format("%.6f", bdfConfig.getDurationOfDataRecord()).replace(",", "."), 8));
        bdfHeader.append(adjustLength(Integer.toString(numberOfSignals), 4));

        StringBuilder labels = new StringBuilder();
        StringBuilder transducerTypes = new StringBuilder();
        StringBuilder physicalDimensions = new StringBuilder();
        StringBuilder physicalMinimums = new StringBuilder();
        StringBuilder physicalMaximums = new StringBuilder();
        StringBuilder digitalMinimums = new StringBuilder();
        StringBuilder digitalMaximums = new StringBuilder();
        StringBuilder preFilterings = new StringBuilder();
        StringBuilder samplesNumbers = new StringBuilder();
        StringBuilder reservedForChannels = new StringBuilder();
        BdfSignalConfig[] signalConfigList = bdfConfig.getSignalsConfigList();
        for (int i = 0; i < signalConfigList.length; i++) {
            BdfSignalConfig signalConfig = signalConfigList[i];
                labels.append(adjustLength(signalConfig.getLabel(), 16));
                transducerTypes.append(adjustLength("Unknown", 80));
                physicalDimensions.append(adjustLength(signalConfig.getPhysicalDimension(), 8));
               int physicalMaximum = signalConfig.getPhysicalMax();
               int physicalMinimum = signalConfig.getPhysicalMin();
               int digitalMax = signalConfig.getDigitalMax();
               int digitalMin = signalConfig.getDigitalMin();

                physicalMinimums.append(adjustLength(String.valueOf(physicalMinimum), 8));
                physicalMaximums.append(adjustLength(String.valueOf(physicalMaximum), 8));
                digitalMinimums.append(adjustLength(String.valueOf(digitalMin), 8));
                digitalMaximums.append(adjustLength(String.valueOf(digitalMax), 8));
                preFilterings.append(adjustLength("None", 80));
                int nrOfSamplesInEachDataRecord = signalConfig.getNumberOfSamplesInEachDataRecord();
                samplesNumbers.append(adjustLength(Integer.toString(nrOfSamplesInEachDataRecord), 8));
                reservedForChannels.append(adjustLength("", 32));
        }
        bdfHeader.append(labels);
        bdfHeader.append(transducerTypes);
        bdfHeader.append(physicalDimensions);
        bdfHeader.append(physicalMinimums);
        bdfHeader.append(physicalMaximums);
        bdfHeader.append(digitalMinimums);
        bdfHeader.append(digitalMaximums);
        bdfHeader.append(preFilterings);
        bdfHeader.append(samplesNumbers);
        bdfHeader.append(reservedForChannels);
        // add first non ascii byte  "255"
        ByteBuffer byteBuffer = ByteBuffer.allocate(bdfHeader.length() + 1);
        byteBuffer.put((byte) 255);
        byteBuffer.put(bdfHeader.toString().getBytes(characterSet));
        return byteBuffer.array();
    }
}
