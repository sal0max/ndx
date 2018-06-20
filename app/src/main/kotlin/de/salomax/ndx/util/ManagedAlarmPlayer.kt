package de.salomax.ndx.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer

@Suppress("unused")
class ManagedAlarmPlayer private constructor(val context: Context) {

    companion object : SingletonHolder<ManagedAlarmPlayer, Context>(::ManagedAlarmPlayer)

    private var alarmPlayer: MediaPlayer? = null

    var isPlaying = false
        private set

    fun play(resId: Int) {
        if (!isPlaying) {
            isPlaying = true
            alarmPlayer = MediaPlayer.create(
                    context,
                    resId,
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build(),
                    (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).generateAudioSessionId())
            alarmPlayer?.start()
        }
    }

    fun stop() {
        if (isPlaying) {
            alarmPlayer?.stop()
            alarmPlayer?.release()
            alarmPlayer = null
            isPlaying = false
        }
    }

}
