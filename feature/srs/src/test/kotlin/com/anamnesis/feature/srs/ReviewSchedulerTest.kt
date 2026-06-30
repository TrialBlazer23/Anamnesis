package com.anamnesis.feature.srs

import com.anamnesis.core.domain.model.Card
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewSchedulerTest {

    private val scheduler = ReviewScheduler()
    private val newCard = Card(lemma = "λόγος", gloss = "word", partOfSpeech = "noun")
    private val today = 1000L

    @Test
    fun gradingNewCardSetsInitialStateAndSchedulesAhead() {
        val result = scheduler.schedule(newCard, Rating.Good, today)
        assertEquals(2.3065, result.stability, 1e-3) // initial stability for Good
        assertEquals(today + 2, result.dueEpochDay)  // interval ~= round(stability)
        assertEquals(today, result.lastReviewEpochDay)
        assertEquals(1, result.reps)
        assertEquals(0, result.lapses)
        assertFalse(result.isNew)
    }

    @Test
    fun againCountsAsLapseAndSchedulesSoon() {
        val result = scheduler.schedule(newCard, Rating.Again, today)
        assertEquals(1, result.lapses)
        assertEquals(today + 1, result.dueEpochDay)
    }

    @Test
    fun higherGradesScheduleFurtherOut() {
        val hard = scheduler.schedule(newCard, Rating.Hard, today).dueEpochDay
        val good = scheduler.schedule(newCard, Rating.Good, today).dueEpochDay
        val easy = scheduler.schedule(newCard, Rating.Easy, today).dueEpochDay
        assertTrue(easy >= good && good >= hard)
    }

    @Test
    fun reviewingExistingCardUsesElapsedTime() {
        val studied = scheduler.schedule(newCard, Rating.Good, today) // due ~today+2
        val again = scheduler.schedule(studied, Rating.Again, studied.dueEpochDay)
        assertTrue("lapse shrinks stability", again.stability < studied.stability)
        assertEquals(2, again.reps)
    }
}
