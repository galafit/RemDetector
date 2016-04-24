package device.ads2ch_v0;

import device.general.Ads;
import device.general.AdsConfiguration;

/**
 * Created by mac on 06/03/15.
 */
public class AdsCh2V0 extends Ads {
    @Override
    public void setAdsConfigurator(AdsConfiguration adsConfiguration) {
            adsConfigurator = new AdsConfiguratorCh2V0(adsConfiguration);
    }
}
