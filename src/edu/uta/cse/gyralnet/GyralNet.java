package edu.uta.cse.gyralnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uga.liulab.djVtkBase.djVtkCell;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkUtil;

public class GyralNet {
	public djVtkFiberData gyralnetData = null;
	public Map<Integer, List<Integer>> adjMap = new HashMap<Integer, List<Integer>>();
	public Set<Integer> threeHGSet = new HashSet<Integer>();
	public List<List<Integer>> pathList = new ArrayList<List<Integer>>();
	public Map<Integer, Set<Integer>> threeHGCounterMap = new HashMap<Integer, Set<Integer>>();
	public float[] pathLength = null;
	public float totalPathLength = 0.0f;
	public float avePathLength = 0.0f;

	public void initialGyralNetData(String fileName) {
		gyralnetData = new djVtkFiberData(fileName);
		System.out.println("****** of Points in gyralnet: " + gyralnetData.nPointNum);
		System.out.println("****** of Cells in gyralnet: " + gyralnetData.nCellNum);

		for (int i = 0; i < gyralnetData.nCellNum; i++) {
			List<djVtkPoint> currentPtList = gyralnetData.getPointsOfCell(i);
			List<Integer> tmpNeighborsList;
			if (!adjMap.containsKey(currentPtList.get(0).pointId)) {
				tmpNeighborsList = new ArrayList<Integer>();
				adjMap.put(currentPtList.get(0).pointId, tmpNeighborsList);
			}
			adjMap.get(currentPtList.get(0).pointId).add(currentPtList.get(1).pointId);
			if (!adjMap.containsKey(currentPtList.get(1).pointId)) {
				tmpNeighborsList = new ArrayList<Integer>();
				adjMap.put(currentPtList.get(1).pointId, tmpNeighborsList);
			}
			adjMap.get(currentPtList.get(1).pointId).add(currentPtList.get(0).pointId);
//				System.out.println(currentPtList.get(0).pointId+"-"+currentPtList.get(1).pointId);
		} // for i

		Set<Integer> ptSet = adjMap.keySet();
		Iterator keyIter = ptSet.iterator();
		while (keyIter.hasNext()) {
			int ptID = (int) keyIter.next();
			if (adjMap.get(ptID).size() >= 3) {
				threeHGSet.add(ptID);
				threeHGCounterMap.put(ptID, new HashSet<Integer>());
			} // if
		} // while
		System.out.println("Total # of 3HG:" + threeHGSet.size() + " ------------------");
	} // initialGyralNetData

	public void generateAllPathsBetween3HGs() {
		Iterator keyIter = threeHGSet.iterator();
		int counter = 1;
		while (keyIter.hasNext()) {
			int current3HGPt = (int) keyIter.next();
			System.out.println(
					"Handling 3HG-" + (counter++) + ":" + current3HGPt);
			List<Integer> tmpNeighborsList = adjMap.get(current3HGPt);
			for (int i = 0; i < tmpNeighborsList.size(); i++)
				if (!threeHGCounterMap.get(current3HGPt).contains(tmpNeighborsList.get(i)))
					this.findPath(current3HGPt, tmpNeighborsList.get(i));
		} // while
		pathLength = new float[pathList.size()];
	} // generateAllPathsBetween3HGs

	private void findPath(int startPtID, int nextPtID) {
		threeHGCounterMap.get(startPtID).add(nextPtID);

		List<Integer> currentPath = new ArrayList<Integer>();
		Set<Integer> visitedPtSet = new HashSet<Integer>();

		currentPath.add(startPtID);
		visitedPtSet.add(startPtID);

		boolean isEnd = true;
		int currentPt = nextPtID;
		int previousPt = startPtID;
		while (isEnd) {
			currentPath.add(currentPt);
			visitedPtSet.add(currentPt);

			List<Integer> tmpNeighborsList = adjMap.get(currentPt);
			if (tmpNeighborsList.size() == 1) {
				this.pathList.add(currentPath);
				isEnd = false;
			} // ==1
			else if (tmpNeighborsList.size() == 2) {
				previousPt = currentPt;
				if (visitedPtSet.contains(tmpNeighborsList.get(0)))

					currentPt = tmpNeighborsList.get(1);
				else
					currentPt = tmpNeighborsList.get(0);
			} // ==2
			else if (tmpNeighborsList.size() >= 3) {
				this.pathList.add(currentPath);
				isEnd = false;
				threeHGCounterMap.get(currentPt).add(previousPt);
			} // ==3
		} // while
	}

	public void calculatePathLength() {
		for (int i = 0; i < this.pathList.size(); i++) {
			float currentPathLength = 0.0f;
			for (int j = 1; j < this.pathList.get(i).size(); j++)
				currentPathLength += djVtkUtil.calDistanceOfPoints(gyralnetData.getPoint(this.pathList.get(i).get(j - 1)),
						gyralnetData.getPoint(this.pathList.get(i).get(j)));
			this.pathLength[i] = currentPathLength;
			this.totalPathLength += currentPathLength;
		} //for
		this.avePathLength = this.totalPathLength/this.pathList.size();
	}
	
	public void saveColorGryalNet ()
	{
		for(int i=0;i<gyralnetData.nPointNum;i++)
			gyralnetData.getPoint(i).cellsList.clear();
		
		List<String> colorList = new ArrayList<String>();
		gyralnetData.nCellNum=this.pathList.size();
		for (int i = 0; i < this.pathList.size(); i++) 
		{
			djVtkCell newCell = new djVtkCell(i);
			for (int j = 0; j < this.pathList.get(i).size(); j++)
			{
				newCell.pointsList.add(gyralnetData.getPoint(this.pathList.get(i).get(j)));
				gyralnetData.getPoint(this.pathList.get(i).get(j)).cellsList.add(newCell);
			}
			gyralnetData.cellsOutput.add(newCell);
			colorList.add(String.valueOf(this.pathLength[i]));
		} //for
		gyralnetData.cellsScalarData.put("EdgeLength", colorList);
		
	}

}
