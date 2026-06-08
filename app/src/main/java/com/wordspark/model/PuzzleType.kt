package com.wordspark.model

enum class PuzzleType {
    /** Letters are scrambled; player must rearrange them to form the correct word. */
    WORD_UNSCRAMBLE,

    /** A sentence with a missing word; player must supply the correct word. */
    FILL_THE_BLANK,

    /** Player must provide a word that starts with the last letter of the given word. */
    WORD_CHAIN
}
