package simple;

import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

public class BezierCurve {
	public Vector3f a;
	public Vector3f b;
	public Vector3f c;
	public Vector3f d;
	
	public BezierCurve(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3) {
		Vector3f temp;
		
		//a = -p0 + 3p1 - 3p2 + p3
		a = new Vector3f(p0);
		a.scale(-1);
		temp = new Vector3f(p1);
		temp.scale(3);
		a.add(temp);
		temp = new Vector3f(p2);
		temp.scale(-3);
		a.add(temp);
		temp = new Vector3f(p3);
		a.add(temp);
		
		//b = 3p0 - 6p1 + 3p2
		b = new Vector3f(p0);
		b.scale(3);
		temp = new Vector3f(p1);
		temp.scale(-6);
		b.add(temp);
		temp = new Vector3f(p2);
		temp.scale(3);
		b.add(temp);
		
		//c = -3p0 + 3p1
		c = new Vector3f(p0);
		c.scale(-3);
		temp = new Vector3f(p1);
		temp.scale(3);
		c.add(temp);
		
		//d = p0
		d = new Vector3f(p0);
	}
	
	public Vector3f[] pointAt(float t) {
		Vector3f[] ret = new Vector3f[2];
		
		Vector3f temp;
		
		Vector3f point = new Vector3f(d);
		temp = new Vector3f(c);
		temp.scale(t);
		point.add(temp);
		temp = new Vector3f(b);
		temp.scale((float) Math.pow(t, 2));
		point.add(temp);
		temp = new Vector3f(a);
		temp.scale((float) Math.pow(t, 3));
		point.add(temp);
		
		Vector3f tan = new Vector3f(c);
		temp = new Vector3f(b);
		temp.scale(2.0f*t);
		tan.add(temp);
		temp = new Vector3f(a);
		temp.scale(3.0f*((float) Math.pow(t, 2)));
		tan.add(temp);
		
		ret[0] = point;
		ret[1] = tan;
		
		return ret;
	}
	
	public static Vector3f[][] getCubicCurve(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, int num) {
		Vector3f[][] ret = new Vector3f[2][num];
		BezierCurve curve = new BezierCurve(p0, p1, p2, p3);
		
		//x(t) = at^3 + bt^2 + ct + d
		for (int i = 0; i < num; i++) {
			float t = ((float) i)/((float) (num - 1));
			Vector3f[] valAt;
			
			valAt = curve.pointAt(t);
			
			ret[0][i] = valAt[0];
			ret[1][i] = valAt[1];
		}
		
		return ret;
	}
	
	public static Vector3f[][] getPiecewiseCurve(int segments, Vector3f[] points, int num) {
		Vector3f[][] ret = new Vector3f[2][num];
		
		BezierCurve[] curves = new BezierCurve[segments];
		
		for (int i = 0; i < segments; i++) {
			int offset = i*3;
			curves[i] = new BezierCurve(points[0+offset], points[1+offset], points[2+offset], points[3+offset]);
		}
		
		for (int i = 0; i < num; i++) {
			float t = ((float) i)/((float) (num - 1));
			
			BezierCurve curve = curves[(int) ((float) i/(((float) num)/((float) segments)))];
			
			Vector3f[] valAt;
			
			if (i != 0 && t*((float) segments) % 1 == 0) {
				valAt = curve.pointAt(1);
			} else {
				valAt = curve.pointAt(t*((float) segments) % 1);
			}
			
			ret[0][i] = valAt[0];
			ret[1][i] = valAt[1];
		}
		
		return ret;
	}
}
