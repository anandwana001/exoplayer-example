package com.akshay.exoplayerexample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akshay.exoplayerexample.databinding.BasicAudioPlayerWithProgressiveMediaSourceBinding
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util


/**
 * Created by akshaynandwana on
 * 24, April, 2020
 **/
class BasicAudioPlayerWithProgressiveMediaSource : AppCompatActivity() {

    // STEP 1: create a ExoPlayer instance
    private var exoPlayer: ExoPlayer? = null

    private var playPauseState = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        BasicAudioPlayerWithProgressiveMediaSourceBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }

    // STEP 2: initialize the ExoPlayer
    private fun initializeExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.let {
            viewBinding.basicAudioPlayerWithProgressiveMediaSourcePlayerView.player = exoPlayer
            // pass progressive file url (MP4, M4A, FMP4, WebM, Matroska, MP3, Ogg, WAV, MPEG-TS, MPEG-PS, FLV, AAC, FLAC, AMR)
            val mediaSource = buildMediaSource(Constants.MKV_URL)
            it.setMediaSource(mediaSource)
            it.playWhenReady = playPauseState
            it.seekTo(currentWindow, playbackPosition)
            it.prepare()
        }
    }

    // creating a ProgressiveMediaSource
    private fun buildMediaSource(url: String): MediaSource {

        // using DefaultHttpDataSourceFactory for http data source
        val dataSourceFactory = DefaultHttpDataSource.Factory()

        // Set a custom authentication request header
        // dataSourceFactory.setDefaultRequestProperty("Header", "Value")

        // .setMp4ExtractorFlags(Mp4Extractor.FLAG_WORKAROUND_IGNORE_EDIT_LISTS)
        // .setConstantBitrateSeekingEnabled(true)

        // create Progressive media source
        return ProgressiveMediaSource.Factory(dataSourceFactory)
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
