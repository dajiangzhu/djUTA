package edu.uta.cse.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkHybridData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class CalFiberDensity {

	public void calFD(String rootDir, String surfaceFile, String fiberFile) {
		djVtkSurData surData = new djVtkSurData(rootDir + surfaceFile);
		djVtkFiberData fiberData = new djVtkFiberData(rootDir + fiberFile);
		djVtkHybridData hybridData = new djVtkHybridData(surData, fiberData);
		hybridData.mapSurfaceToBox();
		hybridData.mapFiberToBox();

		List<String> densityList = new ArrayList<String>();

		System.out.println("---------Surface has "+surData.nPointNum+" points!---------");
		for (int p = 0; p < surData.nPointNum; p++) {
			if (p%100 == 0)
				System.out.println("-----Dealing with Point - " + p + "------");
			Set<djVtkPoint> tmpPts = surData.getNeighbourPoints(p, 3);
			djVtkFiberData tmpFiberData = (djVtkFiberData) hybridData.getFibersConnectToPointsSet(tmpPts)
					.getCompactData();
			float tmpDensity = (float)tmpFiberData.nCellNum / (float)fiberData.nCellNum;
			densityList.add(String.valueOf(tmpDensity));
		}
		surData.pointsScalarData.put("FiberDensity", densityList);
		surData.cellsOutput.addAll(surData.cells);
		surData.writeToVtkFile(rootDir + surfaceFile + "FiberDensity.vtk");
	}

	public static void main(String[] args) {
		if(args.length==2)
		{
			String group = args[0].trim();
			String subID = args[1].trim();
			String rootDir = "./"+group+"/"+subID+"/";
			
			String surfaceFile = "surf.smooth.vtk";
			String fiberFile = "fiber_asc.vtk";
			CalFiberDensity mainHandler = new CalFiberDensity();
			mainHandler.calFD(rootDir, surfaceFile, fiberFile);
			
		}
		else
			System.out.println("Need 2 parameters: Group(AD or CN) SubjectID" );
//		String group = "AD";
//		String subID = "003_S_4136_1";
//		String rootDir = "C:\\Users\\zhud2xx\\git\\djUTA\\" + group + "\\" + subID + "\\";
//		String surfaceFile = "surf.smooth.vtk";
//		String fiberFile = "fiber_asc.vtk";
//
//		CalFiberDensity mainHandler = new CalFiberDensity();
//		mainHandler.calFD(rootDir, surfaceFile, fiberFile);

	}

}
