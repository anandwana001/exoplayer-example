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
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.manifest.DashManifest
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_dash_media_source.*

/**
 * Created by akshaynandwana on
 * 24, April, 2020
 **/
class BasicAudioPlayerWithDashMediaSource : AppCompatActivity() {

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private lateinit var exoPlayerListener: ExoPlayerListener

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_audio_player_with_dash_media_source)

        lifecycle.addObserver(ExoplayerObserver())
        exoPlayerListener = ExoPlayerListener()
    }

    private fun initializeExoPlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        simpleExoPlayer?.let {
            basic_audio_player_with_dash_media_source_player_view.player = simpleExoPlayer
            it.addListener(exoPlayerListener)

            // pass dash file url
            val mediaSource = buildMediaSource(Constants.DASH_URL)

            it.setPlayWhenReady(playWhenReady)
            it.seekTo(currentWindow, playbackPosition)
            it.prepare(mediaSource, false, false)
        }
    }

    // creating a DashMediaSource
    private fun buildMediaSource(url: String): MediaSource {

        // using DefaultHttpDataSourceFactory for http data source
        val dataSourceFactory =
            DefaultHttpDataSourceFactory(Util.getUserAgent(this, "exoplayer-example"))

        // Set a custom authentication request header
        // dataSourceFactory.setDefaultRequestProperty("Header", "Value")

        // just-in-time behavior using a ResolvingDataSource

        // create DASH media source
        return DashMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Constants.uriParser(url))
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
                    val dashManifest = it as DashManifest
                    basic_audio_player_with_dash_media_source_text_view.text =
                        "availabilityStartTimeMs = ${dashManifest.availabilityStartTimeMs} \n durationMs = ${dashManifest.durationMs} \n dynamic = ${dashManifest.dynamic} \n location = ${dashManifest.location} \n minBufferTimeMs = ${dashManifest.minBufferTimeMs} \n minUpdatePeriodMs = ${dashManifest.minUpdatePeriodMs} \n programInformation = ${dashManifest.programInformation} \n publishTimeMs = ${dashManifest.publishTimeMs} \n suggestedPresentationDelayMs = ${dashManifest.suggestedPresentationDelayMs} \n timeShiftBufferDepthMs = ${dashManifest.timeShiftBufferDepthMs} \n utcTiming = ${dashManifest.utcTiming} \n periodCount = ${dashManifest.periodCount}"
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
