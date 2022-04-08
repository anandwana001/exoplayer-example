package com.akshay.exoplayerexample.data

import android.net.Uri

/**
 * Created by akshaynandwana on
 * 25, April, 2020
 **/
data class Media(
    var uri: Uri,
    var mediaId: String,
    var title: String,
    var description: String,
    var bitmapResource: Int
)