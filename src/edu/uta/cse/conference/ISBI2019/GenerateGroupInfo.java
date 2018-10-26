package edu.uta.cse.conference.ISBI2019;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class GenerateGroupInfo {

	public static void main(String[] args) throws BiffException, IOException {
		
		String groupName = "Patient";
		String subListFile = "C:\\D_Drive\\Data\\ADNI\\SubID.txt";
		String subGroupFile = "C:\\D_Drive\\Data\\ADNI\\ADNI_subject_group_map.xls";
		List<String> subList = DicccolUtilIO.loadFileToArrayList(subListFile);
		Set<String> subSet = new HashSet<String>();
		subSet.addAll(subList);
		List<String> outSubIDList = new ArrayList<String>();
		
		
		File excel_groupInfo = new File(subGroupFile);
		Workbook w_groupInfo = Workbook.getWorkbook(excel_groupInfo);
		Sheet sheet_groupInfo = w_groupInfo
				.getSheet("Sheet1");
		
		for(int r=0;r<474;r++)
		{
			String tmpSubID = sheet_groupInfo.getCell(0, r).getContents().trim();
			String tmpGroup = sheet_groupInfo.getCell(1, r).getContents().trim();
//			System.out.println((r+1)+": "+tmpSubID+" "+tmpGroup);
			if(tmpGroup.equals(groupName))
				if(subSet.contains(tmpSubID.trim()))
				{
					outSubIDList.add(tmpSubID);
					System.out.println((r+1)+": "+tmpSubID+" "+tmpGroup);
				}
		} //for r
		DicccolUtilIO.writeArrayListToFile(outSubIDList, "C:\\D_Drive\\Data\\ADNI\\"+groupName+"_List.txt");
	}

}
