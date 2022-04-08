package com.akshay.exoplayerexample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akshay.exoplayerexample.databinding.BasicAudioPlayerWithHlsMediaSourceBinding
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsManifest
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_hls_media_source.*

/**
 * Created by akshaynandwana on
 * 24, April, 2020
 **/
class BasicAudioPlayerWithHLSMediaSource : AppCompatActivity() {

    // STEP 1: create a ExoPlayer instance
    private var exoPlayer: ExoPlayer? = null
    private lateinit var exoPlayerListener: ExoPlayerListener

    private var playPauseState = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        BasicAudioPlayerWithHlsMediaSourceBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        exoPlayerListener = ExoPlayerListener()
    }

    // STEP 2: initialize the ExoPlayer
    private fun initializeExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.let {
            viewBinding.basicAudioPlayerWithHlsMediaSourcePlayerView.player = exoPlayer
            val mediaSource = buildMediaSource(Constants.HLS_URL)
            it.setMediaSource(mediaSource)
            it.playWhenReady = playPauseState
            it.seekTo(currentWindow, playbackPosition)
            it.prepare()
        }
    }

    // creating a HLSMediaSource
    private fun buildMediaSource(url: String): MediaSource {

        // using DefaultHttpDataSourceFactory for http data source
        val dataSourceFactory = DefaultHttpDataSource.Factory()

        // Set a custom authentication request header
        // dataSourceFactory.setDefaultRequestProperty("Header", "Value")

        // setAllowChunklessPreparation()

        // create HLS Media Source
        return HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Constants.uriParser(url)))
    }

    // STEP 4: release the player when not needed
    private fun releasePlayer() {
        exoPlayer?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            playPauseState = this.playWhenReady
            release()
        }
        exoPlayer = null
    }

    inner class ExoPlayerListener : Player.Listener {
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {

            exoPlayer?.let {
                val manifest = it.currentManifest
                manifest?.let {
                    val hlsManifest = it as HlsManifest
                    basic_audio_player_with_hls_media_source_text_view.text =
                        "masterPlaylist = ${hlsManifest.masterPlaylist} \n mediaPlaylist = ${hlsManifest.mediaPlaylist} "
                }
            }
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
