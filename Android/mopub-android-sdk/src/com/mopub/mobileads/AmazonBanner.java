package com.mopub.mobileads;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;

/*
 * Tested with Amazon SDK 4.0.8
 */
public class AmazonBanner extends CustomEventBanner implements AdListener {
    private CustomEventBanner.Listener mBannerListener;
    private AdLayout mAmazonAdView;
    
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
            mBannerListener.onAdFailed();
            return;
        }
        
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        AdLayout.AdSize adSize = AdLayout.AdSize.AD_SIZE_320x50;
        if(widthPixels >= 600) adSize = AdLayout.AdSize.AD_SIZE_600x90; 	// Kindle Fire
        if(widthPixels == 728) adSize = AdLayout.AdSize.AD_SIZE_728x90;		// Only if it matches exactly, since 728 scrolls on 800px wide Kindle Fire HD 7"
        if(widthPixels >= 1024) adSize = AdLayout.AdSize.AD_SIZE_1024x50; 	// Kindle Fire HD
        
        String appId = serverExtras.get("app_id");
        if(appId == null) {
            Log.d("MoPub", "Amazon banner ad app_id is missing.");
            mBannerListener.onAdFailed();
            return;
        }
        AdRegistration.setAppKey(context, appId);
//        AdRegistration.enableTesting(context, true);
//        AdRegistration.enableLogging(context, true);
        
        mAmazonAdView = new AdLayout(activity, adSize);
        mAmazonAdView.setListener(this);
        mAmazonAdView.loadAd(new AdTargetingOptions()); // async task to retrieve an ad    
    }

    @Override
    public void onInvalidate() {
    	if(mAmazonAdView != null) {
    		mAmazonAdView.setListener(null);
    	}
    }

	@Override
	public void onAdCollapsed(AdLayout arg0) {
		Log.d("MoPub", "Amazon banner ad modal dismissed.");
	}

	@Override
	public void onAdExpanded(AdLayout arg0) {
		Log.d("MoPub", "Amazon banner ad clicked.");
	  	if(mBannerListener != null) {
	  		mBannerListener.onClick();
	  	}
	}

	@Override
	public void onAdFailedToLoad(AdLayout arg0, AdError arg1) {
		Log.d("MoPub", "Amazon banner ad failed to load.");
	  	if(mBannerListener != null) {
	  		mBannerListener.onAdFailed();
	  	}
	}

	@Override
	public void onAdLoaded(AdLayout arg0, AdProperties arg1) {
      if (mAmazonAdView != null && mBannerListener != null) {
    	  Log.d("MoPub", "Amazon banner ad loaded successfully. Showing ad...");
	      mBannerListener.onAdLoaded();
	      mBannerListener.setAdContentView(mAmazonAdView);
	  } else if (mBannerListener != null) {
	      mBannerListener.onAdFailed();
	  }
	}

	// NOTE: Amazon does not provide an event about leaving the application
	// If it did, we would call the following in it:
	//	mBannerListener.onLeaveApplication();
}
