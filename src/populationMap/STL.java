package populationMap;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class STL {

	public static final void writeToAsciiFile(String filename, Collection<Triangle> facets) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		
		out.write("solid ");
		
		for (Triangle tri: facets) {
			out.write("\nfacet normal");
			for (float f: tri.getNormal())
				out.write(" "+f);
			out.write("\n\touterloop");
			
			for (int i = 0; i < 3; i ++) {
				out.write("\n\t\tvertex");
				for (float f: tri.getVertex(i)) {
					out.write(" "+f);
				}
			}
			out.write("\n\tendloop");
			out.write("\nendfacet");
		}
		out.write("\nendsolid ");
		
		out.close();
	}
	
	
	public static final void writeToBinaryFile(String filename, Collection<Triangle> facets) throws IOException {
		DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
		
		for (int i = 0; i < 80; i ++)
			out.writeByte(0);
		out.writeInt(facets.size());
		
		for (Triangle tri: facets) {
			for (float f: tri.getNormal())
				out.writeFloat(f);
			for (int i = 0; i < 3; i ++) {
				for (float f: tri.getVertex(i)) {
					out.writeFloat(f);
				}
			}
			out.writeShort(0);
		}
		
		out.close();
	}

}
