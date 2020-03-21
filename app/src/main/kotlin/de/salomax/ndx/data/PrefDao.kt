package de.salomax.ndx.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.lang.IllegalArgumentException

@Dao
interface PrefDao {

    @Query("SELECT * FROM prefs")
    fun getAll(): LiveData<List<Pref>>

    @Query("SELECT `VALUE` FROM prefs WHERE `KEY` = '${Pref.EV_STEPS}' LIMIT 1")
    @TypeConverters(ShutterSpeedsConverter::class)
    fun getEvSteps(): LiveData<ShutterSpeeds?>

    @Query("SELECT `VALUE` FROM prefs WHERE `KEY` = '${Pref.EV_STEPS}' LIMIT 1")
    @TypeConverters(IsoConverter::class)
    fun getIsoSteps(): LiveData<IsoSteps?>

    @Query("SELECT * FROM prefs WHERE `KEY` = '${Pref.ALARM_BEEP}' OR `KEY` = '${Pref.ALARM_VIBRATE}'")
    fun getTimerAlarms(): LiveData<List<Pref>?>

    @Query("SELECT `VALUE` FROM prefs WHERE `KEY` = '${Pref.SHOW_WARNING}' LIMIT 1")
    @TypeConverters(BooleanConverter::class)
    fun isWarningEnabled(): LiveData<Boolean?>

    @Query("SELECT `VALUE` FROM prefs WHERE `KEY` = '${Pref.HAS_PREMIUM}' LIMIT 1")
    @TypeConverters(BooleanConverter::class)
    fun hasPremium(): LiveData<Boolean?>

    @Query("UPDATE prefs SET `VALUE` = 1 WHERE `KEY` = '${Pref.HAS_PREMIUM}'")
    @TypeConverters(BooleanConverter::class)
    fun enablePremium()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pref: Pref)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(prefs: List<Pref>)

    @Query("DELETE FROM prefs")
    fun deleteAll()
}

class ShutterSpeedsConverter {
    @TypeConverter
    fun toShutterSpeed(s: String): ShutterSpeeds {
        return when (s) {
            "1" -> ShutterSpeeds.FULL
            "2" -> ShutterSpeeds.HALF
            else -> ShutterSpeeds.THIRD
        }
    }

    @TypeConverter
    fun fromShutterSpeed(s: ShutterSpeeds): String {
        return when (s) {
            ShutterSpeeds.FULL -> "1"
            ShutterSpeeds.HALF -> "2"
            else -> "3"
        }
    }
}

class IsoConverter {
    @TypeConverter
    fun toShutterSpeed(s: String): IsoSteps {
        return when (s) {
            "1" -> IsoSteps.FULL
            "2" -> IsoSteps.HALF
            else -> IsoSteps.THIRD
        }
    }

    @TypeConverter
    fun fromShutterSpeed(s: IsoSteps): String {
        return when (s) {
            IsoSteps.FULL -> "1"
            IsoSteps.HALF -> "2"
            else -> "3"
        }
    }
}

class BooleanConverter {
    @TypeConverter
    fun toBoolean(s: String): Boolean {
        return when (s) {
            "0" -> false
            "1" -> true
            else -> throw IllegalArgumentException()
        }
    }

    @TypeConverter
    fun fromBoolean(b: Boolean): String {
        return when (b) {
            false -> "0"
            true -> "1"
        }
    }
}