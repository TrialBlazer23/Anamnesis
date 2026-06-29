package com.anamnesis.feature.srs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * FSRS-6 unit tests. Reference values were computed independently from the
 * canonical FSRS-6 formulas (cross-checked against py-fsrs defaults).
 */
class FsrsTest {

    private val fsrs = Fsrs()
    private val eps = 1e-3

    @Test
    fun defaultParametersHave21Weights() {
        assertEquals(21, FSRS6_DEFAULT_PARAMETERS.size)
        assertEquals(0.1542, FSRS6_DEFAULT_PARAMETERS[20], 0.0)
    }

    @Test
    fun initialStabilityEqualsFirstFourWeights() {
        assertEquals(0.212, fsrs.initialState(Rating.Again).stability, eps)
        assertEquals(1.2931, fsrs.initialState(Rating.Hard).stability, eps)
        assertEquals(2.3065, fsrs.initialState(Rating.Good).stability, eps)
        assertEquals(8.2956, fsrs.initialState(Rating.Easy).stability, eps)
    }

    @Test
    fun initialDifficultyMatchesReference() {
        assertEquals(6.4133, fsrs.initialState(Rating.Again).difficulty, eps)
        assertEquals(2.118104, fsrs.initialState(Rating.Good).difficulty, eps)
        assertEquals(1.0, fsrs.initialState(Rating.Easy).difficulty, eps) // clamped to 1.0
    }

    @Test
    fun retrievabilityIsOneAtZeroAndNineTenthsAtStability() {
        val s = 2.3065
        assertEquals(1.0, fsrs.retrievability(0.0, s), eps)
        assertEquals(0.9, fsrs.retrievability(s, s), eps) // calibration identity
        // Monotonically decreasing in elapsed time.
        assertTrue(fsrs.retrievability(1.0, s) > fsrs.retrievability(5.0, s))
    }

    @Test
    fun intervalAtNinetyPercentApproximatesStability() {
        assertEquals(2, fsrs.intervalDays(2.3065))
        assertEquals(8, fsrs.intervalDays(8.2956))
        assertTrue(fsrs.intervalDays(1000.0) > fsrs.intervalDays(100.0))
    }

    @Test
    fun recallStabilityOrdersByRatingAndMatchesReference() {
        val current = MemoryState(stability = 2.3065, difficulty = 2.118104)
        val hard = fsrs.nextState(current, Rating.Hard, elapsedDays = 2.0).stability
        val good = fsrs.nextState(current, Rating.Good, elapsedDays = 2.0).stability
        val easy = fsrs.nextState(current, Rating.Easy, elapsedDays = 2.0).stability

        assertTrue("easy > good > hard", easy > good && good > hard)
        assertEquals(7.513320, hard, eps)
        assertEquals(10.964332, good, eps)
        assertEquals(18.521754, easy, eps)
    }

    @Test
    fun lapseShrinksStability() {
        val current = MemoryState(stability = 2.3065, difficulty = 2.118104)
        val again = fsrs.nextState(current, Rating.Again, elapsedDays = 2.0).stability
        assertEquals(0.607580, again, eps)
        assertTrue("lapse reduces stability", again < current.stability)
    }

    @Test
    fun difficultyMovesWithRatingWithinBounds() {
        val current = MemoryState(stability = 2.3065, difficulty = 2.118104)
        val again = fsrs.nextState(current, Rating.Again, elapsedDays = 2.0).difficulty
        val good = fsrs.nextState(current, Rating.Good, elapsedDays = 2.0).difficulty
        val easy = fsrs.nextState(current, Rating.Easy, elapsedDays = 2.0).difficulty

        assertTrue("Again raises difficulty", again > current.difficulty)
        assertTrue("Easy lowers difficulty", easy < current.difficulty)
        assertEquals(2.116986, good, eps)
        for (d in listOf(again, good, easy)) {
            assertTrue("difficulty within [1,10]", d in 1.0..10.0)
        }
    }

    @Test
    fun sameDayReviewUsesShortTermStability() {
        val current = MemoryState(stability = 2.3065, difficulty = 2.118104)
        // Good same-day should not shrink stability (short-term sinc >= 1).
        val good = fsrs.nextState(current, Rating.Good, elapsedDays = 0.0).stability
        assertTrue(good >= current.stability)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsWrongParameterCount() {
        Fsrs(parameters = doubleArrayOf(1.0, 2.0, 3.0))
    }
}
