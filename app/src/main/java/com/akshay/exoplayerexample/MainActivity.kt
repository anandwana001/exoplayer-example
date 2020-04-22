package com.akshay.exoplayerexample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
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
    private val mp3Url = "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3"
    private val mp4Url =
        "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    private val dashUrl =
        "https://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0"
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
            val mediaSource = buildMediaSource()
            it.addListener(playbackStateListener)
            it.setPlayWhenReady(playWhenReady)
            it.seekTo(currentWindow, playbackPosition)
            it.prepare(mediaSource, false, false)
        }
    }

    private fun buildMediaSource(): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this, "sample_exoplayer")
        return ProgressiveMediaSource.Factory(
            dataSourceFactory
        ).createMediaSource(Uri.parse(mp3Url))
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
