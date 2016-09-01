package populationMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AzimuthalEqualArea {

	public static final float[][] map(String filename, double pp, double lp, int r) throws IOException {
		final BufferedImage img = ImageIO.read(new File("assets/input.png"));
		
		float[][] surface = new float[2*(r+1)][2*(r+1)];
		for (int i = 0; i < surface.length; i ++) {
			for (int j = 0; j < surface[i].length; j ++) {
				if (Math.hypot(i-r-1, j-r-1) < r)
					surface[i][j] = getValue(img, mathCoords((double)(i-r-1)/r, (double)(j-r-1)/r, pp, lp));
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
	
	
	public static final double[] obliquify(double[] coords, double phiP, double lamP) {
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
	
	
	private static final float getValue(BufferedImage ref, double[] coords) {
		final double latitude = coords[0];
		final double longitude = coords[1];
		int x = (int) ((longitude/(2*Math.PI))*ref.getWidth());
		while (x < 0)	x += ref.getWidth();
		x %= ref.getWidth();
		int y = (int) ((0.5 - latitude/(Math.PI))*ref.getHeight());
		if (y >= ref.getHeight())	y = ref.getHeight()-1;
		final int color = ref.getRGB(x, y);
		
		return (color&0x0000ff) + ((color&0x00ff00)>>8) + ((color&0xff0000)>>16);
	}

}
