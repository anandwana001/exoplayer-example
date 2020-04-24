package com.akshay.exoplayerexample.util

import android.net.Uri

/**
 * Created by akshaynandwana on
 * 22, April, 2020
 **/
object Constants {

    val MP3_URL = "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3"
    val MP4_URL =
        "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    val M4A_URL =
        "https://dl.espressif.com/dl/audio/ff-16b-2c-44100hz.m4a"
    val OGG_URL =
        "https://storage.googleapis.com/exoplayer-test-media-1/ogg/play.ogg"
    val WAV_URL =
        "https://dl.espressif.com/dl/audio/ff-16b-1c-44100hz.wav"
    val AAC_URl =
        "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/gear0/fileSequence0.aac"
    val FLAC_URL =
        "https://storage.googleapis.com/exoplayer-test-media-1/flac/play.flac"
    val AMR_URL =
        "https://dl.espressif.com/dl/audio/ff-16b-1c-8000hz.amr"
    val WEBM_URL =
        "https://storage.googleapis.com/exoplayer-test-media-1/gen-3/screens/dash-vod-single-segment/video-vp9-360.webm"
    val MKV_URL =
        "https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv"
    val FLV_URL =
        "https://vod.leasewebcdn.com/bbb.flv?ri=1024&rs=150&start=0"
    val DASH_URL =
        "https://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0"
    val SMOOTH_STREAM_URL =
        "https://playready.directtaps.net/smoothstreaming/SSWSS720H264/SuperSpeedway_720.ism/Manifest"
    val HLS_URL =
        "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"

    fun uriParser(url: String): Uri {
        return Uri.parse(url)
    }
}