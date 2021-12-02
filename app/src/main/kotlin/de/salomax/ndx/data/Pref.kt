package de.salomax.ndx.data

data class Pref(val key: String, val value: String) {

   companion object {
      // steps: full (1), half (2), third (3)
      const val EV_STEPS = "EV_STEPS"

      // sort order: factor (0), name (1)
      const val FILTER_SORT_ORDER = "FILTER_SORT_ORDER"
      // group by filter size
      const val FILTER_GROUP_BY_SIZE = "FILTER_GROUP_BY_SIZE"

      // timer: alarms (boolean)
      const val ALARM_BEEP = "ALARM_BEEP"
      const val ALARM_VIBRATE = "ALARM_VIBRATE"

      // theme: NDx Light (0), Moonlit Dark (1), System Default (2)
      const val THEME = "THEME"

      // theme: pitch black
      const val PITCH_BLACK = "PITCH_BLACK"

      // warning: very long exposures (boolean)
      const val SHOW_WARNING = "SHOW_WARNING"

      // show compensation dial
      const val COMPENSATION_DIAL_ENABLED = "COMPENSATION_DIAL_ENABLED"

      // premium (boolean)
      const val HAS_PREMIUM = "HAS_PREMIUM"
   }

}
