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
        NamedShader("riley", RileyWaveShaderAlgorithm()),
        NamedShader("eliasson", EliassonSunShaderAlgorithm()),
        NamedShader("monet", MonetLiliesShaderAlgorithm()),
        NamedShader("kandinsky", KandinskyCompositionShaderAlgorithm()),
        NamedShader("turrell", TurrellGanzfeldShaderAlgorithm()),
        NamedShader("hokusai", HokusaiWaveShaderAlgorithm()),
        NamedShader("vermeer", VermeerLightShaderAlgorithm()),
        NamedShader("martin", MartinBandsShaderAlgorithm()),
        NamedShader("klimt", KlimtGoldShaderAlgorithm()),
        NamedShader("infinity", KusamaInfinityShaderAlgorithm()),
        NamedShader("lewitt", LeWittWallShaderAlgorithm()),
        NamedShader("caravaggio", CaravaggioChiaroscuroShaderAlgorithm()),
    )
}
