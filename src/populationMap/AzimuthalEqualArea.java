package populationMap;

public class AzimuthalEqualArea {

	public static final float[][] map(String filename, double pp, double lp, int r) {
		float[][] surface = new float[2*(r+1)][2*(r+1)];
		for (int i = 0; i < surface.length; i ++) {
			for (int j = 0; j < surface[i].length; j ++) {
				if (Math.hypot(i-r-1, j-r-1) < r)
					surface[i][j] = (float) (10*Math.random());//getValue(filename, i, j);
				else
					surface[i][j] = Float.NaN;
			}
		}
		return surface;
	}
	
	
	public static final float getValue(String filename, double latitude, double longitude) {
		return 0;
	}

}
