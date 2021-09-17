package de.salomax.ndx.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import de.salomax.ndx.data.model.Compensation
import de.salomax.ndx.data.model.IsoSteps
import de.salomax.ndx.data.model.ShutterSpeeds

abstract class SharedPreferenceLiveData<T>(val sharedPrefs: SharedPreferences,
                                           private val key: String,
                                           private val defValue: T) : LiveData<T>() {

   abstract fun getValueFromPreferences(key: String, defValue: T): T

   private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
      if (key == this.key) {
         value = getValueFromPreferences(key, defValue)
      }
   }

   override fun onActive() {
      super.onActive()
      value = getValueFromPreferences(key, defValue)
      sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
   }

   override fun onInactive() {
      sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
      super.onInactive()
   }
}


// Implementations

// Int
@Suppress("unused")
class SharedPreferenceIntLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Int) :
      SharedPreferenceLiveData<Int>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: Int): Int = sharedPrefs.getInt(key, defValue)
}

// String
@Suppress("unused")
class SharedPreferenceStringLiveData(sharedPrefs: SharedPreferences, key: String, defValue: String?) : SharedPreferenceLiveData<String?>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: String?): String? = sharedPrefs.getString(key, defValue)
}

// Boolean
@Suppress("unused")
class SharedPreferenceBooleanLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Boolean) :
      SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean = sharedPrefs.getBoolean(key, defValue)
}

// Float
@Suppress("unused")
class SharedPreferenceFloatLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Float) :
      SharedPreferenceLiveData<Float>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: Float): Float = sharedPrefs.getFloat(key, defValue)
}

// Long
@Suppress("unused")
class SharedPreferenceLongLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Long) :
      SharedPreferenceLiveData<Long>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: Long): Long = sharedPrefs.getLong(key, defValue)
}

// StringSet
@Suppress("unused")
class SharedPreferenceStringSetLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Set<String>?) :
      SharedPreferenceLiveData<Set<String>?>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: Set<String>?): Set<String>? = sharedPrefs.getStringSet(key, defValue)
}

// IsoSteps
@Suppress("unused")
class SharedPreferenceIsoStepsLiveData(sharedPrefs: SharedPreferences, key: String, defValue: IsoSteps) :
      SharedPreferenceLiveData<IsoSteps>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: IsoSteps): IsoSteps = when (sharedPrefs.getInt(key, 3)) {
      1 -> IsoSteps.FULL
      2 -> IsoSteps.HALF
      else -> IsoSteps.THIRD
   }
}

// ShutterSpeeds
@Suppress("unused")
class SharedPreferenceShutterSpeedsLiveData(sharedPrefs: SharedPreferences, key: String, defValue: ShutterSpeeds) :
      SharedPreferenceLiveData<ShutterSpeeds>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: ShutterSpeeds): ShutterSpeeds = when (sharedPrefs.getInt(key, 3)) {
      1 -> ShutterSpeeds.FULL
      2 -> ShutterSpeeds.HALF
      else -> ShutterSpeeds.THIRD
   }
}

// Compensation
@Suppress("unused")
class SharedPreferenceCompensationLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Compensation) :
      SharedPreferenceLiveData<Compensation>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: Compensation): Compensation = when (sharedPrefs.getInt(key, 3)) {
      1 -> Compensation.FULL
      2 -> Compensation.HALF
      else -> Compensation.THIRD
   }
}


// Kotlin

@Suppress("unused")
fun SharedPreferences.intLiveData(key: String, defValue: Int): SharedPreferenceLiveData<Int> {
   return SharedPreferenceIntLiveData(this, key, defValue)
}

@Suppress("unused")
fun SharedPreferences.stringLiveData(key: String, defValue: String?): SharedPreferenceLiveData<String?> {
   return SharedPreferenceStringLiveData(this, key, defValue)
}

@Suppress("unused")
fun SharedPreferences.booleanLiveData(key: String, defValue: Boolean): SharedPreferenceLiveData<Boolean> {
   return SharedPreferenceBooleanLiveData(this, key, defValue)
}

@Suppress("unused")
fun SharedPreferences.floatLiveData(key: String, defValue: Float): SharedPreferenceLiveData<Float> {
   return SharedPreferenceFloatLiveData(this, key, defValue)
}

@Suppress("unused")
fun SharedPreferences.longLiveData(key: String, defValue: Long): SharedPreferenceLiveData<Long> {
   return SharedPreferenceLongLiveData(this, key, defValue)
}

@Suppress("unused")
fun SharedPreferences.isoStepsLiveData(key: String, defValue: IsoSteps): SharedPreferenceLiveData<IsoSteps> {
   return SharedPreferenceIsoStepsLiveData(this, key, defValue)
}

@Suppress("unused")
fun SharedPreferences.shutterSpeedsLiveData(key: String, defValue: ShutterSpeeds): SharedPreferenceLiveData<ShutterSpeeds> {
   return SharedPreferenceShutterSpeedsLiveData(this, key, defValue)
}

@Suppress("unused")
fun SharedPreferences.compensationLiveData(key: String, defValue: Compensation): SharedPreferenceLiveData<Compensation> {
   return SharedPreferenceCompensationLiveData(this, key, defValue)
}
