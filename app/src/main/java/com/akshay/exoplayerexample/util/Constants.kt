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
    val DASH_URL =
        "https://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0"

    fun uriParser(url: String): Uri {
        return Uri.parse(url)
    }
}