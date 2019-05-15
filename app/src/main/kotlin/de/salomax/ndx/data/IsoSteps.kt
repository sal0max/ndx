package de.salomax.ndx.data

enum class IsoSteps(val values: IntArray) {

    FULL(
            intArrayOf(
                    100,
                    200,
                    400,
                    800,
                    1600,
                    3200,
                    6400)
    ),

    HALF(
            intArrayOf(
                    100, 140,
                    200, 280,
                    400, 560,
                    800, 1100,
                    1600, 2200,
                    3200, 4500,
                    6400, 9000
            )
    ),

    THIRD(
            intArrayOf(
                    100, 125, 160,
                    200, 250, 320,
                    400, 500, 640,
                    800, 1000, 1250,
                    1600, 2000, 2500,
                    3200, 4000, 5000,
                    6400, 8000, 10000)
    );

    operator fun get(position: Int): Int {
        return values[position]
    }
}
