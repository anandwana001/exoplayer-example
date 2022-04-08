package com.akshay.exoplayerexample.util

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.akshay.exoplayerexample.R
import com.akshay.exoplayerexample.ui.BasicAudioPlayerWithNotification
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource


/**
 * Created by akshaynandwana on
 * 25, April, 2020
 **/
class AudioPlayerService : Service(), Player.Listener {

    private var exoPlayer: ExoPlayer? = null

    private var songTitleLiveData = MutableLiveData<String>()
    private var songDescriptionLiveData = MutableLiveData<String>()
    private var songIconLiveData = MutableLiveData<Int>()

    /**
     * ui part
     * notification cancel when null player pass or dismiss by user
     * after player release, player in manager must be set to null
     * action
     * prev and next
     * prev and next in compact view (lock screen)
     * play and pause
     * stop
     * rewind increment
     * fast forward increment
     * notification icon
     */
    private var playerNotificationManager: PlayerNotificationManager? = null

    private var binder = AudioPlayerServiceBinder()

    override fun onCreate() {
        super.onCreate()

        startPlayer()
    }

    private fun startPlayer() {
        val context = this

        exoPlayer = ExoPlayer.Builder(context).build()

        val defaultDataSourceFactory = DefaultHttpDataSource.Factory()

        val concatenatingMediaSource = ConcatenatingMediaSource()

        for (sample in Constants.MP3_SAMPLE_PLAYLIST) {
            val progressiveMediaSource =
                ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(sample.uri))
            concatenatingMediaSource.addMediaSource(progressiveMediaSource)
        }

        exoPlayer?.let {
            it.addListener(this)
            it.setMediaSource(concatenatingMediaSource)
            it.prepare()
            it.playWhenReady = true
        }

        setupNotification(context)
    }

    private fun setupNotification(context: AudioPlayerService) {
        playerNotificationManager = PlayerNotificationManager.Builder(
            context,
            Constants.PLAYBACK_NOTIFICATION_ID, Constants.PLAYBACK_CHANNEL_ID
        ).setChannelNameResourceId(R.string.playback_channel_name)
            .setChannelDescriptionResourceId(R.string.playback_channel_description)
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val intent = Intent(context, BasicAudioPlayerWithNotification::class.java)
                    return PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                override fun getCurrentContentText(player: Player): CharSequence {
                    songDescriptionLiveData.postValue(Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].description)
                    return Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].description
                }

                override fun getCurrentContentTitle(player: Player): CharSequence {
                    songTitleLiveData.postValue(Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].title)
                    return Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].title
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    songIconLiveData.postValue(Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].bitmapResource)
                    return Constants.getBitmap(
                        context,
                        Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].bitmapResource
                    )
                }
            })
            .setNotificationListener(playerNotificationListener)
            .build()
        playerNotificationManager?.setPlayer(exoPlayer)
    }

    private var playerNotificationListener: PlayerNotificationManager.NotificationListener =
        object : PlayerNotificationManager.NotificationListener {

            override fun onNotificationCancelled(
                notificationId: Int,
                dismissedByUser: Boolean
            ) {
                stopSelf()
            }

            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                if (ongoing) {
                    startForeground(notificationId, notification)
                } else {
                    stopForeground(false)
                }

            }
        }

    override fun onDestroy() {
        exoPlayer?.let {
            playerNotificationManager?.let {
                it.setPlayer(null)
                playerNotificationManager = null
            }
            it.removeListener(this)
            it.release()
            exoPlayer = null
        }
        super.onDestroy()
    }

    inner class AudioPlayerServiceBinder : Binder() {

        fun getSimpleExoPlayerInstance() = exoPlayer

        fun getTitleLiveData() = songTitleLiveData
        fun getDescriptionLiveData() = songDescriptionLiveData
        fun getIconLiveData() = songIconLiveData

    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}