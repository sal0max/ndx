package de.salomax.ndx.util

import org.junit.Assert.*
import org.junit.Test

class MathUtilsTest {

   @Test
   fun testMultiply() {
      // null
      assertNull(MathUtils.multiply(4711, null))
      assertNull(MathUtils.multiply(null, 4711))
      assertNull(MathUtils.multiply(null, null))
      // overflow
      assertNull(MathUtils.multiply(-1, Long.MAX_VALUE))
      assertNull(MathUtils.multiply(Long.MAX_VALUE, Int.MAX_VALUE.toLong()))
      // valid
      assertEquals(22193521L,MathUtils.multiply(4711, 4711))
      assertEquals(-4711L,MathUtils.multiply(4711, -1))
      assertEquals(0L,MathUtils.multiply(0, 4711))
   }

   @Test
   fun testFactor2stop() {
      assertEquals("0.0", MathUtils.factor2fstop(1))
      assertEquals("1.6", MathUtils.factor2fstop(3))
      assertEquals("2.0", MathUtils.factor2fstop(4))
      assertEquals("10.0", MathUtils.factor2fstop(1000))
      assertEquals("10.0", MathUtils.factor2fstop(1024))
   }

   @Test
   fun testFactor2fstopRounded() {
      assertEquals("0", MathUtils.factor2fstopRounded(1))
      assertEquals("1.6", MathUtils.factor2fstopRounded(3))
      assertEquals("2", MathUtils.factor2fstopRounded(4))
      assertEquals("10", MathUtils.factor2fstopRounded(1000))
      assertEquals("10", MathUtils.factor2fstopRounded(1024))
   }

   @Test
   fun testFactor2nd() {
      assertEquals("0.0", MathUtils.factor2nd(1))
      assertEquals("0.5", MathUtils.factor2nd(3))
      assertEquals("3.0", MathUtils.factor2nd(1000))
      assertEquals("3.0", MathUtils.factor2nd(1024))
   }

}
