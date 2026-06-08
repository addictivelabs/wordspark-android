package com.wordspark.model

/**
 * Represents a single puzzle presented to the player.
 *
 * @param id       Unique identifier (matches Firestore document id).
 * @param type     Which puzzle mechanic applies (unscramble, fill-blank, word-chain).
 * @param question Prompt shown to the player.
 *                 – WORD_UNSCRAMBLE: the scrambled letters (e.g. "ZKPELA")
 *                 – FILL_THE_BLANK: the sentence with "___" placeholder (e.g. "The ___ is a star")
 *                 – WORD_CHAIN: the word the player must chain from (e.g. "spark")
 * @param answer   The canonical correct answer (case-insensitive match).
 * @param difficulty Determines base score for the puzzle.
 */
data class Puzzle(
    val id: String,
    val type: PuzzleType,
    val question: String,
    val answer: String,
    val difficulty: Difficulty
)
