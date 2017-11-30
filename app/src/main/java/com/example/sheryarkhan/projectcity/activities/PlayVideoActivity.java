package com.example.sheryarkhan.projectcity.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sheryarkhan.projectcity.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PlayVideoActivity extends AppCompatActivity {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private ComponentListener componentListener;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton backBtn;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private String Url;
    private PlaybackControlView playbackControlView;
    // private Uri videoUri;
    private OnSuccessListener onSuccessListener;
    private boolean actionBar = true;
    private Toolbar toolbar;
    private Boolean isToolBar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        playerView = (SimpleExoPlayerView)findViewById(R.id.video_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        playButton = (ImageButton) findViewById(R.id.exo_play);
        pauseButton = (ImageButton) findViewById(R.id.exo_pause);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        componentListener = new ComponentListener();
        Intent i = getIntent();
        Url = i.getStringExtra("url");
        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#7c8287"),android.graphics.PorterDuff.Mode.SRC_ATOP);
        progressBar.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.video_toolbar);
        playerView.setControllerShowTimeoutMs(4000);


    }


   /* @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int eventAction = ev.getAction();
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                if(isToolBar){
                    toolbar.setVisibility(View.GONE);
                    isToolBar = false;
                   // playbackControlView.hide();
                }else {
                    toolbar.setVisibility(View.VISIBLE);
                    isToolBar = true;
                  //  playbackControlView.show();

                }

                break;
        }

        return true;
    }*/

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        /*  int eventAction = event.getAction();
//        switch (eventAction) {
//            case MotionEvent.ACTION_DOWN:*/
//        showAndHideToolbar();
//                /*if(isToolBar) {
//                    hideToolbarAfterSomeSec(playerView.getControllerShowTimeoutMs());
//                }*/
//            /*    break;
//        }*/
//        return true;
//    }

     /*   @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId() == R.id.play_video_image){
            Toast.makeText(this, "Toolbar Gone", Toast.LENGTH_SHORT).show();
        }
        return true;
    }*/

//    private void showAndHideToolbar(){
//
//        if(isToolBar){
//            //playerView.hideController();
//
//            toolbar.setVisibility(View.GONE);
//            isToolBar = false;
//            // playbackControlView.hide();
//
//
//        }else {
//            toolbar.setVisibility(View.VISIBLE);
//            isToolBar = true;
//        }
//    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart","onStartCalled");
        if(Util.SDK_INT > 23){
            initializePlayer();
            playVideo();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume","onResumeCalled");
        //Log.d("onResume1",player.toString());
        //hideSystemUi();
        if((Util.SDK_INT <= 23 || player == null)){
            initializePlayer();

        }
        playVideo();
        Log.d("onResume2",player.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause1",player.toString());
        if(Util.SDK_INT <= 23){
            releasePlayer();
        }
        //Log.d("onPause2",player.toString());
        Log.d("onPause","onPauseCalled");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
        Log.d("onStop","onStopCalled");
    }

    private void initializePlayer(){

        if(player == null) {
            //TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
            player.addListener(componentListener);
            player.setVideoDebugListener(componentListener);
            player.setAudioDebugListener(componentListener);
            playerView.setPlayer(player);
            player.setPlayWhenReady(true);

            player.seekTo(currentWindow, playbackPosition);
        }
    }

    private void playVideo()
    {
        StorageReference filePath = storageReference.child("images").child(Url);
//
//
//        filePath.getDownloadUrl(); .addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                // videoUri = uri;
//                MediaSource mediaSource = buildMediaSource(uri);
//                Log.d("mediasource1",mediaSource.toString());
//                player.prepare(mediaSource, true, false);
//                //videosViewPager.start();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("errorv",e.toString());
//                Toast.makeText(PlayVideoActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
//            }
//        });

        onSuccessListener = new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // videoUri = uri;

                MediaSource mediaSource = buildMediaSource(uri);
                if(player!=null) {
                    Log.d("mediasource1", player.toString());
                    player.prepare(mediaSource, true, false);
                }
                //videosViewPager.start();
            }
        };

        filePath.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("errorv",e.toString());
                Toast.makeText(PlayVideoActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(onSuccessListener);
    }
    private MediaSource buildMediaSource(Uri uri) {


        // DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory("ua",BANDWIDTH_METER);
        //   DashChunkSource.Factory dashChunkSourceFactory =  new DefaultDashChunkSource.Factory(dataSourceFactory);
//
        Cache cache = new SimpleCache(getCacheDir(), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100));
        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory("ua");
        DataSource.Factory cacheDataSourceFactory = new CacheDataSourceFactory(cache,httpDataSourceFactory, 0);
        return new ExtractorMediaSource(uri,cacheDataSourceFactory,new DefaultExtractorsFactory(),null,null);


        // return new ExtractorMediaSource(uri,new DefaultHttpDataSourceFactory("ua"),new DefaultExtractorsFactory(),null,null);
// DashMediaSource(uri,dataSourceFactory,dashChunkSourceFactory,null,null);
    }
    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            player.setVideoListener(null);
            player.setVideoDebugListener(null);
            player.setAudioDebugListener(null);
            player.release();
            player = null;
            onSuccessListener=null;
        }
    }

//    public  void hideToolbarAfterSomeSec(int sec){
//
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//                toolbar.setVisibility(View.GONE);
//                isToolBar = false;
//            }
//        }, sec);
//
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private class ComponentListener implements ExoPlayer.EventListener, VideoRendererEventListener, AudioRendererEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    //toolbar.setVisibility(View.VISIBLE);
                    //progressBar.setVisibility(View.GONE);
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    progressBar.setVisibility(View.GONE);
                    //hideToolbarAfterSomeSec(playerView.getControllerShowTimeoutMs());
                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    progressBar.setVisibility(View.GONE);
                    player.seekTo(0);
                    player.setPlayWhenReady(false);
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d("ExoPlayerLifeCycle", "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady);
        }




        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }


        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onVideoEnabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onVideoInputFormatChanged(Format format) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {

        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioEnabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onAudioInputFormatChanged(Format format) {

        }

        @Override
        public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }

        @Override
        public void onAudioDisabled(DecoderCounters counters) {

        }
    }
}
