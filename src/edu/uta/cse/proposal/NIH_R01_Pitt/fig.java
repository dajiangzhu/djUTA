package edu.uta.cse.proposal.NIH_R01_Pitt;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class fig {

	List<String> ptXYZList;
	List<String> labelList;
	List<String> uniqueLabels;
	
	double[][] connMatrix;
	String[] className = {"LMCI","EMCI","SMC","Normal"};

	List<String> classColor;
	List<List<djVtkPoint>> ClassPts;
	List<djVtkPoint> AllPts;
	
	List<String> pairFileList;
	
//	public void loadData(String DirInput, String methodType, String connType)
//	{
//		ptFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\"+methodType+"\\"+connType+"_pt.txt");
//		labelFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"\\sub_label_1_4.txt");
//		classColor = DicccolUtilIO.loadFileToArrayList(DirInput+"\\ColorDictionary.txt");
//		AllPts = new ArrayList<djVtkPoint>();
//		ClassPts = new ArrayList<List<djVtkPoint>>();
//	}
	
//	public void generateDataPts(String DirInput,String methodType, String connType)
//	{
//		
//		AllPts.clear();
//		for(int i=0;i<dataClass;i++)
//		{
//			List<djVtkPoint> newPtList = new ArrayList<djVtkPoint>();
//			ClassPts.add(newPtList);
//		}
//		
//		for(int i=0;i<ptFileList.size();i++)
//		{
//			int classID = Integer.valueOf(labelFileList.get(i).trim());
//			int ptID = ClassPts.get(classID-1).size();
//			String[] coord = ptFileList.get(i).split("\\s+");
//			djVtkPoint newPt = new djVtkPoint(ptID,Float.valueOf(coord[0].trim()),Float.valueOf(coord[1].trim()),Float.valueOf(coord[2].trim()));
//			AllPts.add(newPt);
//			ClassPts.get(classID-1).add(newPt);
//		}
//		
//		for(int i=0;i<dataClass;i++)
//		{
//			djVtkSurData NewPtVtk = new djVtkSurData();
//			NewPtVtk.nPointNum = ClassPts.get(i).size();
//			NewPtVtk.points.addAll(ClassPts.get(i));
//			NewPtVtk.writePtsToVtkFile(DirInput+"\\"+methodType+"\\"+connType+"_"+className[i]+"_Pts.vtk");
//		}
//	}
	
//	public void generateDataBall(String DirInput,String methodType, String connType)
//	{
//		
//		String templateVtkFile = DirInput + "\\TemplateVtk\\template.0.02.vtk";
//		djVtkSurData templateVtkData = new djVtkSurData(templateVtkFile);
//		
//		djVtkSurData pointData = new djVtkSurData();
//		List<String> attriList = new ArrayList<String>();
//		for(int i=0;i<dataClass;i++)
//			for(int j=0;j<ClassPts.get(i).size();j++)
//			{
//				pointData.points.add(ClassPts.get(i).get(j));
//				attriList.add( classColor.get(i) );
//			}
//		pointData.nPointNum = ptFileList.size();
//		
//		GenerateDICCCOL roiHandler = new GenerateDICCCOL();
//		roiHandler.GenerateDICCCOLBallWithColor(pointData, templateVtkData, attriList, DirInput+"\\"+methodType+"\\Bubbles_"+connType+".vtk");
//		
//	}
//	
	public void generateLines(String DirInput,String fileOutputString) throws IOException
	{
		List<String> connLines = new ArrayList<String>();
		
		for(int i=0;i<pairFileList.size();i++)
		{
			String[] pair = pairFileList.get(i).split("\\s+");
			int start = Integer.valueOf(pair[0].trim())-1;
			int end = Integer.valueOf(pair[1].trim())-1;
			connLines.add("2 "+start+" "+end+ "\r\n");
		}
		
		//Begin to write the vtk file
		FileWriter fw = null;
		fw = new FileWriter(DirInput+"/"+fileOutputString+"_tree.vtk");
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
	
	public void loadData(String DirInput, String ptFileName, String treeFileName, String labelFileName)
	{
		ptXYZList = DicccolUtilIO.loadFileToArrayList(DirInput+"/"+ptFileName);
		List<String> labelFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"/"+labelFileName);
		labelList = new ArrayList<String>();
		uniqueLabels = new ArrayList<String>();
		for(int i=0;i<labelFileList.size();i++)
		{
			String currentLabel = "";
			currentLabel = labelFileList.get(i).split("\\s+")[1].trim();
			labelList.add(currentLabel);
			if (!uniqueLabels.contains(currentLabel))
				uniqueLabels.add( currentLabel );
		}
		AllPts = new ArrayList<djVtkPoint>();
		ClassPts = new ArrayList<List<djVtkPoint>>();
		
		pairFileList = DicccolUtilIO.loadFileToArrayList(DirInput+"/"+treeFileName);
	}
	
	public void generateDataPts(String DirInput,String fileOutputString)
	{
		
		AllPts.clear();
		for(int i=0;i<uniqueLabels.size();i++)
		{
			List<djVtkPoint> newPtList = new ArrayList<djVtkPoint>();
			ClassPts.add(newPtList);
		}
		
		for(int i=0;i<ptXYZList.size();i++)
		{
			int classID = uniqueLabels.indexOf( labelList.get(i) );
			int ptID = ClassPts.get(classID).size();
			String[] coord = ptXYZList.get(i).split("\\s+");
			djVtkPoint newPt = new djVtkPoint(ptID,Float.valueOf(coord[0].trim()),Float.valueOf(coord[1].trim()),Float.valueOf(coord[2].trim()));
			AllPts.add(newPt);
			ClassPts.get(classID).add(newPt);
		}
		
		for(int i=0;i<uniqueLabels.size();i++)
		{
			djVtkSurData NewPtVtk = new djVtkSurData();
			NewPtVtk.nPointNum = ClassPts.get(i).size();
			NewPtVtk.points.addAll(ClassPts.get(i));
			NewPtVtk.writePtsToVtkFile(DirInput+"/"+fileOutputString+"_"+uniqueLabels.get(i)+"_Pts.vtk");
		}
	}
	
	List<List<Integer>> initialSelectedPt()
	{
		List<List<Integer>> selectedClassPts = new ArrayList<List<Integer>>();
		selectedClassPts.add( Arrays.asList(1,2,3) );
		
		
		return selectedClassPts;
	}
	
	public void generaTestDataPts(String DirInput,String fileOutputString)
	{
		
		AllPts.clear();
		for(int i=0;i<uniqueLabels.size();i++)
		{
			List<djVtkPoint> newPtList = new ArrayList<djVtkPoint>();
			ClassPts.add(newPtList);
		}
		
		for(int i=0;i<ptXYZList.size();i++)
		{
			int classID = uniqueLabels.indexOf( labelList.get(i) );
			int ptID = ClassPts.get(classID).size();
			String[] coord = ptXYZList.get(i).split("\\s+");
			djVtkPoint newPt = new djVtkPoint(ptID,Float.valueOf(coord[0].trim()),Float.valueOf(coord[1].trim()),Float.valueOf(coord[2].trim()));
			AllPts.add(newPt);
			ClassPts.get(classID).add(newPt);
		}
		
		List<List<Integer>> selectedClassPts = new ArrayList<List<Integer>>();
		selectedClassPts.add( Arrays.asList(8,15,20,21,22) ); //NC
		selectedClassPts.add( Arrays.asList(1,2,3,7,8) ); //SMC
		selectedClassPts.add( Arrays.asList(1,2,8,11,14) ); //EMCI
		selectedClassPts.add( Arrays.asList(3,4,6,7,9) ); //LMCI
		
		for(int i=0;i<uniqueLabels.size();i++)
		{
			List<djVtkPoint> currentSelectPtList = new ArrayList<djVtkPoint>();
			int tmpPtID = 0;
			for(int j=0;j<ClassPts.get(i).size();j++)
				if(selectedClassPts.get(i).contains(j))
				{
					djVtkPoint currentPt = ClassPts.get(i).get(j);
					currentPt.pointId = tmpPtID++;
					currentSelectPtList.add(currentPt);
				}
			
			djVtkSurData NewPtVtk = new djVtkSurData();
			NewPtVtk.nPointNum = currentSelectPtList.size();
			NewPtVtk.points.addAll(currentSelectPtList);
			NewPtVtk.writePtsToVtkFile(DirInput+"/"+fileOutputString+"_"+uniqueLabels.get(i)+"_SelectedPts.vtk");
		}
	}

	public static void main(String[] args) throws IOException {
		fig mainHandler = new fig();
		
		String DirInput = "./Data/R01_Pet/";
		
//		String ptFileName = "train_3d.txt";
//		String treeFileName = "tree.txt";
//		String labelFileName = "train_meta.txt";
//		String fileOutputString = "NoPet";
		
//		String ptFileName = "train_3d_with_pet.txt";
//		String treeFileName = "tree_with_pet.txt";
//		String labelFileName = "train_meta_with_pet.txt";
//		String fileOutputString = "WithPet";
		
		String ptFileName = "test_3d_with_pet.txt";
		String labelFileName = "test_meta_with_pet.txt";
		String fileOutputString = "TestWithPet";
		
//		String ptFileName = "test_3d.txt";
//		String labelFileName = "test_meta.txt";
//		String fileOutputString = "TestNoPet";
		
		String treeFileName = "tree_with_pet.txt";
		mainHandler.loadData(DirInput, ptFileName, treeFileName, labelFileName);
//		mainHandler.generateDataPts(DirInput, fileOutputString);
//		mainHandler.generateLines(DirInput, fileOutputString);
		mainHandler.generaTestDataPts(DirInput, fileOutputString);
//		mainHandler.generateDataBall(DirInput, methodType, connType);


	}

}
