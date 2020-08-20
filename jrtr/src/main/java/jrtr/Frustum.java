package jrtr;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * Stores the specification of a viewing frustum, or a viewing volume. The
 * viewing frustum is represented by a 4x4 projection matrix. You will extend
 * this class to construct the projection matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface},
 * {@link SimpleSceneManager}) stores a frustum.
 */
public class Frustum {

	private Matrix4f projectionMatrix;

	/**
	 * Construct a default viewing frustum. The frustum is given by a default 4x4
	 * projection matrix.
	 */
	public Frustum() {
		projectionMatrix = new Matrix4f();
		float f[] = { 1.f, 0.f, 0.f, 0.f,
				0.f, 1.f, 0.f, 0.f,
				0.f, 0.f, -1.02f, -2.02f,
				0.f, 0.f, -1.f, 0.f };
		projectionMatrix.set(f);
	}

	/**
	 * Return the 4x4 projection matrix, which is used for example by the renderer.
	 * 
	 * @return the 4x4 projection matrix
	 */
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setProjectionMatrix(Matrix4f m) {
		this.projectionMatrix = m;
	}

	public boolean inBounds(Vector3f point, float r) {
		float root2 = (float) Math.sqrt(2);
		Vector3f norm1 = new Vector3f(1 / root2, 0, 1 / root2);
		Vector3f norm2 = new Vector3f(-1 / root2, 0, 1 / root2);
		Vector3f norm3 = new Vector3f(0, 1 / root2, 1 / root2);
		Vector3f norm4 = new Vector3f(0, -1 / root2, 1 / root2);
		if (point.dot(norm1) < r && point.dot(norm2) < r && point.dot(norm3) < r && point.dot(norm4) < r) {
			return true;
		}
		return false;
	}
}
