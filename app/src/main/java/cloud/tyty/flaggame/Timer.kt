package cloud.tyty.flaggame

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

suspend fun countdown(timeLeft: Int, onUpdate: (Int) -> Unit) {

    var countDown = timeLeft
    // Start a coroutine for countdown
    withContext(Dispatchers.Default) {
        while (countDown > 0) {
            onUpdate(countDown)
            delay(1000) // Delay for 1 second
            countDown-- // Decrement timeLeft
        }
    }
    onUpdate(0)
}