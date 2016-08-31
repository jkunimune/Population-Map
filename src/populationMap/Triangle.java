package populationMap;

public class Triangle {

	private double[][] vertices;
	
	
	
	public Triangle() {
		vertices = new double[3][];
	}
	
	
	public Triangle(double[] v0, double[] v1, double[] v2) {
		this();
		vertices[0] = v0;
		vertices[1] = v1;
		vertices[2] = v2;
	}
	
	
	
	public double[] getVertex(int num) {
		return vertices[num];
	}
	
	
	public void setVertex(int num, double[] v) {
		vertices[num] = v;
	}
	
	
	public double[] getNormal() {
		return null;
	}

}
