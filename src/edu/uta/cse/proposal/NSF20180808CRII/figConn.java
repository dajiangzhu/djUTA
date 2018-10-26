package edu.uta.cse.proposal.NSF20180808CRII;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolConnectivityService;
import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class figConn {

	String surfaceFile = "";
	String fiberFile = "";
	String surfaceSmoothFile = "";
	String predictedDicccolFile = "";
	String signalFile = "";
	String outputStruConnVtkFile = "";
	String outputFunConnVtkFile = "";
	String outputConsistentConnMatrixFile = "";
	String outputConsistentConnVtkFile = "";

	djVtkSurData surData = null;
	djVtkFiberData fiberData = null;
	djVtkSurData surSmoothData = null;

	double[][] structuralConnMatrix;
	double[][] functionalConnMatrix;
	double[][] consistentConnMatrix;

	public void loadData() {
		surData = new djVtkSurData(surfaceFile);
		fiberData = new djVtkFiberData(fiberFile);
		surSmoothData = new djVtkSurData(surfaceSmoothFile);
	}

	public void generateStructuralConnMatrix() {
		DicccolConnectivityService dicccolConnService = new DicccolConnectivityService();
		dicccolConnService.setSurData(surData);
		dicccolConnService.setFiberData(fiberData);
		dicccolConnService.setPredictedDicccol(predictedDicccolFile, 10);
		structuralConnMatrix = dicccolConnService.getStructuralConnectivityMatrix();
		for (int i = 0; i < 358; i++)
			for (int j = 0; j < 358; j++)
				structuralConnMatrix[i][j] *= 30;
		DicccolUtilIO.writeVtkMatrix1(structuralConnMatrix, 358, 358, outputStruConnVtkFile);
	}

	public void generateFunctionalConnMatrix() throws IOException {
		functionalConnMatrix = new double[358][358];
		consistentConnMatrix= new double[358][358];
		List<String> outputList = new ArrayList();
		Correlation correlationService = new Correlation();
		double[][] sigMatrix = DicccolUtilIO.loadFileAsArray(signalFile, 358, 108);
		for (int i = 0; i < 357; i++)
			for (int j = i + 1; j < 358; j++) {
				double tmpCorr = correlationService.Correlation_Pearsons(sigMatrix[i], sigMatrix[j]);
				if (Math.abs(tmpCorr) <= 1.0) {
				} else
					tmpCorr = 0.0;
				if (structuralConnMatrix[i][j] > 0.02)
				{
					functionalConnMatrix[i][j] = functionalConnMatrix[j][i] = tmpCorr / 3.0;
					if(structuralConnMatrix[i][j]>0.2 && functionalConnMatrix[i][j]>0.2)
					{
						consistentConnMatrix[i][j] = consistentConnMatrix[j][i] = 1.0;
						outputList.add(i + "-" + j);
					}
				}
			}
		DicccolUtilIO.writeVtkMatrix1(functionalConnMatrix, 358, 358, outputFunConnVtkFile);
		DicccolUtilIO.writeVtkMatrix1(consistentConnMatrix, 358, 358, outputConsistentConnMatrixFile);
		System.out.println("outputList.size:" + outputList.size());
		GenerateDICCCOL generateDicccolService = new GenerateDICCCOL(surfaceSmoothFile, predictedDicccolFile, 0,
				outputConsistentConnVtkFile);
		generateDicccolService.setDicccolConnList(outputList);
		generateDicccolService.generateConnVtk();

	}

	public static void main(String[] args) throws IOException {
		figConn mainHandler = new figConn();
		String rootDir = "C:\\D_Drive\\data\\Proposal\\";
		String subID = "P4";
		mainHandler.surfaceFile = rootDir + "NSF20180808CRII\\" + subID + ".surf.reg.asc.vtk";
		mainHandler.fiberFile = rootDir + "NSF20180808CRII\\" + subID + ".fiber.reg.asc.vtk";
		mainHandler.predictedDicccolFile = rootDir + "NSF20180808CRII\\" + subID + ".roi.allMat.txt";
		mainHandler.signalFile = rootDir + "NSF20180808CRII\\" + subID + "_signals.txt";
		mainHandler.surfaceSmoothFile = rootDir + "surf10_wavelet2_2mni_new.vtk";
		mainHandler.outputStruConnVtkFile = rootDir + "NSF20180808CRII\\" + subID + ".StruConn.vtk";
		mainHandler.outputFunConnVtkFile = rootDir + "NSF20180808CRII\\" + subID + ".FunConn.vtk";
		mainHandler.outputConsistentConnMatrixFile = rootDir + "NSF20180808CRII\\" + subID + ".ConsistentMatrix.vtk";
		mainHandler.outputConsistentConnVtkFile = rootDir + "NSF20180808CRII\\" + subID + ".ConsistentConn.vtk";

		mainHandler.loadData();
		mainHandler.generateStructuralConnMatrix();
		mainHandler.generateFunctionalConnMatrix();

	}

}
