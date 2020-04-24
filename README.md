# ExoPlayer Example

ExoPlayer is an alternative to Android's MediaPlayer API for playing Video and Audio locally as well as over the internet.
In this repository I will be adding most of the ExoPlayer features and will compare with MediaPlayer API.
Built with Android Architecture Components. ExoPlayer support DASH and SmoothStreaming adaptive playbacks over Android’s MediaPlayer API.

## Advantages of ExoPlayer
 * Player Customization
 * Playlist Creation
 * Video Clipping
 * Video Looping
 * Video Subtitle
 * Support DASH (Dynamic Adaptive Streaming over HTTP)
 * SmoothStreaming with FMP4 container (Adaptive Bitrate Streaming)
 * Advance HLS (HTTP Live Streaming)
 * Widevine common encryption
 * Interactive Media Ads SDK

## Supported Device
 * FireOS (version 4 and earlier)
 * Nexus Player (only when using an HDMI to DVI cable)
 * Emulators (system image has an API level of at least 23)

## Player Components

### Types of Media Source
 * ProgressiveMediaSource - progressive media files
 * DashMediaSource - DASH
 * SsMediaSource - SmoothStreaming
 * HlsMediaSource - HLS
 * SingleSampleMediaSource - loading single media sample, side-loaded subtitle files
 * MergingMediaSource - merge multiple media source, playlist
 * LoopingMediaSource - loop
 * ClippingMediaSource - clip
 * ConcatenatingMediaSource - concat

### Types of Renderers
* MediaCodecVideoRenderer
* MediaCodecAudioRenderer
* TextRenderer
* MetadataRenderer

### Type of TrackSelector
* DefaultTrackSelector

### Type of LoadControl
* DefaultLoadControl

### Threading Model
![](https://exoplayer.dev/doc/reference/com/google/android/exoplayer2/doc-files/exoplayer-threading-model.svg)

Application thread for ExoPlayer instance must be use. Needed for ExoPlayer's UI components or the IMA extension. Looper can be accessed using `Player.getApplicationLooper()`. Listener also work on same thread. Internal thread for playback.

## Player States
 * ExoPlayer.STATE_IDLE
 * ExoPlayer.STATE_BUFFERING
 * ExoPlayer.STATE_READY
 * ExoPlayer.STATE_ENDED

## EventLogger
 * `EventLogger:`**`state`**`[eventTime=0.10, mediaPos=0.00, window=0, true, IDLE]`<br>
 * `EventLogger:`**`seekStarted`**`[eventTime=0.10, mediaPos=0.00, window=0]`<br>
 * `EventLogger:`**`positionDiscontinuity`**`[eventTime=0.10, mediaPos=0.00, window=0, SEEK]`<br>
 * `EventLogger:`**`timeline`**`[eventTime=0.16, mediaPos=0.00, window=0, periodCount=1, windowCount=1, reason=PREPARED
    period [?]
    window [?, false, false]
]`<br>
 * `EventLogger:`**`seekProcessed`**`[eventTime=0.16, mediaPos=0.00, window=0]`<br>
 * `EventLogger:`**`surfaceSize`**`[eventTime=0.25, mediaPos=0.00, window=0, 1080, 792]`<br>
 * `EventLogger:`**`loading`**`[eventTime=0.26, mediaPos=0.00, window=0, period=0, true]`<br>
 * `EventLogger:`**`tracks`**`[eventTime=2.22, mediaPos=0.00, window=0, period=0, []]`<br>
 * `EventLogger:`**`isPlaying`**`[eventTime=2.37, mediaPos=0.00, window=0, period=0, true]`<br>

## How does ExoPlayer works under the hood


## What is DASH


## What is SmoothStreaming
