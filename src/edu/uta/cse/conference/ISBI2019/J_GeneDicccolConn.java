package edu.uta.cse.conference.ISBI2019;

import edu.uga.DICCCOL.DicccolConnectivityService;
import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djNiftiData;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class J_GeneDicccolConn {
	
	private djVtkSurData surData;
	private djVtkFiberData fiberData;
	private djNiftiData rsData;
	private DicccolConnectivityService dicccolConnService;
	
	public void loadData(String surFileName, String fibFileName, String rsfMRIFileName, String predDicccolFileName)
	{
		//
		surData = new djVtkSurData(surFileName);
		fiberData = new djVtkFiberData(fibFileName);
		rsData = new djNiftiData(rsfMRIFileName);
		dicccolConnService = new DicccolConnectivityService();
		dicccolConnService.setSurData(surData);
		dicccolConnService.setFiberData(fiberData);
		dicccolConnService.setFmriData(rsData);
		dicccolConnService.setPredictedDicccol(predDicccolFileName, 10);
	}
	
	public void saveDicccolPtVtk(String saveFilePre)
	{
		DicccolUtilIO.writeToPointsVtkFile(saveFilePre+".DicccolPts.vtk", dicccolConnService.getDicccolPts());
	}
	
	public void saveDicccolStructuralConn(String saveFilePre)
	{
		double[][] structuralConn = dicccolConnService.getStructuralConnectivityMatrix();
		DicccolUtilIO.writeArrayToFile(structuralConn, 358, 358, " ", saveFilePre+".DicccolStructuralConn.txt");
		DicccolUtilIO.writeVtkMatrix1(structuralConn, 358, 358, saveFilePre+".DicccolStructuralConn.vtk");
	}

	public static void main(String[] args) {
		if(args.length!=5)
		{
			System.out.println("Need para: SurFile FiberFile rsfMRIFile PredictionFile saveFilePre");
			System.exit(0);
		}
		J_GeneDicccolConn mainHandler = new J_GeneDicccolConn();
		String surFileName = args[0].trim();
		String fibFileName = args[1].trim();
		String rsfMRIFileName = args[2].trim();
		String predDicccolFileName = args[3].trim();
		String saveFilePre = args[4].trim();
		
		mainHandler.loadData(surFileName, fibFileName, rsfMRIFileName, predDicccolFileName);
		mainHandler.saveDicccolPtVtk(saveFilePre);

	}

}
