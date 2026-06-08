package com.wordspark.app.ui.puzzle

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordspark.app.R
import com.wordspark.app.ui.theme.WordSparkTheme
import kotlinx.coroutines.delay

private data class PuzzleWord(val scrambled: String, val answer: String, val points: Int)

private val samplePuzzles = listOf(
    PuzzleWord("PKRAS", "SPARK", 20),
    PuzzleWord("RDOW", "WORD", 15),
    PuzzleWord("ZZLUPE", "PUZZLE", 25),
    PuzzleWord("INAD", "DAIN", 10),
    PuzzleWord("AMGE", "GAME", 15)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    onPuzzleComplete: (score: Int) -> Unit,
    onBack: () -> Unit,
    totalSeconds: Int = 60,
    modifier: Modifier = Modifier
) {
    var timeLeft by rememberSaveable { mutableIntStateOf(totalSeconds) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var puzzleIndex by rememberSaveable { mutableIntStateOf(0) }
    var answer by rememberSaveable { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var isGameOver by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Countdown timer
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeft > 0) {
                delay(1_000L)
                timeLeft--
            }
            isGameOver = true
        }
    }

    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            delay(800L)
            onPuzzleComplete(score)
        }
    }

    val currentPuzzle = samplePuzzles.getOrNull(puzzleIndex)

    fun submitAnswer() {
        val puzzle = currentPuzzle ?: return
        if (answer.trim().equals(puzzle.answer, ignoreCase = true)) {
            score += puzzle.points
            feedbackMessage = "+${puzzle.points} pts!"
            if (puzzleIndex + 1 >= samplePuzzles.size) {
                isGameOver = true
            } else {
                puzzleIndex++
                answer = ""
            }
        } else {
            feedbackMessage = "Try again!"
        }
    }

    val timerColor = when {
        timeLeft > 30 -> MaterialTheme.colorScheme.primary
        timeLeft > 10 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Puzzle ${puzzleIndex + 1} / ${samplePuzzles.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Timer + Score row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val timerDesc = stringResource(R.string.cd_timer_icon)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.semantics { contentDescription = "$timeLeft seconds remaining" }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = timerDesc,
                        tint = timerColor,
                        modifier = Modifier.size(24.dp)
                    )
                    AnimatedContent(
                        targetState = timeLeft,
                        transitionSpec = {
                            fadeIn(tween(150)) togetherWith fadeOut(tween(150))
                        },
                        label = "timer"
                    ) { time ->
                        Text(
                            text = time.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = timerColor
                        )
                    }
                }

                // Score
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .semantics { contentDescription = "Score: $score" }
                ) {
                    Text(
                        text = "$score pts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Puzzle card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Unscramble this word:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = currentPuzzle?.scrambled ?: "-----",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 52.sp,
                            letterSpacing = 12.sp
                        ),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    if (feedbackMessage.isNotEmpty()) {
                        Text(
                            text = feedbackMessage,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (feedbackMessage.startsWith("+")) {
                                Color(0xFF4CAF50)
                            } else {
                                Color(0xFFF44336)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Answer input
            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it.uppercase() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.puzzle_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        submitAnswer()
                        focusManager.clearFocus()
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Submit button
            Button(
                onClick = {
                    submitAnswer()
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = answer.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.puzzle_submit),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Progress dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                samplePuzzles.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == puzzleIndex) 12.dp else 8.dp)
                            .background(
                                color = when {
                                    index < puzzleIndex -> MaterialTheme.colorScheme.primary
                                    index == puzzleIndex -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PuzzleScreenPreview() {
    WordSparkTheme {
        PuzzleScreen(
            onPuzzleComplete = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PuzzleScreenDarkPreview() {
    WordSparkTheme(darkTheme = true) {
        PuzzleScreen(
            onPuzzleComplete = {},
            onBack = {}
        )
    }
}
