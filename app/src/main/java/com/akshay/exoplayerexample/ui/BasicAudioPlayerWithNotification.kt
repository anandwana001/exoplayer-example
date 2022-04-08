package com.akshay.exoplayerexample.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.akshay.exoplayerexample.R
import com.akshay.exoplayerexample.util.AudioPlayerService
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.basic_audio_player_with_notification.*

/**
 * Created by akshaynandwana on
 * 25, April, 2020
 **/
class BasicAudioPlayerWithNotification : AppCompatActivity() {

    private lateinit var intentService: Intent
    private lateinit var serviceBinder: AudioPlayerService.AudioPlayerServiceBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_audio_player_with_notification)

        intentService = Intent(this, AudioPlayerService::class.java)
        Util.startForegroundService(this, intentService)
    }

    private val serviceConnector =
        object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (service is AudioPlayerService.AudioPlayerServiceBinder) {
                    serviceBinder = service

                    basic_audio_player_with_notification_player_view.player =
                        serviceBinder.getSimpleExoPlayerInstance()

                    serviceBinder.getTitleLiveData()
                        .observe(this@BasicAudioPlayerWithNotification, Observer {
                            basic_audio_player_with_notification_text_view.text = it
                        })

                    serviceBinder.getIconLiveData()
                        .observe(this@BasicAudioPlayerWithNotification, Observer {

                            basic_audio_player_with_notification_text_view_image_view.setImageDrawable(
                                ContextCompat.getDrawable(this@BasicAudioPlayerWithNotification, it)
                            )
                        })
                }
            }
        }

    override fun onStart() {
        super.onStart()
        bindService(intentService, serviceConnector, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        unbindService(serviceConnector)
        super.onStop()
    }
}
