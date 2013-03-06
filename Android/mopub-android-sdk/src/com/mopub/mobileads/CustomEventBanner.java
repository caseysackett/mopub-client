package com.mopub.mobileads;

import java.util.Map;

import android.content.Context;
import android.view.View;

/*
 * CustomEventBanner is a base class for custom events that support banners. By implementing
 * subclasses of CustomEventBanner, you can enable the MoPub SDK to natively support a wider
 * variety of third-party ad networks, or execute any of your application code on demand.
 * 
 * At runtime, the MoPub SDK will find and instantiate a CustomEventBanner subclass as needed
 * and invoke its loadAd() method.
 */
public abstract class CustomEventBanner {
    
    /*
     * When the MoPub SDK receives a response indicating it should load a custom event, it will send
     * this message to your custom event class. Your implementation of this method can either load
     * a banner ad from a third-party ad network, or execute any application code. It must also
     * notify the provided CustomEventBanner.Listener Object of certain lifecycle events.
     * 
     * The localExtras parameter is a Map containing additional custom data that is set within
     * your application by calling MoPubView.setLocalExtras(Map<String, Object>). Note that the
     * localExtras Map is a copy of the Map supplied to setLocalExtras().
     * 
     * The serverExtras parameter is a Map containing additional custom data configurable on the
     * MoPub website that you want to associate with a given custom event request. This data may be
     * used to pass dynamic information, such as publisher IDs, without changes in application code.
     */
    public abstract void loadAd(Context context,
            CustomEventBanner.Listener customEventBannerListener, Map<String, Object> localExtras,
            Map<String, String> serverExtras);
    
    /*
     * Called when a Custom Event is being invalidated or destroyed. Perform any final cleanup here.
     */
    public abstract void onInvalidate();
    
    public interface Listener {
        /*
         * Your custom event subclass must call this method when it successfully loads an ad.
         * Failure to do so will disrupt the mediation waterfall and cause future ad requests to
         * stall.
         */
        public abstract void onAdLoaded();
        
        /*
         * Your custom event subclass must call this method when it fails to load an ad.
         * Failure to do so will disrupt the mediation waterfall and cause future ad requests to
         * stall.
         */
        public abstract void onAdFailed();
        
        /*
         * Your custom event subclass should call this method when a user taps on a banner ad.
         * This method is optional.
         */
        public abstract void onClick();
        
        /*
         * Your custom event subclass should call this method if the ad will cause the user to leave
         * the application (e.g. for the Play Store or browser). This method is optional.
         */
        public abstract void onLeaveApplication();
        
        /*
         * Your custom event subclass should call this method when it successfully loads an ad
         * and needs to display the provided View. This method is optional.
         */
        public abstract void setAdContentView(View view);
    }
}
