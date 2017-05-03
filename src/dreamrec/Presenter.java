package dreamrec;

import data.CompressionType;
import data.DataSeries;
import filters.FilterBandPass_Alfa;
import filters.FilterDerivativeRem;
import filters.FilterHiPass;
import filters.HiPassCollectingFilter;
import functions.Abs;
import functions.Constant;
import graph.Graph;
import graph.GraphType;
import graph.GraphViewer;
import graph.colors.MonoColorSelector;
import graph.colors.TestColorSelector;
import gui.MainWindow;
import rem.SaccadeBatchDetector;

import java.awt.*;

/**
 * Created by mac on 19/02/15.
 */
public class Presenter implements  ControllerListener {
    private final double PREVIEW_TIME_FREQUENCY = 50.0 / 750;

    MainWindow mainWindow;
    GraphViewer graphViewer;


    public Presenter(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void dataStoreUpdated(Object dataStore) {
        graphViewer = new GraphViewer();
        graphViewer.setPreviewFrequency(PREVIEW_TIME_FREQUENCY);
        mainWindow.setGraphViewer(graphViewer);

        if(dataStore instanceof DataStore) {
            DataStore dataStore1 = (DataStore) dataStore;
            configureGraphViewer(dataStore1);
            dataStore1.addListener(new DataStoreListener() {
                @Override
                public void onDataUpdate() {
                    graphViewer.autoScroll();
                }
            });
        }

        if(dataStore instanceof RemDataStore) {
            RemDataStore remDataStore = (RemDataStore) dataStore;
            configureRemGraphViewer(remDataStore);
            remDataStore.addListener(new DataStoreListener() {
                @Override
                public void onDataUpdate() {
                    graphViewer.autoScroll();
                }
            });
        }
    }

    private void configureRemGraphViewer(RemDataStore remDataStore) {
        rem(remDataStore);
    }

    private void configureGraphViewer(DataStore dataStore) {
        for(int i = 0; i < dataStore.getNumberOfChannels(); i++) {
            DataSeries channel = dataStore.getChannelData(i);
            graphViewer.addGraphPanel(1, true);
            graphViewer.addGraph(channel);
        }

        if(dataStore.getNumberOfChannels() > 0) {
            DataSeries channel = dataStore.getChannelData(0);
            graphViewer.addPreviewPanel(1, false);
            DataSeries velocityRem =  new Abs(new FilterDerivativeRem(channel));
            graphViewer.addPreview(velocityRem, CompressionType.AVERAGE);
        }

    }


    private void rem(RemDataStore remDataStore) {
        int eogCutOffPeriod = 10; //sec. to remove steady component (cutoff_frequency = 1/cutoff_period )
        DataSeries eogFull = remDataStore.getEogData();
        DataSeries eog = new HiPassCollectingFilter(eogFull, eogCutOffPeriod);
        DataSeries accMovement = remDataStore.getAccMovementData();
        DataSeries isSleep = remDataStore.isSleep();


        FilterDerivativeRem eogDerivativeRem =  new FilterDerivativeRem(eogFull);
        DataSeries eogDerivativeRemAbs =  new Abs(eogDerivativeRem);

        graphViewer.addGraphPanel(3, true);
        graphViewer.addGraph(eog);

        DataSeries alfa = new FilterHiPass(new FilterBandPass_Alfa(eogFull), 2);
        graphViewer.addGraphPanel(2, true);
        graphViewer.addGraph(alfa);

        graphViewer.addGraphPanel(1, false);
        graphViewer.addGraph(accMovement);
        graphViewer.addGraph(new Constant(accMovement, remDataStore.getAccMovementLimit()));

        graphViewer.addPreviewPanel(2, false);
        graphViewer.addPreview(eogDerivativeRemAbs, CompressionType.MAX);
        graphViewer.addPreview(isSleep, GraphType.BOOLEAN, CompressionType.BOOLEAN);

    }



    private void sasha(RemDataStore remDataStore) {
        int eogCutOffPeriod = 10; //sec. to remove steady component (cutoff_frequency = 1/cutoff_period )
        DataSeries eogFull = remDataStore.getEogData();
        DataSeries eog = new HiPassCollectingFilter(eogFull, eogCutOffPeriod);
        DataSeries accMovement = remDataStore.getAccMovementData();
        DataSeries isSleep = remDataStore.isSleep();

        double accMovementLimit = remDataStore.getAccMovementLimit();


        FilterDerivativeRem eogDerivativeRem =  new FilterDerivativeRem(eogFull);
        //DataSeries eogDerivativeRem =  new FilterLowPass(new FilterDerivativeRem(eogFull), 25.0);
        DataSeries eogDerivativeRemAbs =  new Abs(eogDerivativeRem);

        SaccadeBatchDetector saccades = new SaccadeBatchDetector(eogFull);

        graphViewer.addGraphPanel(6, true);
      //  graphViewer.addGraph(new Graph(isSleep, GraphType.BOOLEAN, new BooleanColorSelector(accMovement, accMovementLimit)), CompressionType.BOOLEAN, 0);
        graphViewer.addGraph(new Graph(eog, GraphType.VERTICAL_LINE, new TestColorSelector()), CompressionType.AVERAGE, 0);


        graphViewer.addGraphPanel(2, false);
        graphViewer.addGraph(new Graph(accMovement, GraphType.BAR, new MonoColorSelector(Color.DARK_GRAY.darker())),CompressionType.AVERAGE,1);
        graphViewer.addGraph(new Constant(accMovement, accMovementLimit));

        graphViewer.addPreviewPanel(2, false);
        graphViewer.addPreview(eogDerivativeRemAbs, CompressionType.MAX);
        graphViewer.addPreview(isSleep, GraphType.BOOLEAN, CompressionType.BOOLEAN);

    }

}
