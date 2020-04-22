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
import kotlinx.android.synthetic.main.handling_basic_audio_player.*

/**
 * Created by akshaynandwana on
 * 22, April, 2020
 **/

class HandlingBasicAudioPlayer: AppCompatActivity() {

    private var simpleExoPlayer: SimpleExoPlayer? = null

    // handle player with activity lifecycle
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.handling_basic_audio_player)

        lifecycle.addObserver(ExoplayerObserver())
    }

    private fun initializeExoPlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        simpleExoPlayer?.let {
            handling_basic_audio_player_player_view.player = simpleExoPlayer
            val mediaSource = buildMediaSource(Constants.MP3_URL)

            // playing as soon as the player is ready
            it.setPlayWhenReady(playWhenReady)

            // playing back from the same position where user left
            it.seekTo(currentWindow, playbackPosition)

            it.prepare(mediaSource, false, false)
        }
    }

    private fun buildMediaSource(url: String): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this, "basic_audio_player")
        return ProgressiveMediaSource.Factory(
            dataSourceFactory
        ).createMediaSource(Constants.uriParser(url))
    }

    private fun releasePlayer() {
        simpleExoPlayer?.let {
            playWhenReady = it.getPlayWhenReady()
            playbackPosition = it.getCurrentPosition()
            currentWindow = it.getCurrentWindowIndex()
            it.release()
            simpleExoPlayer = null
        }
    }

    inner class ExoplayerObserver : LifecycleObserver {

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
