package edu.uta.cse.conference.ISBI2018;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djVtkCell;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class GenerateFreeSurferLabelPatch {
	
	djVtkSurData surfaceData;
	
	public void loadSurface()
	{
		String surfaceFile = "./Data_ISBI2018/lh.vtk";
		surfaceData = new djVtkSurData(surfaceFile.trim());
		surfaceData.printInfo();
	}
	
	public Set loadLabel()
	{
		String labelFile = "./Data_ISBI2018/AllLabels/lh.superiortemporal.label";
		int ptNumber = -1;
		Set ptSet = new HashSet<Integer>();
		List<String> context = DicccolUtilIO.loadFileToArrayList(labelFile);
		for(String line:context)
		{
			String[] currentArray = line.split("\\s+");
			if(currentArray.length==1)
				ptNumber = Integer.valueOf(currentArray[0].trim());
			if(currentArray.length==5)
				ptSet.add(Integer.valueOf(currentArray[0].trim()));
		}
		return ptSet;
	}
	
	public void generateLabelPatch(Set ptSet)
	{
		List<djVtkCell> AllCells = this.surfaceData.getAllCells();
		this.surfaceData.cellsOutput.clear();
		List<djVtkCell> newCells = new ArrayList<djVtkCell>();
		for(djVtkCell currentCell:AllCells)
		{
//			System.out.println("Cell-"+currentCell.cellId+" ...");
			if(ptSet.contains( currentCell.pointsList.get(0).pointId) && ptSet.contains( currentCell.pointsList.get(1).pointId) && ptSet.contains( currentCell.pointsList.get(2).pointId))
			{
				System.out.println("Cell-:"+currentCell.cellId+" Yes!");
				this.surfaceData.cellsOutput.add(currentCell);
			}
//			else
//				System.out.println("Cell-:"+currentCell.cellId+" NO!")
		}
		System.out.println(newCells.size());
		this.surfaceData.writeToVtkFileCompact("./Data_ISBI2018/lh_lh.superiortemporal.label.vtk");
		
		
	}
	
	
	public void loadAllSig()
	{
		
	}

	public static void main(String[] args) {
		GenerateFreeSurferLabelPatch mainHandler = new GenerateFreeSurferLabelPatch();
		mainHandler.loadSurface();
		mainHandler.generateLabelPatch(mainHandler.loadLabel());

	}

}
