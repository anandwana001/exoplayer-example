package com.akshay.exoplayerexample

import android.net.Uri

/**
 * Created by akshaynandwana on
 * 25, April, 2020
 **/
data class Sample(
    var uri: Uri,
    var mediaId: String,
    var title: String,
    var description: String,
    var bitmapResource: Int
)