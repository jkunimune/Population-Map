package populationMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class Main {

	public static final double[] POLE = {0.9, 0.7};
	
	public static final int RADIUS = 100;
	
	public static final int DEPTH = 4;
	
	public static final float SCALE = (float) 0.000005;
	
	public static final float OFFSET = 3;
	
	public static final double SIGMA = .01;
	
	public static final int SKIP = 1;
	
	public static final float STEP = (float) Math.PI/40;
	
	
	
	public static void main(String[] args) {
	
		System.out.println("begin "+System.currentTimeMillis()%86400000);
		
		float[][] map;
		try {
			map = AzimuthalEqualArea.map("assets/input.png", -POLE[0], POLE[1], RADIUS);
		} catch (IOException e1) {
			System.err.println(e1);
			map = new float[1][1];
		}
		
		System.out.println("check "+System.currentTimeMillis()%86400000);
		
		Collection<Triangle> facets = new ArrayList<Triangle>(4);
		
		facets.addAll(buildCircle());
		facets.addAll(buildCylinder());
		facets.addAll(buildSurface(map));
		facets.addAll(buildMergezone(map));
		
		try {
			STL.writeToAsciiFile("assets/myMap.stl", facets);
		} catch (IOException e) {}
		
		System.out.println("done  "+System.currentTimeMillis()%86400000);
	
	}

	private static Collection<Triangle> buildCircle() {
		Collection<Triangle> facets = new ArrayList<Triangle>();
		
		final float[] p1 = baseVertex(0);
		float[] p2 = baseVertex(STEP);
		float[] p3;
		for (float theta = 2*STEP; theta < 2*Math.PI; theta += STEP) {
			p3 = baseVertex(theta);
			facets.add(new Triangle(p1, p2, p3));
			p2 = p3;
		}
		
		return facets;
	}

	private static Collection<Triangle> buildCylinder() {
		Collection<Triangle> facets = new ArrayList<Triangle>();
		
		for (float theta = 0; theta < 2*Math.PI; theta += STEP) {
			final float[] p1 = baseVertex(theta-STEP);
			final float[] p2 = edgeVertex(theta-STEP);
			final float[] p3 = baseVertex(theta);
			final float[] p4 = edgeVertex(theta);
			facets.add(new Triangle(p1, p2, p3));
			facets.add(new Triangle(p2, p3, p4));
		}
		
		return facets;
	}

	private static Collection<Triangle> buildSurface(float[][] surface) {
		Collection<Triangle> facets = new ArrayList<Triangle>();
		
		for (int y = 1; y < surface.length-1; y ++) {
			for (int x = 1; x < surface[y].length-1; x ++) {
				final float[] p1 = dataVertex(x+0, y+0, surface);
				final float[] p2 = dataVertex(x+1, y+0, surface);
				final float[] p3 = dataVertex(x+0, y+1, surface);
				final float[] p4 = dataVertex(x-1, y+1, surface);
				final Triangle T1 = new Triangle(p1, p2, p3);
				if (T1.isValid())
					facets.add(T1);
				final Triangle T2 = new Triangle(p1, p3, p4);
				if (T2.isValid())
					facets.add(T2);
			}
		}
		
		return facets;
	}


	private static Collection<Triangle> buildMergezone(float[][] surface) {
		Collection<Triangle> facets = new ArrayList<Triangle>();
		
		ArrayList<float[]> mapRim = new ArrayList<float[]>();	// build a list of edges of the map rim in order
		ArrayList<Double> angles = new ArrayList<Double>();
		for (int y = 1; y < surface.length-1; y ++) {
			for (int x = 1; x < surface[y].length-1; x ++) {
				if (!Float.isNaN(surface[y][x]) &&
						(Float.isNaN(surface[y][x-1]) || Float.isNaN(surface[y][x+1]) ||
						Float.isNaN(surface[y-1][x]) || Float.isNaN(surface[y+1][x]) ||
						Float.isNaN(surface[y+1][x-1]) || Float.isNaN(surface[y-1][x+1]))) {
					final float[] p = dataVertex(x, y, surface);
					double theta = Math.atan2(y-surface.length/2, x-surface[y].length/2);
					if (theta<0)	theta += 2*Math.PI;
					int i = 0;
					while (i < mapRim.size() && angles.get(i) < theta)	i ++;
					mapRim.add(i, p);
					angles.add(i, theta);
				}
			}
		}
		
		final int P = mapRim.size();
		for (int i = 1; i <= P; i ++) {
			float theta = 0;
			while (theta <= angles.get(i%P))	theta += STEP;
			float[] edgeP = edgeVertex(theta-STEP);
			facets.add(new Triangle(edgeP, mapRim.get(i%P), mapRim.get(i-1)));
		}
		
		for (float theta = 0; theta < 2*Math.PI; theta += STEP) {
			int i = 0;
			while (angles.get(i) < theta)	i ++;
			facets.add(new Triangle(edgeVertex(theta), edgeVertex(theta-STEP), mapRim.get((i+P-1)%P)));
		}
		
		return facets;
	}

	private static float[] edgeVertex(float theta) {
		final float[] output = {(float) (RADIUS*Math.cos(theta)), (float) (RADIUS*Math.sin(theta)), 0};
		return output;
	}

	private static float[] baseVertex(float theta) {
		final float[] output = {(float) (RADIUS*Math.cos(theta)), (float) (RADIUS*Math.sin(theta)), -DEPTH};
		return output;
	}

	private static float[] dataVertex(int x, int y, float[][] map) {
		if (y == map.length-1) {
			final float[] output = {Float.NaN, Float.NaN, Float.NaN};
			return output;
		}
		
		float[] output = {x-map[y].length/2, y-map.length/2, map[y][x]*SCALE+OFFSET};
		return output;
	}

}
