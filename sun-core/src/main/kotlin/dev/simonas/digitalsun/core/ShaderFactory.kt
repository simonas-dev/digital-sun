package dev.simonas.digitalsun.core

import dev.simonas.digitalsun.core.shaders.WarmColorShaderAlgorithm

data class ShaderPreset(
    val name: String,
    val params: ShaderParameters,
)

object ShaderFactory {
    fun allPresets(): List<ShaderPreset> = WarmColorShaderAlgorithm.allPresets()
}
