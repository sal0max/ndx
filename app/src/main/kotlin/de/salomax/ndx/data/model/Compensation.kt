package de.salomax.ndx.data.model

import android.text.Spannable
import android.text.SpannableString
import de.salomax.ndx.util.TextUtils

enum class Compensation(val text: Array<Spannable>, val offset: IntArray) {

   FULL(
         arrayOf(
               SpannableString("-6"),
               SpannableString("-5"),
               SpannableString("-4"),
               SpannableString("-3"),
               SpannableString("-2"),
               SpannableString("-1"),
               SpannableString("0"),
               SpannableString("1"),
               SpannableString("2"),
               SpannableString("3"),
               SpannableString("4"),
               SpannableString("5"),
               SpannableString("6")),
         intArrayOf(
               -6,
               -5,
               -4,
               -3,
               -2,
               -1,
               0,
               1,
               2,
               3,
               4,
               5,
               6)
   ),

   HALF(
         arrayOf(
               SpannableString("-6"), TextUtils.getFraction("-5", 1, 2),
               SpannableString("-5"), TextUtils.getFraction("-4", 1, 2),
               SpannableString("-4"), TextUtils.getFraction("-3", 1, 2),
               SpannableString("-3"), TextUtils.getFraction("-2", 1, 2),
               SpannableString("-2"), TextUtils.getFraction("-1", 1, 2),
               SpannableString("-1"), TextUtils.getFraction("-0", 1, 2),
               SpannableString("0"), TextUtils.getFraction("0", 1, 2),
               SpannableString("1"), TextUtils.getFraction("1", 1, 2),
               SpannableString("2"), TextUtils.getFraction("2", 1, 2),
               SpannableString("3"), TextUtils.getFraction("3", 1, 2),
               SpannableString("4"), TextUtils.getFraction("4", 1, 2),
               SpannableString("5"), TextUtils.getFraction("5", 1, 2),
               SpannableString("6")),
         intArrayOf(
               -12, -11,
               -10, -9,
               -8, -7,
               -6, -5,
               -4, -3,
               -2, -1,
               0, 1,
               2, 3,
               4, 5,
               6, 7,
               8, 9,
               10, 11,
               12)
   ),

   THIRD(
         arrayOf(
               SpannableString("-6"), TextUtils.getFraction("-5", 2, 3), TextUtils.getFraction("-5", 1, 3),
               SpannableString("-5"), TextUtils.getFraction("-4", 2, 3), TextUtils.getFraction("-4", 1, 3),
               SpannableString("-4"), TextUtils.getFraction("-3", 2, 3), TextUtils.getFraction("-3", 1, 3),
               SpannableString("-3"), TextUtils.getFraction("-2", 2, 3), TextUtils.getFraction("-2", 1, 3),
               SpannableString("-2"), TextUtils.getFraction("-1", 2, 3), TextUtils.getFraction("-1", 1, 3),
               SpannableString("-1"), TextUtils.getFraction("-0", 2, 3), TextUtils.getFraction("-0", 1, 3),
               SpannableString("0"), TextUtils.getFraction("0", 1, 3), TextUtils.getFraction("0", 2, 3),
               SpannableString("1"), TextUtils.getFraction("1", 1, 3), TextUtils.getFraction("1", 2, 3),
               SpannableString("2"), TextUtils.getFraction("2", 1, 3), TextUtils.getFraction("2", 2, 3),
               SpannableString("3"), TextUtils.getFraction("3", 1, 3), TextUtils.getFraction("3", 2, 3),
               SpannableString("4"), TextUtils.getFraction("4", 1, 3), TextUtils.getFraction("4", 2, 3),
               SpannableString("5"), TextUtils.getFraction("5", 1, 3), TextUtils.getFraction("5", 2, 3),
               SpannableString("6")),
         intArrayOf(
               -18, -17, -16,
               -15, -14, -13,
               -12, -11, -10,
               -9, -8, -7,
               -6, -5, -4,
               -3, -2, -1,
               0, 1, 2,
               3, 4, 5,
               6, 7, 8,
               9, 10, 11,
               12, 13, 14,
               15, 16, 17,
               18
         )
   );

}
