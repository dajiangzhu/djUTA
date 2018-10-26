package edu.uta.cse.conference.ISBI2019;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;
import edu.uga.liulab.djVtkBase.djNiftiData;
import edu.uga.liulab.djVtkBase.djVtkCell;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkHybridData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class GenerateConn {

	private int NumOfLabel = 34;
	private int NumOfROI = NumOfLabel * 2;

	private double[][] structuralConnectivityMatrix = new double[NumOfROI][NumOfROI];
	private double[][] functionalConnectivityMatrix = new double[NumOfROI][NumOfROI];

	private String[][] labelDic;
	private djVtkSurData surDataL;
	private djVtkSurData surDataR;
	private djVtkFiberData fiberData;
	private djVtkHybridData hybridDataL;
	private djVtkHybridData hybridDataR;
	private djNiftiData rsData;
	private Map<String, List> mapLabelPoints = new HashMap<String, List>();

	public String subID;
	public String rootDIR;
	public String surface;

	public void loadDataForStrucConn() {
		// load the info of all labels
		 labelDic = DicccolUtilIO.loadFileAsStringArray(rootDIR + "/aparc.annot.ctab",
		 34, 6);
//		labelDic = DicccolUtilIO.loadFileAsStringArray(rootDIR + "\\aparc.annot.ctab", 34, 6);

		// load surface (left and right) and fiber data
		 surDataL = new djVtkSurData(rootDIR + "/" + this.subID +
		 "/Surf/vtk/lh."+this.surface+"_transform.vtk");
		 surDataR = new djVtkSurData(rootDIR + "/" + this.subID +
		 "/Surf/vtk/rh."+this.surface+"_transform.vtk");
//		surDataL = new djVtkSurData(rootDIR + "\\" + this.subID + "\\Surf\\vtk\\lh.pial_transform.vtk");
//		surDataR = new djVtkSurData(rootDIR + "\\" + this.subID + "\\Surf\\vtk\\rh.pial_transform.vtk");
		 fiberData = new djVtkFiberData(
		 rootDIR + "/" + this.subID +
		 "/Fiber/preprocessing/fiber/MedINRIA_StreamLine/fiber.asc.vtk");
//		fiberData = new djVtkFiberData(
//				rootDIR + "\\" + this.subID + "\\Fiber\\preprocessing\\fiber\\MedINRIA_StreamLine\\fiber.asc.vtk");

		// matching the surface and fiber
		hybridDataL = new djVtkHybridData(surDataL, fiberData);
		hybridDataL.mapSurfaceToBox();
		hybridDataL.mapFiberToBox();
		hybridDataR = new djVtkHybridData(surDataR, fiberData);
		hybridDataR.mapSurfaceToBox();
		hybridDataR.mapFiberToBox();

		// load info of points in each label
		for (int i = 0; i < NumOfLabel; i++) {
			String lableName = labelDic[i][1];
			List<Integer> ptsInLable_L = new ArrayList<Integer>();
			 List<String> labelInfo_L = DicccolUtilIO
			 .loadFileToArrayList(rootDIR + "/AllROILabels/" + this.subID + "/lh." +
			 lableName + ".label");
//			List<String> labelInfo_L = DicccolUtilIO
//					.loadFileToArrayList(rootDIR + "\\AllROILabels\\" + this.subID + "\\lh." + lableName + ".label");
			for (int j = 2; j < labelInfo_L.size(); j++)
				ptsInLable_L.add(Integer.valueOf(labelInfo_L.get(j).trim().split("\\s+")[0]));
			mapLabelPoints.put(lableName + "_L", ptsInLable_L);

			List<Integer> ptsInLable_R = new ArrayList<Integer>();
			 List<String> labelInfo_R = DicccolUtilIO
			 .loadFileToArrayList(rootDIR + "/AllROILabels/" + this.subID + "/rh." +
			 lableName + ".label");
//			List<String> labelInfo_R = DicccolUtilIO
//					.loadFileToArrayList(rootDIR + "\\AllROILabels\\" + this.subID + "\\rh." + lableName + ".label");
			for (int j = 2; j < labelInfo_R.size(); j++)
				ptsInLable_R.add(Integer.valueOf(labelInfo_R.get(j).trim().split("\\s+")[0]));
			mapLabelPoints.put(lableName + "_R", ptsInLable_R);
		} // for i
	}

	private double[] getAvgBOLD(Set<djVtkPoint> tmpPoints) {
		double[] boldsSig = new double[this.rsData.tSize];
		List<djVtkPoint> pointsWhtinLable = new ArrayList<djVtkPoint>();
		pointsWhtinLable.addAll(tmpPoints);
		for (int i = 0; i < pointsWhtinLable.size(); i++)
			for (int t = 0; t < this.rsData.tSize; t++)
				boldsSig[t] += this.rsData.getValueBasedOnPhysicalCoordinate(pointsWhtinLable.get(i).x,
						pointsWhtinLable.get(i).y, pointsWhtinLable.get(i).z, t);
		for (int t = 0; t < this.rsData.tSize; t++)
			boldsSig[t] = boldsSig[t] / tmpPoints.size();
		return boldsSig;
	}

	public void loadDataForFunctionalConn() {
		// load the info of all labels
		 labelDic = DicccolUtilIO.loadFileAsStringArray(rootDIR + "/aparc.annot.ctab",
		 34, 6);
//		labelDic = DicccolUtilIO.loadFileAsStringArray(rootDIR + "\\aparc.annot.ctab", 34, 6);

//		 load surface (left and right) and fiber data
		 surDataL = new djVtkSurData(rootDIR + "/" + this.subID +
		 "/Surf/vtk/lh."+this.surface+"_transform.vtk");
		 surDataR = new djVtkSurData(rootDIR + "/" + this.subID +
		 "/Surf/vtk/rh."+this.surface+"_transform.vtk");
			rsData = new djNiftiData(
					rootDIR + "/" + this.subID + "/fMRI/rsfProcess/bolds_fmri2dti.nii.gz");
//		surDataL = new djVtkSurData(rootDIR + "\\" + this.subID + "\\Surf\\vtk\\lh.pial_transform.vtk");
//		surDataR = new djVtkSurData(rootDIR + "\\" + this.subID + "\\Surf\\vtk\\rh.pial_transform.vtk");
//		rsData = new djNiftiData(
//				rootDIR + "\\" + this.subID + "\\fMRI\\rsfProcess\\bolds_fmri2dti.nii.gz");

		// load info of points in each label
		for (int i = 0; i < NumOfLabel; i++) {
			String lableName = labelDic[i][1];
			List<Integer> ptsInLable_L = new ArrayList<Integer>();
			 List<String> labelInfo_L = DicccolUtilIO
			 .loadFileToArrayList(rootDIR + "/AllROILabels/" + this.subID + "/lh." +
			 lableName + ".label");
//			List<String> labelInfo_L = DicccolUtilIO
//					.loadFileToArrayList(rootDIR + "\\AllROILabels\\" + this.subID + "\\lh." + lableName + ".label");
			for (int j = 2; j < labelInfo_L.size(); j++)
				ptsInLable_L.add(Integer.valueOf(labelInfo_L.get(j).trim().split("\\s+")[0]));
			mapLabelPoints.put(lableName + "_L", ptsInLable_L);

			List<Integer> ptsInLable_R = new ArrayList<Integer>();
			 List<String> labelInfo_R = DicccolUtilIO
			 .loadFileToArrayList(rootDIR + "/AllROILabels/" + this.subID + "/rh." +
			 lableName + ".label");
//			List<String> labelInfo_R = DicccolUtilIO
//					.loadFileToArrayList(rootDIR + "\\AllROILabels\\" + this.subID + "\\rh." + lableName + ".label");
			for (int j = 2; j < labelInfo_R.size(); j++)
				ptsInLable_R.add(Integer.valueOf(labelInfo_R.get(j).trim().split("\\s+")[0]));
			mapLabelPoints.put(lableName + "_R", ptsInLable_R);
		} // for i

	}

	public void generateStructuralConn() {
		List<Set<Integer>> rawConnectivityInfo = new ArrayList<Set<Integer>>();
		Set<Integer> allFiberIndex = new HashSet<Integer>();

		for (int i = 0; i < NumOfLabel; i++) { // NumOfLabel=34
			String lableName = labelDic[i][1];
			Set<djVtkPoint> tmpPointsL = new HashSet<djVtkPoint>();
			for (int p = 0; p < this.mapLabelPoints.get(lableName + "_L").size(); p++)
				tmpPointsL.add(this.surDataL.getPoint((int) this.mapLabelPoints.get(lableName + "_L").get(p)));
			List<djVtkCell> tmpCellListL = hybridDataL.getFibersConnectToPointsSet(tmpPointsL).cellsOutput;
			Set<Integer> newFiberIDSetL = new HashSet<Integer>();
			for (int j = 0; j < tmpCellListL.size(); j++)
				newFiberIDSetL.add(tmpCellListL.get(j).cellId);
			rawConnectivityInfo.add(newFiberIDSetL);
			allFiberIndex.addAll(newFiberIDSetL);
		} // for NumOfLabel=34
		for (int i = 0; i < NumOfLabel; i++) { // NumOfLabel=34
			String lableName = labelDic[i][1];
			Set<djVtkPoint> tmpPointsR = new HashSet<djVtkPoint>();
			for (int p = 0; p < this.mapLabelPoints.get(lableName + "_R").size(); p++)
				tmpPointsR.add(this.surDataR.getPoint((int) this.mapLabelPoints.get(lableName + "_R").get(p)));
			List<djVtkCell> tmpCellListR = hybridDataR.getFibersConnectToPointsSet(tmpPointsR).cellsOutput;
			Set<Integer> newFiberIDSetR = new HashSet<Integer>();
			for (int j = 0; j < tmpCellListR.size(); j++)
				newFiberIDSetR.add(tmpCellListR.get(j).cellId);
			rawConnectivityInfo.add(newFiberIDSetR);
			allFiberIndex.addAll(newFiberIDSetR);
		} // for NumOfLabel=34

		// calculating the connectivity
		for (int i = 0; i < rawConnectivityInfo.size() - 1; i++)
			for (int j = i + 1; j < rawConnectivityInfo.size(); j++) {
				int count = 0;
				List<Integer> tmpList = new ArrayList<Integer>();
				tmpList.addAll(rawConnectivityInfo.get(i));
				for (int m = 0; m < tmpList.size(); m++) {
					int currentCellID = tmpList.get(m);
					if (rawConnectivityInfo.get(j).contains(currentCellID))
						count++;
				} // for m
				structuralConnectivityMatrix[i][j] = structuralConnectivityMatrix[j][i] = (double) count
						/ (double) allFiberIndex.size();
			} // for j
	}

	public void generateFunctionalConn() {
		double[][] allBold = new double[NumOfROI][NumOfROI];

		for (int i = 0; i < NumOfLabel; i++) { // NumOfLabel=34
			String lableName = labelDic[i][1];
//			System.out.println(lableName+"_L");
			// get all points within this label area
			Set<djVtkPoint> tmpPointsL = new HashSet<djVtkPoint>();
			for (int p = 0; p < this.mapLabelPoints.get(lableName + "_L").size(); p++)
				tmpPointsL.add(this.surDataL.getPoint((int) this.mapLabelPoints.get(lableName + "_L").get(p)));
			allBold[i] = this.getAvgBOLD(tmpPointsL);
		} // for NumOfLabel=34

		for (int i = NumOfLabel; i < NumOfLabel * 2; i++) { // NumOfLabel=34
			String lableName = labelDic[i-NumOfLabel][1];
//			System.out.println(lableName+"_R");
			// get all points within this label area
			Set<djVtkPoint> tmpPointsR = new HashSet<djVtkPoint>();
			for (int p = 0; p < this.mapLabelPoints.get(lableName + "_R").size(); p++)
				tmpPointsR.add(this.surDataR.getPoint((int) this.mapLabelPoints.get(lableName + "_R").get(p)));
			allBold[i] = this.getAvgBOLD(tmpPointsR);
		} // for NumOfLabel=34

		// calculating the connectivity
		Correlation correlationHandler = new Correlation();
		for (int i = 0; i < NumOfROI - 1; i++)
			for (int j = i + 1; j < NumOfROI; j++)
				functionalConnectivityMatrix[i][j] = functionalConnectivityMatrix[j][i] = correlationHandler
						.Correlation_Pearsons(allBold[i], allBold[j]);
	}

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Need Para:RootDIR SubID white/pial structural/functional");
			System.exit(0);
		}

		GenerateConn mainHandler = new GenerateConn();
		mainHandler.rootDIR = args[0].trim();
		mainHandler.subID = args[1].trim();
		mainHandler.surface = args[2].trim();

		if (args[3].trim().equals("structural")) {
			mainHandler.loadDataForStrucConn();
			mainHandler.generateStructuralConn();
			DicccolUtilIO.writeVtkMatrix1(mainHandler.structuralConnectivityMatrix, mainHandler.NumOfROI,
					mainHandler.NumOfROI,
					mainHandler.rootDIR + "/" + mainHandler.subID + "/" + mainHandler.subID + "."+mainHandler.surface+".StruConn.vtk");
			DicccolUtilIO.writeArrayToFile(mainHandler.structuralConnectivityMatrix, mainHandler.NumOfROI, mainHandler.NumOfROI, ",", mainHandler.rootDIR + "/" + mainHandler.subID + "/" + mainHandler.subID + "."+mainHandler.surface+".StruConn.txt");
			
		} else if (args[3].trim().equals("functional")) {
			mainHandler.loadDataForFunctionalConn();
			mainHandler.generateFunctionalConn();
			DicccolUtilIO.writeVtkMatrix1(mainHandler.functionalConnectivityMatrix, mainHandler.NumOfROI,
					mainHandler.NumOfROI,
					mainHandler.rootDIR + "/" + mainHandler.subID + "/" + mainHandler.subID + "."+mainHandler.surface+".FunConn.vtk");
			DicccolUtilIO.writeArrayToFile(mainHandler.functionalConnectivityMatrix, mainHandler.NumOfROI,
					mainHandler.NumOfROI, ",",
					mainHandler.rootDIR + "/" + mainHandler.subID + "/" + mainHandler.subID + "."+mainHandler.surface+".FunConn.txt");
		} else
			System.out.println("Need Para:SubID structural/functional");

	}

}
