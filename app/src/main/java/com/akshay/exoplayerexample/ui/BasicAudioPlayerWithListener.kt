package com.akshay.exoplayerexample.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.akshay.exoplayerexample.R
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_listener.*

/**
 * Created by akshaynandwana on
 * 24, April, 2020
 **/
class BasicAudioPlayerWithListener : AppCompatActivity() {

    private var simpleExoPlayer: SimpleExoPlayer? = null

    // create listener instance
    private lateinit var exoPlayerListener: ExoPlayerListener
    private lateinit var eventLogger: EventLogger

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_audio_player_with_listener)

        lifecycle.addObserver(ExoplayerObserver())

        // initialise listener
        exoPlayerListener = ExoPlayerListener()
        eventLogger = EventLogger(null)
    }

    private fun initializeExoPlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        simpleExoPlayer?.let {
            basic_audio_player_with_listener_player_view.player = simpleExoPlayer

            // add listener
            it.addListener(exoPlayerListener)

            // add log listener
            it.addAnalyticsListener(eventLogger)

            val mediaSource = buildMediaSource(Constants.MP3_URL)
            it.setPlayWhenReady(playWhenReady)
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

            // remove listener
            it.removeListener(exoPlayerListener)

            // remove log listener
            it.removeAnalyticsListener(eventLogger)

            playWhenReady = it.getPlayWhenReady()
            playbackPosition = it.getCurrentPosition()
            currentWindow = it.getCurrentWindowIndex()
            it.release()
            simpleExoPlayer = null
        }
    }

    inner class ExoPlayerListener : Player.EventListener {

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            super.onPlaybackParametersChanged(playbackParameters)
        }

        override fun onSeekProcessed() {
            super.onSeekProcessed()
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            super.onTracksChanged(trackGroups, trackSelections)
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            super.onLoadingChanged(isLoading)
        }

        override fun onPositionDiscontinuity(reason: Int) {
            super.onPositionDiscontinuity(reason)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
        }

        override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
            super.onPlaybackSuppressionReasonChanged(playbackSuppressionReason)
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
        }

        override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {
            super.onTimelineChanged(timeline, manifest, reason)
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                    basic_audio_player_with_listener_progress_view_text_view.text = "STATE_IDLE"
                }
                ExoPlayer.STATE_BUFFERING -> {
                    basic_audio_player_with_listener_progress_view_text_view.text = "STATE_BUFFERING"
                    basic_audio_player_with_listener_progress_view.visibility = View.VISIBLE
                }
                ExoPlayer.STATE_READY -> {
                    basic_audio_player_with_listener_progress_view_text_view.text = "STATE_READY"
                    basic_audio_player_with_listener_progress_view.visibility = View.INVISIBLE
                }
                ExoPlayer.STATE_ENDED -> {
                    basic_audio_player_with_listener_progress_view_text_view.text = "STATE_ENDED"
                }
                else -> {
                    basic_audio_player_with_listener_progress_view_text_view.text = "UNKNOWN"
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
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
