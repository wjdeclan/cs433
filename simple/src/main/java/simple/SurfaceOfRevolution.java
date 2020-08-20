package simple;

import javax.vecmath.Vector3f;

public class SurfaceOfRevolution {
	public float[] vertices;
	public float[] normals;
	public float[] texcoords;
	public int[] indices;
	
	public SurfaceOfRevolution(Vector3f[] points, Vector3f[] tangents, int k) {
		vertices = new float[3 * k * points.length];
		normals = new float[3 * k * points.length];
		texcoords = new float[2 * k * points.length];
		
		//3 indices per triangle, 2 triangles per box, n - 1 boxes per face, k faces
		indices = new int[3 * k * 2 * (points.length - 1)];
		
		Vector3f[] normalVecs = new Vector3f[tangents.length];
		for (int i = 0; i < normalVecs.length; i++) {
			tangents[i].normalize();
			normalVecs[i] = new Vector3f(-tangents[i].y, tangents[i].x, 0);
		}
		
		float step = 360f/(float) k;
		for (int i = 0; i < k; i++) {
			float angle = step*(float) i;
			for (int j = 0; j < points.length; j++) {
				int offset = 3*(i*points.length + j);
				
				float cosAngle = (float) Math.cos(Math.toRadians(angle));
				float sinAngle = (float) Math.sin(Math.toRadians(angle));
				
				vertices[offset + 0] = cosAngle * points[j].x;
				vertices[offset + 1] = points[j].y;
				vertices[offset + 2] = sinAngle * points[j].x;
				normals[offset + 0] = cosAngle * normalVecs[j].x;
				normals[offset + 1] = normalVecs[j].y;
				normals[offset + 2] = sinAngle * normalVecs[j].x;
				
				offset = 2*(i*points.length + j);
				texcoords[offset + 0] = (float) j/(float) points.length;
				texcoords[offset + 1] = angle/360f;
			}
		}
		
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < points.length - 1; j++) {
				int offset = 6*(i*(points.length - 1) + j);
				//self favored triangle
				indices[offset + 0] = (i * points.length) + j;
				indices[offset + 1] = (i * points.length) + (j + 1) % points.length;
				indices[offset + 2] = (i+1) % points.length * points.length + (j + 1) % points.length;
				//neighbor favored triangle
				indices[offset + 3] = (i * points.length) + j;
				indices[offset + 4] = (i+1) % points.length * points.length + (j + 1) % points.length;
				indices[offset + 5] = (i+1) % points.length * points.length + j;
			}
		}
	}
}
