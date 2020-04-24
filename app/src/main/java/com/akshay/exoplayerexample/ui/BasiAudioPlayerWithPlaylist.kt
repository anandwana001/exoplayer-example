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
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_playlist.*

/**
 * Created by akshaynandwana on
 * 24, April, 2020
 **/
class BasiAudioPlayerWithPlaylist : AppCompatActivity() {

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private lateinit var exoPlayerListener: ExoPlayerListener

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_audio_player_with_playlist)

        lifecycle.addObserver(ExoplayerObserver())
        exoPlayerListener = ExoPlayerListener()
    }

    private fun initializeExoPlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        simpleExoPlayer?.let {
            basic_audio_player_with_playlist_player_view.player = simpleExoPlayer
            it.addListener(exoPlayerListener)

            // pass multiple file url
            val mediaSource = buildMediaSource(Constants.MP3_URL, Constants.MP4_URL)

            it.setPlayWhenReady(playWhenReady)
            it.seekTo(currentWindow, playbackPosition)
            it.prepare(mediaSource, false, false)
        }
    }

    // creating a ConcatenatingMediaSource
    private fun buildMediaSource(url1: String, url2: String): MediaSource {

        val dataSourceFactory =
            DefaultHttpDataSourceFactory(Util.getUserAgent(this, "exoplayer-example"))

        val mediaSource1 = ProgressiveMediaSource.Factory(dataSourceFactory)
            .setTag("mediaSource1")
            .createMediaSource(Constants.uriParser(url1))

        val mediaSource2 = ProgressiveMediaSource.Factory(dataSourceFactory)
            .setTag("mediaSource2")
            .createMediaSource(Constants.uriParser(url2))

        // Heterogeneous Playlist
        return ConcatenatingMediaSource(mediaSource1, mediaSource2, mediaSource1, mediaSource2)
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

        override fun onPositionDiscontinuity(reason: Int) {
            basic_audio_player_with_playlist_text_view.text =
                "currentTag = ${simpleExoPlayer!!.currentTag}"
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
