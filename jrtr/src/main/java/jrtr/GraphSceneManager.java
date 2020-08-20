package jrtr;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;

import javax.vecmath.*;

public class GraphSceneManager implements SceneManagerInterface {

	private SceneNode root;
	private LinkedList<Light> lights;
	private Camera camera;
	private Frustum frustum;
	
	private boolean doCull = true;

	/**
	 * Implement the iterative graph traversal here.
	 */
	private class GraphSceneManagerItr implements SceneManagerIterator {
		private class RItem {
			public SceneNode n;
			public Matrix4f m;

			public RItem(SceneNode n, Matrix4f m) {
				this.n = n;
				this.m = m;
			}
		}

		private Stack<RItem> nodes = new Stack<RItem>();
		private LinkedList<RenderItem> items = new LinkedList<RenderItem>();
		private ListIterator<RenderItem> itr;

		// Dummy implementation.
		public GraphSceneManagerItr(GraphSceneManager sceneManager)
		{
			Matrix4f iden = new Matrix4f();
			iden.setIdentity();
			nodes.push(new RItem(sceneManager.root, iden));
			while (nodes.empty() == false) {
				RItem r = nodes.pop();
				SceneNode n = r.n;
				if (n instanceof TransformGroup) {
					Iterator<SceneNode> i = ((TransformGroup) n).getChildrenIterator();
					while (i.hasNext()) {
						SceneNode next = i.next();
						Matrix4f nextM = new Matrix4f(r.m);
						nextM.mul(((TransformGroup) n).getTransformation());
						nodes.push(new RItem(next, nextM));
					}
				} else if (n instanceof ShapeNode) {
					Matrix4f m = new Matrix4f(r.m);
					Shape s = ((ShapeNode) n).getShape();
					m.mul(s.getTransformation());
					if (doCull) {
						Vector4f p = new Vector4f();
						Matrix4f c = new Matrix4f(camera.getCameraMatrix());
						c.mul(m);
						c.getColumn(3, p);
						if (frustum.inBounds(new Vector3f(p.x, p.y, p.z), ((ShapeNode) n).getBoundingRadius())) {
							items.add(new RenderItem(s, m));
						}
					} else {
						items.add(new RenderItem(s, m));
					}
				}
			}
			
			itr = (ListIterator<RenderItem>) items.iterator();
		}

		// Dummy implementation.
		public boolean hasNext() {
			return itr.hasNext();
		}

		// Dummy implementation.
		public RenderItem next() {
			return itr.next();
		}
	}

	public GraphSceneManager(SceneNode root) {
		this.root = root;
		camera = new Camera();
		frustum = new Frustum();
		lights = new LinkedList<Light>();
	}

	public Camera getCamera() {
		return camera;
	}

	public Frustum getFrustum() {
		return frustum;
	}

	public SceneManagerIterator iterator() {
		return new GraphSceneManagerItr(this);
	}

	public void addLight(Light light) {
		lights.add(light);
	}

	public Iterator<Light> lightIterator() {
		return lights.iterator();
	}

	public void toggleCull() {
		doCull = !doCull;
	}
}