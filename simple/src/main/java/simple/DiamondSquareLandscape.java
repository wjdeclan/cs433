package simple;

import javax.vecmath.Vector3f;

public class DiamondSquareLandscape {
	public float[] vertices;
	public float[] normals;
	public float[] colors;
	public int[] indices;
	
	public Vector3f[][] dsMap;
	
	public DiamondSquareLandscape(int n) {
		int resolution = (int) (Math.pow(2, n) + 1);
		float rand = 6f;
		int step = resolution - 1;
		
		vertices = new float[resolution * resolution * 3];
		normals = new float[resolution * resolution * 3];
		colors = new float[resolution * resolution * 3];
		indices = new int[3 * 2 * (resolution - 1) * (resolution - 1)];
		
		dsMap = new Vector3f[resolution][resolution];
		dsMap[0][0] = new Vector3f(-resolution*0.3f, -resolution*0.3f, 0);
		dsMap[resolution-1][0] = new Vector3f(resolution*0.3f, -resolution*0.3f, -10);
		dsMap[0][resolution-1] = new Vector3f(-resolution*0.3f, resolution*0.3f, 10);
		dsMap[resolution-1][resolution-1] = new Vector3f(resolution*0.3f, resolution*0.3f, -5f);
		
		
		while (step >= 2) {
			diamond(rand, resolution, step);
			square(rand, resolution, step);
			rand = rand * 0.75f;
			step = step / 2;
		}
		
		Vector3f[][] normalMap = new Vector3f[resolution][resolution];
		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				normalMap[i][j] = new Vector3f(0,0,0);
			}
		}
		
		for (int i = 0; i < resolution-1; i++) {
			for (int j = 0; j < resolution-1; j++) {
				Vector3f a = new Vector3f(dsMap[i][j]);
				Vector3f b = new Vector3f(dsMap[i+1][j+1]);
				Vector3f v1 = new Vector3f(dsMap[i][j+1]);
				v1.sub(a);
				b.sub(a);
				a.cross(b, v1);
				a.normalize();

				normalMap[i][j].add(a);
				normalMap[i+1][j+1].add(a);
				normalMap[i][j+1].add(a);
				
				a = new Vector3f(dsMap[i][j]);
				b = new Vector3f(dsMap[i+1][j+1]);
				Vector3f v2 = new Vector3f(dsMap[i+1][j]);
				v2.sub(a);
				b.sub(a);
				a.cross(v2, b);
				a.normalize();
				
				normalMap[i][j].add(a);
				normalMap[i+1][j+1].add(a);
				normalMap[i+1][j].add(a);
			}
		}
		
		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				normalMap[i][j].normalize();
			}
		}
		
		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				int offset = 3*(i*resolution + j);
				vertices[offset+0] = dsMap[i][j].x;
				vertices[offset+1] = dsMap[i][j].y;
				vertices[offset+2] = dsMap[i][j].z;
				
				normals[offset+0] = normalMap[i][j].x;
				normals[offset+1] = normalMap[i][j].y;
				normals[offset+2] = normalMap[i][j].z;
				
				float col = dsMap[i][j].z/10f;
				col += (Math.random()-0.5f)*0.25f;
				if (col > 0.2f) {
					colors[offset+0] = col;
					colors[offset+1] = col;
					colors[offset+2] = col;
				} else if (col > -0.1f) {
					colors[offset+0] = col+0.2f;
					colors[offset+1] = (col+0.2f)*0.8f;
					colors[offset+2] = (col+0.2f)*0.2f;
				}	else {
					colors[offset+0] = 0;
					colors[offset+1] = 1-(1+col);
					colors[offset+2] = 0;
				}
				
				if (i < resolution - 1 && j < resolution - 1) {
					offset = 6*(i*(resolution-1) + j);
					indices[offset+0] = i*resolution + j;
					indices[offset+1] = i*resolution + j+1;
					indices[offset+2] = (i+1)*resolution + j+1;
					
					indices[offset+3] = i*resolution + j;
					indices[offset+4] = (i+1)*resolution + j;
					indices[offset+5] = (i+1)*resolution + j+1;
				}
			}
		}
	}
	
	private void diamond(float rand, int resolution, int step) {
		int halfstep = step / 2;
		
		for (int i = halfstep; i < resolution; i+=step) {
			for (int j = halfstep; j < resolution; j+= step) {
				Vector3f a = dsMap[i-halfstep][j-halfstep];
				Vector3f b = dsMap[i+halfstep][j-halfstep];
				Vector3f c = dsMap[i-halfstep][j+halfstep];
				Vector3f d = dsMap[i+halfstep][j+halfstep];
				
				Vector3f n = new Vector3f((a.x + d.x)/2f, (a.y + d.y)/2f, (((float) Math.random())-0.5f)*rand+(a.z + d.z + b.z + c.z)/4f);
				dsMap[i][j] = n;
			}
		}
	}
	
	private void square(float rand, int resolution, int step) {
		int halfstep = step / 2;
		
		for (int i = 0; i < resolution; i+=halfstep) {
			for (int j = 0; j < resolution; j+= halfstep) {
				if (dsMap[i][j] == null) {
					Vector3f a = null, b = null, c = null, d = null;
					float cumulative = 0f;
					float count = 0f;
					if (j > 0) {
						a = dsMap[i][j-halfstep];
						cumulative += a.z;
						count += 1;
					}
					if (j < resolution - 1) {
						b = dsMap[i][j+halfstep];
						cumulative += b.z;
						count += 1;
					}
					if (i > 0) {
						c = dsMap[i-halfstep][j];
						cumulative += c.z;
						count += 1;
					}
					if (i < resolution - 1) {
						d = dsMap[i+halfstep][j];
						cumulative += d.z;
						count += 1;
					}
					Vector3f n = new Vector3f(0, 0, 0);
					if (a != null && b != null) {
						n.x = (a.x + b.x)/2f;
						n.y = (a.y + b.y)/2f;
					} else {
						n.x = (c.x + d.x)/2f;
						n.y = (c.y + d.y)/2f;
					}
					n.z = cumulative/count+(((float) Math.random())-0.5f)*rand;
					
					dsMap[i][j] = n;
				}
			}
		}
	}
}
