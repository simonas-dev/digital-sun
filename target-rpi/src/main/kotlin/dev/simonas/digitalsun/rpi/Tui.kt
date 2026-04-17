package dev.simonas.digitalsun.rpi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.NonInteractivePolicy.Ignore
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.height
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Color.Companion.White
import com.jakewharton.mosaic.ui.Color.Companion.Yellow
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Spacer
import com.jakewharton.mosaic.ui.Text
import dev.simonas.digitalsun.core.NamedShader
import dev.simonas.digitalsun.core.PixelShader
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicReference
import kotlin.system.exitProcess

private val Muted = Color(128, 128, 128)

fun runTui(
    shaders: List<NamedShader>,
    currentShader: AtomicReference<PixelShader>,
    currentShaderName: AtomicReference<String>,
    shaderFps: AtomicReference<Double>,
    renderFps: AtomicReference<Double>,
) = runMosaicBlocking(onNonInteractive = Ignore) {
    var currentName by remember { mutableStateOf(currentShaderName.get()) }
    var sFps by remember { mutableDoubleStateOf(0.0) }
    var rFps by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(250)
            currentName = currentShaderName.get()
            sFps = shaderFps.get()
            rFps = renderFps.get()
        }
    }

    fun selectByIndex(index: Int): Boolean {
        if (index !in shaders.indices) return false
        val s = shaders[index]
        currentShader.set(s.shader)
        currentShaderName.set(s.name)
        currentName = s.name
        return true
    }

    Column(
        modifier = Modifier.onKeyEvent { event ->
            val cur = shaders.indexOfFirst { it.name == currentName }
            when (event) {
                KeyEvent("ArrowDown") -> selectByIndex((cur + 1) % shaders.size)
                KeyEvent("ArrowUp") -> selectByIndex((cur - 1 + shaders.size) % shaders.size)
                KeyEvent("q") -> exitProcess(0)
                else -> false
            }
        },
    ) {
        Text("DIGITAL SUN", color = Yellow)
        Text("FPS  shader=%.1f  render=%.1f".format(sFps, rFps), color = Muted)
        Spacer(Modifier.height(1))
        for (s in shaders) {
            val isCurrent = s.name == currentName
            val marker = if (isCurrent) " <--" else "    "
            val color = if (isCurrent) Yellow else White
            Text("  ${s.name}$marker", color = color)
        }
        Spacer(Modifier.height(1))
        Text("Use ↑/↓ to switch shader, q to quit", color = Muted)
    }
}
