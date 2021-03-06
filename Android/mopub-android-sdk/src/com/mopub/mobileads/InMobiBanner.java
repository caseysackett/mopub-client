package com.mopub.mobileads;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.inmobi.androidsdk.IMAdListener;
import com.inmobi.androidsdk.IMAdRequest.ErrorCode;
import com.inmobi.androidsdk.IMAdView;

/*
 * Tested with InMobi SDK 3.6.2.
 */
public class InMobiBanner extends CustomEventBanner implements IMAdListener {
    private CustomEventBanner.Listener mBannerListener;
    private IMAdView mInMobiBanner;
    private int mAdSize;

    /*
     * Abstract methods from CustomEventBanner
     */
    @Override
    public void loadAd(Context context, CustomEventBanner.Listener bannerListener,
            Map<String, Object> localExtras, Map<String, String> serverExtras) {
        mBannerListener = bannerListener;
        
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            // You may also pass in an Activity Context in the localExtras map and retrieve it here.
        	activity = (Activity)localExtras.get("activity");
        }
        
        if (activity == null) {
        	if(mBannerListener != null) {
        		mBannerListener.onAdFailed();
        	}
            return;
        }
        
        /*
         * You may also pass this String down in the serverExtras Map by specifying Custom Event Data
         * in MoPub's web interface.
         */
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        mAdSize = IMAdView.INMOBI_AD_UNIT_320X50;
        if(widthPixels >= 600) mAdSize = IMAdView.INMOBI_AD_UNIT_468X60; 	// Kindle Fire
        if(widthPixels == 728) mAdSize = IMAdView.INMOBI_AD_UNIT_728X90;		// Only if it matches exactly

        String appId = serverExtras.get("app_id");
        if(appId == null) {
        	try {
	        	ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
	            appId = ai.metaData.get("inmobi_ads_app_id").toString();
        	} catch(Throwable t) {
        		Log.e("MoPub", "Could not find inmobi_ads_app_id in meta-data in Android manifest");
        	}
        }
        if(appId == null) {
            Log.d("MoPub", "InMobi banner ad app_id is missing.");
        	if(mBannerListener != null) {
        		mBannerListener.onAdFailed();
        	}
            return;
        }
        
        mInMobiBanner = new IMAdView(activity, mAdSize, appId);
        mInMobiBanner.setIMAdListener(this);
        mInMobiBanner.loadNewAd();
    }

    @Override
    public void onInvalidate() {
    	if(mInMobiBanner != null) {
    		mInMobiBanner.setIMAdListener(null);
    	}
    }

    /*
     * IMAdListener implementation
     */
    @Override
    public void onAdRequestCompleted(IMAdView adView) {
        if (mInMobiBanner != null && mBannerListener != null) {
            Log.d("MoPub", "InMobi banner ad loaded successfully. Showing ad...");
            if(mBannerListener != null) {
	            mBannerListener.onAdLoaded();
	            mBannerListener.setAdContentView(mInMobiBanner);
            }
        } else if (mBannerListener != null) {
           	mBannerListener.onAdFailed();
        }
    }

    @Override
    public void onAdRequestFailed(IMAdView adView, ErrorCode errorCode) {
        Log.d("MoPub", "InMobi banner ad failed to load.");
	    if(mBannerListener != null) {
	    	mBannerListener.onAdFailed();
	    }
    }

    @Override
    public void onDismissAdScreen(IMAdView adView) {
        Log.d("MoPub", "InMobi banner ad modal dismissed.");
    }

    @Override
    public void onLeaveApplication(IMAdView adView) {
        Log.d("MoPub", "InMobi banner ad click has left the application context.");
    	if(mBannerListener != null) {
    		mBannerListener.onLeaveApplication();
    	}
    }

    @Override
    public void onShowAdScreen(IMAdView adView) {
        Log.d("MoPub", "InMobi banner ad clicked.");
    	if(mBannerListener != null) {
    		mBannerListener.onClick();
    	}
    }
}
