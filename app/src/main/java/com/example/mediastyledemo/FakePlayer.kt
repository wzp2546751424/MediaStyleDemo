package com.example.mediastyledemo

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat


object FakePlayer {

    private var playing = false
    private var initialized = false
    private var currentTime = 0L
    private var lastStartTime = 0L

    private var session: MediaSessionCompat? = null

    val MAX_TIME = 2 * 60 * 1000L

    fun setSession(s: MediaSessionCompat?) {
        session = s
    }

    fun init() {
        initialized = true
    }

    fun play() {
        playing = true
        lastStartTime = System.currentTimeMillis()
        session?.let {
            val builder = FakeMusicService.currentService?.stateBuilder?.setState(PlaybackStateCompat.STATE_PLAYING, currentTime, 1f)
            if (builder != null) {
                it.setPlaybackState(builder.build())
            }
        }
    }

    fun isPlaying() = playing

    fun getCurrentTime() = currentTime

    fun stop() {
        playing = false
        currentTime = System.currentTimeMillis() - lastStartTime
        session?.let {
            val builder = FakeMusicService.currentService?.stateBuilder?.setState(PlaybackStateCompat.STATE_PAUSED, currentTime, 1f)
            if (builder != null) {
                it.setPlaybackState(builder.build())
            }
        }
    }

    fun seekTo(time: Long) {
        currentTime = time
    }

    fun getMeteData(): MediaMetadataCompat {
        val mediaMetaDataBuilder = MediaMetadataCompat.Builder()
        mediaMetaDataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, MAX_TIME)
        return mediaMetaDataBuilder.build()
    }


}