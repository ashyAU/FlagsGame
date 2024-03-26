package cloud.tyty.flaggame

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

/**
 * Composable function for a game where the user guesses the country.
 * @param navController NavController for navigating to other destinations.
 */
@SuppressLint("DiscouragedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuessTheCountry(navController: NavController, isCountdownMode: Boolean) {

    // Getting the context
    val context = LocalContext.current

    // Retrieving country codes and their corresponding keys
    val countryCodes = countryCodes(context)
    val keyList = countryCodes.keys.map { it }

    // State for selected country
    var (selectedCountry, onCountrySelected) = remember { mutableStateOf("") }

    // State for displaying guess status
    var textGuessStatus by rememberSaveable { mutableStateOf("") }

    // State for text color
    val textColor = remember { mutableStateOf(Color.Black) }

    // State for displaying incorrect text
    var incorrectText by rememberSaveable { mutableStateOf("") }

    // State for randomly selecting a country key
    val randomCountryKey = rememberSaveable {
        mutableStateOf(keyList.random().toString().lowercase())
    }

    var buttonClickText by rememberSaveable { mutableStateOf("Submit") }

    var timeRemaining by rememberSaveable { mutableStateOf(10) } // Initial time remaining

    if (isCountdownMode) {
        // Start an async task that decrements the timer every 1000ms
        LaunchedEffect(timeRemaining) {
            countdown(timeRemaining) { updatedTime ->
                timeRemaining = updatedTime // Update time remaining
            }
        }
    }

    if (timeRemaining == 0 && textGuessStatus != "CORRECT!") {
        textGuessStatus = "WRONG!"
        textColor.value = Color.Red
        incorrectText = "Correct Answer: ${countryCodes[randomCountryKey.value.uppercase()]}"
        buttonClickText = "Next"
    }

    // Creating a Box composable
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )
    {
        // Creating a TopAppBar
        TopAppBar(
            title = {
                Text(
                    text = "Guess the Country",
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    })
            },
        )
        // Creating a resource id
        val resourceId = remember {
            mutableIntStateOf(
                context.resources.getIdentifier(
                    randomCountryKey.value,
                    "drawable",
                    context.packageName
                )
            )
        }
        // Creating a Column composable
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Displaying guess status
            Text(
                text = textGuessStatus,
                color = textColor.value,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            // Displaying incorrect text
            Text(
                text = incorrectText,
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            // Displaying image
            Image(
                painterResource(id = resourceId.intValue),
                contentDescription = "null",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .border(4.dp, Color.Black)
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            // Creating a Box composable
            Box(modifier = Modifier.fillMaxHeight(0.4f)) {
                // Creating a LazyColumn
                LazyColumn {
                    items(keyList.sortedBy { it.toString() }) { index ->
                        val country = countryCodes[index]
                        Spacer(modifier = Modifier.padding(vertical = 0.5.dp))
                        // Creating a Card composable
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(if (country == selectedCountry) Color.Blue else Color.Black),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCountrySelected(country.toString()) },
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = country.toString(),
                                    modifier = Modifier.clickable { onCountrySelected(country.toString()) },
                                    color = if (country == selectedCountry) Color.Blue else Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 10.dp))

            // State for button text

            // Creating a Button composable
            Button(onClick = {
                when (buttonClickText) {
                    "Next" -> {
                        textGuessStatus = ""
                        textColor.value = Color.Black
                        incorrectText = ""
                        selectedCountry = ""
                        randomCountryKey.value = keyList.random().toString().lowercase()
                        buttonClickText = "Submit"

                        resourceId.intValue = context.resources.getIdentifier(
                            randomCountryKey.value,
                            "drawable",
                            context.packageName
                        )
                        timeRemaining = 10
                    }

                    "Submit" -> {
                        buttonClickText = "Next"
                        if (selectedCountry == countryCodes[randomCountryKey.value.uppercase()]) {
                            textGuessStatus = "CORRECT!"
                            textColor.value = Color.Green
                        } else {
                            textGuessStatus = "WRONG!"
                            textColor.value = Color.Red
                            incorrectText =
                                "Correct Answer: ${countryCodes[randomCountryKey.value.uppercase()]}"
                        }
                    }
                }

            }, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text(text = buttonClickText)
            }
            Text(
                text = "Time Remaining: $timeRemaining",
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold
            )
        }
    }
}