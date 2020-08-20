#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D myTexture;

// Variables passed in from the vertex shader
in float ndotl[2];
in vec3 lColor[2];
in vec2 frag_texcoord;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	// The built-in GLSL function "texture" performs the texture lookup
	frag_shaded = (ndotl[0] * vec4(lColor[0], 0) + ndotl[1] * vec4(lColor[1], 0)) * texture(myTexture, frag_texcoord);
}

