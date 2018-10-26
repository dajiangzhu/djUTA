package edu.uta.cse.conference.ISBI2019;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djVtkCell;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class TestLabel {
	
	public String rootDir = "C:\\D_Drive\\Data\\ADNI";
	public String[][] labelDic;
	public djVtkSurData surfaceData;
	public String hemi = "lh";
	public Map<String, List> mapLabels = new HashMap<String, List>();
	
	public void loadSurface(String surfaceID)
	{
		String surfaceFile = rootDir + "\\" + surfaceID +"\\Surf\\vtk\\"+hemi+".pial_transform.vtk";
		surfaceData = new djVtkSurData(surfaceFile);
	}
	
	public void loadLabelDiction (String surfaceID)
	{
		labelDic = DicccolUtilIO.loadFileAsStringArray(rootDir + "\\" + surfaceID +"\\Surf\\label\\aparc.annot.ctab", 34, 6);
	}
	
	public void loadLabels(String surfaceID)
	{
		for(int i=0;i<34;i++)
		{
			String lableName = labelDic[i][1];
			System.out.println(lableName);
			List<String> ptsInLable = new ArrayList<String>();
			List<String> labelInfo = DicccolUtilIO.loadFileToArrayList(rootDir + "\\" + surfaceID +"\\AllLabels\\"+hemi+"."+lableName+".label");
			for(int j=2;j<labelInfo.size();j++)
				ptsInLable.add( labelInfo.get(j).trim().split("\\s+")[0] );
			mapLabels.put(lableName+"."+hemi, ptsInLable);
		}
	}
	
	public void generatePatchByLabel(String surfaceID)
	{
		String lableName = "precentral";
		List<String> labelInfo = mapLabels.get(lableName+"."+hemi);
		Set<djVtkCell> cellsForLabel = new HashSet<djVtkCell>();
		for(int i=0;i<labelInfo.size();i++)
			cellsForLabel.addAll( surfaceData.getPoint( Integer.valueOf(labelInfo.get(i)) ).cellsList );
		surfaceData.cellsOutput.addAll(cellsForLabel);
		surfaceData.writeToVtkFile(rootDir + "\\" + surfaceID +"\\Surf\\vtk\\"+hemi+".pial_transform.vtk"+lableName+".vtk");
		
	}

	public static void main(String[] args) {
		String subID = "002_S_0413";
		TestLabel mainHandler = new TestLabel();
		mainHandler.loadLabelDiction(subID);
		mainHandler.loadLabels(subID);
		mainHandler.loadSurface(subID);
		mainHandler.generatePatchByLabel(subID);
		

	}

}
