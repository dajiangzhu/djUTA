package edu.uta.cse.test;

import edu.uga.liulab.djVtkBase.djNiftiData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class test1 {
	
	public void calAtlasSize()
	{
		djNiftiData aalTemplate = new djNiftiData("aal.nii.gz");
		int xSize = aalTemplate.xSize;
		int ySize = aalTemplate.ySize;
		int zSize = aalTemplate.zSize;
		System.out.println("XSize="+xSize+"   YSize="+ySize+"   ZSize="+zSize);

		int[] x_min = new int[90];
		int[] x_max = new int[90];
		int[] y_min = new int[90];
		int[] y_max = new int[90];
		int[] z_min = new int[90];
		int[] z_max = new int[90];
		int[] numOfLabe = new int[90];
		
		double avgVox = 0.0;

		for (int l = 1; l <= 90; l++) {
			System.out.println("*************   Label - "+l+"   ********************");
			int xMin = 1000;
			int xMax = -1;
			int yMin = 1000;
			int yMax = -1;
			int zMin = 1000;
			int zMax = -1;
			for (int x = 0; x < xSize; x++)
				for (int y = 0; y < ySize; y++)
					for (int z = 0; z < zSize; z++) {
						int currentLabel = (int) aalTemplate.getValueBasedOnVolumeCoordinate(x, y, z, 0);
						if (currentLabel == l) {
							numOfLabe[l - 1]++;
							if (x < xMin)
								xMin = x;
							if (x > xMax)
								xMax = x;
							if (y < yMin)
								yMin = y;
							if (y > yMax)
								yMax = y;
							if (z < zMin)
								zMin = z;
							if (z > zMax)
								zMax = z;
						} // if

					} // for z
			int tmpVolNum = ((xMax-xMin+1)*(yMax-yMin+1)*(zMax-zMin+1));
			avgVox += tmpVolNum;
			System.out.println("XMin= "+xMin+"   XMax= "+xMax+" YMin="+yMin+"   YMax="+yMax+"   ZMin="+zMin+"   ZMax="+zMax+"  NumOfVoxel="+ tmpVolNum);
		} // for l
		avgVox /=90.0;
		System.out.println("******************** AvgNumOfVoxel = "+ avgVox);
		System.out.println("******************** SuggestBlockSize = "+ Math.cbrt(avgVox));
	}

	public void testFreeSurferSurMapping()
	{
		djNiftiData t1ToDTIData = new djNiftiData("t1_2_dti.nii");
		djVtkSurData surData_L = new djVtkSurData("lh.pial.smooth.vtk");
		djVtkSurData surData_R = new djVtkSurData("rh.pial.smooth.vtk");
		djVtkPoint pt_L = surData_L.getPoint(94428);
		djVtkPoint pt_R = surData_R.getPoint(159895);
		int x = (int)(pt_R.x*(-1/1.25)+t1ToDTIData.xSize/2);
		int y = (int)(pt_R.y*(1/1.25)+t1ToDTIData.ySize/2);
		int z = (int)(pt_R.z*(1/1.25)+t1ToDTIData.zSize/2);
		System.out.println("Surface: x_L="+pt_L.x+"  y_R="+pt_L.y+"   z_R="+pt_L.z);
		System.out.println("Surface: x_R="+pt_R.x+"  y_R="+pt_R.y+"   z_R="+pt_R.z);
		System.out.println("T1: x="+x+"  y="+y+"   z="+z);
	}
	
	public void testAccessT1()
	{
		djNiftiData t1OriData = new djNiftiData("T1w_acpc_dc_restore_brain.nii.gz");
		
		System.out.println("t1OriData.nDims:"+t1OriData.nDims);
		System.out.println("t1OriData.xSize:"+t1OriData.xSize);
		System.out.println("t1OriData.ySize:"+t1OriData.ySize);
		System.out.println("t1OriData.zSize:"+t1OriData.zSize);
		System.out.println("t1OriData.offset:"+t1OriData.offset);
		System.out.println("t1OriData.Spacing:"+t1OriData.Spacing[0]+"  "+t1OriData.Spacing[1]+"   "+t1OriData.Spacing[2]);
		
		djNiftiData t1ToDTIData = new djNiftiData("t1_2_dti.nii");
		System.out.println("t1ToDTIData.nDims:"+t1ToDTIData.nDims);
		System.out.println("t1ToDTIData.xSize:"+t1ToDTIData.xSize);
		System.out.println("t1ToDTIData.ySize:"+t1ToDTIData.ySize);
		System.out.println("t1ToDTIData.zSize:"+t1ToDTIData.zSize);
		System.out.println("t1ToDTIData.offset:"+t1ToDTIData.offset);
		System.out.println("t1ToDTIData.Spacing:"+t1ToDTIData.Spacing[0]+"  "+t1ToDTIData.Spacing[1]+"   "+t1ToDTIData.Spacing[2]);
	}
	
	public static void main(String[] args) {
		test1 mainHandler = new test1();
//		mainHandler.testFreeSurferSurMapping();
		mainHandler.testAccessT1();
		

	}

}
