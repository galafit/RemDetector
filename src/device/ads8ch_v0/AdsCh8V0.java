package device.ads8ch_v0;

import device.general.Ads;
import device.general.AdsConfiguration;

/**
 * Created by mac on 22/03/15.
 */
public class AdsCh8V0  extends Ads {

    @Override
    public void setAdsConfigurator(AdsConfiguration adsConfiguration) {
        adsConfigurator = new AdsConfiguratorCh8V0(adsConfiguration);
    }
}

