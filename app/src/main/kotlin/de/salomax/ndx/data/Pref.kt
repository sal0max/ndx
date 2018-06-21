package de.salomax.ndx.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "prefs")
data class Pref(@PrimaryKey @ColumnInfo(name = "KEY") val key: String,
                @ColumnInfo(name = "VALUE") val value: String) {

    companion object {
        const val EV_STEPS = "EV_STEPS"
        const val FILTER_SORT_ORDER = "FILTER_SORT_ORDER"
        const val ALARM_BEEP = "ALARM_BEEP"
        const val ALARM_VIBRATE = "ALARM_VIBRATE"
        const val SHOW_WARNING = "SHOW_WARNING"
    }
}
