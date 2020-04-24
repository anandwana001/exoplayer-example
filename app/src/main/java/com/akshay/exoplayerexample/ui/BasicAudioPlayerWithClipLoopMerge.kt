package com.akshay.exoplayerexample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.akshay.exoplayerexample.R
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_clip_loop_merge.*

/**
 * Created by akshaynandwana on
 * 25, April, 2020
 **/
class BasicAudioPlayerWithClipLoopMerge : AppCompatActivity() {

    private var simpleExoPlayer: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_audio_player_with_clip_loop_merge)

        lifecycle.addObserver(ExoplayerObserver())
    }

    private fun initializeExoPlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        simpleExoPlayer?.let {
            basic_audio_player_with_clip_loop_merge_player_view.player = simpleExoPlayer

            // pass multiple file url
            val mediaSource = buildMediaSource(Constants.MP4_URL, Constants.MP4_WITH_SUBTITLE_URL)

            it.setPlayWhenReady(playWhenReady)
            it.seekTo(currentWindow, playbackPosition)
            it.prepare(mediaSource, false, false)
        }
    }

    // creating a ConcatenatingMediaSource
    private fun buildMediaSource(videoUrl: String, url: String): MediaSource {

        val dataSourceFactory =
            DefaultHttpDataSourceFactory(Util.getUserAgent(this, "exoplayer-example"))

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .setTag("mediaSource1")
            .createMediaSource(Constants.uriParser(url))

        val mediaSourceVideo = ProgressiveMediaSource.Factory(dataSourceFactory)
            .setTag("mediaSource2")
            .createMediaSource(Constants.uriParser(videoUrl))

        // clip media in microseconds
        val clippingMediaSource = ClippingMediaSource(mediaSourceVideo, 120000000, 150000000)

        // loop media
        val loopingMediaSource = LoopingMediaSource(clippingMediaSource, 2)

        // create subtitle format
        val subtitleFormat = Format.createTextSampleFormat(
            "subtile-id",
            MimeTypes.APPLICATION_SUBRIP,
            C.SELECTION_FLAG_DEFAULT,
            null
        )
        val subtitleSource = SingleSampleMediaSource.Factory(dataSourceFactory)
            .createMediaSource(
                Constants.uriParser(Constants.SUBTITLE_URL),
                subtitleFormat,
                C.TIME_UNSET
            )

        val mergingMediaSource = MergingMediaSource(mediaSource, subtitleSource)

        return ConcatenatingMediaSource(loopingMediaSource, mergingMediaSource)
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
