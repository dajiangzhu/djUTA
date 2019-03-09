package edu.uta.cse.journal.yunli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;

public class Journal_1 {
	
	String surfaceSmoothFile = "";
	String predictedDicccolFile = "";
	String connPairFile = "";
	int numOfTopConn = -1;
	
	private List<String> preprocessingPairList()
	{
		List<String> processedList = new ArrayList<String>();
		
		
		return processedList;
	}
	
	public void generateDicccolConn () throws IOException
	{
		List<String> outputList = DicccolUtilIO.loadFileToArrayList(connPairFile);
		GenerateDICCCOL generateDicccolService = new GenerateDICCCOL(surfaceSmoothFile, predictedDicccolFile, 0,
				connPairFile+"_conn.vtk");
		generateDicccolService.setDicccolConnList(outputList);
		generateDicccolService.generateConnVtk();
	}

	public static void main(String[] args) {
		Journal_1 mainHandler = new Journal_1();
		
		if(args.length!=2)
		{
			System.out.println("Need 2 paras: connectivityFile(pair list, i.e. 1-2) N(Top N connectivity you want to show)");
			System.exit(0);
		}
		mainHandler.surfaceSmoothFile = "./surf10_wavelet2_2mni_new.vtk";
		mainHandler.predictedDicccolFile = "./Dicccol.roi.allMat.txt";
		mainHandler.connPairFile = args[0].trim();
		mainHandler.numOfTopConn = Integer.valueOf(args[1].trim());

	}

}
