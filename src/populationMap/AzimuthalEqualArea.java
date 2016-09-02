package populationMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AzimuthalEqualArea {

	public static final float[][] map(String filename, double pp, double lp, int r) throws IOException {
		final double[][] data = loadEsriAsc();
		
		System.out.println("check "+System.currentTimeMillis()%86400000);
		
		float[][] surface = new float[2*(r+1)][2*(r+1)];
		for (int i = 0; i < surface.length; i ++) {
			for (int j = 0; j < surface[i].length; j ++) {
				if (Math.hypot(i-r-1, j-r-1) < r)
					surface[i][j] = getValue(data, mathCoords((double)(i-r-1)/r, (double)(j-r-1)/r, pp, lp));
				else
					surface[i][j] = Float.NaN;
			}
		}
		return surface;
	}
	
	
	private static final double[] mathCoords(double x, double y, double phiP, double lamP) {
		return obliquify(eaAzimuth(x, y), phiP, lamP);
	}
	
	
	private static final double[] eaAzimuth(double x, double y) {
		final double latitude = Math.asin(2*Math.pow(Math.hypot(x, y), 2) - 1);
		final double longitude = Math.atan2(y, x);
		final double[] output = {latitude, longitude};
		return output;
	}
	
	
	private static final double[] obliquify(double[] coords, double phiP, double lamP) {
		final double phi1 = coords[0];
		final double lam1 = coords[1];
		double phif = Math.asin(Math.sin(phiP)*Math.sin(phi1) + Math.cos(phiP)*Math.cos(lam1)*Math.cos(phi1));
		double lamf;
		double innerFunc = Math.sin(phi1)/Math.cos(phiP)/Math.cos(phif)-Math.tan(phiP)*Math.tan(phif); // used for calculating lon
		if (phiP  == Math.PI/2)				// accounts for special case when phiP = pi/2
			lamf = lam1+Math.PI;
		else if (phiP == -Math.PI/2)		// accounts for special case when phiP = -pi/2
			lamf = -lam1;
		else if (Math.abs(innerFunc) > 1) {	// accounts for special case when cos(phiP) = --> 0
			if ((lam1 == Math.PI && phi1 < -phiP) || (lam1 != Math.PI && phi1 < phiP))
				lamf = Math.PI+lamP;
			else
				lamf = lamP;
		}
		else if (Math.sin(lam1) < 0)
			lamf = lamP + Math.acos(Math.sin(phi1)/Math.cos(phiP)/Math.cos(phif)-Math.tan(phiP)*Math.tan(phif));
		else
			lamf = lamP - Math.acos(Math.sin(phi1)/Math.cos(phiP)/Math.cos(phif)-Math.tan(phiP)*Math.tan(phif));
		
		final double[] output = {phif, lamf};
		return output;
	}
	
	
	private static final float getValue(double[][] data, double[] coords) {
		final double latitude = coords[0];
		final double longitude = coords[1];
		int x = (int) ((longitude/(2*Math.PI))*data[0].length);
		while (x < 0)	x += data[0].length;
		x += data[0].length;
		int y = (int) ((0.5 - latitude/(Math.PI))*data.length);
		if (y == data.length)	y = data.length-1;
		y += data.length;
		
		final int r = (int) (3*data.length*Main.SIGMA)/Main.SKIP*Main.SKIP;
		final double s = data.length*Main.SIGMA;
		int valSum = 0;
		int gasSum = 0;
		for (int dy = -r; dy <= r; dy += Main.SKIP) {
			for (int dx = -r; dx <= r; dx += Main.SKIP) {
				final double dr2 = dx*dx*Math.pow(Math.sin(latitude),2) + dy*dy;
				final double gaussian = Math.exp(-dr2/(2*s*s));
				valSum += gaussian*data[(y+dy)%data.length][(x+dx)%data[0].length];
				gasSum += gaussian;
			}
		}
		
		if (1 / data[y%data.length][x%data[0].length] < 0)
			return (float) (valSum/gasSum) - 10000000;
		return (float) (valSum/gasSum);
	}
	
	
	private static final float getLand(double[][] data, double[] coords) {
		final double latitude = coords[0];
		final double longitude = coords[1];
		int x = (int) ((longitude/(2*Math.PI))*data[0].length);
		while (x < 0)	x += data[0].length;
		x += data[0].length;
		int y = (int) ((0.5 - latitude/(Math.PI))*data.length);
		if (y == data.length)	y = data.length-1;
		y += data.length;
		
		if (data[(y)%data.length][(x)%data[0].length] > 0)
			return 1000000;
		else
			return 0;
	}
	
	
	private static double[][] loadEsriAsc() throws IOException {
		final BufferedReader in = new BufferedReader(new FileReader("assets/glds00ag.asc"));
		
		final int ncols = Integer.parseInt(in.readLine().substring(14));
		final int nrows = Integer.parseInt(in.readLine().substring(14));
		final int xllcorner = Integer.parseInt(in.readLine().substring(14));
		final int yllcorner = Integer.parseInt(in.readLine().substring(14));
		final double cellsize = Double.parseDouble(in.readLine().substring(14));
		final double NODATA = Double.parseDouble(in.readLine().substring(14));
		
		final int startYIdx = 0;//(int)(Math.round((yllcorner+90)/cellsize));
		
		double[][] map = new double[(int)Math.round(180/cellsize)][];
		/*for (int i = 0; i < startYIdx; i ++) {
			map[i] = new double[(int)Math.round(360/cellsize)];
			for (int j = 0; j < map[i].length; j ++) {
				map[i][j] = 0;
			}
		}*/
		for (int i = 0; i < nrows; i ++) {
			final String[] row = in.readLine().split(" ");
			final int yIdx = i + startYIdx;
			map[yIdx] = new double[(int)Math.round(360/cellsize)];
			for (int j = 0; j < ncols; j ++) {
				final int xIdx = j + (int)Math.round((xllcorner+180)/cellsize);
				map[yIdx][xIdx] = Double.parseDouble(row[j]);
				if (map[yIdx][xIdx] == NODATA)
					map[yIdx][xIdx] = -0.0;
			}
		}
		for (int i = startYIdx+nrows; i < map.length; i ++) {
			map[i] = new double[(int)Math.round(360/cellsize)];
			for (int j = 0; j < ncols; j ++) {
				map[i][j] = 0;
			}
		}
		
		in.close();
		return map;
	}

}
