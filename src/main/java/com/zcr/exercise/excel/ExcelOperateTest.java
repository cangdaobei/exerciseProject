package com.zcr.exercise.excel;


import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.StringUtil;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExcelOperateTest {

    public static void main(String[] args) throws IOException {
        try {
            String path = "C:\\Users\\仓刀北\\Desktop\\Logbook\\4_Engine_Information( Conditional)";
            String pathNew = "C:\\Users\\仓刀北\\Desktop\\Logbook-New\\4_Engine_Information( Conditional)";
            File files = new File(path);
            String[] lists = files.list();
            for (String list : lists) {
                System.out.println(list);
                File file = new File(path +"\\"+list);
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new HSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0);
                Row rowBegin = sheet.getRow(0);
                int cellBeginNum = rowBegin.getPhysicalNumberOfCells();
                int flightDateNum = 0;
                for (int i = 0; i < cellBeginNum; i++) {
                    if(rowBegin.getCell(i).getStringCellValue().equalsIgnoreCase("FLIGHT_DATE")){
                        flightDateNum = i;
                        break;
                    }
                }
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
                List<Long> newDateTimeList = new ArrayList<>();
                for (int i = 1; i <= physicalNumberOfRows; i++) {
                    Row row = sheet.getRow(i);
                    if(null == row){
                        break;
                    }
                    Cell cell = row.getCell(flightDateNum);
                    if(null == cell){
                        break;
                    }
                    switch (cell.getCellTypeEnum()){
                        case NUMERIC:
                            Date falightDate = cell.getDateCellValue();
                            newDateTimeList.add(falightDate.getTime());
                            continue;
                        case STRING:
                            String stringCellValue = cell.getStringCellValue();
                            if(!StringUtils.isEmpty(stringCellValue)){
                                System.out.println(stringCellValue);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                newDateTimeList.add(simpleDateFormat.parse(stringCellValue).getTime());
                            }
                            continue;
                        default:
                            break;
                    }
                }
                Collections.sort(newDateTimeList,Collections.reverseOrder());
                Date newDateTime = new Date(newDateTimeList.get(0));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String flightDateNew = simpleDateFormat.format(newDateTime);
                System.out.println(flightDateNew);
                fis.close();
                String[] fileName = list.split("xls_");
                String fileNameNew = fileName[0]+flightDateNew+".xls";
                File fileNew = new File(pathNew + "\\" + fileNameNew);
                file.renameTo(fileNew);
                FileOutputStream fos=new FileOutputStream(file);
                workbook.write(fos);
                fos.close();//关闭文件输出流
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
