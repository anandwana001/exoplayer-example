package com.akshay.exoplayerexample.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.akshay.exoplayerexample.databinding.BasicAudioPlayerWithListenerBinding
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_listener.*

/**
 * Created by akshaynandwana on
 * 24, April, 2020
 **/
class BasicAudioPlayerWithListener : AppCompatActivity() {

    // STEP 1: create a ExoPlayer instance
    private var exoPlayer: ExoPlayer? = null

    // create listener instance
    private lateinit var exoPlayerListener: ExoPlayerListener
    private lateinit var eventLogger: EventLogger

    private var playPauseState = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        BasicAudioPlayerWithListenerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        // initialise listener
        exoPlayerListener = ExoPlayerListener()
        eventLogger = EventLogger(null)
    }

    // STEP 2: initialize the ExoPlayer
    private fun initializeExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.let {
            viewBinding.basicAudioPlayerWithListenerPlayerView.player = exoPlayer
            val mediaSource = buildMediaSource(Constants.MP3_URL)
            it.setMediaSource(mediaSource)
            it.playWhenReady = playPauseState
            it.seekTo(currentWindow, playbackPosition)

            // add listener
            it.addListener(exoPlayerListener)
            // add log listener
            it.addAnalyticsListener(eventLogger)
            it.prepare()
        }
    }

    private fun buildMediaSource(url: String): MediaSource {
        val dataSourceFactory = DefaultDataSource.Factory(this)
        return ProgressiveMediaSource.Factory(
            dataSourceFactory
        ).createMediaSource(MediaItem.fromUri(Constants.uriParser(url)))
    }

    // STEP 4: release the player when not needed
    private fun releasePlayer() {
        exoPlayer?.run {

            // remove listener
            this.removeListener(exoPlayerListener)

            // remove log listener
            this.removeAnalyticsListener(eventLogger)

            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            playPauseState = this.playWhenReady
            release()
        }
        exoPlayer = null
    }

    inner class ExoPlayerListener : Player.Listener {

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

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                    basic_audio_player_with_listener_progress_view_text_view.text = "STATE_IDLE"
                }
                ExoPlayer.STATE_BUFFERING -> {
                    basic_audio_player_with_listener_progress_view_text_view.text =
                        "STATE_BUFFERING"
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

    // STEP 5: using OnLifecycleEvent annotations to work with activity lifecycle callbacks

    /**
     * Checking API level 24, as 24 and above android supports multi window.
     * We can modify our player on the basis of the activity lifecycle by initialising
     * or releasing the player. onResume and onPause.
     */
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializeExoPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT < 24 || exoPlayer == null)) {
            initializeExoPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }
}
