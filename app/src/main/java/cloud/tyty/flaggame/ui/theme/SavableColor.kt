package cloud.tyty.flaggame.ui.theme

import androidx.compose.ui.graphics.Color


// Implementation
//val correctTextColorIndex = rememberSaveable { mutableStateOf(0) }
//
//val correctTextColor = remember {
//    mutableStateOf(getColorByIndex(correctTextColorIndex.value))
//}

fun getColorByIndex(index: Int): Color {
    return when (index) {
        0 -> Color.Green
        1 -> Color.Red
        else -> Color.Black
    }
}
