package de.salomax.ndx.data

data class Pref(val key: String, val value: String) {

   companion object {
      // steps: full (1), half (2), third (3)
      const val EV_STEPS = "EV_STEPS"

      // sort order: factor (0), name (1)
      const val FILTER_SORT_ORDER = "FILTER_SORT_ORDER"

      // timer: alarms (boolean)
      const val ALARM_BEEP = "ALARM_BEEP"
      const val ALARM_VIBRATE = "ALARM_VIBRATE"

      // theme: NDx Light (0), Moonlit Dark (1), Pitch Black (2)
      const val THEME = "THEME"

      // warning: very long exposures (boolean)
      const val SHOW_WARNING = "SHOW_WARNING"

      // premium (boolean)
      const val HAS_PREMIUM = "HAS_PREMIUM"
   }

}
