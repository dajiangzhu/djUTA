package edu.uta.cse.conference.ISBI2019;

import java.util.Iterator;
import java.util.List;

import edu.uga.liulab.djVtkBase.djVtkSurData;

public class J_CombineSurface {
	private djVtkSurData surData1;
	private djVtkSurData surData2;
	
	private void loadData(String surFile1, String surFile2)
	{
		surData1 = new djVtkSurData(surFile1);
		surData2 = new djVtkSurData(surFile2);
	}
	
	private void combineSurfaces(String ourPutSur)
	{
		int startPtIndex = surData1.nPointNum;
		for(int p=0;p<surData2.nPointNum;p++)
		{
			surData2.getPoint(p).pointId = startPtIndex+p;
			surData1.points.add( surData2.getPoint(p) );
		}
		
		int startCellIndex = surData1.nCellNum;
		for(int c=0;c<surData2.nCellNum;c++)
		{
			surData2.getcell(c).cellId += startCellIndex+c;
			surData1.cells.add( surData2.getcell(c) );
		}
		
		surData1.nPointNum = surData1.nPointNum + surData2.nPointNum;
		surData1.nCellNum = surData1.nCellNum + surData2.nCellNum;
		surData1.cellsOutput.addAll(surData1.getAllCells());
		if (surData1.pointsScalarData.size() > 0) {
			Iterator iterCellData = surData1.pointsScalarData.keySet().iterator();
			while (iterCellData.hasNext()) {
				String tmpAttriName = (String) iterCellData.next();
				List<String> newAttriList = surData1.pointsScalarData.get(tmpAttriName);
				newAttriList.addAll(surData2.pointsScalarData.get(tmpAttriName));
				surData1.pointsScalarData.remove(tmpAttriName);
				surData1.pointsScalarData.put(tmpAttriName, newAttriList);
				}
			}
		surData1.writeToVtkFile(ourPutSur);
	}

	public static void main(String[] args) {
		if(args.length!=3)
		{
			System.out.println("Need para: sur1 sur2 outSuf");
			System.exit(0);
		}
		J_CombineSurface mainHandler = new J_CombineSurface();
		String surFileName1 = args[0].trim();
		String surFileName2 = args[1].trim();
		String outputFileName = args[2].trim();
		mainHandler.loadData(surFileName1, surFileName2);
		mainHandler.combineSurfaces(outputFileName);
	}

}
