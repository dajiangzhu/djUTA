package edu.uta.cse.conference.MICCAI2019;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;
import edu.uta.cse.conference.ISBI2019.Figure;

public class WangAnalysis {

	List<String> ptFileList;
	List<String> labelFileList;
	double[][] connMatrix;
//	String[] className = {"LMCI","EMCI","SMC","Normal"};
	String[] className = {"Normal","EMCI","LMCI"};
	int dataClass = 3;
	List<String> classColor;
	List<List<djVtkPoint>> ClassPts;
	List<djVtkPoint> AllPts;
	
	public void loadData(String DirInput)
	{
		ptFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\"+"pts_1.txt");
		labelFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\subLabel.txt");
		classColor = DicccolUtilIO.loadFileToArrayList(DirInput+"\\ColorDictionary.txt");
		AllPts = new ArrayList<djVtkPoint>();
		ClassPts = new ArrayList<List<djVtkPoint>>();
	}
	
	public void generateDataPts(String DirInput)
	{
		float scaler = 20.0f;
		AllPts.clear();
		for(int i=0;i<dataClass;i++)
		{
			List<djVtkPoint> newPtList = new ArrayList<djVtkPoint>();
			ClassPts.add(newPtList);
		}
		
		for(int i=0;i<ptFileList.size();i++)
		{
			int classID = Integer.valueOf(labelFileList.get(i).trim());
			classID++;
			int ptID = ClassPts.get(classID-1).size();
			String[] coord = ptFileList.get(i).split("\\s+");
			djVtkPoint newPt = new djVtkPoint(ptID,scaler*Float.valueOf(coord[0].trim()),scaler*Float.valueOf(coord[1].trim()),scaler*Float.valueOf(coord[2].trim()));
			AllPts.add(newPt);
			ClassPts.get(classID-1).add(newPt);
		}
		
		for(int i=0;i<dataClass;i++)
		{
			djVtkSurData NewPtVtk = new djVtkSurData();
			NewPtVtk.nPointNum = ClassPts.get(i).size();
			NewPtVtk.points.addAll(ClassPts.get(i));
			NewPtVtk.writePtsToVtkFile(DirInput+"\\"+className[i]+"_Pts.vtk");
		}
	}
	
	public void generateDataBall(String DirInput)
	{
		
		String templateVtkFile = DirInput + "\\TemplateVtk\\template.0.02.vtk";
		djVtkSurData templateVtkData = new djVtkSurData(templateVtkFile);
		
		djVtkSurData pointData = new djVtkSurData();
		List<String> attriList = new ArrayList<String>();
		for(int i=0;i<dataClass;i++)
			for(int j=0;j<ClassPts.get(i).size();j++)
			{
				pointData.points.add(ClassPts.get(i).get(j));
				attriList.add( classColor.get(i) );
			}
		pointData.nPointNum = ptFileList.size();
		
		GenerateDICCCOL roiHandler = new GenerateDICCCOL();
		roiHandler.GenerateDICCCOLBallWithColor(pointData, templateVtkData, attriList, DirInput+"\\Bubbles.vtk");
		
	}
	
	public void saveTree(String DirInput)
	{
		List<String> outputList = new ArrayList<String>();
		Set<Integer> testSet = new HashSet<Integer>();
		int[][] adjM = DicccolUtilIO.loadFileAsIntArray(DirInput+"\\adjMatrix.txt", 386, 386);
		for(int i=0;i<385;i++)
			for(int j=i+1;j<386;j++)
				if(adjM[i][j]==1)
				{
					outputList.add((i+1)+"     "+(j+1));
					testSet.add(i+1);
					testSet.add(j+1);
				}
		System.out.println("Size of set: "+testSet.size());
		DicccolUtilIO.writeArrayListToFile(outputList, DirInput+"\\tree.txt");
		
	}
	
	public void generateLines(String DirInput) throws IOException
	{
		List<String> connLines = new ArrayList<String>();
		List<String> pairFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\tree.txt");
		for(int i=0;i<pairFileList.size();i++)
		{
			String[] pair = pairFileList.get(i).split("\\s+");
			int start = Integer.valueOf(pair[0].trim())-1;
			int end = Integer.valueOf(pair[1].trim())-1;
			connLines.add("2 "+start+" "+end+ "\r\n");
		}
		
		//Begin to write the vtk file
		FileWriter fw = null;
		fw = new FileWriter(DirInput+"\\tree.vtk");
		fw.write("# vtk DataFile Version 3.0\r\n");
		fw.write("vtk output\r\n");
		fw.write("ASCII\r\n");
		fw.write("DATASET POLYDATA\r\n");
		fw.write("POINTS " + AllPts.size() + " float\r\n");
		for (int i = 0; i < AllPts.size(); i++)
			fw.write(AllPts.get(i).x + " " + AllPts.get(i).y + " " + AllPts.get(i).z + "\r\n");
		fw.write("LINES " + connLines.size() + " " + (connLines.size() * 3) + " \r\n");
		for (int i = 0; i < connLines.size(); i++)
			fw.write(connLines.get(i));
		fw.close();
		System.out.println("Write file done!");
	}
	
	public void colorSurface(String DirInput)
	{
		String hemi = "rh";
		String surfaceFileL = DirInput + "\\"+hemi+".pial.vtk";
		djVtkSurData surfaceData = new djVtkSurData(surfaceFileL);
		float[] pintCount = new float[surfaceData.nPointNum];
		
		List<String> ROICountList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\ROI_pattern_count.txt");

		for(int l=75;l<150;l++)
		{
			String[] currentLine = ROICountList.get(l).split("\\s+");
			String currentLabel = currentLine[4].trim();
			System.out.println("-------------> "+currentLabel);
			float currentCount = Float.valueOf(currentLine[0].trim())/21.0f;
			System.out.println("--count:  "+currentCount);
			
			List<String> labelInfo = DicccolUtilIO.loadFileToArrayList(DirInput+"\\allLabelsPial2009\\"+hemi+"."+currentLabel+".label");
			for(int i=2;i<labelInfo.size();i++)
				pintCount[ Integer.valueOf(labelInfo.get(i).trim().split("\\s+")[0]) ] = currentCount;
		}
		List<String> colorAttri = new ArrayList<String>();
		for(int i=0;i<surfaceData.nPointNum;i++)
			colorAttri.add( String.valueOf( pintCount[i]) );
		surfaceData.pointsScalarData.put("PatternCount", colorAttri);
		surfaceData.cellsOutput.addAll(surfaceData.getAllCells());
		surfaceData.writeToVtkFile(DirInput + "\\"+hemi+".pial_PatternCount.vtk");
		
	}

	public static void main(String[] args) throws IOException {
		WangAnalysis mainHandler = new WangAnalysis();
		
		String DirInput = "C:\\D_Drive\\2019MICCAI\\wang";
		
//		mainHandler.saveTree(DirInput);
		
		//draw the learned structure
//		mainHandler.loadData(DirInput);
//		mainHandler.generateDataPts(DirInput);
//		mainHandler.generateLines(DirInput);
//		mainHandler.generateDataBall(DirInput);
		
		//color the surface with pattern count
		mainHandler.colorSurface(DirInput);


	}

}