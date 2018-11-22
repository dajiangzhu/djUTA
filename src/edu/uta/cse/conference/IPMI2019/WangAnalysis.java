package edu.uta.cse.conference.IPMI2019;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;
import edu.uta.cse.conference.ISBI2019.Figure;

public class WangAnalysis {

	List<String> ptFileList;
	List<String> labelFileList;
	double[][] connMatrix;
	String[] className = {"LMCI","EMCI","SMC","Normal"};
	int dataClass = 4;
	List<String> classColor;
	List<List<djVtkPoint>> ClassPts;
	List<djVtkPoint> AllPts;
	
	public void loadData(String DirInput, String methodType, String connType)
	{
		ptFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\"+methodType+"\\"+connType+"_pt.txt");
		labelFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\sub_label.txt");
		classColor = DicccolUtilIO.loadFileToArrayList(DirInput+"\\ColorDictionary.txt");
		AllPts = new ArrayList<djVtkPoint>();
		ClassPts = new ArrayList<List<djVtkPoint>>();
	}
	
	public void generateDataPts(String DirInput,String methodType, String connType)
	{
		
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
			NewPtVtk.writePtsToVtkFile(DirInput+"\\"+methodType+"\\"+connType+"_"+className[i]+"_Pts.vtk");
		}
	}
	
	public void generateDataBall(String DirInput,String methodType, String connType)
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
		roiHandler.GenerateDICCCOLBallWithColor(pointData, templateVtkData, attriList, DirInput+"\\"+methodType+"\\Bubbles_"+connType+".vtk");
		
	}
	
	public void generateLines(String DirInput,String methodType, String connType) throws IOException
	{
		List<String> connLines = new ArrayList<String>();
		List<String> pairFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\"+methodType+"\\tree.txt");
		for(int i=0;i<pairFileList.size();i++)
		{
			String[] pair = pairFileList.get(i).split("\\s+");
			int start = Integer.valueOf(pair[0].trim())-1;
			int end = Integer.valueOf(pair[1].trim())-1;
			connLines.add("2 "+start+" "+end+ "\r\n");
		}
		
		//Begin to write the vtk file
		FileWriter fw = null;
		fw = new FileWriter(DirInput+"\\"+methodType+"\\"+connType+"_tree.vtk");
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
		WangAnalysis mainHandler = new WangAnalysis();
		String methodType = "multiview-white";
		String connType = "func";
		String DirInput = "C:\\D_Drive\\2019IPMI\\MultiViewResult";
		
		mainHandler.loadData(DirInput, methodType, connType);
		mainHandler.generateDataPts(DirInput, methodType, connType);
		mainHandler.generateLines(DirInput, methodType, connType);
		mainHandler.generateDataBall(DirInput, methodType, connType);


	}

}
