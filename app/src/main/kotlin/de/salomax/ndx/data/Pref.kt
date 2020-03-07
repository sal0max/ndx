package de.salomax.ndx.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prefs")
data class Pref(
        @PrimaryKey @ColumnInfo(name = "KEY") val key: String,
        @ColumnInfo(name = "VALUE") val value: String) {

    companion object {
        const val EV_STEPS = "EV_STEPS"
        const val FILTER_SORT_ORDER = "FILTER_SORT_ORDER"
        const val ALARM_BEEP = "ALARM_BEEP"
        const val THEME = "THEME"
        const val ALARM_VIBRATE = "ALARM_VIBRATE"
        const val SHOW_WARNING = "SHOW_WARNING"
        const val HAS_PREMIUM = "HAS_PREMIUM"
    }
}
