package de.salomax.ndx.data.model

import android.text.Spanned
import android.text.SpannedString
import de.salomax.ndx.util.TextUtils

/**
 * Shutter speeds are in microseconds to avoid dealing with floating points
 */
enum class ShutterSpeeds(val doubleValues: LongArray, val htmlValues: Array<Spanned>) {

   FULL(
         longArrayOf(
               1000000L * 30,
               1000000L * 15,
               1000000L * 8,
               1000000L * 4,
               1000000L * 2,
               1000000L * 1,
               1000000L / 2,
               1000000L / 4,
               1000000L / 8,
               1000000L / 15,
               1000000L / 30,
               1000000L / 60,
               1000000L / 125,
               1000000L / 250,
               1000000L / 500,
               1000000L / 1000,
               1000000L / 2000,
               1000000L / 4000,
               1000000L / 8000),
         arrayOf(
               SpannedString("30"),
               SpannedString("15"),
               SpannedString("8"),
               SpannedString("4"),
               SpannedString("2"),
               SpannedString("1"),
               TextUtils.getFraction(2),
               TextUtils.getFraction(4),
               TextUtils.getFraction(8),
               TextUtils.getFraction(15),
               TextUtils.getFraction(30),
               TextUtils.getFraction(60),
               TextUtils.getFraction(125),
               TextUtils.getFraction(250),
               TextUtils.getFraction(500),
               TextUtils.getFraction(1000),
               TextUtils.getFraction(2000),
               TextUtils.getFraction(4000),
               TextUtils.getFraction(8000))
   ),

   HALF(
         longArrayOf(
               1000000L * 30,               1000000L * 20,
               1000000L * 15,               1000000L * 10,
               1000000L * 8,                1000000L * 6,
               1000000L * 4,                1000000L * 3,
               1000000L * 2,                100000L * 15,
               1000000L * 1,                10000000L / 15,
               1000000L / 2,                1000000L / 3,
               1000000L / 4,                1000000L / 6,
               1000000L / 8,                1000000L / 10,
               1000000L / 15,               1000000L / 20,
               1000000L / 30,               1000000L / 45,
               1000000L / 60,               1000000L / 90,
               1000000L / 125,              1000000L / 180,
               1000000L / 250,              1000000L / 350,
               1000000L / 500,              1000000L / 750,
               1000000L / 1000,             1000000L / 1500,
               1000000L / 2000,             1000000L / 3000,
               1000000L / 4000,             1000000L / 6000,
               1000000L / 8000),
         arrayOf(
               SpannedString("30"),         SpannedString("20"),
               SpannedString("15"),         SpannedString("10"),
               SpannedString("8"),          SpannedString("6"),
               SpannedString("4"),          SpannedString("3"),
               SpannedString("2"),          SpannedString("1.5"),
               SpannedString("1"),          TextUtils.getFraction(1.5),
               TextUtils.getFraction(2),    TextUtils.getFraction(3),
               TextUtils.getFraction(4),    TextUtils.getFraction(6),
               TextUtils.getFraction(8),    TextUtils.getFraction(10),
               TextUtils.getFraction(15),   TextUtils.getFraction(20),
               TextUtils.getFraction(30),   TextUtils.getFraction(45),
               TextUtils.getFraction(60),   TextUtils.getFraction(90),
               TextUtils.getFraction(125),  TextUtils.getFraction(180),
               TextUtils.getFraction(250),  TextUtils.getFraction(350),
               TextUtils.getFraction(500),  TextUtils.getFraction(750),
               TextUtils.getFraction(1000), TextUtils.getFraction(1500),
               TextUtils.getFraction(2000), TextUtils.getFraction(3000),
               TextUtils.getFraction(4000), TextUtils.getFraction(6000),
               TextUtils.getFraction(8000))
   ),

   THIRD(
         longArrayOf(
               1000000L * 30,               1000000L * 25,               1000000L * 20,
               1000000L * 15,               1000000L * 13,               1000000L * 10,
               1000000L * 8,                1000000L * 6,                1000000L * 5,
               1000000L * 4,                1000000L * 3,                100000L * 25,
               1000000L * 2,                100000L * 16,                100000L * 13,
               1000000L * 1,                10000000L / 13,              10000000L / 16,
               1000000L / 2,                10000000L / 25,              1000000L / 3,
               1000000L / 4,                1000000L / 5,                1000000L / 6,
               1000000L / 8,                1000000L / 10,               1000000L / 13,
               1000000L / 15,               1000000L / 20,               1000000L / 25,
               1000000L / 30,               1000000L / 40,               1000000L / 50,
               1000000L / 60,               1000000L / 80,               1000000L / 100,
               1000000L / 125,              1000000L / 160,              1000000L / 200,
               1000000L / 250,              1000000L / 320,              1000000L / 400,
               1000000L / 500,              1000000L / 640,              1000000L / 800,
               1000000L / 1000,             1000000L / 1250,             1000000L / 1600,
               1000000L / 2000,             1000000L / 2500,             1000000L / 3200,
               1000000L / 4000,             1000000L / 5000,             1000000L / 6400,
               1000000L / 8000),
         arrayOf(
               SpannedString("30"),         SpannedString("25"),         SpannedString("20"),
               SpannedString("15"),         SpannedString("13"),         SpannedString("10"),
               SpannedString("8"),          SpannedString("6"),          SpannedString("5"),
               SpannedString("4"),          SpannedString("3"),          SpannedString("2.5"),
               SpannedString("2"),          SpannedString("1.6"),        SpannedString("1.3"),
               SpannedString("1"),          TextUtils.getFraction(1.3),  TextUtils.getFraction(1.6),
               TextUtils.getFraction(2),    TextUtils.getFraction(2.5),  TextUtils.getFraction(3),
               TextUtils.getFraction(4),    TextUtils.getFraction(5),    TextUtils.getFraction(6),
               TextUtils.getFraction(8),    TextUtils.getFraction(10),   TextUtils.getFraction(13),
               TextUtils.getFraction(15),   TextUtils.getFraction(20),   TextUtils.getFraction(25),
               TextUtils.getFraction(30),   TextUtils.getFraction(40),   TextUtils.getFraction(50),
               TextUtils.getFraction(60),   TextUtils.getFraction(80),   TextUtils.getFraction(100),
               TextUtils.getFraction(125),  TextUtils.getFraction(160),  TextUtils.getFraction(200),
               TextUtils.getFraction(250),  TextUtils.getFraction(320),  TextUtils.getFraction(400),
               TextUtils.getFraction(500),  TextUtils.getFraction(640),  TextUtils.getFraction(800),
               TextUtils.getFraction(1000), TextUtils.getFraction(1250), TextUtils.getFraction(1600),
               TextUtils.getFraction(2000), TextUtils.getFraction(2500), TextUtils.getFraction(3200),
               TextUtils.getFraction(4000), TextUtils.getFraction(5000), TextUtils.getFraction(6400),
               TextUtils.getFraction(8000))
   );
}
