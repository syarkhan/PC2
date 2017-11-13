package com.example.sheryarkhan.projectcity.activities;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.*;

import com.example.sheryarkhan.projectcity.R;

import java.util.ArrayList;
import java.util.List;

public class SampleActivity extends AppCompatActivity {
    private NativeAd nativeAd;

    private LinearLayout  nativeAdContainer;
    private CardView  adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        AdSettings.addTestDevice("d799c50e3b4c9e58d4412f125770179b");
        showNativeAd();
    }


    private void showNativeAd() {
        nativeAd = new NativeAd(this, "2051713708390959_2051806708381659");
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                Log.d("AdError",error.toString());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d("AdLoaded",ad.toString());
                if (ad != nativeAd) {
                    return;
                }

                String titleForAd = nativeAd.getAdTitle();
                NativeAd.Image coverImage = nativeAd.getAdCoverImage();
                NativeAd.Image iconForAd = nativeAd.getAdIcon();
                String socialContextForAd = nativeAd.getAdSocialContext();
                String titleForAdButton = nativeAd.getAdCallToAction();
                String textForAdBody = nativeAd.getAdBody();
                NativeAd.Rating appRatingForAd = nativeAd.getAdStarRating();

                // Add the Ad view into the ad container.
                nativeAdContainer = (LinearLayout) findViewById(R.id.native_ad_container);
                LayoutInflater inflater = LayoutInflater.from(SampleActivity.this);
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                adView = (CardView) inflater.inflate(R.layout.fb_ad_layout, nativeAdContainer, false);
                nativeAdContainer.addView(adView);

                // Create native UI using the ad metadata.
                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdTitle());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdBody.setText(nativeAd.getAdBody());
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                // Download and display the cover image.
                nativeAdMedia.setNativeAd(nativeAd);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(SampleActivity.this, nativeAd, true);
                adChoicesContainer.addView(adChoicesView);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);
                nativeAd.registerViewForInteraction(nativeAdContainer,clickableViews);


            // Request an ad
            //nativeAd.loadAd();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d("AdClicked",ad.toString());
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d("AdImpression",ad.toString());
            }
        });

        // Request an ad
        nativeAd.loadAd();
    }
}
