package edu.uta.cse.conference.IPMI2019;

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

public class WangFig1 {

	private int NumOfLabel = 34;
	private int NumOfROI = NumOfLabel * 2;

	private double[][] structuralConnectivityMatrix = new double[NumOfROI][NumOfROI];

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
		labelDic = DicccolUtilIO.loadFileAsStringArray(rootDIR + "/aparc.annot.ctab", 34, 6);
		// labelDic = DicccolUtilIO.loadFileAsStringArray(rootDIR +
		// "\\aparc.annot.ctab", 34, 6);

		// load surface (left and right) and fiber data
		surDataL = new djVtkSurData(rootDIR + "\\" + this.subID + "\\Surf\\vtk\\lh." + this.surface + "_transform.vtk");
		surDataR = new djVtkSurData(rootDIR + "\\" + this.subID + "\\Surf\\vtk\\rh." + this.surface + "_transform.vtk");
		fiberData = new djVtkFiberData(
				rootDIR + "\\" + this.subID + "\\Fiber\\preprocessing\\fiber\\MedINRIA_StreamLine\\fiber.asc.vtk");

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
					.loadFileToArrayList(rootDIR + "\\AllROILabels\\" + this.subID + "\\lh." + lableName + ".label");
			for (int j = 2; j < labelInfo_L.size(); j++)
				ptsInLable_L.add(Integer.valueOf(labelInfo_L.get(j).trim().split("\\s+")[0]));
			mapLabelPoints.put(lableName + "_L", ptsInLable_L);

			List<Integer> ptsInLable_R = new ArrayList<Integer>();
			List<String> labelInfo_R = DicccolUtilIO
					.loadFileToArrayList(rootDIR + "\\AllROILabels\\" + this.subID + "\\rh." + lableName + ".label");
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
		Map<String, List<Integer>> rawConnectivityMap = new HashMap<String, List<Integer>>();
		for (int i = 0; i < rawConnectivityInfo.size() - 1; i++)
			for (int j = i + 1; j < rawConnectivityInfo.size(); j++) {
				int count = 0;
				List<Integer> tmpList = new ArrayList<Integer>();
				tmpList.addAll(rawConnectivityInfo.get(i));
				List<Integer> connectionList = new ArrayList<Integer>();
				for (int m = 0; m < tmpList.size(); m++) {
					int currentCellID = tmpList.get(m);
					if (rawConnectivityInfo.get(j).contains(currentCellID))
					{
						count++;
						connectionList.add(currentCellID);
					}
				} // for m
				structuralConnectivityMatrix[i][j] = structuralConnectivityMatrix[j][i] = (double) count;
				rawConnectivityMap.put(i+"-"+j, connectionList);
			} // for j
		
		//check if has i, j and k that i connect j, j connect k and k connect i
//		System.out.println("13-23: "+structuralConnectivityMatrix[12][22]);
//		System.out.println("13-28: "+structuralConnectivityMatrix[12][27]);
//		System.out.println("23-28: "+structuralConnectivityMatrix[22][2]);
		//Output fibers connecting to these three ROIs
		List<Integer> RoiCandidateList = new ArrayList<Integer>();
		RoiCandidateList.add(9);
		RoiCandidateList.add(22);
		RoiCandidateList.add(30);
		RoiCandidateList.add(31);
		RoiCandidateList.add(43);
		RoiCandidateList.add(56);
		RoiCandidateList.add(64);
		RoiCandidateList.add(65);
		this.fiberData.cellsOutput.clear();
		for(int i=0;i<RoiCandidateList.size()-1;i++)
			for(int j=i+1;j<RoiCandidateList.size();j++)
			{
				List<Integer> tmpCellIds = rawConnectivityMap.get(RoiCandidateList.get(i)+"-"+RoiCandidateList.get(j));
				for(int k=0;k<tmpCellIds.size();k++)
					this.fiberData.cellsOutput.add(this.fiberData.getcell(tmpCellIds.get(k)));
			}
		this.fiberData.writeToVtkFileCompact(rootDIR + "\\" + this.subID + "\\fibers_8ROIs.vtk");
		
		
//		double threshold = 150.0;
//		for(int i=0;i<NumOfROI-1;i++)
//			for(int j=i+1;j<NumOfROI;j++)
//				if(structuralConnectivityMatrix[i][j]>=threshold)
//					for(int k=0;k<NumOfROI;k++)
//						if(structuralConnectivityMatrix[j][k]>=threshold && k!=i && structuralConnectivityMatrix[k][i]>=threshold)
//							System.out.println("************ "+(i+1)+"-"+(j+1)+"("+structuralConnectivityMatrix[i][j]+") "+(j+1)+"-"+(k+1)+"("+structuralConnectivityMatrix[j][k]+") "+(k+1)+"-"+(i+1)+"("+structuralConnectivityMatrix[k][i]+")");
		
	}

	public void test() {
		djVtkPoint tmpPoint = this.surDataL.getPoint(99501);
		int[] volumeCoord;
		float[] physicalCoord = new float[3];
		physicalCoord[0] = tmpPoint.x;
		physicalCoord[1] = tmpPoint.y;
		physicalCoord[2] = tmpPoint.z;
		volumeCoord = rsData.convertFromPhysicalToVolume(physicalCoord);
		System.out.println("x-" + volumeCoord[0] + "  y-" + volumeCoord[1] + "  z-" + volumeCoord[2]);

		double[] boldsSig = new double[this.rsData.tSize];
		for (int t = 0; t < this.rsData.tSize; t++) {
			// boldsSig[t] = this.rsData.getValueBasedOnPhysicalCoordinate(tmpPoint.x,
			// tmpPoint.y, tmpPoint.z, t);
			boldsSig[t] = this.rsData.getValueBasedOnVolumeCoordinate(99, 66, 51, t);
			System.out.println("t-" + t + ": " + boldsSig[t]);
		}

	}

	public static void main(String[] args) {

		WangFig1 mainHandler = new WangFig1();
		mainHandler.rootDIR = "C:\\D_Drive\\Data\\ADNI";
		mainHandler.subID = "002_S_0413";
		mainHandler.surface = "white";

		mainHandler.loadDataForStrucConn();
		mainHandler.generateStructuralConn();

	}

}
