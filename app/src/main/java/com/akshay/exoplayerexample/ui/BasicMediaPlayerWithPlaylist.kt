package com.akshay.exoplayerexample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akshay.exoplayerexample.R
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_playlist.*

/**
 * Created by akshaynandwana on
 * 24, April, 2020
 **/
class BasicMediaPlayerWithPlaylist : AppCompatActivity() {

    // STEP 1: create a ExoPlayer instance
    private var exoPlayer: ExoPlayer? = null
    private lateinit var exoPlayerListener: ExoPlayerListener

    private var playPauseState = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_audio_player_with_playlist)

        exoPlayerListener = ExoPlayerListener()
    }

    // STEP 2: initialize the ExoPlayer
    private fun initializeExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.let {
            it.addListener(exoPlayerListener)
            basic_audio_player_with_playlist_player_view.player = exoPlayer
            // pass multiple file url
            val mediaSource = buildMediaSource(Constants.MP3_URL, Constants.MP4_URL)
            it.setMediaSource(mediaSource)
            it.playWhenReady = playPauseState
            it.seekTo(currentWindow, playbackPosition)
            it.prepare()
        }
    }

    // creating a ConcatenatingMediaSource
    private fun buildMediaSource(url1: String, url2: String): MediaSource {

        val dataSourceFactory = DefaultHttpDataSource.Factory()

        val mediaSource1 = ProgressiveMediaSource.Factory(dataSourceFactory)

            .createMediaSource(MediaItem.fromUri(Constants.uriParser(url1)))

        val mediaSource2 = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Constants.uriParser(url2)))

        // Heterogeneous Playlist
        return ConcatenatingMediaSource(mediaSource1, mediaSource2, mediaSource1, mediaSource2)
    }

    // STEP 4: release the player when not needed
    private fun releasePlayer() {
        exoPlayer?.run {
            removeListener(exoPlayerListener)
            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            playPauseState = this.playWhenReady
            release()
        }
        exoPlayer = null
    }


    inner class ExoPlayerListener : Player.Listener {

        override fun onPositionDiscontinuity(reason: Int) {
            basic_audio_player_with_playlist_text_view.text =
                "reason = ${reason}"
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
