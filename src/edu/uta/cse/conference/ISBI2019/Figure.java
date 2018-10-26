package edu.uta.cse.conference.ISBI2019;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class Figure {
	List<String> ptFileList;
	List<String> labelFileList;
	double[][] connMatrix;
	String[] className = {"LMCI","EMCI","SMC","Normal"};
	List<List<djVtkPoint>> ClassPts;
	List<djVtkPoint> AllPts;
	
	public void loadData(String DirInput, String connType, String numOfFeature)
	{
		ptFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\feature-"+numOfFeature+"-Node\\"+connType+"_"+numOfFeature+"_pt.txt");
		labelFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\sub_label.txt");
		AllPts = new ArrayList<djVtkPoint>();
		ClassPts = new ArrayList<List<djVtkPoint>>();
	}
	
	public void generateDataPts(String DirOutput,String connType, String numOfFeature)
	{
		int dataClass = 4;
		AllPts.clear();
		
		for(int i=0;i<dataClass;i++)
		{
			List<djVtkPoint> newPtList = new ArrayList<djVtkPoint>();
			ClassPts.add(newPtList);
		}
		
		for(int i=0;i<ptFileList.size();i++)
		{
			int classID = Integer.valueOf(labelFileList.get(i).trim());
			int ptID = ClassPts.get(classID-1).size();
			String[] coord = ptFileList.get(i).split("\\s+");
			djVtkPoint newPt = new djVtkPoint(ptID,Float.valueOf(coord[0].trim()),Float.valueOf(coord[1].trim()),Float.valueOf(coord[2].trim()));
			AllPts.add(newPt);
			ClassPts.get(classID-1).add(newPt);
		}
		
		for(int i=0;i<dataClass;i++)
		{
			djVtkSurData NewPtVtk = new djVtkSurData();
			NewPtVtk.nPointNum = ClassPts.get(i).size();
			NewPtVtk.points.addAll(ClassPts.get(i));
			NewPtVtk.writePtsToVtkFile(DirOutput+"\\feature-"+numOfFeature+"-Node\\"+connType+"_"+numOfFeature+"_"+className[i]+"_Pts.vtk");
		}
	}
	
	public void generateLines(String DirInput, String connType, String numOfFeature) throws IOException
	{
		List<String> connLines = new ArrayList<String>();
		List<String> pairFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\feature-"+numOfFeature+"-Node\\"+connType+"_"+numOfFeature+"_tree.txt");
		for(int i=0;i<pairFileList.size();i++)
		{
			String[] pair = pairFileList.get(i).split("\\s+");
			int start = Integer.valueOf(pair[0].trim())-1;
			int end = Integer.valueOf(pair[1].trim())-1;
			connLines.add("2 "+start+" "+end+ "\r\n");
		}
		
		//Begin to write the vtk file
		FileWriter fw = null;
		fw = new FileWriter(DirInput+"\\feature-"+numOfFeature+"-Node\\"+connType+"_"+numOfFeature+"_tree.vtk");
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

	public static void main(String[] args) throws IOException {
		Figure mainHandler = new Figure();
		String connType = "func";
		String numOfFeature = "50";
		String DirInput = "C:\\D_Drive\\2019ISBI";
		String DirOutput = "C:\\D_Drive\\2019ISBI";
		
		mainHandler.loadData(DirInput, connType, numOfFeature);
		mainHandler.generateDataPts(DirInput, connType, numOfFeature);
		mainHandler.generateLines(DirInput, connType, numOfFeature);


	}
}
