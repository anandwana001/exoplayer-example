package com.akshay.exoplayerexample.ui

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.akshay.exoplayerexample.databinding.BasicAudioPlayerWithDashMediaSourceBinding
import com.akshay.exoplayerexample.util.Constants
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.manifest.DashManifest
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_dash_media_source.*


/**
 * Created by akshaynandwana on
 * 24, April, 2020
 **/
class BasicVideoPlayerWithDashMediaSource : AppCompatActivity() {

    private var exoPlayer: ExoPlayer? = null
    private lateinit var exoPlayerListener: ExoPlayerListener

    private var playPauseState = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private var isFullScreen = false

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        BasicAudioPlayerWithDashMediaSourceBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        exoPlayerListener = ExoPlayerListener()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                val layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                viewBinding.basicAudioPlayerWithDashMediaSourcePlayerView.layoutParams =
                    layoutParams
                hideSystemUI()
                isFullScreen = true
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                val layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0
                )
                viewBinding.basicAudioPlayerWithDashMediaSourcePlayerView.layoutParams =
                    layoutParams
                showSystemUI()
                isFullScreen = false
            }
            Configuration.ORIENTATION_UNDEFINED -> {
                val layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0
                )
                viewBinding.basicAudioPlayerWithDashMediaSourcePlayerView.layoutParams =
                    layoutParams
                showSystemUI()
                isFullScreen = false
            }
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, mainContainer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            mainContainer
        ).show(WindowInsetsCompat.Type.systemBars())
        supportActionBar?.show()
    }

    private fun initializeExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.let {
            viewBinding.basicAudioPlayerWithDashMediaSourcePlayerView.player = exoPlayer
            it.addListener(exoPlayerListener)

            // pass dash file url
            val mediaSource = buildMediaSource(Constants.DASH_URL)
            it.setMediaSource(mediaSource)
            it.playWhenReady = playPauseState
            it.seekTo(currentWindow, playbackPosition)
            it.prepare()
        }
    }

    // creating a DashMediaSource
    private fun buildMediaSource(url: String): MediaSource {

        // using DefaultHttpDataSource for http data source
        val dataSourceFactory = DefaultHttpDataSource.Factory()

        // Set a custom authentication request header
        // dataSourceFactory.setDefaultRequestProperty("Header", "Value")

        // just-in-time behavior using a ResolvingDataSource

        // create DASH media source
        return DashMediaSource.Factory(dataSourceFactory)
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
                    val dashManifest = it as DashManifest
                    viewBinding.basicAudioPlayerWithDashMediaSourceTextView.text =
                        "availabilityStartTimeMs = ${dashManifest.availabilityStartTimeMs} \n durationMs = ${dashManifest.durationMs} \n dynamic = ${dashManifest.dynamic} \n location = ${dashManifest.location} \n minBufferTimeMs = ${dashManifest.minBufferTimeMs} \n minUpdatePeriodMs = ${dashManifest.minUpdatePeriodMs} \n programInformation = ${dashManifest.programInformation} \n publishTimeMs = ${dashManifest.publishTimeMs} \n suggestedPresentationDelayMs = ${dashManifest.suggestedPresentationDelayMs} \n timeShiftBufferDepthMs = ${dashManifest.timeShiftBufferDepthMs} \n utcTiming = ${dashManifest.utcTiming} \n periodCount = ${dashManifest.periodCount}"
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
