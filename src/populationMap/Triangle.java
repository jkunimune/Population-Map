package populationMap;

public class Triangle {

	private float[][] vertices;
	
	
	
	public Triangle() {
		vertices = new float[3][];
	}
	
	
	public Triangle(float[] v0, float[] v1, float[] v2) {
		this();
		vertices[0] = v0;
		vertices[1] = v1;
		vertices[2] = v2;
	}
	
	
	
	public float[] getVertex(int num) {
		return vertices[num];
	}
	
	
	public void setVertex(int num, float[] v) {
		vertices[num] = v;
	}
	
	
	public float[] getNormal() {
		return null;
	}

}
