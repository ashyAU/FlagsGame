package cloud.tyty.flaggame

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


/**
 * Composable function representing an advanced level game screen.
 * This screen allows the user to play a guessing game at an advanced level.
 *
 * @param navController NavController to handle navigation within the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdvancedLevel(navController: NavController, isCountdownMode: Boolean) {
    // Retrieve the current context
    val context = LocalContext.current

    // Mutable state for tracking the number of guesses remaining
    val guessesRemaining = rememberSaveable { mutableIntStateOf(3) }

    // Mutable state for the text of the button
    val buttonText = remember { mutableStateOf("Submit") }

    // Mutable state for triggering generation of new state
    var generateNewState by remember { mutableStateOf(false) }

    // Mutable state for tracking if the button has been clicked
    var buttonClicked by remember { mutableStateOf(false) }

    // Mutable list to track the correctness of guesses
    val resultsList = rememberSaveable { mutableListOf(false, false, false) }



    // Launched effect to reset generation of new state
    LaunchedEffect(generateNewState) {
        generateNewState = false
    }

    // Mutable state to track correctness of current guess
    val isCorrect = rememberSaveable {
        mutableStateOf(false)
    }

    // Launched effect to reset button click
    LaunchedEffect(buttonClicked) {
        buttonClicked = false
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
    // Update button text when guesses are exhausted or all guesses are correct
    if (guessesRemaining.intValue == 0 || resultsList.all { it } && resultsList.isNotEmpty() || timeRemaining == 0) {
        buttonText.value = "Next"
    }
    if (timeRemaining == 0)
    {
        guessesRemaining.intValue = 0
    }
    // Scaffold for the advanced level game screen
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Advanced Level") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )

        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Display feedback text based on correctness of guesses
            Text(
                text = if (resultsList.any { !it } && guessesRemaining.intValue == 0 || timeRemaining == 0) "WRONG!"
                else if (resultsList.all { it } && guessesRemaining.intValue != 3) "CORRECT!"
                else "",
                color = if (resultsList.any { !it } && guessesRemaining.intValue == 0 || timeRemaining == 0) Color.Red
                else Color.Green,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Display remaining guesses count
            Text(text = "Guesses Remaining ${guessesRemaining.intValue}")

            Spacer(modifier = Modifier.padding(10.dp))

            // Row to display guessing cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Generate and track correctness of each guessing card
                listOf(1, 2).forEachIndexed { index, _ ->
                    isCorrect.value = countryGuessingCard(
                        context,
                        Modifier.weight(1f),
                        generateNewState,
                        guessesRemaining.intValue,
                        buttonClicked
                    )
                    resultsList[index] = isCorrect.value
                }
            }

            // Generate and track correctness of the third guessing card
            isCorrect.value = countryGuessingCard(
                context = context,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 10.dp),
                generateNewState,
                guessesRemaining.intValue,
                buttonClicked
            )
            resultsList[2] = isCorrect.value

            Spacer(modifier = Modifier.padding(vertical = 5.dp))

            // Button to submit or proceed to next step
            Button(onClick = {
                buttonClicked = true
                when (buttonText.value) {
                    "Submit" -> {
                        generateNewState = false
                        guessesRemaining.intValue--
                    }

                    "Next" -> {
                        // Reset game state for next round
                        resultsList.fill(false)
                        generateNewState = true
                        buttonText.value = "Submit"
                        guessesRemaining.intValue = 3
                        timeRemaining = 10
                    }
                }
            }, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text(text = buttonText.value)
            }
            Text(
                text = "Time Remaining: $timeRemaining",
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold
            )

        }
    }
}

/**
 * @param context Context of the application.
 * @param modifier Modifier for customizing the appearance of the card.
 * @param generateNew Boolean indicating whether to generate a new guessing card.
 * @param guessesRemaining Integer indicating the number of remaining guesses.
 * @param buttonClicked Boolean indicating whether the submit button has been clicked.
 * @return Boolean indicating whether the entered country name is correct.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun countryGuessingCard(
    context: Context,
    modifier: Modifier,
    generateNew: Boolean,
    guessesRemaining: Int,
    buttonClicked: Boolean
): Boolean {
    // Retrieve country codes
    val countryCode = remember(context) { countryCodes(context) }

    // Mutable state for the chosen country
    var chosenCountry by remember { mutableStateOf(countryCode.entries.random()) }

    // Mutable state for the correct answer
    var correctAnswer by rememberSaveable {
        mutableStateOf("")
    }

    // Mutable state for read-only mode of text field
    var isReadOnly by rememberSaveable {
        mutableStateOf(false)
    }

    // Mutable state for text field color
    var textFieldColor by remember {
        mutableStateOf(Color.Black)
    }

    // Extract flag information
    val flag by rememberSaveable(chosenCountry) {
        mutableStateOf(
            chosenCountry.key.toString().lowercase()
                    to chosenCountry.value.toString()
        )
    }

    // Mutable state for text field value
    var textField by rememberSaveable {
        mutableStateOf("")
    }

    // Check correctness of entered country name when guesses are exhausted
    if (guessesRemaining == 0 && textField.lowercase().trim() != chosenCountry.value.toString()
            .lowercase()
    ) {
        correctAnswer = flag.second
    }

    // Update text field color based on correctness after button click
    if (buttonClicked && textField.lowercase().trim() == chosenCountry.value.toString()
            .lowercase()
    ) {
        textFieldColor = Color.Green
        isReadOnly = true
    } else if (buttonClicked && textField.lowercase().trim() != chosenCountry.value.toString()
            .lowercase()
    ) {
        textFieldColor = Color.Red
    }

    // Retrieve resource ID for flag image
    val resourceId = context.resources.getIdentifier(
        flag.first,
        "drawable",
        context.packageName
    )

    // Generate new guessing card if required
    if (generateNew) {
        chosenCountry = countryCode.entries.random()
        textField = ""
        correctAnswer = ""
        isReadOnly = false
        textFieldColor = Color.Black
    }

    // Card displaying the flag image and text field
    Card(
        modifier = modifier.padding(horizontal = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Display flag image
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = "Flag",
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .border(4.dp, Color.Black)
                )
            }

            // Text field for entering country name
            TextField(
                readOnly = isReadOnly,
                value = textField,
                onValueChange = { textField = it },
                placeholder = {
                    Text(
                        text = "Country Name"
                    )
                },
                minLines = 2,
                textStyle = TextStyle(color = textFieldColor)
            )

            // Display correct answer if applicable
            Text(text = correctAnswer, color = Color.Blue, textAlign = TextAlign.Center)
        }
    }
    // Return whether entered country name is correct
    return textField.lowercase() == chosenCountry.value.toString().lowercase()
}


