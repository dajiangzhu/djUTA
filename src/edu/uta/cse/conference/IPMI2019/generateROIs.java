package edu.uta.cse.conference.IPMI2019;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtil;
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
	public 	List<String> labelSurCombineList = new ArrayList<String>();
	
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
	
	public void generatePatchByLabel(String surfaceID, int label, String connType, String outputDir, int numConsidered)
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
			newSurface.writeToVtkFileCompact(outputDir+"\\"+connType+"_"+numConsidered+"\\patch.label."+(label+1)+".lh."+labelDic[label][0]+".pial_transform.compact.vtk");
			labelSurCombineList.add(outputDir+"\\"+connType+"_"+numConsidered+"\\patch.label."+(label+1)+".lh."+labelDic[label][0]+".pial_transform.compact.vtk");
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
			newSurface.writeToVtkFileCompact(outputDir+"\\"+connType+"_"+numConsidered+"\\patch.label."+(label+1)+".rh."+labelDic[label][0]+".pial_transform.compact.vtk");
			labelSurCombineList.add(outputDir+"\\"+connType+"_"+numConsidered+"\\patch.label."+(label+1)+".rh."+labelDic[label][0]+".pial_transform.compact.vtk");
		}		
	}
	
	public void generateROIBubble(String surfaceID, int label, float size, String connType, String outputDir, int numConsidered)
	{
		String templateVtkFile = "C:\\D_Drive\\2019ISBI\\FigData\\TemplateVtk\\template.1.vtk";
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
		roiHandler.GenerateDICCCOLBallWithColor_ChangeSize(pointData, templateVtkData, attriList, size, outputDir+"\\"+connType+"_"+numConsidered+"\\bubble.label."+(label+1)+".rh."+labelDic[label][0]+".pial_transform.vtk");
		
	}
	
	public void generateSelectedROI(String surfaceID, String connType, String rootDir, List<String> pairInfo, int numConsidered) throws IOException
	{
		List<String> connLines = new ArrayList<String>();
		List<String> connLineLabelNames = new ArrayList<String>();
		float[] labelcount = new float[68];
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
		DicccolUtilIO.writeArrayListToFile(connLineLabelNames, rootDir+"\\"+connType+"_"+numConsidered+"\\linkLabelNames.txt");
		
		List<String> labelCountList = new ArrayList<String>();
		labelSurCombineList.clear();
		for(int i=0;i<68;i++)
		{
			if(labelcount[i]>0.0f)
			{
				System.out.println("Generate ROI: "+(i+1)+" "+labelDic[i][0]);
				labelCountList.add("["+(i+1)+"]: "+labelDic[i][0]+" - "+labelcount[i]);
				this.generatePatchByLabel(surfaceID, i, connType, rootDir, numConsidered);
//				this.generateROIBubble(surfaceID, i, 0.5f+5.0f*(labelcount[i]/numConsidered), connType, rootDir, numConsidered);
				this.generateROIBubble(surfaceID, i, 0.5f+(0.2f*numConsidered)*(labelcount[i]/numConsidered), connType, rootDir, numConsidered);
			} //if
		} //for 68 rois
		DicccolUtilIO.writeArrayListToFile(labelCountList, rootDir+"\\"+connType+"_"+numConsidered+"\\labelCount.txt");
		DicccolUtil.surfaceCombine(labelSurCombineList, rootDir+"\\"+connType+"_"+numConsidered+"\\labelCombine.vtk");
		
		//generate links
		//Begin to write the vtk file
				FileWriter fw = null;
				fw = new FileWriter( rootDir+"\\"+connType+"_"+numConsidered+"\\link.vtk");
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
	
	public void generateFigForPairConn(String surfaceID, String connType, String rootDir, String pairFile, int numConsidered) throws IOException
	{
		List<String> pairsFileList = DicccolUtilIO.loadFileToArrayList(rootDir+"\\lap_fs_"+connType+".txt");
		List<String> pairList = new ArrayList<String>();
		for(int i=0;i<numConsidered;i++)
		{
			String[] tmpStrucLine = pairsFileList.get(i).split("\\s+");
			pairList.add(tmpStrucLine[0].trim()+" "+tmpStrucLine[1].trim());
		}
		this.generateSelectedROI(surfaceID, connType, rootDir, pairList, numConsidered);
		
	}
	
	public void evaluatePairs()
	{
		String rootDir = "C:\\D_Drive\\2019IPMI\\MultiViewResult";
		List<String> strucPairs = DicccolUtilIO.loadFileToArrayList(rootDir+"\\multiview-white\\lap_fs_stru.txt");
		List<String> funcPairs = DicccolUtilIO.loadFileToArrayList(rootDir+"\\multiview-white\\lap_fs_func.txt");
		List<String> strucPairList = new ArrayList<String>();
		List<String> funcPairList = new ArrayList<String>();
		int numOfConsidered = 50;
		for(int i=0;i<numOfConsidered;i++)
		{
			String[] tmpStrucLine = strucPairs.get(i).split("\\s+");
			strucPairList.add(tmpStrucLine[0].trim()+"-"+tmpStrucLine[1].trim());
			String[] tmpFuncLine = funcPairs.get(i).split("\\s+");
			funcPairList.add(tmpFuncLine[0].trim()+"-"+tmpFuncLine[1].trim());
		}
		int count = 1;
		for(int i=0;i<numOfConsidered;i++)
			if(funcPairList.contains(strucPairList.get(i)))
			{
				System.out.println("["+count+"]: "+strucPairList.get(i));
				count++;
			}
	}

	public static void main(String[] args) throws IOException {
		String subID = "002_S_0413";
		String connType = "func";
		generateROIs mainHandler = new generateROIs();
		mainHandler.loadLabelDiction(subID);
		mainHandler.loadSurface(subID);
		mainHandler.generateFigForPairConn(subID, connType, "C:\\D_Drive\\2019IPMI\\MultiViewResult\\multiview-white", "lap_fs_"+connType+".txt", 20);
		
//		mainHandler.evaluatePairs();
		

	}

}
