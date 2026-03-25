package tools.mo3ta.kam

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kam",
    ) {
        App()
    }
}