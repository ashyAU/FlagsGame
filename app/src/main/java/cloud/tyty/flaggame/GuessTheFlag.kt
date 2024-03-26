package cloud.tyty.flaggame

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cloud.tyty.flaggame.ui.theme.getColorByIndex


/**
 * Composable function for the Guess The Flag game screen.
 *
 * @param navController The navigation controller for navigating between composables.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "DiscouragedApi")
@Composable
fun GuessTheFlag(navController: NavController, isCountdownMode: Boolean) {
    // Get the context
    val context = LocalContext.current

    // Get a map of country codes and their corresponding country names
    val countryCodes = countryCodes(context)

    // Choose a random country code and its corresponding country name as the correct answer
    val correctFlagMap = countryCodes.entries.random()

    // Initialize mutable states for correct and incorrect flags, list of flags, correct text, and correct text color index
    var correctFlag by rememberSaveable {
        mutableStateOf(correctFlagMap.key.toString().lowercase() to correctFlagMap.value.toString())
    }
    var incorrectFlags by rememberSaveable {
        mutableStateOf(
            listOf(
                countryCodes.entries.random().key.toString().lowercase(),
                countryCodes.entries.random().key.toString().lowercase()
            )
        )
    }
    var list by rememberSaveable {
        mutableStateOf(
            mutableListOf<String>().apply {
                addAll(listOf(correctFlag.first, *incorrectFlags.toTypedArray()))
                shuffle()
            }
        )
    }
    val correctText = rememberSaveable { mutableStateOf("") }
    var correctTextColorIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    // Countdown value that is used for the timer game mode
    var timeRemaining by rememberSaveable { mutableStateOf(10) } // Initial time remaining

    if (isCountdownMode) {
        // Start an async task that decrements the timer every 1000ms
        LaunchedEffect(timeRemaining) {
            countdown(timeRemaining) { updatedTime ->
                timeRemaining = updatedTime // Update time remaining
            }
        }
    }

    if (timeRemaining == 0 && correctText.value != "CORRECT!")
    {
        correctText.value = "WRONG!"
        correctTextColorIndex = 1
    }

    // Top app bar
    TopAppBar(
        title = { Text(text = "Guess The Flag") },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )

    // Column for organizing UI components vertically
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Box to display the correct flag text
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = correctFlag.second,
                fontSize = 24.sp,
                maxLines = 4,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Text to display correctness feedback
        Text(
            text = correctText.value,
            color = getColorByIndex(correctTextColorIndex),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Spacer for layout padding
        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        // Display flags in the list
        list.forEach { flag ->
            val resourceId = context.resources.getIdentifier(
                flag,
                "drawable",
                context.packageName
            )
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "Flag",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .border(2.dp, Color.Black)
                    .clickable {
                        if (flag == correctFlag.first) {
                            correctText.value = "CORRECT!"
                            correctTextColorIndex = 0
                        } else {
                            correctText.value = "WRONG!"
                            correctTextColorIndex = 1
                        }
                    }
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
        }

        // Button to move to the next question
        Button(
            onClick = {
                correctFlag =
                    correctFlagMap.key.toString().lowercase() to correctFlagMap.value.toString()
                incorrectFlags = listOf(
                    countryCodes.entries.random().key.toString().lowercase(),
                    countryCodes.entries.random().key.toString().lowercase()
                )
                list = mutableListOf<String>().apply {
                    addAll(listOf(correctFlag.first, *incorrectFlags.toTypedArray()))
                    shuffle()
                }
                correctText.value = ""
                correctTextColorIndex = 2
                timeRemaining = 10
            },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(text = "Next")
        }
        Text(
            text = "Time Remaining: $timeRemaining",
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold
        )
    }
}
