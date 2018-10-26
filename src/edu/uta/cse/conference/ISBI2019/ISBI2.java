package edu.uta.cse.conference.ISBI2019;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djNiftiData;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class ISBI2 {

	private String rootDir;
	private djNiftiData oriData;
	private djNiftiData predictedData;
	private djNiftiData outputData;
	private djVtkSurData surfaceDataL;
	private djVtkSurData surfaceDataR;
	private float[][][] disMatrix;
	private float[] errDistribution;
	private float[] errDistributionAll = new float[11];

	public void loadData(String oriT1File, String predictedT1File, String surfaceFileL, String surfaceFileR) {
		oriData = new djNiftiData(oriT1File);
		outputData = new djNiftiData(oriT1File);
		predictedData = new djNiftiData(predictedT1File);
		surfaceDataL = new djVtkSurData(surfaceFileL);
		surfaceDataR = new djVtkSurData(surfaceFileR);
	}

	private int[] convertFromPhysicalToVolume(float[] physicalCoord) {
		int[] volumeCoord = new int[3];
		volumeCoord[0] = (int) ((physicalCoord[0] / oriData.Spacing[0]) + (oriData.xSize / 2));
		volumeCoord[1] = (int) ((physicalCoord[1] / oriData.Spacing[1]) + (oriData.ySize / 2));
		volumeCoord[2] = (int) ((physicalCoord[2] / oriData.Spacing[2]) + (oriData.zSize / 2));
		return volumeCoord;
	}

	public void setDirstribtuion(float ratio) {

		if (ratio <= 0.1)
			errDistribution[0]++;
		else if (ratio <= 0.2)
			errDistribution[1]++;
		else if (ratio <= 0.3)
			errDistribution[2]++;
		else if (ratio <= 0.4)
			errDistribution[3]++;
		else if (ratio <= 0.5)
			errDistribution[4]++;
		else if (ratio <= 0.6)
			errDistribution[5]++;
		else if (ratio <= 0.7)
			errDistribution[6]++;
		else if (ratio <= 0.8)
			errDistribution[7]++;
		else if (ratio <= 0.9)
			errDistribution[8]++;
		else if (ratio <= 1.0)
			errDistribution[9]++;
		else
			errDistribution[10]++;
	}

	public void calDistribtuion(int numOfVoxel) {
		for (int i = 0; i < 11; i++)
			errDistribution[i] /= numOfVoxel;
	}

	public void calAbsDis(int subID) {
		if (oriData.xSize != predictedData.xSize || oriData.ySize != predictedData.ySize
				|| oriData.zSize != predictedData.zSize || oriData.tSize != predictedData.tSize) {
			System.out.println("Dim does not fit!");
			System.exit(0);
		}
		disMatrix = new float[oriData.xSize][oriData.ySize][oriData.zSize];
		int effectiveVoxelNum = 0;
		for (int x = 0; x < oriData.xSize; x++)
			for (int y = 0; y < oriData.ySize; y++)
				for (int z = 0; z < oriData.zSize; z++) {
					if (oriData.getValueBasedOnVolumeCoordinate(x, y, z, 0) > 0.01f) {
						effectiveVoxelNum++;
						float tmpOriValue = oriData.getValueBasedOnVolumeCoordinate(x, y, z, 0);
						float tmpPredictedValue = predictedData.getValueBasedOnVolumeCoordinate(x, y, z, 0);
						float errRatio = Math.abs((tmpOriValue - tmpPredictedValue) / tmpOriValue);
						this.setDirstribtuion(errRatio);
						disMatrix[x][y][z] = errRatio;
					} // if
				} // z
		// totalError /= (maxIntensity-minIntensity);
		this.calDistribtuion(effectiveVoxelNum);
		System.out.println("errDistribution:");
		List<String> outDisList = new ArrayList<String>();
		for (int i = 0; i < 11; i++) {
			outDisList.add(String.valueOf(errDistribution[i]));
			System.out.println(errDistribution[i]);
			errDistributionAll[i] += errDistribution[i];
		}
		DicccolUtilIO.writeArrayListToFile(outDisList, this.rootDir + "\\" + subID + ".ErrDis.txt");
		System.out.println("effectiveVoxelNum: " + effectiveVoxelNum);
	}

	public void normalizeDisMatrix() {
		float tmpMax = -100.0f;
		float tmpMin = 1000.0f;

		for (int x = 0; x < oriData.xSize; x++)
			for (int y = 0; y < oriData.ySize; y++)
				for (int z = 0; z < oriData.zSize; z++) {
					if (disMatrix[x][y][z] > tmpMax)
						tmpMax = disMatrix[x][y][z];
					if (disMatrix[x][y][z] < tmpMin)
						tmpMin = disMatrix[x][y][z];
				} // z
		for (int x = 0; x < oriData.xSize; x++)
			for (int y = 0; y < oriData.ySize; y++)
				for (int z = 0; z < oriData.zSize; z++)
					disMatrix[x][y][z] = (disMatrix[x][y][z] - tmpMin) / (tmpMax - tmpMin);
	}

	public void mapErrorToSurface(djVtkSurData surfaceData, int subID, String hemi) {
//		this.normalizeDisMatrix();
		List<String> errList = new ArrayList<String>();
		float[] tmpPhyCoord = new float[3];
		for (int p = 0; p < surfaceData.nPointNum; p++) {
			tmpPhyCoord[0] = surfaceData.getPoint(p).x;
			tmpPhyCoord[1] = surfaceData.getPoint(p).y;
			tmpPhyCoord[2] = surfaceData.getPoint(p).z;
			int[] tmpVolCoord = this.convertFromPhysicalToVolume(tmpPhyCoord);
			// float valOri =
			// oriData.getValueBasedOnPhysicalCoordinate(surfaceData.getPoint(p).x,
			// surfaceData.getPoint(p).y, surfaceData.getPoint(p).z, 0);
			// float valPredicted =
			// predictedData.getValueBasedOnPhysicalCoordinate(surfaceData.getPoint(p).x,
			// surfaceData.getPoint(p).y, surfaceData.getPoint(p).z, 0);
			// float tmpDis = Math.abs(valOri-valPredicted);
			errList.add(String.valueOf(disMatrix[tmpVolCoord[0]][tmpVolCoord[1]][tmpVolCoord[2]]));
		} // for all points
		surfaceData.cellsOutput.addAll(surfaceData.getAllCells());
		surfaceData.pointsScalarData.put("PridictionError", errList);
		surfaceData.writeToVtkFile(this.rootDir + "\\" + subID + "." + hemi + ".ErrColor.vtk");
	}

	public static void main(String[] args) {
		ISBI2 maindler = new ISBI2();
		maindler.rootDir = "C:\\D_Drive\\Data\\HCP\\ISBI2019";
		List<String> subList = DicccolUtilIO.loadFileToArrayList(maindler.rootDir + "\\SubID.txt");
		for (int i = 0; i < subList.size(); i++) {
			int subID = Integer.valueOf(subList.get(i).trim());
			maindler.errDistribution = new float[11];
			System.out.println("############################## Dealing with " + (i + 1) + ": SubID = " + subID
					+ " ##############################");
			String oriT1File = maindler.rootDir + "\\accuracy_map_files\\" + subID + "_t1_2_b0.nii.gz";
			String predictedT1File = maindler.rootDir + "\\accuracy_map_files\\" + subID + "_averaged_xz_t1.nii";
			String oriSurfaceL = maindler.rootDir + "\\orig_surf\\" + subID + "\\orig_Surf\\vtk\\lh.white_orig.vtk";
			String oriSurfaceR = maindler.rootDir + "\\orig_surf\\" + subID + "\\orig_Surf\\vtk\\rh.white_orig.vtk";
			maindler.loadData(oriT1File, predictedT1File, oriSurfaceL, oriSurfaceR);
			maindler.calAbsDis(subID);
			maindler.mapErrorToSurface(maindler.surfaceDataL, subID, "L");
			maindler.mapErrorToSurface(maindler.surfaceDataR, subID, "R");
		}

//		List<String> outDisList = new ArrayList<String>();
//		for (int i = 0; i < 11; i++) {
//			maindler.errDistributionAll[i] /= subList.size();
//			outDisList.add(String.valueOf(maindler.errDistributionAll[i]));
//			DicccolUtilIO.writeArrayListToFile(outDisList, maindler.rootDir + "\\All30.ErrDis.txt");
//		}

	}

}
