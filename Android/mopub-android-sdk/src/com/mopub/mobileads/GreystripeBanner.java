package com.mopub.mobileads;

import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.greystripe.sdk.GSAd;
import com.greystripe.sdk.GSAdErrorCode;
import com.greystripe.sdk.GSAdListener;
import com.greystripe.sdk.GSMobileBannerAdView;

/*
 * Tested with Greystripe SDK 2.1.
 */
public class GreystripeBanner extends CustomEventBanner implements GSAdListener {
    private CustomEventBanner.Listener mBannerListener;
    private GSMobileBannerAdView mGreystripeAd;

    /*
     * Abstract methods from CustomEventBanner
     */
    @Override
    public void loadAd(Context context, CustomEventBanner.Listener bannerListener,
            Map<String, Object> localExtras, Map<String, String> serverExtras) {
        mBannerListener = bannerListener;
        
        String appId = serverExtras.get("app_id");
        if(appId == null) {
        	try {
	        	ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
	            appId = ai.metaData.get("greystripe_ads_app_id").toString();
        	} catch(Throwable t) {
        		Log.e("MoPub", "Could not find greystripe_ads_app_id in meta-data in Android manifest");
        	}
        }
        if(appId == null) {
            Log.d("MoPub", "Greystripe banner ad app_id is missing.");
            if(mBannerListener != null) {
            	mBannerListener.onAdFailed();
            }
            return;
        }
        
        mGreystripeAd = new GSMobileBannerAdView(context, appId);
        mGreystripeAd.addListener(this);
        mGreystripeAd.refresh();
    }

    @Override
    public void onInvalidate() {
    	if(mGreystripeAd != null) {
    		mGreystripeAd.removeListener(this);
    	}
    }

    /*
     * GSAdListener implementation
     */
    @Override
    public void onAdClickthrough(GSAd greystripeAd) {
        Log.d("MoPub", "Greystripe banner ad clicked.");
        if(mBannerListener != null) {
        	mBannerListener.onClick();
        }
    }

    @Override
    public void onAdDismissal(GSAd greystripeAd) {
        Log.d("MoPub", "Greystripe banner ad modal dismissed.");
    }

    @Override
    public void onFailedToFetchAd(GSAd greystripeAd, GSAdErrorCode errorCode) {
        Log.d("MoPub", "Greystripe banner ad failed to load.");
        if(mBannerListener != null) {
        	mBannerListener.onAdFailed();
        }
    }

    @Override
    public void onFetchedAd(GSAd greystripeAd) {
        if (mGreystripeAd != null & mGreystripeAd.isAdReady()) {
            Log.d("MoPub", "Greystripe banner ad loaded successfully. Showing ad...");
            if(mBannerListener != null) {
	            mBannerListener.onAdLoaded();
	            mBannerListener.setAdContentView(mGreystripeAd);
            }
        } else {
            if(mBannerListener != null) {
            	mBannerListener.onAdFailed();
            }
        }
    }

    @Override
    public void onAdCollapse(GSAd greystripeAd) {
    }

    @Override
    public void onAdExpansion(GSAd greystripeAd) {
    }
}
