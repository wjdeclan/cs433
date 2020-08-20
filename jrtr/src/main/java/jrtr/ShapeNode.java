package jrtr;

public class ShapeNode implements SceneNode {
	
	private Shape shape;
	private float rad;
	
	public ShapeNode(Shape shape)
	{
		this(shape, 1);
	}
	
	public ShapeNode(Shape shape, float rad) {
		this.shape = shape;
		this.rad = rad;
	}
	
	public Shape getShape()
	{
		return shape;
	}
	
	public float getBoundingRadius() {
		return rad;
	}
}