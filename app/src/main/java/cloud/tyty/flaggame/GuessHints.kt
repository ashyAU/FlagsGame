package cloud.tyty.flaggame

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@SuppressLint("DiscouragedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuessHints(navController: NavController, isCountdownMode: Boolean) {
    val context = LocalContext.current
    val countryCodes = countryCodes(context)

    var flag by remember {
        mutableStateOf(
            countryCodes.entries.random()
        )
    }
    // flag image (png)
    val resourceId = rememberSaveable {
        mutableIntStateOf(
            context.resources.getIdentifier(
                flag.key.toString().lowercase(),
                "drawable",
                context.packageName
            )
        )
    }
    var timeRemaining by rememberSaveable { mutableStateOf(10) } // Initial time remaining

    if (isCountdownMode) {
        // Start an async task that decrements the timer every 1000ms
        LaunchedEffect(timeRemaining) {
            countdown(timeRemaining) { updatedTime ->
                timeRemaining = updatedTime // Update time remaining
            }
        }
    }


    // country name
    var countryName by rememberSaveable { mutableStateOf(flag.value.toString().lowercase()) }

    var hiddenText by rememberSaveable { mutableStateOf("_".repeat(countryName.length)) }

    countryName.forEachIndexed { index, it ->
        if (it == ' ') {
            hiddenText = hiddenText.replaceRange(index, index + 1, " ")
        }
    }

    var hiddenTextStringBuilder by remember { mutableStateOf(StringBuilder(hiddenText)) }

    var buttonText by remember { mutableStateOf("Submit") }

    // the char we're using to check if any matches
    val guessedLetter = rememberSaveable { mutableStateOf("") }


    var countryNameDisplay by remember {
        mutableStateOf("")
    }
    // Guess Status, i.e incorrect/correct
    val guessStatus = rememberSaveable { mutableStateOf("") }
    // color of guess status
    val guessColor = remember { mutableStateOf(Color.Black) }
    val guessesRemaining = rememberSaveable { mutableIntStateOf(3) }

    if (timeRemaining == 0 && guessStatus.value != "CORRECT!")
    {
        buttonText = "Next"
        guessStatus.value = "WRONG!"
        guessColor.value = Color.Red
        countryNameDisplay = countryName
    }

    TopAppBar(title = { Text(text = "Guess Hints") }, navigationIcon = {
        IconButton(onClick = {navController.navigateUp()}) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(vertical = 20.dp))
        Text(text = "Guesses Remaining: ${guessesRemaining.intValue}")
        Text(text = guessStatus.value, color = guessColor.value, textAlign = TextAlign.Center, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = countryNameDisplay, color = Color.Blue, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Text(
            text = hiddenText,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            maxLines = 3,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Image(
            painterResource(id = resourceId.intValue),
            contentDescription = "null",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .border(4.dp, Color.Black)
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            TextField(value = guessedLetter.value, onValueChange = {
                if (it.length <= 1) {
                    guessedLetter.value = it.lowercase()
                }
            }, modifier = Modifier.fillMaxWidth(0.3f))
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            Button(onClick = {
                when (buttonText) {
                    "Submit" -> {
                        if (guessedLetter.value.isEmpty()) {
                            guessedLetter.value = "_"
                        }
                        if (countryName.contains(guessedLetter.value)) {
                            for (i in countryName.indices) {
                                if (countryName[i] == guessedLetter.value.first()) {
                                    hiddenTextStringBuilder.setCharAt(
                                        i,
                                        guessedLetter.value.first()
                                    )
                                }
                            }
                        } else {
                            guessesRemaining.intValue--
                        }

                        hiddenText = hiddenTextStringBuilder.toString()
                        guessedLetter.value = ""
                        if (guessesRemaining.intValue == 0) {
                            buttonText = "Next"
                        }
                    }

                    "Next" -> {
                        // Resetting the state of the app
                        flag = countryCodes.entries.random()
                        countryName = flag.value.toString().lowercase()
                        hiddenText = "_".repeat(countryName.length)

                        countryName.forEachIndexed { index, it ->
                            if (it == ' ') {
                                hiddenText = hiddenText.replaceRange(index, index + 1, " ")
                            }
                        }

                        hiddenTextStringBuilder = StringBuilder(hiddenText)
                        buttonText = "Submit"
                        guessedLetter.value = ""
                        guessStatus.value = ""
                        guessColor.value = Color.Black
                        guessesRemaining.intValue = 3
                        timeRemaining = 10
                        countryNameDisplay = ""

                        // Generating a new random entry
                        resourceId.intValue = context.resources.getIdentifier(
                            flag.key.toString().lowercase(),
                            "drawable",
                            context.packageName
                        )
                    }
                }
            }, modifier = Modifier.fillMaxWidth(0.6f)) {
                if (hiddenText == countryName) {
                    buttonText = "Next"
                    guessStatus.value = "CORRECT!"
                    guessColor.value = Color.Green
                }
                if (guessesRemaining.intValue == 0) {
                    buttonText = "Next"
                    guessStatus.value = "WRONG!"
                    guessColor.value = Color.Red
                    countryNameDisplay = countryName
                }
                Text(text = buttonText)
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Text(
            text = "Time Remaining: $timeRemaining",
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(vertical = 100.dp))
    }
}


