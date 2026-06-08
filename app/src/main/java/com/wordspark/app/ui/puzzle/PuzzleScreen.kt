package com.wordspark.app.ui.puzzle

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordspark.app.ui.theme.WordSparkTheme
import kotlinx.coroutines.delay

private val SAMPLE_PUZZLES = listOf(
    "SPARK" to "A small fiery particle",
    "BLAZE" to "A large fire",
    "EMBER" to "A glowing piece of coal",
    "FLAME" to "A hot glowing body of ignited gas",
    "TORCH" to "A burning stick for light",
    "FLARE" to "A sudden brief burst of light",
    "SCORCH" to "To burn the surface of",
    "KINDLE" to "To light or set fire to",
    "IGNITE" to "To catch fire or burst into flame",
    "SINGE" to "To burn slightly"
)

private const val TIMER_SECONDS = 60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    onPuzzleComplete: (score: Int, streak: Int) -> Unit = { _, _ -> },
    onBackPressed: () -> Unit = {}
) {
    var timeLeft by rememberSaveable { mutableIntStateOf(TIMER_SECONDS) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var userInput by rememberSaveable { mutableStateOf("") }
    var feedback by remember { mutableStateOf<Boolean?>(null) }
    var isGameOver by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isGameOver = true
        }
    }

    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            delay(500L)
            onPuzzleComplete(score, 1)
        }
    }

    LaunchedEffect(feedback) {
        if (feedback != null) {
            delay(400L)
            feedback = null
            if (currentIndex < SAMPLE_PUZZLES.lastIndex) {
                currentIndex++
                userInput = ""
            } else {
                isGameOver = true
            }
        }
    }

    fun submitAnswer() {
        val currentWord = SAMPLE_PUZZLES[currentIndex].first
        val correct = userInput.trim().uppercase() == currentWord.uppercase()
        if (correct) score++
        feedback = correct
    }

    val timerColor = when {
        timeLeft > 20 -> MaterialTheme.colorScheme.primary
        timeLeft > 10 -> Color(0xFFFFA726)
        else -> MaterialTheme.colorScheme.error
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "WordSpark",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier.semantics { contentDescription = "Go back" }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { contentDescription = "Score: $score" }
                )
                TimerDisplay(
                    timeLeft = timeLeft,
                    timerColor = timerColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { timeLeft / TIMER_SECONDS.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .semantics { contentDescription = "$timeLeft seconds remaining" },
                color = timerColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "${currentIndex + 1} / ${SAMPLE_PUZZLES.size}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "clue_animation"
            ) { index ->
                ClueCard(
                    clue = SAMPLE_PUZZLES[index].second,
                    feedback = feedback
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it.uppercase() },
                label = { Text("Your answer") },
                placeholder = { Text("Type the word…") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { submitAnswer() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .semantics { contentDescription = "Answer input field" },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { submitAnswer() },
                enabled = userInput.isNotBlank() && feedback == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .semantics { contentDescription = "Submit answer" },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Submit",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun TimerDisplay(
    timeLeft: Int,
    timerColor: Color
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(56.dp)
            .background(
                color = timerColor.copy(alpha = 0.15f),
                shape = CircleShape
            )
            .semantics { contentDescription = "Timer: $timeLeft seconds" }
    ) {
        Text(
            text = "$timeLeft",
            style = MaterialTheme.typography.titleLarge,
            color = timerColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ClueCard(
    clue: String,
    feedback: Boolean?
) {
    val containerColor = when (feedback) {
        true -> Color(0xFF4CAF50).copy(alpha = 0.2f)
        false -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
        null -> MaterialTheme.colorScheme.surface
    }
    val feedbackText = when (feedback) {
        true -> "✓ Correct!"
        false -> "✗ Wrong"
        null -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Clue: $clue" },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = clue,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            if (feedbackText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = feedbackText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (feedback == true) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PuzzleScreenPreview() {
    WordSparkTheme {
        PuzzleScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PuzzleScreenDarkPreview() {
    WordSparkTheme {
        PuzzleScreen()
    }
}
