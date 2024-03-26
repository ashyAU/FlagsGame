package cloud.tyty.flaggame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * MainActivity class responsible for setting up the main content of the application.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content of the activity
        setContent {
            // A surface container using the 'background' color from the theme
            MainContent()
        }
    }
}

/**
 * Composable function to define the main content of the application.
 */
@Preview
@Composable
fun MainContent() {
    // Initialize NavController to manage navigation within the app
    val navController = rememberNavController()

    // Define the navigation graph with various destinations
    NavHost(navController = navController, startDestination = Composables.HomePage.route) {
        composable(Composables.HomePage.route) {
            HomePage(navController = navController)
        }
        composable(Composables.GuessTheCountry.route) {
            GuessTheCountry(navController, switchState.value)
        }
        composable(Composables.GuessHints.route) {
            GuessHints(navController, switchState.value)
        }
        composable(Composables.GuessTheFlags.route) {
            GuessTheFlag(navController, switchState.value)
        }
        composable(Composables.AdvancedLevel.route) {
            AdvancedLevel(navController, switchState.value)
        }
    }
}

/**
 * Composable function representing the home page of the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {
    // Mutable state to control the countdown mode switch
    var switch by rememberSaveable {
        mutableStateOf(false)
    }

    // Top app bar
    TopAppBar(title = {
        Text("Flag Games", textAlign = TextAlign.Center)
    })

    // Column to organize UI components vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row to display countdown mode switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(text = "Enable Countdown Mode?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Switch(checked = switch, onCheckedChange = {
                switch = !switch
                switchState.value = switch
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons to navigate to different game modes
        ButtonHome(navController, Composables.GuessTheCountry, "Guess the Country")
        ButtonHome(navController, Composables.GuessHints, "Guess Hints")
        ButtonHome(navController, Composables.GuessTheFlags, "Guess the Flags")
        ButtonHome(navController, Composables.AdvancedLevel, "Advanced Level")
    }
}

// Global variable that checks if switch is enabled or disabled
val switchState = mutableStateOf(false)

/**
 * Composable function for a button to navigate to a specific destination.
 */
@Composable
fun ButtonHome(navController: NavController, composable: Composables, text: String) {
    Button(
        onClick = { navController.navigate(composable.route) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = text, fontSize = 20.sp)
    }
}

/**
 * Sealed class representing different composable destinations within the app.
 */
sealed class Composables(val route: String) {
    data object HomePage : Composables("home_page")
    data object GuessTheCountry : Composables("guess_the_country")
    data object GuessHints : Composables("guess_hints")
    data object GuessTheFlags : Composables("guess_the_flags")
    data object AdvancedLevel : Composables("advanced_level")
}