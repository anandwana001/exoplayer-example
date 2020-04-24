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
Dynamic Adaptive Streaming over HTTP (DASH) so what is Adaptive Streaming first?
Also known as Adaptive Bitrate Streaming, (bitrate - speed of the internet connection) it is a technique to stream multimedia. What it does differently is it detect's the user bandwidth and machine capacity and adjust the media quality based on it.

![](https://upload.wikimedia.org/wikipedia/commons/4/4b/Adaptive_streaming_overview_daseddon_2011_07_28.png)
Encoder encodes the single media file in multiple bit rates and this result in less buffereing and fast start time.

**Let's understand the problem**
Earlier days we have something called progressive streaming. Let's understand this with an example.
Suppose we have a video file 1280*720px on our server. People with different screen resolution, sizes will now stream this video.
1. Screen with 1920*1080px, video is too small
2. Screen with 1280*720px, video will play ok
3. Screen with 854*480px, video is too large<br>

Second problem is **Buffering**
Video provider creates file according to the screen size. This solves the first problem. For Slow and Fast internet, adaptive video stream switch to lower file type which will give user no buffering situation in slow internet. Encoder broke the video in multiple segments of 2-4secs long, and at the end of a segment based on user bandwidth, segment can be switch to lower or upper quality.


**Dynamic Adaptive Streaming over HTTP also known as MPEG-DASH**, the media file broken into the multiple segments and are provided over HTTP. This uses bit rate adaptation (ABR) algorithm.<br>
There are `.mpd` files, MPEG-DASH Media Presentation Description, which consist of Period which has information of different view angles or with different codecs, audio components for different languages or with different types of information, subtitle or caption components and much more in deep.

## What is SmoothStreaming
