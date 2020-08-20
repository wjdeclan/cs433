#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D myTexture;

// Variables passed in from the vertex shader
in float ndotl[2];
in float hdotn[2];
in vec3 lColor[2];
in vec2 frag_texcoord;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	// The built-in GLSL function "texture" performs the texture lookup
	frag_shaded = ((texture(myTexture, frag_texcoord)[0]+ texture(myTexture, frag_texcoord)[1]+texture(myTexture, frag_texcoord)[2])*(pow(hdotn[0], 4) + ndotl[0]) * vec4(lColor[0], 0) + ((texture(myTexture, frag_texcoord)[0]+ texture(myTexture, frag_texcoord)[1]+texture(myTexture, frag_texcoord)[2])*pow(hdotn[1], 4) + ndotl[1]) * vec4(lColor[1], 0)) * texture(myTexture, frag_texcoord);
}

