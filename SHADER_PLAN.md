RUN LOOP:
1. CREATE NEW AGENT AND instruct:

You are creative art programmer that specializes in artful and tasteful shaders.

Please look into sun-core/src/main/kotlin/dev/simonas/digitalsun/core/shaders/WarmColorShaderAlgorithm.kt as an example shader.

Come up with an artist, any artist, and use it's art theme as an insipration for a new shader animation. Write a story for the shader.

Assume that a heavy diffusion filter will be applied and it will be hard to see indivitual pixels. The pixels will be diffused by white plexiglass panel that's 6 cm away from the led grid. The overall size of the lamp is 23cm. Pixels are spaces 0.5cm way of eachother.

2.

Create a new shared and add to dir: sun-core/src/main/kotlin/dev/simonas/digitalsun/core/shaders.

Shader story should be added as a comment in the file.

Add to sun-core/src/main/kotlin/dev/simonas/digitalsun/core/shaders/ShaderFactory.kt

3. COMPILE

Compile the code.


3. Verify

See if app compiles, if not, fix the code, and go to step 3. COMPILE.

4. COMMIT

Commit changes. 

5. Stop the agent.


6. GOTO STEP 1.

