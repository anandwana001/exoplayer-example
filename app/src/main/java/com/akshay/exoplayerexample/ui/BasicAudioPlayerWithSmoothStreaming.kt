package com.akshay.exoplayerexample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.akshay.exoplayerexample.R
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_smooth_streaming.*

/**
 * Created by akshaynandwana on
 * 24, April, 2020
 **/
class BasicAudioPlayerWithSmoothStreaming : AppCompatActivity() {

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private lateinit var exoPlayerListener: ExoPlayerListener

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_audio_player_with_smooth_streaming)

        lifecycle.addObserver(ExoplayerObserver())
        exoPlayerListener = ExoPlayerListener()
    }

    private fun initializeExoPlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        simpleExoPlayer?.let {
            basic_audio_player_with_smooth_streaming_player_view.player = simpleExoPlayer
            it.addListener(exoPlayerListener)

            // smooth stream url
            val mediaSource = buildMediaSource(Constants.SMOOTH_STREAM_URL)

            it.setPlayWhenReady(playWhenReady)
            it.seekTo(currentWindow, playbackPosition)
            it.prepare(mediaSource, false, false)
        }
    }

    private fun buildMediaSource(url: String): MediaSource {

        val dataSourceFactory =
            DefaultHttpDataSourceFactory(Util.getUserAgent(this, "exoplayer-example"))

        // smooth stream media source
        return SsMediaSource.Factory(dataSourceFactory).createMediaSource(Constants.uriParser(url))
    }

    private fun releasePlayer() {
        simpleExoPlayer?.let {
            it.removeListener(exoPlayerListener)
            playWhenReady = it.getPlayWhenReady()
            playbackPosition = it.getCurrentPosition()
            currentWindow = it.getCurrentWindowIndex()
            it.release()
            simpleExoPlayer = null
        }
    }

    inner class ExoPlayerListener : Player.EventListener {
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            simpleExoPlayer?.let {
                val manifest = it.currentManifest
                manifest?.let {
                    val smoothStreamManifest = it as SsManifest
                    basic_audio_player_with_smooth_streaming_text_view.text =
                        "durationUs = ${smoothStreamManifest.durationUs} \n dvrWindowLengthUs = ${smoothStreamManifest.dvrWindowLengthUs} \n isLive = ${smoothStreamManifest.isLive} \n lookAheadCount = ${smoothStreamManifest.lookAheadCount} \n majorVersion = ${smoothStreamManifest.majorVersion} \n minorVersion = ${smoothStreamManifest.minorVersion} \n protectionElement = ${smoothStreamManifest.protectionElement} \n streamElements = ${smoothStreamManifest.streamElements}"
                }
            }
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
