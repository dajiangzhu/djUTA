package edu.uta.cse.conference.ISBI2018;

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
	String[] className = {"AD","EMCI","LMCI","Normal","SMC"};
	List<List<djVtkPoint>> ClassPts;
	List<djVtkPoint> AllPts;
	
	public void loadData(String DirInput, String filePre)
	{
		ptFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\"+filePre+"Pt.txt");
		labelFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\"+filePre+"Label.txt");
		AllPts = new ArrayList<djVtkPoint>();
		ClassPts = new ArrayList<List<djVtkPoint>>();
	}
	
	public void generateDataPts(String DirOutput,String filePre)
	{
		int dataClass = 5;
		
		for(int i=0;i<dataClass;i++)
		{
			List<djVtkPoint> newPtList = new ArrayList<djVtkPoint>();
			ClassPts.add(newPtList);
		}
		String[] tmpArrayX = ptFileList.get(0).split(",");
		String[] tmpArrayY = ptFileList.get(1).split(",");
		String[] tmpArrayZ = ptFileList.get(2).split(",");
		for(int i=0;i<tmpArrayX.length;i++)
		{
			int classID = Integer.valueOf(labelFileList.get(i).trim());
			int ptID = ClassPts.get(classID-1).size();
			djVtkPoint newPt = new djVtkPoint(ptID,Float.valueOf(tmpArrayX[i].trim()),Float.valueOf(tmpArrayY[i].trim()),Float.valueOf(tmpArrayZ[i].trim()));
			AllPts.add(newPt);
			ClassPts.get(classID-1).add(newPt);
		}
		
		for(int i=0;i<dataClass;i++)
		{
			djVtkSurData NewPtVtk = new djVtkSurData();
			NewPtVtk.nPointNum = ClassPts.get(i).size();
			NewPtVtk.points.addAll(ClassPts.get(i));
			NewPtVtk.writePtsToVtkFile(DirOutput+"\\"+filePre+"_"+className[i]+"_Pts.vtk");
		}
		
	}
	
	public void generateLines(String DirInput, String DirOutput,String filePre) throws IOException
	{
		connMatrix = DicccolUtilIO.loadFileAsArray(DirInput+"\\"+filePre+"Conn.txt", AllPts.size(), AllPts.size(),",");
		List<String> connLines = new ArrayList<String>();
		for(int i=0;i<AllPts.size()-1;i++)
			for(int j=i+1;j<AllPts.size();j++)
				if(Math.abs(connMatrix[i][j])>0.1)
					connLines.add("2 "+i+" "+j+ "\r\n");
		
		//Begin to write the vtk file
		FileWriter fw = null;
		fw = new FileWriter(DirOutput+"\\"+filePre+".Conn.vtk");
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
		String DirInput = "C:\\D_Drive\\2018ISBI\\all_116feature_tsne\\all_116feature_tsne";
		String DirOutput = "C:\\D_Drive\\2018ISBI\\all_116feature_tsne\\vtk";
//		for(int r=1;r<117;r++)
//		{
//			System.out.println("Region-"+r+"-------------------------");
//			String filePre = "region_"+r+"_";
//			mainHandler.loadData(DirInput, filePre);
//			mainHandler.generateDataPts(DirOutput, filePre);
//			mainHandler.generateLines(DirInput, DirOutput, filePre);
//		}
//		
		for(int g=1;g<8;g++)
		{
			System.out.println("Group-"+g+"-------------------------");
			int start = (g-1)*20+1;
			int end = start+19;
			String filePre = "group"+g+"_time"+start+"to"+end+"_";
			mainHandler.loadData(DirInput, filePre);
			mainHandler.generateDataPts(DirOutput, filePre);
			mainHandler.generateLines(DirInput, DirOutput, filePre);
		}
		
		//group1_time1to20_Pt


	}

}
