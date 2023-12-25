package com.example.mediastyledemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle


class FakeMusicService : Service() {

    private val TAG = "FakeMusicService"
    private val CHANNEL_ID = "MyForegroundServiceChannel"
    private val NOTIFICATION_ID = 1

    private lateinit var mediaSession: MediaSessionCompat
    val stateBuilder = PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SEEK_TO)


    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, this.packageName + "testSession")
        currentService = this
        FakePlayer.init()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        Toast.makeText(this, "service created", Toast.LENGTH_SHORT).show()
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {

            override fun onPlay() {
                Log.d(TAG, "onPlay: ")
                FakePlayer.play()
                Toast.makeText(this@FakeMusicService, "on play", Toast.LENGTH_SHORT).show()
            }

            override fun onPause() {
                Log.d(TAG, "onPause: ")
                FakePlayer.stop()
                Toast.makeText(this@FakeMusicService, "onPause", Toast.LENGTH_SHORT).show()
            }

            override fun onStop() {
                FakePlayer.stop()
                Toast.makeText(this@FakeMusicService, "onStop", Toast.LENGTH_SHORT).show()
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                Log.d(TAG, "onSkipToNext: ")
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                Log.d(TAG, "onSkipToPrevious: ")
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                //FakePlayer.seekTo(pos)
            }

            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                Toast.makeText(this@FakeMusicService, "onMediaButtonEvent", Toast.LENGTH_SHORT).show()
                return super.onMediaButtonEvent(mediaButtonEvent)
            }

        })
        stateBuilder.setBufferedPosition(FakePlayer.MAX_TIME)
        mediaSession.isActive = true
        mediaSession.setMetadata(FakePlayer.getMeteData())
        FakePlayer.setSession(mediaSession)
        FakePlayer.play()
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val mediaStyle = MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0, 1, 2) // 设置在紧凑视图中显示的动作按钮
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)


        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Foreground Service")
            .setContentText("Your app is running in the foreground")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setLargeIcon(largeIcon)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(mediaStyle)
            .setOnlyAlertOnce(true) // 仅在第一次显示通知时触发声音、振动

        // 添加媒体控制按钮
        // 添加媒体控制按钮
        /*if (FakePlayer.isPlaying()) {
            builder.addAction(NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", null))
        } else {
            builder.addAction(NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", null))
        }
        builder.addAction(NotificationCompat.Action(android.R.drawable.ic_media_previous, "Previous", null))
        builder.addAction(NotificationCompat.Action(android.R.drawable.ic_media_next, "Next", null))*/
       // builder.setProgress(FakePlayer.MAX_TIME.toInt(),  FakePlayer.getCurrentTime().toInt(), false)

        return builder.build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    private fun stopMyself() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        FakePlayer.setSession(null)
        Toast.makeText(this, "service destroyed", Toast.LENGTH_SHORT).show()
    }

    companion object {

        @JvmStatic
        var currentService: FakeMusicService? = null

        @JvmStatic
        fun stopThisService() {
            currentService?.stopMyself()
            currentService = null
        }

    }

}