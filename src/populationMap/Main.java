package populationMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
	
		final float[][] points = {	{ 0, 0, 0 },
									{ 1, 0, 0 },
									{ 0, 1, 0 },
									{ 0, 0, 1 }
								};
		
		List<Triangle> facets = new ArrayList<Triangle>(4);
		facets.add(new Triangle(points[0], points[1], points[2]));
		facets.add(new Triangle(points[0], points[2], points[3]));
		facets.add(new Triangle(points[0], points[3], points[1]));
		facets.add(new Triangle(points[1], points[2], points[3]));
		
		try {
			STL.writeToAsciiFile("assets/myMap.stl", facets);
		} catch (IOException e) {}
	
	}

}
