package charts;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class MatlabCode {

	
	private int[] load;
	
	public void produceCode(int[][] map)
	{
		load = new int[map[0].length];
		for(int j = 0; j < map[0].length; j++)
		{
			for(int i = 0; i < map.length; i++)
			{
				if(map[i][j] == 1)
				{
					load[j]++;
				}
			}
		}
		
		
		
		try {
			
			PrintWriter writer = new PrintWriter("C:/Users/Andreas Sitaras/Desktop/matlab_code.txt", "UTF-8");
			
			
			StringBuilder strain = new StringBuilder();
			strain.append("Strain1 = [");
			
			for(int i = 0; i < load.length; i++)
			{
				strain.append(load[i] + " ");
			}
			strain.append("];");
			writer.print(strain.toString());
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	
}
