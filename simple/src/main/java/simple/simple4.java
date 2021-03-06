package simple;

import jrtr.*;
import jrtr.glrenderer.*;
import jrtr.swrenderer.*;

import javax.swing.*;
import java.awt.event.*;
import javax.vecmath.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple 3D rendering application using the 3D rendering API 
 * provided by the package {@link jrtr}. Opens a 3D rendering window and 
 * shows a rotating cube. 
 */
public class simple4
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static Shader normalShader;
	static Shader diffuseShader;
	static Material material;
	static SimpleSceneManager sceneManager;
	static Shape cylinder;
	static Shape torus;
	static Shape shape;
	static float currentstep, basicstep;

	/**
	 * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to 
	 * provide a call-back function for initialization. 
	 */ 
	public final static class SimpleRenderPanel extends SWRenderPanel
	{
		/**
		 * Initialization call-back. We initialize our renderer and scene here.
		 * We construct a simple 3D scene consisting of a cube and start a timer 
		 * task to generate an animation.
		 * 
		 * @param r	the render context that is associated with this render panel
		 */
		public void init(RenderContext r)
		{
			renderContext = r;
										
			// Make a scene manager and add the object
			sceneManager = new SimpleSceneManager();
			cylinder = new Shape(makeCylinder(6));
			torus = new Shape(makeTorus(8));
			shape = torus;
			sceneManager.addShape(shape);

			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);
			
			// Load some more shaders
		    normalShader = renderContext.makeShader();
		    try {
		    	normalShader.load("../jrtr/shaders/normal.vert", "../jrtr/shaders/normal.frag");
		    } catch(Exception e) {
		    	System.out.print("Problem with shader:\n");
		    	System.out.print(e.getMessage());
		    }
	
		    diffuseShader = renderContext.makeShader();
		    try {
		    	diffuseShader.load("../jrtr/shaders/diffuse.vert", "../jrtr/shaders/diffuse.frag");
		    } catch(Exception e) {
		    	System.out.print("Problem with shader:\n");
		    	System.out.print(e.getMessage());
		    }

		    // Make a material that can be used for shading
			material = new Material();
			material.shader = diffuseShader;
			material.diffuseMap = renderContext.makeTexture();
			try {
				material.diffuseMap.load("../textures/plant.jpg");
			} catch(Exception e) {				
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}

			// Register a timer task
		    Timer timer = new Timer();
		    basicstep = 0.01f;
		    currentstep = basicstep;
		    timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);
		}

		/**
		 * Make a mesh for a cube.
		 * 
		 * @return vertexData the data representing the cube mesh
		 */
		private VertexData makeCube()
		{
			// Make a simple geometric object: a cube
		
			// The vertex positions of the cube
			float v[] = {-1,-1,1, 1,-1,1, 1,1,1, -1,1,1,		// front face
				         -1,-1,-1, -1,-1,1, -1,1,1, -1,1,-1,	// left face
					  	 1,-1,-1, -1,-1,-1, -1,1,-1, 1,1,-1,		// back face
						 1,-1,1, 1,-1,-1, 1,1,-1, 1,1,1,		// right face
						 1,1,1, 1,1,-1, -1,1,-1, -1,1,1,		// top face
						-1,-1,1, -1,-1,-1, 1,-1,-1, 1,-1,1};	// bottom face

			// The vertex normals 
			float n[] = {0,0,1, 0,0,1, 0,0,1, 0,0,1,			// front face
				         -1,0,0, -1,0,0, -1,0,0, -1,0,0,		// left face
					  	 0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1,		// back face
						 1,0,0, 1,0,0, 1,0,0, 1,0,0,			// right face
						 0,1,0, 0,1,0, 0,1,0, 0,1,0,			// top face
						 0,-1,0, 0,-1,0, 0,-1,0, 0,-1,0};		// bottom face

			// The vertex colors
			float c[] = {1,0,0, 1,0,0, 1,0,0, 1,0,0,
					     0,1,0, 0,1,0, 0,1,0, 0,1,0,
						 1,0,0, 1,0,0, 1,0,0, 1,0,0,
						 0,1,0, 0,1,0, 0,1,0, 0,1,0,
						 0,0,1, 0,0,1, 0,0,1, 0,0,1,
						 0,0,1, 0,0,1, 0,0,1, 0,0,1};

			// Texture coordinates 
			float uv[] = {0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1};

			// Construct a data structure that stores the vertices, their
			// attributes, and the triangle mesh connectivity
			VertexData vertexData = renderContext.makeVertexData(24);
			vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
			vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
			vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
			vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
			
			// The triangles (three vertex indices for each triangle)
			int indices[] = {0,2,3, 0,1,2,			// front face
							 4,6,7, 4,5,6,			// left face
							 8,10,11, 8,9,10,		// back face
							 12,14,15, 12,13,14,	// right face
							 16,18,19, 16,17,18,	// top face
							 20,22,23, 20,21,22};	// bottom face

			vertexData.addIndices(indices);
			
			return vertexData;
		}
		
		/**
		 * Make a mesh for a cylinder.
		 * 
		 * @return vertexData the data representing the cylinder mesh
		 */
		private VertexData makeCylinder(int seg)
		{
			int vlen = 3*2*(seg+1); //coord is 3 elements long, 2 faces, face has seg + 1 (center) coords;
			
			float v[] = new float[vlen];
			float c[] = new float[vlen];
			
			for (int i = 0; i < vlen/2; i += 3) { //fill in colors alternatingly
				if (i % 2 == 0) { //black
					c[i] = 0;
					c[i+1] = 0;
					c[i+2] = 0;
					c[i+vlen/2] = 0;
					c[i+1+vlen/2] = 0;
					c[i+2+vlen/2] = 0;
				} else { //white
					c[i] = 1;
					c[i+1] = 1;
					c[i+2] = 1;
					c[i+vlen/2] = 1;
					c[i+1+vlen/2] = 1;
					c[i+2+vlen/2] = 1;
				}
			}
			
			//color the center of the top and bottom faces black
			c[0] = 0;
			c[1] = 0;
			c[2] = 0;
			c[vlen/2] = 0;
			c[vlen/2+1] = 0;
			c[vlen/2+2] = 0;

			//position the center vertices
			v[0] = 0;
			v[1] = 0;
			v[2] = 1;
			v[vlen/2] = 0;
			v[vlen/2+1] = 0;
			v[vlen/2+2] = -1;
			
			//get angle of each segment
			float step = 360/(float) seg;

			//space between vertices on each face within v[]
			int f = (seg+1)*3;
			
			for (int i = 0; i < seg; i++) {
				float angle = step*(float) i;
				int e = (i+1)*3;
				v[e] = (float) Math.sin(Math.toRadians(angle));
				v[e+1] = (float) Math.cos(Math.toRadians(angle));
				v[e+2] = 1;
				v[f+e] = (float) Math.sin(Math.toRadians(angle));
				v[f+e+1] = (float) Math.cos(Math.toRadians(angle));
				v[f+e+2] = -1;
			}
			
			// Construct a data structure that stores the vertices, their
			// attributes, and the triangle mesh connectivity
			VertexData vertexData = renderContext.makeVertexData((seg+1)*2);
			vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
			vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
			
			int indices[] = new int[seg*4*3]; //top triangle, 2 side triangles, bottom triangle, each takes 3 vertex indices
			
			for (int i = 1; i <= seg; i++) {
				int e = (i-1)*3*4; //4 triangles, 3 indices
				//top face
				indices[e] = 0;
				indices[e+1] = i;
				indices[e+2] = i%seg + 1;
				//side face, top favored
				indices[e+3] = i%seg + 1;
				indices[e+4] = i;
				indices[e+5] = i+(seg+1);
				//side face, bottom favored
				indices[e+6] = i%seg + 1;
				indices[e+7] = i+(seg+1);
				indices[e+8] = (i%seg + 1)+(seg+1);
				//bottom face
				indices[e+9] = (i%seg + 1)+(seg+1);
				indices[e+10] = i+(seg+1);
				indices[e+11] = seg+1;
			}

			vertexData.addIndices(indices);
			
			return vertexData;
		}
		
		/**
		 * Make a mesh for a torus.
		 * 
		 * @return vertexData the data representing the torus mesh
		 */
		private VertexData makeTorus(int seg)
		{
			int vlen = 3*seg*seg; //coord is 3 elements long, circle defining a cross section is seg units, circle defining the torus is seg units
			
			float v[] = new float[vlen];
			float c[] = new float[vlen];
			
			for (int i = 0; i < seg; i++) { //fill in colors alternatingly
				int color = i % 2;
				for (int j = 0; j < seg; j++) {
					c[(i*seg+j)*3] = color;
					c[(i*seg+j)*3+1] = 0.5f;
					c[(i*seg+j)*3+2] = 1 - color;
				}
			}
			
			//get angle of each segment
			float step = 360/(float) seg;
			float R = 2f;
			float r = 1;
			
			for (int i = 0; i < seg; i++) { //fill in colors alternatingly
				float angle1 = step*(float) i;
				for (int j = 0; j < seg; j++) {
					float angle2 = step*(float) j;
					//parametric defintion of a torus
					//R is torus radius
					//r is tube radius
					v[(i*seg+j)*3] = (float) ((R + r*Math.cos(Math.toRadians(angle2)))*Math.cos(Math.toRadians(angle1)));
					v[(i*seg+j)*3+1] = (float) ((R + r*Math.cos(Math.toRadians(angle2)))*Math.sin(Math.toRadians(angle1)));
					v[(i*seg+j)*3+2] = (float) (r*Math.sin(Math.toRadians(angle2)));
				}
			}
			
			// Construct a data structure that stores the vertices, their
			// attributes, and the triangle mesh connectivity
			VertexData vertexData = renderContext.makeVertexData(seg*seg);
			vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
			vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
			
			int indices[] = new int[(seg*seg)*6]; //seg faces per seg sides, with 2 triangles per face, and 3 vertices per triangle
			
			for (int i = 0; i < seg; i++) {
				for (int j = 0; j < seg; j++) {
					//2 triangles * 3 vertices = 6
					//self favored triangle
					indices[(i*seg+j)*6] = i*seg+j;
					indices[(i*seg+j)*6+1] = i*seg+(j+1)%seg;
					indices[(i*seg+j)*6+2] = ((i+1)%seg)*seg+(j+1)%seg;
					//neighbor favored triangle
					indices[(i*seg+j)*6+3] = i*seg+j;
					indices[(i*seg+j)*6+4] = ((i+1)%seg)*seg+(j+1)%seg;
					indices[(i*seg+j)*6+5] = ((i+1)%seg)*seg+j;
					
				}
			}

			vertexData.addIndices(indices);
			
			return vertexData;
		}
	}

	/**
	 * A timer task that generates an animation. This task triggers
	 * the redrawing of the 3D scene every time it is executed.
	 */
	public static class AnimationTask extends TimerTask
	{
		public void run()
		{
			// Update transformation by rotating with angle "currentstep"
    		Matrix4f t = shape.getTransformation();
    		Matrix4f rotX = new Matrix4f();
    		rotX.rotX(currentstep);
    		Matrix4f rotY = new Matrix4f();
    		rotY.rotY(currentstep);
    		t.mul(rotX);
    		t.mul(rotY);
    		shape.setTransformation(t);
    		
    		// Trigger redrawing of the render window
    		renderPanel.getCanvas().repaint(); 
		}
	}

	/**
	 * A mouse listener for the main window. This can be
	 * used to process mouse events.
	 */
	public static class SimpleMouseListener implements MouseListener
	{
    	public void mousePressed(MouseEvent e) {}
    	public void mouseReleased(MouseEvent e) {}
    	public void mouseEntered(MouseEvent e) {}
    	public void mouseExited(MouseEvent e) {}
    	public void mouseClicked(MouseEvent e) {
    		if (currentstep == 0) {
    			currentstep = basicstep;
    		} else {
    			currentstep = 0;
    		}
    	}
	}
	
	/**
	 * A key listener for the main window. Use this to process key events.
	 * Currently this provides the following controls:
	 * 's': stop animation
	 * 'p': play animation
	 * '+': accelerate rotation
	 * '-': slow down rotation
	 * 'd': default shader
	 * 'n': shader using surface normals
	 * 'm': use a material for shading
	 */
	public static class SimpleKeyListener implements KeyListener
	{
		public void keyPressed(KeyEvent e)
		{
			switch(e.getKeyChar())
			{
				case 's': {
					// Stop animation
					currentstep = 0;
					break;
				}
				case 'p': {
					// Resume animation
					currentstep = basicstep;
					break;
				}
				case '+': {
					// Accelerate roation
					currentstep += basicstep;
					break;
				}
				case '-': {
					// Slow down rotation
					currentstep -= basicstep;
					break;
				}
				case 'c': {
					sceneManager.removeShape(shape);
					shape = cylinder;
					sceneManager.addShape(shape);
					break;
				}
				case 't': {
					sceneManager.removeShape(shape);
					shape = torus;
					sceneManager.addShape(shape);
					break;
				}
				case 'n': {
					// Remove material from shape, and set "normal" shader
					shape.setMaterial(null);
					renderContext.useShader(normalShader);
					break;
				}
				case 'd': {
					// Remove material from shape, and set "default" shader
					shape.setMaterial(null);
					renderContext.useDefaultShader();
					break;
				}
				case 'm': {
					// Set a material for more complex shading of the shape
					if(shape.getMaterial() == null) {
						shape.setMaterial(material);
					} else
					{
						shape.setMaterial(null);
						renderContext.useDefaultShader();
					}
					break;
				}
			}
			
			// Trigger redrawing
			renderPanel.getCanvas().repaint();
		}
		
		public void keyReleased(KeyEvent e)
		{
		}

		public void keyTyped(KeyEvent e)
        {
        }

	}
	
	/**
	 * The main function opens a window 3D rendering window, implemented by the class
	 * {@link SimpleRenderPanel}. {@link SimpleRenderPanel#init} is then called backed 
	 * for initialization automatically by the Java event dispatching thread (EDT), see
	 * <a href="https://stackoverflow.com/questions/7217013/java-event-dispatching-thread-explanation" target="_blank">
	 * this discussion on stackoverflow</a> and <a href="https://en.wikipedia.org/wiki/Event_dispatching_thread" target="_blank">
	 * this explanation on wikipedia</a>. Additional event listeners are added to handle mouse
	 * and keyboard events from the EDT. {@link SimpleRenderPanel#init}
	 * constructs a simple 3D scene, and starts a timer task to generate an animation.
	 */
	public static void main(String[] args)
	{		
		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();
		
		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("simple");
		jframe.setSize(500, 500);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window

		// Add a mouse and key listener
	    renderPanel.getCanvas().addMouseListener(new SimpleMouseListener());
	    renderPanel.getCanvas().addKeyListener(new SimpleKeyListener());
		renderPanel.getCanvas().setFocusable(true);   	    	    
	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setVisible(true); // show window
	}
}
