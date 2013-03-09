package com.mopub.mobileads;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.inmobi.androidsdk.IMAdInterstitial;
import com.inmobi.androidsdk.IMAdInterstitialListener;
import com.inmobi.androidsdk.IMAdRequest.ErrorCode;

/*
 * Tested with InMobi SDK 3.6.2.
 */
public class InMobiInterstitial extends CustomEventInterstitial implements IMAdInterstitialListener {
    private CustomEventInterstitial.Listener mInterstitialListener;
    private IMAdInterstitial mInMobiInterstitial;

    /*
     * Abstract methods from CustomEventInterstitial
     */
    @Override
    public void loadInterstitial(Context context, CustomEventInterstitial.Listener interstitialListener,
            Map<String, Object> localExtras, Map<String, String> serverExtras) {
        mInterstitialListener = interstitialListener;
        
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            // You may also pass in an Activity Context in the localExtras map and retrieve it here.
        	activity = (Activity)localExtras.get("activity");
        }
        
        if (activity == null) {
        	if(mInterstitialListener != null) {
        		mInterstitialListener.onAdFailed();
        	}
            return;
        }
        
        String appId = serverExtras.get("app_id");
        if(appId == null) {
        	try {
	        	ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
	            appId = ai.metaData.get("inmobi_interstitial_ads_app_id").toString();
        	} catch(Throwable t) {
        		Log.e("MoPub", "Could not find inmobi_interstitial_ads_app_id in meta-data in Android manifest");
        	}
        }
        if(appId == null) {
            Log.d("MoPub", "InMobi interstitial ad app_id is missing.");
        	if(mInterstitialListener != null) {
        		mInterstitialListener.onAdFailed();
        	}
            return;
        }
        
        mInMobiInterstitial = new IMAdInterstitial(activity, appId);
        mInMobiInterstitial.setIMAdInterstitialListener(this);
        mInMobiInterstitial.loadNewAd();
    }
    
    @Override
    public void showInterstitial() {
        Log.d("MoPub", "Showing InMobi interstitial ad.");
    	if(mInMobiInterstitial != null) {
    		mInMobiInterstitial.show();
        	if(mInterstitialListener != null) {
        		mInterstitialListener.onShowInterstitial();
        	}
    	}
    }

    @Override
    public void onInvalidate() {
    	if(mInMobiInterstitial != null) {
    		mInMobiInterstitial.setIMAdInterstitialListener(null);
    	}
    }

    /*
     * IMAdListener implementation
     */
    @Override
    public void onAdRequestFailed(IMAdInterstitial adInterstitial, ErrorCode errorCode) {
        Log.d("MoPub", "InMobi interstitial ad failed to load.");
    	if(mInterstitialListener != null) {
    		mInterstitialListener.onAdFailed();
    	}
    }

    @Override
    public void onAdRequestLoaded(IMAdInterstitial adInterstitial) {
        Log.d("MoPub", "InMobi interstitial ad loaded successfully.");
    	if(mInterstitialListener != null) {
    		mInterstitialListener.onAdLoaded();
    	}
    }

    @Override
    public void onDismissAdScreen(IMAdInterstitial adInterstitial) {
        Log.d("MoPub", "InMobi interstitial ad dismissed.");
    	if(mInterstitialListener != null) {
    		mInterstitialListener.onDismissInterstitial();
    	}
    }

    @Override
    public void onLeaveApplication(IMAdInterstitial adInterstitial) {
        Log.d("MoPub", "InMobi interstitial ad leaving application.");
    	if(mInterstitialListener != null) {
    		mInterstitialListener.onClick();
    		mInterstitialListener.onLeaveApplication();
    	}
    }

    @Override
    public void onShowAdScreen(IMAdInterstitial adInterstitial) {
    }
}
