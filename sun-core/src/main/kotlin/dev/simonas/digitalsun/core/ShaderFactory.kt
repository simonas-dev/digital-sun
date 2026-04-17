package dev.simonas.digitalsun.core

import dev.simonas.digitalsun.core.shaders.WarmColorShaderAlgorithm

data class NamedShader(
    val name: String,
    val shader: PixelShader,
)

object ShaderFactory {
    fun all(noiseGenerator: NoiseGenerator): List<NamedShader> = buildList {
        addAll(WarmColorShaderAlgorithm.all(noiseGenerator))
    }
}
