package com.wordspark.engine

import com.wordspark.model.Puzzle
import com.wordspark.model.PuzzleType

/**
 * Core game engine: answer evaluation, timer tracking, and scoring.
 *
 * Each game session creates one [PuzzleEngine]. Call [startTimer] when the first puzzle is
 * presented. Per-puzzle elapsed time is passed directly to [computeScore] so callers can
 * snapshot the clock at the moment the player submits.
 *
 * @param timerDurationSeconds Total countdown duration (defaults to 60 s per spec).
 * @param clock                Replaceable time source; defaults to wall-clock millis.
 *                             Injected in tests to avoid real-time waits.
 */
class PuzzleEngine(
    val timerDurationSeconds: Long = 60L,
    private val clock: () -> Long = { System.currentTimeMillis() }
) {

    private var startTimeMs: Long = 0L
    private var timerStarted: Boolean = false

    // ── Timer ────────────────────────────────────────────────────────────────

    fun startTimer() {
        startTimeMs = clock()
        timerStarted = true
    }

    /** Seconds elapsed since [startTimer] was called. Returns 0 if not yet started. */
    fun getElapsedSeconds(): Long {
        if (!timerStarted) return 0L
        return (clock() - startTimeMs) / 1000L
    }

    /** Seconds remaining on the 60-second countdown. Clamps to 0 when expired. */
    fun getRemainingSeconds(): Long = maxOf(0L, timerDurationSeconds - getElapsedSeconds())

    fun isTimerExpired(): Boolean = getRemainingSeconds() == 0L

    // ── Answer evaluation ─────────────────────────────────────────────────────

    /**
     * Returns true when [userAnswer] is the correct solution for [puzzle].
     *
     * Matching is always case-insensitive and ignores leading/trailing whitespace.
     * Additional rules per type:
     *  - WORD_UNSCRAMBLE: letters must form the exact answer word.
     *  - FILL_THE_BLANK: the supplied word must match the answer exactly.
     *  - WORD_CHAIN: the answer must start with the last letter of the question word.
     */
    fun evaluateAnswer(puzzle: Puzzle, userAnswer: String): Boolean = when (puzzle.type) {
        PuzzleType.WORD_UNSCRAMBLE -> evaluateUnscramble(puzzle, userAnswer)
        PuzzleType.FILL_THE_BLANK  -> evaluateFillBlank(puzzle, userAnswer)
        PuzzleType.WORD_CHAIN      -> evaluateWordChain(puzzle, userAnswer)
    }

    private fun evaluateUnscramble(puzzle: Puzzle, userAnswer: String): Boolean {
        val normalised = userAnswer.trim().lowercase()
        val expected = puzzle.answer.lowercase()
        if (normalised != expected) return false
        // The user's input must use exactly the same letter multiset as the question.
        return normalised.toSortedCharList() == puzzle.question.trim().lowercase().toSortedCharList()
    }

    private fun evaluateFillBlank(puzzle: Puzzle, userAnswer: String): Boolean =
        userAnswer.trim().lowercase() == puzzle.answer.lowercase()

    private fun evaluateWordChain(puzzle: Puzzle, userAnswer: String): Boolean {
        val trimmed = userAnswer.trim()
        if (trimmed.isEmpty()) return false
        val requiredFirstChar = puzzle.question.trim().lastOrNull()?.lowercaseChar() ?: return false
        if (trimmed.first().lowercaseChar() != requiredFirstChar) return false
        return trimmed.lowercase() == puzzle.answer.lowercase()
    }

    // ── Scoring ───────────────────────────────────────────────────────────────

    /**
     * Computes the player's score for a single puzzle.
     *
     * Formula: `basePoints × speedMultiplier`
     *
     * @param puzzle         The puzzle that was presented.
     * @param correct        Whether the answer was correct.
     * @param elapsedSeconds Seconds the player spent before submitting (snapshot at submission).
     */
    fun computeScore(puzzle: Puzzle, correct: Boolean, elapsedSeconds: Long): Int {
        if (!correct) return 0
        return (puzzle.difficulty.basePoints * computeSpeedMultiplier(elapsedSeconds)).toInt()
    }

    /**
     * Speed multiplier brackets (relative to the 60-second timer):
     *
     * | Elapsed (s) | Multiplier |
     * |-------------|------------|
     * | ≤ 10        | 2.0×       |
     * | ≤ 20        | 1.5×       |
     * | ≤ 40        | 1.2×       |
     * | ≤ 60        | 1.0×       |
     * | > 60        | 0.5×       |
     */
    fun computeSpeedMultiplier(elapsedSeconds: Long): Double = when {
        elapsedSeconds <= 10 -> 2.0
        elapsedSeconds <= 20 -> 1.5
        elapsedSeconds <= 40 -> 1.2
        elapsedSeconds <= 60 -> 1.0
        else                 -> 0.5
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun String.toSortedCharList(): List<Char> = this.toList().sorted()
}
