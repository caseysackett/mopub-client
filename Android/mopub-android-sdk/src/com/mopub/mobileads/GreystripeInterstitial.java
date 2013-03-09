package com.mopub.mobileads;

import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.greystripe.sdk.GSAd;
import com.greystripe.sdk.GSAdErrorCode;
import com.greystripe.sdk.GSAdListener;
import com.greystripe.sdk.GSFullscreenAd;
import com.mopub.mobileads.CustomEventInterstitial;

/*
 * Tested with Greystripe SDK 2.1.
 */
public class GreystripeInterstitial extends CustomEventInterstitial implements GSAdListener {
    private CustomEventInterstitial.Listener mInterstitialListener;
    private GSFullscreenAd mGreystripeAd;
    
    /*
     * Abstract methods from CustomEventInterstitial
     */
    @Override
    public void loadInterstitial(Context context, CustomEventInterstitial.Listener interstitialListener,
            Map<String, Object> localExtras, Map<String, String> serverExtras) {
        mInterstitialListener = interstitialListener;

        String appId = serverExtras.get("app_id");
        if(appId == null) {
        	try {
	        	ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
	            appId = ai.metaData.get("greystripe_interstitial_ads_app_id").toString();
        	} catch(Throwable t) {
        		Log.e("MoPub", "Could not find greystripe_interstitial_ads_app_id in meta-data in Android manifest");
        	}
        }
        if(appId == null) {
            Log.d("MoPub", "Greystripe interstitial ad app_id is missing.");
            if(mInterstitialListener != null) {
            	mInterstitialListener.onAdFailed();
            }
            return;
        }
        
        mGreystripeAd = new GSFullscreenAd(context, appId);
        mGreystripeAd.addListener(this);
        mGreystripeAd.fetch();
    }

    @Override
    public void showInterstitial() {
    	if(mGreystripeAd != null) {
	        if (!mGreystripeAd.isAdReady()) {
	            if(mInterstitialListener != null) {
	            	mInterstitialListener.onAdFailed();
	            }
	            return;
	        }
	        
	        Log.d("MoPub", "Showing Greystripe interstitial ad.");
	        mGreystripeAd.display();
            if(mInterstitialListener != null) {
            	mInterstitialListener.onShowInterstitial();
            }
    	}
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
        Log.d("MoPub", "Greystripe interstitial ad clicked.");
        if(mInterstitialListener != null) {
	        mInterstitialListener.onClick();
	
	        /*
	         * XXX: When a Greystripe interstitial is dismissed as a result of a user click, the
	         * onAdDismissal callback does not get fired. This call ensures that the custom event
	         * listener is informed of all dismissals.
	         */
	        mInterstitialListener.onDismissInterstitial();
        }
    }

    @Override
    public void onAdDismissal(GSAd greystripeAd) {
        Log.d("MoPub", "Greystripe interstitial ad dismissed.");
        if(mInterstitialListener != null) {
        	mInterstitialListener.onDismissInterstitial();
        }
    }

    @Override
    public void onFailedToFetchAd(GSAd greystripeAd, GSAdErrorCode errorCode) {
        Log.d("MoPub", "Greystripe interstitial ad failed to load.");
        if(mInterstitialListener != null) {
        	mInterstitialListener.onAdFailed();
        }
    }

    @Override
    public void onFetchedAd(GSAd greystripeAd) {
        if (mGreystripeAd != null && mGreystripeAd.isAdReady()) {
            Log.d("MoPub", "Greysripe interstitial ad loaded successfully.");
            if(mInterstitialListener != null) {
            	mInterstitialListener.onAdLoaded();
            }
        } else if(mInterstitialListener != null) {
           	mInterstitialListener.onAdFailed();
        }
    }

    @Override
    public void onAdCollapse(GSAd greystripeAd) {
    }

    @Override
    public void onAdExpansion(GSAd greystripeAd) {
    }
}
