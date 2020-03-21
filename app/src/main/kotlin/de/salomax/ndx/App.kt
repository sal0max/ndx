package de.salomax.ndx

import android.annotation.SuppressLint
import android.app.Application

@SuppressLint("Registered")
open class App : Application() {

   @Suppress("RedundantOverride")
   override fun onCreate() {
      super.onCreate()
   }
}
