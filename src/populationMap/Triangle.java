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
		float[] v1 = new float[3];
		for (int i = 0; i < 3; i ++)
			v1[i] = vertices[1][i] - vertices[0][i];
		
		float[] v2 = new float[3];
		for (int i = 0; i < 3; i ++)
			v2[i] = vertices[2][i] - vertices[0][i];
		
		float[] v1xv2 = new float[3];
		for (int i = 0; i < 3; i ++)
			v1xv2[i] = v1[(i+1)%3]*v2[(i+2)%3] - v2[(i+1)%3]*v1[(i+2)%3];
		
		final float norm = (float) Math.sqrt(Math.pow(v1xv2[0], 2) + Math.pow(v1xv2[1], 2) + Math.pow(v1xv2[2], 2));
		for (int i = 0; i < 3; i ++)
			v1xv2[i] /= norm;
		
		return v1xv2;
	}
	
	
	public boolean isValid() {
		for (float[] v: vertices)
			for (float f: v)
				if (Float.isNaN(f))
					return false;
		return true;
	}

}
