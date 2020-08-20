#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;
uniform vec4 lightPosition[2];
uniform vec3 lightColor[2];
uniform int nLights;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out float ndotl[2];
out vec3 lColor[2];
out vec2 frag_texcoord;

void main()
{
	// Compute dot product of normal and light direction
	// and pass color to fragment shader
	// Note: here we assume "lightDirection" is specified in camera coordinates,
	// so we transform the normal to camera coordinates, and we don't transform
	// the light direction, i.e., it stays in camera coordinates
	ndotl[0] = max(dot(modelview * vec4(normal,0), normalize(lightPosition[0]-(modelview * position))),0);
	ndotl[1] = max(dot(modelview * vec4(normal,0), normalize(lightPosition[1]-(modelview * position))),0);
	
	lColor[0] = 100 * lightColor[0] / (distance(lightPosition[0], (modelview * position))*distance(lightPosition[0], (modelview * position)));
	lColor[1] = 100 * lightColor[1] / (distance(lightPosition[1], (modelview * position))*distance(lightPosition[1], (modelview * position)));

	// Pass texture coordiantes to fragment shader, OpenGL automatically
	// interpolates them to each pixel  (in a perspectively correct manner) 
	frag_texcoord = texcoord;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
