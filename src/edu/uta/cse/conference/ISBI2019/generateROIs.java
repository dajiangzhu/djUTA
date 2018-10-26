package edu.uta.cse.conference.ISBI2019;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;
import edu.uga.liulab.djVtkBase.djVtkCell;
import edu.uga.liulab.djVtkBase.djVtkDataDictionary;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class generateROIs {

	public String rootDir = "C:\\D_Drive\\Data\\ADNI";
	public String[][] labelDic = new String[68][4];
	public djVtkSurData surfaceDataL;
	public djVtkSurData surfaceDataR;
	public String hemi = "lh";
	public Map<String, List<djVtkPoint>> mapLabelPoints = new HashMap<String, List<djVtkPoint>>();
	public djVtkPoint[] labelCenterPoinArray = new djVtkPoint[68];
	
	public void loadSurface(String surfaceID)
	{
		String surfaceFileL = rootDir + "\\" + surfaceID +"\\Surf\\vtk\\lh.pial_transform.vtk";
		surfaceDataL = new djVtkSurData(surfaceFileL);
		String surfaceFileR = rootDir + "\\" + surfaceID +"\\Surf\\vtk\\rh.pial_transform.vtk";
		surfaceDataR = new djVtkSurData(surfaceFileR);
	}
	
	public void loadLabelDiction (String surfaceID)
	{
		String[][] tmpDic = DicccolUtilIO.loadFileAsStringArray(rootDir + "\\aparc.annot.ctab",34, 6);
		for(int i=0;i<34;i++)
		{
			labelDic[i][0] = "lh." + tmpDic[i][1].trim();
			labelDic[i][1] = tmpDic[i][2].trim();
			labelDic[i][2] = tmpDic[i][3].trim();
			labelDic[i][3] = tmpDic[i][4].trim();
		}
		for(int i=0;i<34;i++)
		{
			labelDic[i+34][0] = "rh." + tmpDic[i][1].trim();
			labelDic[i+34][1] = tmpDic[i][2].trim();
			labelDic[i+34][2] = tmpDic[i][3].trim();
			labelDic[i+34][3] = tmpDic[i][4].trim();
		}
		
		for(int i=0;i<68;i++)
			labelCenterPoinArray[i] = new djVtkPoint(0, 0.0f, 0.0f, 0.0f);
			
	}
	
	public void generatePatchByLabel(String surfaceID, int label)
	{
		if(label<34)
		{
			List<String> labelInfo = DicccolUtilIO.loadFileToArrayList(rootDir +"\\AllROILabels\\"+surfaceID+"\\"+labelDic[label][0]+".label");
			List<Integer> ptsInLable = new ArrayList<Integer>();
			for(int i=2;i<labelInfo.size();i++)
				ptsInLable.add( Integer.valueOf(labelInfo.get(i).trim().split("\\s+")[0]) );
			
			Set<djVtkCell> cellsForLabel = new HashSet<djVtkCell>();
			surfaceDataL.cellsOutput.clear();
			List<djVtkPoint> tmpPointList = new ArrayList<djVtkPoint>();
			for(int p=0;p<ptsInLable.size();p++)
			{
				djVtkPoint tmpPoint = surfaceDataL.getPoint( ptsInLable.get(p) );
				tmpPointList.add(tmpPoint);
				cellsForLabel.addAll( tmpPoint.cellsList );
			}
			mapLabelPoints.put(labelDic[label][0],tmpPointList);
			surfaceDataL.cellsOutput.addAll(cellsForLabel);
			djVtkSurData newSurface = (djVtkSurData)surfaceDataL.getCompactData();
			newSurface.cell_alias = djVtkDataDictionary.VTK_FIELDNAME_SURFACE_CELL;
			List<String> ptcolor = new ArrayList<String>();
			for(int i=0;i<newSurface.nPointNum;i++)
				ptcolor.add( Float.valueOf(labelDic[label][1].toString())/255.0+" "+Float.valueOf(labelDic[label][2].toString())/255.0+" "+Float.valueOf(labelDic[label][3].toString())/255.0);
			newSurface.pointsScalarData.put("color", ptcolor);
			newSurface.writeToVtkFileCompact("C:\\D_Drive\\2019ISBI\\FigData\\label."+(label+1)+".lh."+labelDic[label][0]+".pial_transform.compact.vtk");
		}
		else
		{
			List<String> labelInfo = DicccolUtilIO.loadFileToArrayList(rootDir +"\\AllROILabels\\"+surfaceID+"\\"+labelDic[label][0]+".label");
			List<Integer> ptsInLable = new ArrayList<Integer>();
			for(int i=2;i<labelInfo.size();i++)
				ptsInLable.add( Integer.valueOf(labelInfo.get(i).trim().split("\\s+")[0]) );
			
			Set<djVtkCell> cellsForLabel = new HashSet<djVtkCell>();
			surfaceDataR.cellsOutput.clear();
			List<djVtkPoint> tmpPointList = new ArrayList<djVtkPoint>();
			for(int p=0;p<ptsInLable.size();p++)
			{
				djVtkPoint tmpPoint = surfaceDataR.getPoint( ptsInLable.get(p) );
				tmpPointList.add(tmpPoint);
				cellsForLabel.addAll( surfaceDataR.getPoint( ptsInLable.get(p) ).cellsList );
			}
			mapLabelPoints.put(labelDic[label][0],tmpPointList);
			surfaceDataR.cellsOutput.addAll(cellsForLabel);
			djVtkSurData newSurface = (djVtkSurData)surfaceDataR.getCompactData();
			newSurface.cell_alias = djVtkDataDictionary.VTK_FIELDNAME_SURFACE_CELL;
			List<String> ptcolor = new ArrayList<String>();
			for(int i=0;i<newSurface.nPointNum;i++)
				ptcolor.add( Float.valueOf(labelDic[label][1].toString())/255.0+" "+Float.valueOf(labelDic[label][2].toString())/255.0+" "+Float.valueOf(labelDic[label][3].toString())/255.0);
			newSurface.pointsScalarData.put("color", ptcolor);
			newSurface.writeToVtkFileCompact("C:\\D_Drive\\2019ISBI\\FigData\\label."+(label+1)+".rh."+labelDic[label][0]+".pial_transform.compact.vtk");
		}		
	}
	
	public void generatePatchForAllLabels(String surfaceID)
	{
		for(int label=0;label<68;label++)
			this.generatePatchByLabel(surfaceID, label);
	}
	
	public void generateROIBubble(String surfaceID, int label, int size)
	{
		String templateVtkFile = "C:\\D_Drive\\2019ISBI\\FigData\\TemplateVtk\\template."+size+".vtk";
		djVtkSurData templateVtkData = new djVtkSurData(templateVtkFile);
		List<djVtkPoint> labelPointList = mapLabelPoints.get(labelDic[label][0]);
		List<String> attriList = new ArrayList<String>();
		attriList.add(Float.valueOf(labelDic[label][1].toString())/255.0+" "+Float.valueOf(labelDic[label][2].toString())/255.0+" "+Float.valueOf(labelDic[label][3].toString())/255.0);
		
		float centerX = 0.0f;
		float centerY = 0.0f;
		float centerZ = 0.0f;
		for(int i=0;i<labelPointList.size();i++)
		{
			centerX += labelPointList.get(i).x;
			centerY += labelPointList.get(i).y;
			centerZ += labelPointList.get(i).z;
		}
		centerX /= labelPointList.size();
		centerY /= labelPointList.size();
		centerZ /= labelPointList.size();
		djVtkSurData pointData = new djVtkSurData();
		djVtkPoint tmpPoint = new djVtkPoint(0, centerX, centerY, centerZ);
		pointData.points.add(tmpPoint);
		labelCenterPoinArray[label] = tmpPoint;
		pointData.nPointNum = 1;
		
		GenerateDICCCOL roiHandler = new GenerateDICCCOL();
		roiHandler.GenerateDICCCOLBallWithColor(pointData, templateVtkData, attriList, "C:\\D_Drive\\2019ISBI\\FigData\\label."+(label+1)+".rh."+labelDic[label][0]+".pial_transform.bubble.vtk");
		
	}
	
	public void generateSelectedROI(String surfaceID, String pairFile) throws IOException
	{
		List<String> pairInfo = DicccolUtilIO.loadFileToArrayList(pairFile);
		List<String> connLines = new ArrayList<String>();
		List<String> connLineLabelNames = new ArrayList<String>();
		int[] labelcount = new int[68];
		for(int i=0;i<pairInfo.size();i++)
		{
			String[] tmpLine = pairInfo.get(i).split("\\s+");
			int start = Integer.valueOf(tmpLine[0].trim())-1;
			int end = Integer.valueOf(tmpLine[1].trim())-1;
			connLineLabelNames.add( labelDic[start][0]+" - "+labelDic[end][0] );
			labelcount[start]++;
			labelcount[end]++;
			connLines.add("2 "+start+" "+end+ "\r\n");
		}
		DicccolUtilIO.writeArrayListToFile(connLineLabelNames, "C:\\D_Drive\\2019ISBI\\FigData\\"+Paths.get(pairFile).getFileName().toString()+"linkLabelNames.txt");
		
		for(int i=0;i<68;i++)
		{
			if(labelcount[i]>0)
			{
				System.out.println("Generate ROI: "+(i+1)+" "+labelDic[i][0]);
				this.generatePatchByLabel(surfaceID, i);
				this.generateROIBubble(surfaceID, i, labelcount[i]);
			} //if
		} //for 68 rois
		
		//generate links
		//Begin to write the vtk file
				FileWriter fw = null;
				fw = new FileWriter("C:\\D_Drive\\2019ISBI\\FigData\\"+Paths.get(pairFile).getFileName().toString()+"link.vtk");
				fw.write("# vtk DataFile Version 3.0\r\n");
				fw.write("vtk output\r\n");
				fw.write("ASCII\r\n");
				fw.write("DATASET POLYDATA\r\n");
				fw.write("POINTS " + labelCenterPoinArray.length + " float\r\n");
				for (int i = 0; i < labelCenterPoinArray.length; i++)
					fw.write(labelCenterPoinArray[i].x + " " + labelCenterPoinArray[i].y + " " + labelCenterPoinArray[i].z + "\r\n");
				fw.write("LINES " + connLines.size() + " " + (connLines.size() * 3) + " \r\n");
				for (int i = 0; i < connLines.size(); i++)
					fw.write(connLines.get(i));
				fw.close();
				System.out.println("Write file done!");
	}

	public static void main(String[] args) throws IOException {
		String subID = "002_S_0413";
		generateROIs mainHandler = new generateROIs();
		mainHandler.loadLabelDiction(subID);
		mainHandler.loadSurface(subID);
//		mainHandler.generatePatchForAllLabels(subID);
		mainHandler.generateSelectedROI(subID, "C:\\D_Drive\\2019ISBI\\feature-10-Node\\func_10_pair.txt");
		

	}

}
