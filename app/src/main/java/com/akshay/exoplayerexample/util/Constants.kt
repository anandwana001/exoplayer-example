package com.akshay.exoplayerexample.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.annotation.DrawableRes
import com.akshay.exoplayerexample.R
import com.akshay.exoplayerexample.data.Media


/**
 * Created by akshaynandwana on
 * 22, April, 2020
 **/
object Constants {

    const val MP3_URL =
        "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3"
    const val MP4_URL =
        "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    const val M4A_URL =
        "https://dl.espressif.com/dl/audio/ff-16b-2c-44100hz.m4a"
    const val OGG_URL =
        "https://storage.googleapis.com/exoplayer-test-media-1/ogg/play.ogg"
    const val WAV_URL =
        "https://dl.espressif.com/dl/audio/ff-16b-1c-44100hz.wav"
    const val AAC_URl =
        "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/gear0/fileSequence0.aac"
    const val FLAC_URL =
        "https://storage.googleapis.com/exoplayer-test-media-1/flac/play.flac"
    const val AMR_URL =
        "https://dl.espressif.com/dl/audio/ff-16b-1c-8000hz.amr"
    const val WEBM_URL =
        "https://storage.googleapis.com/exoplayer-test-media-1/gen-3/screens/dash-vod-single-segment/video-vp9-360.webm"
    const val MKV_URL =
        "https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv"
    const val FLV_URL =
        "https://vod.leasewebcdn.com/bbb.flv?ri=1024&rs=150&start=0"
    const val DASH_URL =
        "https://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0"
    const val SMOOTH_STREAM_URL =
        "https://playready.directtaps.net/smoothstreaming/SSWSS720H264/SuperSpeedway_720.ism/Manifest"
    const val HLS_URL =
        "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"
    const val MP4_WITH_SUBTITLE_URL =
        "http://www.storiesinflight.com/js_videosub/jellies.mp4"
    const val SUBTITLE_URL =
        "http://www.storiesinflight.com/js_videosub/jellies.srt"

    val MP3_SAMPLE_PLAYLIST = arrayOf(
        Media(
            uriParser("https://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3"),
            "audio_1",
            "Jazz in Paris",
            "Jazz for the masses",
            R.drawable.goku
        ),
        Media(
            uriParser("https://storage.googleapis.com/automotive-media/The_Messenger.mp3"),
            "audio_2",
            "The messenger",
            "Hipster guide to London",
            R.drawable.goku_second
        ),
        Media(
            uriParser("https://storage.googleapis.com/automotive-media/Talkies.mp3"),
            "audio_3",
            "Talkies",
            "If it talks like a duck and walks like a duck.",
            R.drawable.goku_third
        )
    )

    const val PLAYBACK_CHANNEL_ID = "playback_channel"
    const val PLAYBACK_NOTIFICATION_ID: Int = 1

    fun uriParser(url: String): Uri {
        return Uri.parse(url)
    }

    fun getBitmap(context: Context, @DrawableRes bitmapResource: Int): Bitmap? {
        return (context.getResources().getDrawable(bitmapResource) as BitmapDrawable).bitmap
    }
}