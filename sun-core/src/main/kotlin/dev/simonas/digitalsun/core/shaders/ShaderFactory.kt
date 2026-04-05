package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.NoiseGenerator
import dev.simonas.digitalsun.core.PixelShader

data class NamedShader(
    val name: String,
    val shader: PixelShader,
)

object ShaderFactory {
    fun all(noiseGenerator: NoiseGenerator): List<NamedShader> = listOf(
        NamedShader("startup", StartupShaderAlgorithm()),
        NamedShader("torsion", TorsionShaderAlgorithm()),
        NamedShader("warp", WarpFbmShaderAlgorithm()),
        NamedShader("warm", WarmColorShaderAlgorithm(noiseGenerator)),
        NamedShader("red", V1RedShaderAlgorithm(noiseGenerator)),
        NamedShader("rothko", RothkoFieldShaderAlgorithm()),
        NamedShader("hilma", HilmaSpiralsShaderAlgorithm()),
        NamedShader("kusama", KusamaDotsShaderAlgorithm()),
    )
}
