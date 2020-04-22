package com.akshay.exoplayerexample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    private lateinit var playbackStateListener: PlaybackStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycle.addObserver(ExoplayerObserver())
        playbackStateListener = PlaybackStateListener()
    }

    private fun initializeExoPlayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)
        simpleExoPlayer?.let {
            playerView.player = simpleExoPlayer
            val mediaSource = buildMediaSource(Constants.MP3_URL)
            it.addListener(playbackStateListener)
            it.setPlayWhenReady(playWhenReady)
            it.seekTo(currentWindow, playbackPosition)
            it.prepare(mediaSource, false, false)
        }
    }

    private fun buildMediaSource(url: String): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this, "sample_exoplayer")
        return ProgressiveMediaSource.Factory(
            dataSourceFactory
        ).createMediaSource(Uri.parse(url))
    }

    private fun hideSystemUi() {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun releasePlayer() {
        simpleExoPlayer?.let {
            it.removeListener(playbackStateListener)
            playWhenReady = it.getPlayWhenReady()
            playbackPosition = it.getCurrentPosition()
            currentWindow = it.getCurrentWindowIndex()
            it.release()
            simpleExoPlayer = null
        }
    }

    private class PlaybackStateListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            lateinit var exoPlayerState: String
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                    exoPlayerState = "STATE_IDLE"
                }
                ExoPlayer.STATE_BUFFERING -> {
                    exoPlayerState = "STATE_BUFFERING"
                }
                ExoPlayer.STATE_READY -> {
                    exoPlayerState = "STATE_READY"
                }
                ExoPlayer.STATE_ENDED -> {
                    exoPlayerState = "STATE_ENDED"
                }
                else -> {
                    exoPlayerState = "UNKNOWN"
                }
            }
            Log.d(
                "ExoPlayerLog",
                "changed state to ${exoPlayerState} playWhenReady: ${playWhenReady}"
            )
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
            if (Util.SDK_INT >= 24) {
                releasePlayer()
            }
        }

    }

}
