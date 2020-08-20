A series of simple programs using the JRTR package for rendering.


Simple - Alternating Torus/Circle objects (press T/C respectively)

Simple2 - Animated scene

Simple3 - Virtual trackball on a teapot object (press N to show teapot, use mouse to drag the 'trackball' around)

Simple4 - Simple but with color data for the torus

Simple5 - Lighting on a cube, cyclinder, and teapot (press Z/C/Y respectively), a torus is also shown, but does not have lighting information


Simple7 - Animated scene using GraphSceneManager

Simple8 - Scene with many teapots that allows toggling culling (press L) in the GraphSceneManager to demonstrate efficiency

Simple9 - Scene generated with BezierCurve and SurfaceOfRevolution

Simple10 - Generates a scene of a plane over a mountain range (randomly generated DiamondSquareLandscape, colored by vertex height), that can be navigated with WASD


GraphSceneManager - A SceneManager that uses TransformGroups to allow for layers of transformations to more smoothly animate scenes, also provides culling of objects outside the camera's view

BezierCurve - Generates piecewise or cubic curves based on a list of Vector3f points in space with a given resolution (num)

SurfaceOfRevolution - Generates the indices, texture coordinates, and normals of a surface of revolution given two arrays of points (on a curve) and their tangents, and a number of slices to make (k)

DiamondSquareLandscape - Given an int n, generates a 2^n+1 by 2^n+1 set of randomized triangles, colored by their height, using the diamond-square algorithm


blinn - Blinn shader

diffuse2 - Diffuse shader for multiple lights

diffuse3 - Allows for multiple colored lights


Only modified files are included
