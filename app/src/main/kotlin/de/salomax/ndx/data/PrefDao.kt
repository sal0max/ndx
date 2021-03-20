package de.salomax.ndx.data

import android.content.Context
import androidx.preference.PreferenceManager
import de.salomax.ndx.data.model.Compensation
import de.salomax.ndx.data.model.IsoSteps
import de.salomax.ndx.data.model.ShutterSpeeds

class PrefDao private constructor(context: Context) {

   companion object {
      private var INSTANCE: PrefDao? = null

      fun getInstance(context: Context): PrefDao {
         if (INSTANCE == null) {
            synchronized(PrefDao::class) {
               INSTANCE = PrefDao(context)
            }
         }
         return INSTANCE!!
      }
   }

   private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

   /*
    * ev steps
    */

   fun getIsoSteps(): SharedPreferenceLiveData<IsoSteps> {
      return sharedPreferences.isoStepsLiveData(Pref.EV_STEPS, IsoSteps.THIRD)
   }

   fun getEvSteps(): SharedPreferenceLiveData<ShutterSpeeds> {
      return sharedPreferences.shutterSpeedsLiveData(Pref.EV_STEPS, ShutterSpeeds.THIRD)
   }

   fun getCompensationSteps(): SharedPreferenceLiveData<Compensation> {
      return sharedPreferences.compensationLiveData(Pref.EV_STEPS, Compensation.THIRD)
   }

   fun setEvSteps(evSteps: Int) {
      sharedPreferences.edit().putInt(Pref.EV_STEPS, evSteps).apply()
   }

   /*
    * filter sort order
    */

   fun getFilterSortOrder(): SharedPreferenceLiveData<Int> {
      return sharedPreferences.intLiveData(Pref.FILTER_SORT_ORDER, 0)
   }

   fun setFilterSortOrder(sortOrder: Int) {
      sharedPreferences.edit().putInt(Pref.FILTER_SORT_ORDER, sortOrder).apply()
   }

   /*
    * alarm
    */

   fun shouldAlarmBeep(): SharedPreferenceLiveData<Boolean> {
      return sharedPreferences.booleanLiveData(Pref.ALARM_BEEP, false)
   }

   fun shouldAlarmBeepSync(): Boolean {
      return sharedPreferences.getBoolean(Pref.ALARM_BEEP, false)
   }

   fun setAlarmBeep(enabled: Boolean) {
      sharedPreferences.edit().putBoolean(Pref.ALARM_BEEP, enabled).apply()
   }

   fun shouldAlarmVibrate(): SharedPreferenceLiveData<Boolean> {
      return sharedPreferences.booleanLiveData(Pref.ALARM_VIBRATE, false)
   }

   fun shouldAlarmVibrateSync(): Boolean {
      return sharedPreferences.getBoolean(Pref.ALARM_VIBRATE, false)
   }

   fun setAlarmVibrate(enabled: Boolean) {
      sharedPreferences.edit().putBoolean(Pref.ALARM_VIBRATE, enabled).apply()
   }

   /*
    * exposure time warning
    */

   fun isWarningEnabled(): SharedPreferenceLiveData<Boolean> {
      return sharedPreferences.booleanLiveData(Pref.SHOW_WARNING, false)
   }

   fun setWarning(enabled: Boolean) {
      sharedPreferences.edit().putBoolean(Pref.SHOW_WARNING, enabled).apply()
   }

   /*
    * compensation dial
    */

   fun isCompensationDialEnabled(): SharedPreferenceLiveData<Boolean> {
      return sharedPreferences.booleanLiveData(Pref.COMPENSATION_DIAL_ENABLED, false)
   }

   fun setCompensationDialEnabled(enabled: Boolean) {
      sharedPreferences.edit().putBoolean(Pref.COMPENSATION_DIAL_ENABLED, enabled).apply()
   }

   /*
    * premium
    */

   fun hasPremium(): SharedPreferenceLiveData<Boolean> {
      return sharedPreferences.booleanLiveData(Pref.HAS_PREMIUM, false)
   }

   fun hasPremiumSync(): Boolean {
      return sharedPreferences.getBoolean(Pref.HAS_PREMIUM, false)
   }

   fun enablePremium(enabled: Boolean) {
      sharedPreferences.edit().putBoolean(Pref.HAS_PREMIUM, enabled).apply()
   }

   /*
    * theme
    */

   fun getTheme(): SharedPreferenceLiveData<Int> {
      return sharedPreferences.intLiveData(Pref.THEME, 0)
   }

   fun getThemeSync(): Int {
      return sharedPreferences.getInt(Pref.THEME, 0)
   }

   fun setTheme(theme: Int) {
      sharedPreferences.edit().putInt(Pref.THEME, theme).apply()
   }


}
