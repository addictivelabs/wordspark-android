package com.wordspark.engine

import com.wordspark.model.Difficulty
import com.wordspark.model.Puzzle
import com.wordspark.model.PuzzleType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PuzzleEngineTest {

    // Fake clock: start at 0, advanced manually between calls via `advanceMs`.
    private var fakeClockMs = 0L
    private lateinit var engine: PuzzleEngine

    @Before
    fun setUp() {
        fakeClockMs = 0L
        engine = PuzzleEngine(timerDurationSeconds = 60L, clock = { fakeClockMs })
    }

    // ── Helper factories ──────────────────────────────────────────────────────

    private fun unscramblePuzzle(
        question: String = "PKRSA",
        answer: String = "SPARK",
        difficulty: Difficulty = Difficulty.MEDIUM
    ) = Puzzle("u1", PuzzleType.WORD_UNSCRAMBLE, question, answer, difficulty)

    private fun fillBlankPuzzle(
        question: String = "The ___ is a star",
        answer: String = "sun",
        difficulty: Difficulty = Difficulty.EASY
    ) = Puzzle("f1", PuzzleType.FILL_THE_BLANK, question, answer, difficulty)

    private fun wordChainPuzzle(
        question: String = "spark",
        answer: String = "kite",
        difficulty: Difficulty = Difficulty.HARD
    ) = Puzzle("w1", PuzzleType.WORD_CHAIN, question, answer, difficulty)

    // ── Timer tests ───────────────────────────────────────────────────────────

    @Test
    fun `elapsed seconds is zero before timer starts`() {
        assertEquals(0L, engine.getElapsedSeconds())
    }

    @Test
    fun `elapsed seconds tracks fake clock after start`() {
        engine.startTimer()
        fakeClockMs = 15_000L
        assertEquals(15L, engine.getElapsedSeconds())
    }

    @Test
    fun `remaining seconds decrements correctly`() {
        engine.startTimer()
        fakeClockMs = 20_000L
        assertEquals(40L, engine.getRemainingSeconds())
    }

    @Test
    fun `remaining seconds clamps to zero when expired`() {
        engine.startTimer()
        fakeClockMs = 90_000L
        assertEquals(0L, engine.getRemainingSeconds())
    }

    @Test
    fun `isTimerExpired is false before expiry`() {
        engine.startTimer()
        fakeClockMs = 59_000L
        assertFalse(engine.isTimerExpired())
    }

    @Test
    fun `isTimerExpired is true at exactly 60 seconds`() {
        engine.startTimer()
        fakeClockMs = 60_000L
        assertTrue(engine.isTimerExpired())
    }

    // ── WORD_UNSCRAMBLE evaluation ─────────────────────────────────────────────

    @Test
    fun `unscramble - exact correct answer passes`() {
        val puzzle = unscramblePuzzle(question = "PKRSA", answer = "SPARK")
        assertTrue(engine.evaluateAnswer(puzzle, "SPARK"))
    }

    @Test
    fun `unscramble - case insensitive match passes`() {
        val puzzle = unscramblePuzzle(question = "PKRSA", answer = "SPARK")
        assertTrue(engine.evaluateAnswer(puzzle, "spark"))
    }

    @Test
    fun `unscramble - leading and trailing whitespace is trimmed`() {
        val puzzle = unscramblePuzzle(question = "PKRSA", answer = "SPARK")
        assertTrue(engine.evaluateAnswer(puzzle, "  spark  "))
    }

    @Test
    fun `unscramble - wrong word fails`() {
        val puzzle = unscramblePuzzle(question = "PKRSA", answer = "SPARK")
        assertFalse(engine.evaluateAnswer(puzzle, "parks"))
    }

    @Test
    fun `unscramble - answer with wrong letters fails even if same length`() {
        // "SPARK" and "SPORK" share the same letter count but differ in content.
        val puzzle = unscramblePuzzle(question = "PKRSA", answer = "SPARK")
        assertFalse(engine.evaluateAnswer(puzzle, "SPORK"))
    }

    @Test
    fun `unscramble - empty answer fails`() {
        val puzzle = unscramblePuzzle()
        assertFalse(engine.evaluateAnswer(puzzle, ""))
    }

    // ── FILL_THE_BLANK evaluation ──────────────────────────────────────────────

    @Test
    fun `fill blank - correct word passes`() {
        val puzzle = fillBlankPuzzle(answer = "sun")
        assertTrue(engine.evaluateAnswer(puzzle, "sun"))
    }

    @Test
    fun `fill blank - case insensitive match passes`() {
        val puzzle = fillBlankPuzzle(answer = "sun")
        assertTrue(engine.evaluateAnswer(puzzle, "SUN"))
    }

    @Test
    fun `fill blank - whitespace trimmed`() {
        val puzzle = fillBlankPuzzle(answer = "sun")
        assertTrue(engine.evaluateAnswer(puzzle, " sun "))
    }

    @Test
    fun `fill blank - wrong word fails`() {
        val puzzle = fillBlankPuzzle(answer = "sun")
        assertFalse(engine.evaluateAnswer(puzzle, "moon"))
    }

    @Test
    fun `fill blank - empty answer fails`() {
        val puzzle = fillBlankPuzzle()
        assertFalse(engine.evaluateAnswer(puzzle, ""))
    }

    // ── WORD_CHAIN evaluation ─────────────────────────────────────────────────

    @Test
    fun `word chain - correct answer starting with last letter passes`() {
        // question ends in 'k', answer "kite" starts with 'k'
        val puzzle = wordChainPuzzle(question = "spark", answer = "kite")
        assertTrue(engine.evaluateAnswer(puzzle, "kite"))
    }

    @Test
    fun `word chain - case insensitive match passes`() {
        val puzzle = wordChainPuzzle(question = "spark", answer = "kite")
        assertTrue(engine.evaluateAnswer(puzzle, "KITE"))
    }

    @Test
    fun `word chain - answer that doesn't start with required letter fails`() {
        // "apple" starts with 'a', not 'k'
        val puzzle = wordChainPuzzle(question = "spark", answer = "kite")
        assertFalse(engine.evaluateAnswer(puzzle, "apple"))
    }

    @Test
    fun `word chain - correct start letter but wrong word fails`() {
        // starts with 'k' but is not the expected answer
        val puzzle = wordChainPuzzle(question = "spark", answer = "kite")
        assertFalse(engine.evaluateAnswer(puzzle, "king"))
    }

    @Test
    fun `word chain - empty answer fails`() {
        val puzzle = wordChainPuzzle()
        assertFalse(engine.evaluateAnswer(puzzle, ""))
    }

    @Test
    fun `word chain - whitespace-only answer fails`() {
        val puzzle = wordChainPuzzle()
        assertFalse(engine.evaluateAnswer(puzzle, "   "))
    }

    @Test
    fun `word chain - question ending uppercase is matched case-insensitively`() {
        val puzzle = wordChainPuzzle(question = "SPARK", answer = "kite")
        assertTrue(engine.evaluateAnswer(puzzle, "kite"))
    }

    // ── Speed multiplier ──────────────────────────────────────────────────────

    @Test
    fun `speed multiplier is 2x at exactly 10 seconds`() {
        assertEquals(2.0, engine.computeSpeedMultiplier(10L), 0.0)
    }

    @Test
    fun `speed multiplier is 2x below 10 seconds`() {
        assertEquals(2.0, engine.computeSpeedMultiplier(5L), 0.0)
    }

    @Test
    fun `speed multiplier is 1_5x at exactly 20 seconds`() {
        assertEquals(1.5, engine.computeSpeedMultiplier(20L), 0.0)
    }

    @Test
    fun `speed multiplier is 1_5x between 11 and 20 seconds`() {
        assertEquals(1.5, engine.computeSpeedMultiplier(15L), 0.0)
    }

    @Test
    fun `speed multiplier is 1_2x at exactly 40 seconds`() {
        assertEquals(1.2, engine.computeSpeedMultiplier(40L), 0.001)
    }

    @Test
    fun `speed multiplier is 1_2x between 21 and 40 seconds`() {
        assertEquals(1.2, engine.computeSpeedMultiplier(30L), 0.001)
    }

    @Test
    fun `speed multiplier is 1x at exactly 60 seconds`() {
        assertEquals(1.0, engine.computeSpeedMultiplier(60L), 0.0)
    }

    @Test
    fun `speed multiplier is 0_5x when over 60 seconds`() {
        assertEquals(0.5, engine.computeSpeedMultiplier(90L), 0.0)
    }

    // ── Score computation ─────────────────────────────────────────────────────

    @Test
    fun `score is zero for incorrect answer`() {
        val puzzle = fillBlankPuzzle(difficulty = Difficulty.HARD)
        assertEquals(0, engine.computeScore(puzzle, correct = false, elapsedSeconds = 5L))
    }

    @Test
    fun `score is base x multiplier for correct answer - EASY fast`() {
        // EASY = 100 base, 2x multiplier (≤10s) → 200
        val puzzle = fillBlankPuzzle(difficulty = Difficulty.EASY)
        assertEquals(200, engine.computeScore(puzzle, correct = true, elapsedSeconds = 5L))
    }

    @Test
    fun `score is base x multiplier for correct answer - MEDIUM normal`() {
        // MEDIUM = 200 base, 1.2x multiplier (≤40s) → 240
        val puzzle = unscramblePuzzle(difficulty = Difficulty.MEDIUM)
        assertEquals(240, engine.computeScore(puzzle, correct = true, elapsedSeconds = 30L))
    }

    @Test
    fun `score is base x multiplier for correct answer - HARD slow`() {
        // HARD = 300 base, 1x multiplier (≤60s) → 300
        val puzzle = wordChainPuzzle(difficulty = Difficulty.HARD)
        assertEquals(300, engine.computeScore(puzzle, correct = true, elapsedSeconds = 60L))
    }

    @Test
    fun `score is halved when over 60 seconds - HARD`() {
        // HARD = 300 base, 0.5x multiplier → 150
        val puzzle = wordChainPuzzle(difficulty = Difficulty.HARD)
        assertEquals(150, engine.computeScore(puzzle, correct = true, elapsedSeconds = 75L))
    }

    @Test
    fun `score is maximum for HARD puzzle answered instantly`() {
        // HARD = 300 base, 2x multiplier → 600
        val puzzle = wordChainPuzzle(difficulty = Difficulty.HARD)
        assertEquals(600, engine.computeScore(puzzle, correct = true, elapsedSeconds = 0L))
    }
}
