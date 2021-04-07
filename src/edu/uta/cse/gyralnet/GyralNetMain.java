package edu.uta.cse.gyralnet;

public class GyralNetMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "/Users/dajiangzhu/Desktop/HDR/Data/SelectedDataForFig1/HCP/117122_new/lh_GyralNet_point_cell_smooth.vtk";
		GyralNet GyralNetMainHandler = new GyralNet();
		GyralNetMainHandler.initialGyralNetData(fileName);
		GyralNetMainHandler.generateAllPathsBetween3HGs();
		GyralNetMainHandler.calculatePathLength();
		System.out.println("****** pathList size: "+GyralNetMainHandler.pathList.size());
		for(int i=0;i<GyralNetMainHandler.pathList.size();i++)
			System.out.println("Path-"+i+": "+GyralNetMainHandler.pathList.get(i));
		System.out.println("****** Total PathLength: "+GyralNetMainHandler.totalPathLength);
		System.out.println("****** Ave PathLength: "+GyralNetMainHandler.avePathLength);
		GyralNetMainHandler.saveColorGryalNet();
		GyralNetMainHandler.gyralnetData.writeToVtkFile(fileName+".EdgeColor.vtk");
	}

}
