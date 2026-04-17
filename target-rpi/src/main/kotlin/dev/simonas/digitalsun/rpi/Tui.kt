package dev.simonas.digitalsun.rpi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.NonInteractivePolicy.Ignore
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.height
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.text.SpanStyle
import com.jakewharton.mosaic.text.buildAnnotatedString
import com.jakewharton.mosaic.text.withStyle
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Color.Companion.White
import com.jakewharton.mosaic.ui.Color.Companion.Yellow
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Spacer
import com.jakewharton.mosaic.ui.Text
import dev.simonas.digitalsun.core.NoiseType
import dev.simonas.digitalsun.core.ShaderParameters
import dev.simonas.digitalsun.core.ShaderPreset
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicReference
import kotlin.system.exitProcess

private val Muted = Color(128, 128, 128)
private val Edited = Color(255, 165, 0)

private enum class StepModifier { Normal, Coarse, Fine }

private fun stepMult(mod: StepModifier) = when (mod) {
    StepModifier.Normal -> 1.0
    StepModifier.Coarse -> 10.0
    StepModifier.Fine -> 0.1
}

private interface Knob {
    val name: String
    fun display(p: ShaderParameters): String
    fun change(p: ShaderParameters, sign: Int, mod: StepModifier): ShaderParameters
}

private fun continuous(
    label: String,
    base: Double,
    decimals: Int,
    get: (ShaderParameters) -> Double,
    set: (ShaderParameters, Double) -> ShaderParameters,
    min: Double = -Double.MAX_VALUE,
    max: Double = Double.MAX_VALUE,
): Knob = object : Knob {
    override val name = label
    override fun display(p: ShaderParameters) = "%.${decimals}f".format(get(p))
    override fun change(p: ShaderParameters, sign: Int, mod: StepModifier): ShaderParameters {
        val newVal = (get(p) + sign * base * stepMult(mod)).coerceIn(min, max)
        return set(p, newVal)
    }
}

private fun integer(
    label: String,
    base: Int,
    get: (ShaderParameters) -> Int,
    set: (ShaderParameters, Int) -> ShaderParameters,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
): Knob = object : Knob {
    override val name = label
    override fun display(p: ShaderParameters) = get(p).toString()
    override fun change(p: ShaderParameters, sign: Int, mod: StepModifier): ShaderParameters {
        val mult = when (mod) {
            StepModifier.Coarse -> 10
            else -> 1  // Fine = no-op for ints
        }
        val newVal = (get(p) + sign * base * mult).coerceIn(min, max)
        return set(p, newVal)
    }
}

private fun <E : Enum<E>> cycle(
    label: String,
    values: List<E>,
    get: (ShaderParameters) -> E,
    set: (ShaderParameters, E) -> ShaderParameters,
): Knob = object : Knob {
    override val name = label
    override fun display(p: ShaderParameters) = get(p).name
    override fun change(p: ShaderParameters, sign: Int, mod: StepModifier): ShaderParameters {
        val idx = values.indexOf(get(p))
        val newIdx = ((idx + sign) % values.size + values.size) % values.size
        return set(p, values[newIdx])
    }
}

private fun degree(
    label: String,
    get: (ShaderParameters) -> Double,
    set: (ShaderParameters, Double) -> ShaderParameters,
): Knob = object : Knob {
    override val name = label
    override fun display(p: ShaderParameters) = "%3d°".format((get(p) * 360).toInt())
    override fun change(p: ShaderParameters, sign: Int, mod: StepModifier): ShaderParameters {
        val baseDeg = 5.0
        val newDeg = ((get(p) * 360) + sign * baseDeg * stepMult(mod)).coerceIn(0.0, 360.0)
        return set(p, newDeg / 360.0)
    }
}

fun runTui(
    presets: List<ShaderPreset>,
    currentParams: AtomicReference<ShaderParameters>,
    currentPresetName: AtomicReference<String>,
    shaderFps: AtomicReference<Double>,
    renderFps: AtomicReference<Double>,
) = runMosaicBlocking(onNonInteractive = Ignore) {
    val originals = remember { presets.associate { it.name to it.params } }
    val edited = remember { presets.associate { it.name to it.params }.toMutableMap() }
    val knobs = remember {
        listOf(
            integer("seed", 1, { it.seed }, { p, v -> p.copy(seed = v) }, min = 0),
            continuous("spatialScale", 0.01, 3, { it.spatialScale }, { p, v -> p.copy(spatialScale = v) }, min = 0.0, max = 5.0),
            continuous("timeScale", 0.01, 3, { it.timeScale }, { p, v -> p.copy(timeScale = v) }, min = 0.0, max = 5.0),
            cycle("noiseType", NoiseType.entries.toList(), { it.noiseType }, { p, v -> p.copy(noiseType = v) }),
            continuous("alphaPower", 0.1, 2, { it.alphaPower }, { p, v -> p.copy(alphaPower = v) }, min = 0.0, max = 20.0),
            continuous("alphaMin", 0.05, 2, { it.alphaMin }, { p, v -> p.copy(alphaMin = v) }, min = 0.0, max = 1.0),
            continuous("alphaMax", 0.05, 2, { it.alphaMax }, { p, v -> p.copy(alphaMax = v) }, min = 0.0, max = 1.0),
            integer("fbmOctaves", 1, { it.fbmOctaves }, { p, v -> p.copy(fbmOctaves = v) }, min = 1, max = 16),
            continuous("fbmLacunarity", 0.1, 3, { it.fbmLacunarity }, { p, v -> p.copy(fbmLacunarity = v) }, min = 1.0, max = 5.0),
            continuous("fbmGain", 0.02, 3, { it.fbmGain }, { p, v -> p.copy(fbmGain = v) }, min = 0.0, max = 1.0),
            degree("hueRange.min", { it.hueRange.min }, { p, v -> p.copy(hueRange = p.hueRange.copy(min = v)) }),
            degree("hueRange.max", { it.hueRange.max }, { p, v -> p.copy(hueRange = p.hueRange.copy(max = v)) }),
        )
    }

    var presetName by remember { mutableStateOf(currentPresetName.get()) }
    var params by remember { mutableStateOf(currentParams.get()) }
    var selectedRow by remember { mutableIntStateOf(0) }
    var sFps by remember { mutableDoubleStateOf(0.0) }
    var rFps by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(250)
            sFps = shaderFps.get()
            rFps = renderFps.get()
        }
    }

    fun applyParams(newParams: ShaderParameters) {
        edited[presetName] = newParams
        currentParams.set(newParams)
        params = newParams
    }

    fun switchPreset(direction: Int) {
        val idx = presets.indexOfFirst { it.name == presetName }
        val newIdx = ((idx + direction) % presets.size + presets.size) % presets.size
        val newName = presets[newIdx].name
        presetName = newName
        currentPresetName.set(newName)
        val newParams = edited.getValue(newName)
        currentParams.set(newParams)
        params = newParams
    }

    fun resetCurrent() {
        applyParams(originals.getValue(presetName))
    }

    val totalRows = knobs.size + 1

    Column(
        modifier = Modifier.onKeyEvent { event ->
            val mod = when {
                event.shift -> StepModifier.Coarse
                event.alt -> StepModifier.Fine
                else -> StepModifier.Normal
            }
            when (event.key) {
                "ArrowUp" -> {
                    selectedRow = (selectedRow - 1 + totalRows) % totalRows
                    true
                }
                "ArrowDown" -> {
                    selectedRow = (selectedRow + 1) % totalRows
                    true
                }
                "ArrowLeft", "ArrowRight" -> {
                    val sign = if (event.key == "ArrowLeft") -1 else 1
                    if (selectedRow == 0) {
                        switchPreset(sign)
                    } else {
                        val knob = knobs[selectedRow - 1]
                        applyParams(knob.change(params, sign, mod))
                    }
                    true
                }
                "r", "R" -> { resetCurrent(); true }
                "q", "Q" -> exitProcess(0)
                else -> false
            }
        },
    ) {
        val isModified = params != originals.getValue(presetName)
        val presetColor = if (selectedRow == 0) Yellow else White
        val presetArrow = if (selectedRow == 0) "→" else " "
        Text(
            buildAnnotatedString {
                append("$presetArrow Preset: $presetName")
                if (isModified) {
                    withStyle(SpanStyle(color = Edited)) {
                        append("  [MODIFIED]")
                    }
                }
            },
            color = presetColor,
        )
        Text("  FPS  shader=%.1f  render=%.1f".format(sFps, rFps), color = Muted)
        Spacer(Modifier.height(1))

        knobs.forEachIndexed { i, knob ->
            val rowIdx = i + 1
            val isFocused = selectedRow == rowIdx
            val origValue = knob.display(originals.getValue(presetName))
            val curValue = knob.display(params)
            val isDirty = origValue != curValue
            val arrow = if (isFocused) "→" else " "
            val color = when {
                isFocused -> Yellow
                isDirty -> Edited
                else -> White
            }
            val origSuffix = if (isDirty) "  (was $origValue)" else ""
            Text("$arrow %-16s %s%s".format(knob.name, curValue, origSuffix), color = color)
        }

        Spacer(Modifier.height(1))
        Text("←/→ change · ↑/↓ select · Shift=×10 · Alt=×0.1 · R reset · q quit", color = Muted)
    }
}
