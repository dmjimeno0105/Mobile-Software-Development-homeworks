package edu.vt.cs3714.tutorial_05

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {
    companion object {
        const val MAIN_TRACK_COMPLETED = "music completed"
    }

    private var musicPlayer: MusicPlayer? = null

    // Binder given to clients (contains a reference to the service)
    private val iBinder = LocalBinder()

    // Class used for the client Binder.
    inner class LocalBinder : Binder() {
        // Return this instance of MusicService so clients can call public methods.
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    /**
     * Broadcast track completed
     */
    fun mainTrackCompleted() {
        val intent = Intent(MAIN_TRACK_COMPLETED)
        sendBroadcast(intent)
    }

    fun musicPlayer(): MusicPlayer? {
        return musicPlayer
    }

    fun updateSeeker1Progress(progress: Int) {
        musicPlayer?.seeker1Progress = progress
    }

    fun updateSeeker2Progress(progress: Int) {
        musicPlayer?.seeker2Progress = progress
    }

    fun updateSeeker3Progress(progress: Int) {
        musicPlayer?.seeker3Progress = progress
    }

    override fun onBind(intent: Intent?): IBinder {
        return iBinder
    }

    override fun onCreate() {
        super.onCreate()
        musicPlayer = MusicPlayer(this)
    }

    fun setupOverlappingSounds() {
        musicPlayer?.setupOverlappingSounds()
    }

    fun startOverlappingSound(track: Int) {
        musicPlayer?.playOverlappingSound(track)
    }

    fun pauseOverlappingSound(track: Int) {
        musicPlayer?.pauseOverlappingSound(track)
    }

    /**
     * Starts main track
     *
     * @return duration of track
     */
    fun startMainTrack(): Int? {
        return musicPlayer?.playMainTrack()
    }

    fun pauseMainTrack() {
        musicPlayer?.pauseMainTrack()
    }

    fun resumeMainTrack() {
        musicPlayer?.resumeMainTrack()
    }

    fun resetMainTrackAndOverlappingSounds() {
        musicPlayer?.resetMainTrack()
        musicPlayer?.resetOverlappingSounds()
    }

    fun getMainTrackStatus(): Int {
        return musicPlayer?.getMainTrackStatus() ?: -1
    }

    // the code below is needed to setup the notification for the MusicService
    private val CHANNEL_ID = "Music Service Channel ID"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        // The intent to open MainActivity
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            // Add extra to specify which fragment to load
            putExtra("navigateTo", "PlayingFragment")
        }

        // The PendingIntent to launch MainActivity with the above intent
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Music")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText("Hello World!")
            .setContentIntent(pendingIntent)
            .build()

        // Start the service in the foreground
        startForeground(123, notification)

        return START_STICKY
    }


    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID, "Music Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }
}