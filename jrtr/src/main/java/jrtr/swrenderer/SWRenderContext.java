package jrtr.swrenderer;

import jrtr.RenderContext;
import jrtr.RenderItem;
import jrtr.SceneManagerInterface;
import jrtr.SceneManagerIterator;
import jrtr.Shader;
import jrtr.Texture;
import jrtr.VertexData;
import jrtr.Material;

import java.awt.image.*;
import javax.vecmath.*;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * A skeleton for a software renderer. It works in combination with
 * {@link SWRenderPanel}, which displays the output image. In project 2 you will
 * implement your own rasterizer in this class.
 * <p>
 * To use the software renderer, you will simply replace {@link GLRenderPanel}
 * with {@link SWRenderPanel} in the user application.
 */
public class SWRenderContext implements RenderContext {

	private SceneManagerInterface sceneManager;
	private BufferedImage colorBuffer;
	private int[] zeroColorBuffer;
	private int width, height;

	// Rendering pipeline state variables
	private Matrix4f viewportMatrix;
	private Matrix4f projectionMatrix;
	private float[] vertexColor;
	private float[] vertexNormal;
	private float[] vertexTexCoords;
	private float[] zBuffer;

	public SWRenderContext() {
		// Initialize rendering pipeline state variables to default values
		projectionMatrix = new Matrix4f();
		viewportMatrix = new Matrix4f();
		vertexColor = new float[3];
		vertexColor[0] = 1.f;
		vertexColor[1] = 1.f;
		vertexColor[2] = 1.f;
		vertexNormal = new float[3];
		vertexTexCoords = new float[2];
	}

	public void setSceneManager(SceneManagerInterface sceneManager) {
		this.sceneManager = sceneManager;
	}

	/**
	 * This is called by the SWRenderPanel to render the scene to the software frame
	 * buffer.
	 */
	public void display() {
		if (sceneManager == null)
			return;

		beginFrame();

		SceneManagerIterator iterator = sceneManager.iterator();
		while (iterator.hasNext()) {
			draw(iterator.next());
		}

		endFrame();
	}

	/**
	 * This is called by the {@link SWJPanel} to obtain the color buffer that will
	 * be displayed.
	 */
	public BufferedImage getColorBuffer() {
		return colorBuffer;
	}

	/**
	 * Set a new viewport size. The render context will also need to store a
	 * viewport matrix, which you need to reset here.
	 */
	public void setViewportSize(int width, int height) {
		this.width = width;
		this.height = height;

		// Set viewport matrix, note that the y coordinate
		// is multiplied by -1, because the java BufferedImage
		// has its origin at the top right
		viewportMatrix = new Matrix4f();
		viewportMatrix.setIdentity();
		viewportMatrix.setElement(0, 0, (float) width / 2.f);
		viewportMatrix.setElement(0, 3, (float) width / 2.f);
		viewportMatrix.setElement(1, 1, -(float) height / 2.f);
		viewportMatrix.setElement(1, 3, (float) height / 2.f);
		viewportMatrix.setElement(2, 2, .5f);
		viewportMatrix.setElement(2, 3, .5f);

		// Allocate framebuffer
		colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		zeroColorBuffer = new int[width * height];
		zBuffer = new float[width * height];
	}

	/**
	 * Clear the framebuffer here.
	 */
	private void beginFrame() {
		projectionMatrix = sceneManager.getFrustum().getProjectionMatrix();

		// Clear framebuffer
		colorBuffer.setRGB(0, 0, width, height, zeroColorBuffer, 0, width);
		zBuffer = new float[width * height];
	}

	private void endFrame() {
	}

	/**
	 * The main rendering method. This collects all information necessary to render
	 * each triangle and calls @drawTriangle to perform the rasterization.
	 */
	private void draw(RenderItem renderItem) {
		VertexData vertexData = renderItem.getShape().getVertexData();
		LinkedList<VertexData.VertexElement> vertexElements = vertexData.getElements();
		int indices[] = vertexData.getIndices();

		// Don't draw if there are no indices
		if (indices == null)
			return;

		// Vertex attributes for a triangle
		float[][] colors = new float[3][3];
		float[][] positions = new float[3][4];
		float[][] normals = new float[3][3];
		float[][] texCoords = new float[3][2];

		// Construct full transformation matrix
		Matrix4f t = new Matrix4f(viewportMatrix);
		t.mul(projectionMatrix);
		t.mul(sceneManager.getCamera().getCameraMatrix());
		t.mul(renderItem.getT());

		// Draw geometry
		int k = 0; // index of triangle vertex
		for (int j = 0; j < indices.length; j++) {
			int i = indices[j];

			// Iterate over vertex elements, i.e., position, color, normal, texture, etc.
			ListIterator<VertexData.VertexElement> itr = vertexElements.listIterator(0);
			while (itr.hasNext()) {
				VertexData.VertexElement e = itr.next();
				if (e.getSemantic() == VertexData.Semantic.POSITION) {
					Vector4f p = new Vector4f(e.getData()[i * 3], e.getData()[i * 3 + 1], e.getData()[i * 3 + 2], 1);
					t.transform(p);
					positions[k][0] = p.x;
					positions[k][1] = p.y;
					positions[k][2] = p.z;
					positions[k][3] = p.w;

					// Assign the other "state variables" to the current vertex
					colors[k][0] = vertexColor[0];
					colors[k][1] = vertexColor[1];
					colors[k][2] = vertexColor[2];

					normals[k][0] = vertexNormal[0];
					normals[k][1] = vertexNormal[1];
					normals[k][2] = vertexNormal[2];

					texCoords[k][0] = vertexTexCoords[0];
					texCoords[k][1] = vertexTexCoords[1];

					k++;
				}
				// Read the "state variables" for color, normals, textures, if they are
				// available
				if (e.getSemantic() == VertexData.Semantic.COLOR) {
					vertexColor[0] = e.getData()[i * 3];
					vertexColor[1] = e.getData()[i * 3 + 1];
					vertexColor[2] = e.getData()[i * 3 + 2];
				}
				if (e.getSemantic() == VertexData.Semantic.NORMAL) {
					vertexNormal[0] = e.getData()[i * 3];
					vertexNormal[1] = e.getData()[i * 3 + 1];
					vertexNormal[2] = e.getData()[i * 3 + 2];
				}
				if (e.getSemantic() == VertexData.Semantic.TEXCOORD) {
					vertexTexCoords[0] = e.getData()[i * 2];
					vertexTexCoords[1] = e.getData()[i * 2 + 1];
				}
			}

			if (k == 3) {
				drawTriangle(positions, colors, normals, texCoords, renderItem.getShape().getMaterial());
				k = 0;
			}
		}
	}

	/**
	 * Draw a triangle. Implement triangle rasterization here. You will need to
	 * include a z-buffer to resolve visibility.
	 */
	void drawTriangle(float positions[][], float colors[][], float normals[][], float texCoords[][], Material mat) {
		int iS = 0;
		int iE = width;
		int jS = 0;
		int jE = height;
		Matrix3f pMat = new Matrix3f();
		Matrix3f cMat = new Matrix3f();
		Matrix3f pCoeff = new Matrix3f();
		for (int i = 0; i < 3; i++) {
			pMat.setRow(i, positions[i][0], positions[i][1], positions[i][3]);
			cMat.setRow(i, colors[i][0], colors[i][1], colors[i][2]);
		}

		float[] w = new float[3];
		pMat.getColumn(2, w);
		if (w[0] < 0 && w[1] < 0 && w[2] < 0) {
			return;
		} else if (w[0] > 0 && w[1] > 0 && w[2] > 0) {
			float[] x = new float[3];
			float[] y = new float[3];
			pMat.getColumn(0, x);
			pMat.getColumn(1, y);
			iS = Math.round(Math.max(Math.min(x[0] / w[0], Math.min(x[1] / w[1], x[2] / w[2])), 0));
			iE = Math.round(Math.min(Math.max(x[0] / w[0], Math.max(x[1] / w[1], x[2] / w[2])), width));
			jS = Math.round(Math.max(Math.min(y[0] / w[0], Math.min(y[1] / w[1], y[2] / w[2])), 0));
			jE = Math.round(Math.min(Math.max(y[0] / w[0], Math.max(y[1] / w[1], y[2] / w[2])), height));
		}

		pCoeff.invert(pMat);
		Matrix3f cCoeff = new Matrix3f(pCoeff);
		cCoeff.mul(cMat);

		Vector3f v = new Vector3f();
		Vector3f wInv = new Vector3f();
		pCoeff.getRow(0, v);
		wInv.setX(v.dot(new Vector3f(1, 1, 1)));
		pCoeff.getRow(1, v);
		wInv.setY(v.dot(new Vector3f(1, 1, 1)));
		pCoeff.getRow(2, v);
		wInv.setZ(v.dot(new Vector3f(1, 1, 1)));

		for (int i = iS; i < iE; i++) {
			for (int j = jS; j < jE; j++) {
				float x = i + 0.5f;
				float y = j + 0.5f;

				float[] alpha = new float[3];
				pCoeff.getColumn(0, alpha);
				float[] beta = new float[3];
				pCoeff.getColumn(1, beta);
				float[] gamma = new float[3];
				pCoeff.getColumn(2, gamma);
				if (alpha[0] * x + alpha[1] * y + alpha[2] > 0) {
					if (beta[0] * x + beta[1] * y + beta[2] > 0) {
						if (gamma[0] * x + gamma[1] * y + gamma[2] > 0) {
							float wInvVal = wInv.getX() * x + wInv.getY() * y + wInv.getZ();
							if (wInvVal > zBuffer[i + j * width]) {
								wInv.setZ(v.dot(new Vector3f(1, 1, 1)));
								// colorBuffer.setRGB(i, j, ((int) colors[0][0] * 255 << 16)
								// | ((int) colors[0][1] * 255 << 8) | ((int) colors[0][2] * 255));

								Vector3f col = new Vector3f();
								cCoeff.getColumn(0, col);
								float red = (col.getX() * x + col.getY() * y + col.getZ()) / wInvVal;
								cCoeff.getColumn(1, col);
								float green = (col.getX() * x + col.getY() * y + col.getZ()) / wInvVal;
								cCoeff.getColumn(2, col);
								float blue = (col.getX() * x + col.getY() * y + col.getZ()) / wInvVal;

								colorBuffer.setRGB(i, j,
										((int) (red * 255) << 16) | ((int) (green * 255) << 8) | ((int) (blue * 255)));

								zBuffer[i + j * width] = wInvVal;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public Shader makeShader() {
		return new SWShader();
	}

	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public void useShader(Shader s) {
	}

	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public void useDefaultShader() {
	}

	/**
	 * Does nothing. We will not implement textures for the software renderer.
	 */
	public Texture makeTexture() {
		return new SWTexture();
	}

	public VertexData makeVertexData(int n) {
		return new SWVertexData(n);
	}
}
