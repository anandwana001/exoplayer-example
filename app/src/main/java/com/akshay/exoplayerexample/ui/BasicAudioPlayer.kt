package com.akshay.exoplayerexample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.akshay.exoplayerexample.R
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player.*

/**
 * Created by akshaynandwana on
 * 22, April, 2020
 **/

class BasicAudioPlayer : AppCompatActivity() {

    // STEP 1: create a SimpleExoPlayer instance
    private var simpleExoPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_audio_player)

        lifecycle.addObserver(ExoplayerObserver())
    }

    // STEP 2: initialize the ExoPlayer
    private fun initializeExoPlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        simpleExoPlayer?.let {
            basic_audio_player_player_view.player = simpleExoPlayer
            val mediaSource = buildMediaSource(Constants.MP3_URL)
            it.prepare(mediaSource, false, false)
        }
    }

    /**
     * Media Source List: ProgressiveMediaSource,
     * DashMediaSource, SsMediaSource, HlsMediaSource,
     * ConcatenatingMediaSource, ClippingMediaSource, LoopingMediaSource, MergingMediaSource
     */
    // STEP 3: creating a Media Source of our requirements
    private fun buildMediaSource(url: String): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this, "basic_audio_player")
        return ProgressiveMediaSource.Factory(
            dataSourceFactory
        ).createMediaSource(Constants.uriParser(url))
    }

    // STEP 4: release the player when not needed
    private fun releasePlayer() {
        simpleExoPlayer?.let {
            it.release()
            simpleExoPlayer = null
        }
    }

    // STEP 5: using OnLifecycleEvent annotations to work with activity lifecycle callbacks
    inner class ExoplayerObserver : LifecycleObserver {

        /**
         * Checking API level 24, as 24 and above android supports multi window.
         * We can modify our player on the basis of the activity lifecycle by initialising
         * or releasing the player. onResume and onPause.
         */
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            if ((Util.SDK_INT < 24 || simpleExoPlayer == null)) {
                initializeExoPlayer()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            if (Util.SDK_INT >= 24) {
                initializeExoPlayer()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            if (Util.SDK_INT >= 24) {
                releasePlayer()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            if (Util.SDK_INT < 24) {
                releasePlayer()
            }
        }
    }
}
