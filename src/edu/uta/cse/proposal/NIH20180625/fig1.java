package edu.uta.cse.proposal.NIH20180625;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;

public class fig1 {

	public void generateDicccolPoints() {
		String surfaceFile = "C:\\D_Drive\\data\\Proposal\\surf10_wavelet2_2mni_new.vtk";
		String predictionFile = "C:\\D_Drive\\data\\Proposal\\final.358.mat";
		int column = 0;
		String outPutVtkFile = "C:\\D_Drive\\data\\Proposal\\surf10_wavelet2_2mni_DICCCOLPts.vtk";

		GenerateDICCCOL generateDicccolService = new GenerateDICCCOL(surfaceFile, predictionFile, column,
				outPutVtkFile);
		generateDicccolService.generatePointsVtk();
	}

	public void generateStructuralConn() throws IOException {
		double dThreshold = 70.0;
		List<String> outputList = new ArrayList();
		double[][] connMatrix = DicccolUtilIO.loadFileAsArray("C:\\D_Drive\\data\\Proposal\\P6.structuralConn.txt", 358,
				358);
		for (int i = 0; i < 357; i++)
			for (int j = i + 1; j < 358; j++)
				if (Math.abs(i - j) > 180 && connMatrix[i][j] >= dThreshold)
					outputList.add(i + "-" + j);
		System.out.println("outputList.size:" + outputList.size());

		String surfaceFile = "C:\\D_Drive\\data\\Proposal\\surf10_wavelet2_2mni_new.vtk";
		String predictionFile = "C:\\D_Drive\\data\\Proposal\\final.358.mat";
		int column = 0;
		String outPutVtkFile = "C:\\D_Drive\\data\\Proposal\\P6.structuralConn";

		GenerateDICCCOL generateDicccolService = new GenerateDICCCOL(surfaceFile, predictionFile, column,
				outPutVtkFile);
		generateDicccolService.setDicccolConnList(outputList);
		generateDicccolService.generateConnVtk();
	}

	public void generateFunctionalConn() throws IOException {
		List<String> outputList = new ArrayList();
		double dThreshold = 0.8;
		Correlation correlationService = new Correlation();
		double[][] sigMatrix = DicccolUtilIO.loadFileAsArray("C:\\D_Drive\\data\\Proposal\\P6_signals.txt", 358, 108);
		for (int i = 0; i < 357; i++)
			for (int j = i + 1; j < 358; j++)
				if (Math.abs(i - j) > 180 && correlationService.Correlation_Pearsons(sigMatrix[i], sigMatrix[j]) >= dThreshold)
					outputList.add(i + "-" + j);

		System.out.println("outputList.size:" + outputList.size());

		String surfaceFile = "C:\\D_Drive\\data\\Proposal\\surf10_wavelet2_2mni_new.vtk";
		String predictionFile = "C:\\D_Drive\\data\\Proposal\\final.358.mat";
		int column = 0;
		String outPutVtkFile = "C:\\D_Drive\\data\\Proposal\\P6.functionalConn";

		GenerateDICCCOL generateDicccolService = new GenerateDICCCOL(surfaceFile, predictionFile, column,
				outPutVtkFile);
		generateDicccolService.setDicccolConnList(outputList);
		generateDicccolService.generateConnVtk();

	}

	public static void main(String[] args) throws IOException {
		fig1 mainHandler = new fig1();
		// mainHandler.generateDicccolPoints();
		mainHandler.generateStructuralConn();
//		mainHandler.generateFunctionalConn();

	}

}
