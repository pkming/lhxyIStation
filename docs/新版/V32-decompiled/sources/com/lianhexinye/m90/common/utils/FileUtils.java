package com.lianhexinye.m90.common.utils;

import android.content.Context;
import android.media.MediaPlayer;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.Constants;
import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel;
import com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModelDao;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModel;
import com.lianhexinye.m90.greendao.gen.BusLineInfoModelDao;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.BusLineModelDao;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModel;
import com.lianhexinye.m90.greendao.gen.ConfigInfoModelDao;
import com.lianhexinye.m90.greendao.gen.MaintenanceModel;
import com.lianhexinye.m90.greendao.gen.MaintenanceModelDao;
import com.lianhexinye.m90.greendao.gen.MessageModel;
import com.lianhexinye.m90.greendao.gen.MessageModelDao;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.greenrobot.greendao.query.WhereCondition;

/* JADX INFO: loaded from: classes2.dex */
public class FileUtils {
    /* JADX WARN: Removed duplicated region for block: B:66:0x00ab A[Catch: IOException -> 0x00a7, TryCatch #3 {IOException -> 0x00a7, blocks: (B:62:0x00a3, B:66:0x00ab, B:68:0x00b0), top: B:79:0x00a3 }] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x00b0 A[Catch: IOException -> 0x00a7, TRY_LEAVE, TryCatch #3 {IOException -> 0x00a7, blocks: (B:62:0x00a3, B:66:0x00ab, B:68:0x00b0), top: B:79:0x00a3 }] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x00a3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean copySDToSD(java.lang.String r8, java.lang.String r9) throws java.lang.Throwable {
        /*
            r0 = 0
            r1 = 0
            java.lang.String r2 = "/"
            int r2 = r9.lastIndexOf(r2)     // Catch: java.lang.Throwable -> L81 java.lang.Exception -> L84
            java.lang.String r2 = r9.substring(r1, r2)     // Catch: java.lang.Throwable -> L81 java.lang.Exception -> L84
            java.io.File r3 = new java.io.File     // Catch: java.lang.Throwable -> L81 java.lang.Exception -> L84
            r3.<init>(r2)     // Catch: java.lang.Throwable -> L81 java.lang.Exception -> L84
            boolean r2 = r3.exists()     // Catch: java.lang.Throwable -> L81 java.lang.Exception -> L84
            if (r2 != 0) goto L1a
            r3.mkdirs()     // Catch: java.lang.Throwable -> L81 java.lang.Exception -> L84
        L1a:
            r2 = 1
            java.io.File r3 = new java.io.File     // Catch: java.io.IOException -> L4a java.lang.Throwable -> L81 java.lang.Exception -> L84
            r3.<init>(r9)     // Catch: java.io.IOException -> L4a java.lang.Throwable -> L81 java.lang.Exception -> L84
            boolean r4 = r3.exists()     // Catch: java.io.IOException -> L4a java.lang.Throwable -> L81 java.lang.Exception -> L84
            if (r4 != 0) goto L2a
            r3.createNewFile()     // Catch: java.io.IOException -> L4a java.lang.Throwable -> L81 java.lang.Exception -> L84
            goto L4e
        L2a:
            java.io.File r4 = new java.io.File     // Catch: java.io.IOException -> L4a java.lang.Throwable -> L81 java.lang.Exception -> L84
            r4.<init>(r8)     // Catch: java.io.IOException -> L4a java.lang.Throwable -> L81 java.lang.Exception -> L84
            java.io.File r5 = new java.io.File     // Catch: java.io.IOException -> L4a java.lang.Throwable -> L81 java.lang.Exception -> L84
            r5.<init>(r9)     // Catch: java.io.IOException -> L4a java.lang.Throwable -> L81 java.lang.Exception -> L84
            long r6 = getFileSizes(r4)     // Catch: java.lang.Exception -> L45 java.io.IOException -> L4a java.lang.Throwable -> L81
            int r4 = (int) r6     // Catch: java.lang.Exception -> L45 java.io.IOException -> L4a java.lang.Throwable -> L81
            long r5 = getFileSizes(r5)     // Catch: java.lang.Exception -> L45 java.io.IOException -> L4a java.lang.Throwable -> L81
            int r5 = (int) r5     // Catch: java.lang.Exception -> L45 java.io.IOException -> L4a java.lang.Throwable -> L81
            if (r4 != r5) goto L41
            return r2
        L41:
            r3.createNewFile()     // Catch: java.lang.Exception -> L45 java.io.IOException -> L4a java.lang.Throwable -> L81
            goto L4e
        L45:
            r3 = move-exception
            r3.printStackTrace()     // Catch: java.io.IOException -> L4a java.lang.Throwable -> L81 java.lang.Exception -> L84
            goto L4e
        L4a:
            r3 = move-exception
            r3.printStackTrace()     // Catch: java.lang.Throwable -> L81 java.lang.Exception -> L84
        L4e:
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L81 java.lang.Exception -> L84
            r3.<init>(r9)     // Catch: java.lang.Throwable -> L81 java.lang.Exception -> L84
            java.io.FileInputStream r9 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L79 java.lang.Exception -> L7d
            r9.<init>(r8)     // Catch: java.lang.Throwable -> L79 java.lang.Exception -> L7d
            r8 = 1024(0x400, float:1.435E-42)
            byte[] r8 = new byte[r8]     // Catch: java.lang.Throwable -> L75 java.lang.Exception -> L77
        L5c:
            int r0 = r9.read(r8)     // Catch: java.lang.Throwable -> L75 java.lang.Exception -> L77
            if (r0 <= 0) goto L66
            r3.write(r8, r1, r0)     // Catch: java.lang.Throwable -> L75 java.lang.Exception -> L77
            goto L5c
        L66:
            r3.flush()     // Catch: java.io.IOException -> L70
            r9.close()     // Catch: java.io.IOException -> L70
            r3.close()     // Catch: java.io.IOException -> L70
            goto L74
        L70:
            r8 = move-exception
            r8.printStackTrace()
        L74:
            return r2
        L75:
            r8 = move-exception
            goto L7b
        L77:
            r8 = move-exception
            goto L7f
        L79:
            r8 = move-exception
            r9 = r0
        L7b:
            r0 = r3
            goto La1
        L7d:
            r8 = move-exception
            r9 = r0
        L7f:
            r0 = r3
            goto L86
        L81:
            r8 = move-exception
            r9 = r0
            goto La1
        L84:
            r8 = move-exception
            r9 = r0
        L86:
            r8.printStackTrace()     // Catch: java.lang.Throwable -> La0
            if (r0 == 0) goto L91
            r0.flush()     // Catch: java.io.IOException -> L8f
            goto L91
        L8f:
            r8 = move-exception
            goto L9c
        L91:
            if (r9 == 0) goto L96
            r9.close()     // Catch: java.io.IOException -> L8f
        L96:
            if (r0 == 0) goto L9f
            r0.close()     // Catch: java.io.IOException -> L8f
            goto L9f
        L9c:
            r8.printStackTrace()
        L9f:
            return r1
        La0:
            r8 = move-exception
        La1:
            if (r0 == 0) goto La9
            r0.flush()     // Catch: java.io.IOException -> La7
            goto La9
        La7:
            r9 = move-exception
            goto Lb4
        La9:
            if (r9 == 0) goto Lae
            r9.close()     // Catch: java.io.IOException -> La7
        Lae:
            if (r0 == 0) goto Lb7
            r0.close()     // Catch: java.io.IOException -> La7
            goto Lb7
        Lb4:
            r9.printStackTrace()
        Lb7:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.common.utils.FileUtils.copySDToSD(java.lang.String, java.lang.String):boolean");
    }

    public static long getFileSizes(File file) throws Exception {
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            long jAvailable = fileInputStream.available();
            fileInputStream.close();
            return jAvailable;
        }
        file.createNewFile();
        return 0L;
    }

    public static boolean delAllFile(String str) {
        File file;
        File file2 = new File(str);
        if (!file2.exists() || !file2.isDirectory()) {
            return false;
        }
        String[] list = file2.list();
        boolean z = false;
        for (int i = 0; i < list.length; i++) {
            if (str.endsWith(File.separator)) {
                file = new File(str + list[i]);
            } else {
                file = new File(str + File.separator + list[i]);
            }
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                delAllFile(str + "/" + list[i]);
                delFolder(str + "/" + list[i]);
                z = true;
            }
        }
        return z;
    }

    public static void delFolder(String str) {
        try {
            delAllFile(str);
            new File(str.toString()).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[][] getXLSData(File file, int i) throws IOException {
        int i2;
        String stringCellValue;
        ArrayList arrayList = new ArrayList();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook(new POIFSFileSystem(bufferedInputStream));
        int i3 = 0;
        int i4 = 0;
        while (true) {
            int i5 = 2;
            short s = 1;
            if (i3 >= hSSFWorkbook.getNumberOfSheets()) {
                break;
            }
            HSSFSheet sheetAt = hSSFWorkbook.getSheetAt(i3);
            int i6 = i;
            while (i6 <= sheetAt.getLastRowNum()) {
                HSSFRow row = sheetAt.getRow(i6);
                if (row == null) {
                    i2 = i6;
                } else {
                    int lastCellNum = row.getLastCellNum() + s;
                    if (lastCellNum > i4) {
                        i4 = lastCellNum;
                    }
                    String[] strArr = new String[i4];
                    Arrays.fill(strArr, "");
                    short s2 = 0;
                    boolean z = false;
                    while (true) {
                        if (s2 > row.getLastCellNum()) {
                            i2 = i6;
                            break;
                        }
                        HSSFCell cell = row.getCell(s2);
                        if (cell != null) {
                            cell.setEncoding(s);
                            int cellType = cell.getCellType();
                            if (cellType == 0) {
                                i2 = i6;
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date dateCellValue = cell.getDateCellValue();
                                    stringCellValue = dateCellValue != null ? new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(dateCellValue) : "";
                                } else {
                                    stringCellValue = new DecimalFormat("0").format(cell.getNumericCellValue());
                                }
                            } else if (cellType != s) {
                                if (cellType != i5) {
                                    if (cellType != 3 && cellType == 4) {
                                        stringCellValue = cell.getBooleanCellValue() == s ? "Y" : "N";
                                    }
                                    i2 = i6;
                                } else if (!cell.getStringCellValue().equals("")) {
                                    stringCellValue = cell.getStringCellValue();
                                } else {
                                    i2 = i6;
                                    stringCellValue = cell.getNumericCellValue() + "";
                                }
                                i2 = i6;
                            } else {
                                i2 = i6;
                                stringCellValue = cell.getStringCellValue();
                            }
                            if (s2 != 0 && stringCellValue.trim().equals("")) {
                                break;
                            }
                            strArr[s2] = rightTrim(stringCellValue);
                            s2 = (short) (s2 + 1);
                            i6 = i2;
                            i5 = 2;
                            s = 1;
                            z = true;
                        } else {
                            i2 = i6;
                            if (s2 != 0) {
                            }
                            strArr[s2] = rightTrim(stringCellValue);
                            s2 = (short) (s2 + 1);
                            i6 = i2;
                            i5 = 2;
                            s = 1;
                            z = true;
                        }
                    }
                    if (z) {
                        arrayList.add(strArr);
                    }
                }
                i6 = i2 + 1;
                i5 = 2;
                s = 1;
            }
            i3++;
        }
        bufferedInputStream.close();
        String[][] strArr2 = (String[][]) Array.newInstance((Class<?>) String.class, arrayList.size(), i4);
        for (int i7 = 0; i7 < strArr2.length; i7++) {
            strArr2[i7] = (String[]) arrayList.get(i7);
        }
        return strArr2;
    }

    public static void createXLSExcel(String str, String str2, String[] strArr) throws Throwable {
        FileOutputStream fileOutputStream;
        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                try {
                    HSSFRow hSSFRowCreateRow = hSSFWorkbook.createSheet(str2).createRow(0);
                    for (short s = 0; s < strArr.length; s = (short) (s + 1)) {
                        HSSFCell hSSFCellCreateCell = hSSFRowCreateRow.createCell(s);
                        hSSFCellCreateCell.setEncoding((short) 1);
                        hSSFCellCreateCell.setCellValue(new String(strArr[s].getBytes("utf-8"), "utf-8"));
                    }
                    fileOutputStream = new FileOutputStream(str);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } catch (FileNotFoundException e2) {
                e = e2;
            } catch (IOException e3) {
                e = e3;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            hSSFWorkbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e4) {
            e = e4;
            fileOutputStream2 = fileOutputStream;
            e.printStackTrace();
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
        } catch (IOException e5) {
            e = e5;
            fileOutputStream2 = fileOutputStream;
            e.printStackTrace();
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:126:0x022f A[Catch: IOException -> 0x022b, TRY_LEAVE, TryCatch #10 {IOException -> 0x022b, blocks: (B:122:0x0227, B:126:0x022f), top: B:134:0x0227 }] */
    /* JADX WARN: Removed duplicated region for block: B:134:0x0227 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:155:? A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void writeToXLSExcel(java.lang.String r17, java.lang.String r18, java.util.List<java.util.Map> r19) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 567
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.common.utils.FileUtils.writeToXLSExcel(java.lang.String, java.lang.String, java.util.List):void");
    }

    public static String rightTrim(String str) {
        if (str == null) {
            return "";
        }
        int length = str.length();
        for (int i = length - 1; i >= 0 && str.charAt(i) == ' '; i--) {
            length--;
        }
        return str.substring(0, length);
    }

    public static boolean deleteByPath(String str) {
        File file = new File(str);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    public static void copyArretsFileToSD(Context context, String[] strArr, String str) {
        if (!new File(str).exists()) {
            new File(str).mkdirs();
        }
        try {
            for (String strSubstring : strArr) {
                InputStream resourceAsStream = context.getClass().getClassLoader().getResourceAsStream("assets/" + strSubstring);
                if (-1 != strSubstring.lastIndexOf("/")) {
                    strSubstring = strSubstring.substring(strSubstring.lastIndexOf("/") + 1);
                }
                FileOutputStream fileOutputStream = new FileOutputStream(new File(str + "/" + strSubstring));
                byte[] bArr = new byte[1024];
                while (true) {
                    int i = resourceAsStream.read(bArr);
                    if (i == -1) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, i);
                    fileOutputStream.flush();
                }
                resourceAsStream.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readBusLineXls(String str, int i, String str2, String str3, StringBuffer stringBuffer) {
        BusLineModelDao busLineModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao();
        if (stringBuffer.toString().trim().equals("")) {
            busLineModelDao.deleteAll();
            stringBuffer.append("deleteAll");
        }
        try {
            String[][] xLSData = getXLSData(new File(str), 1);
            if (xLSData != null) {
                for (int i2 = 0; i2 < xLSData.length; i2++) {
                    BusLineModel busLineModel = new BusLineModel();
                    if (xLSData[i2] != null && xLSData[i2].length > 0) {
                        for (int i3 = 0; i3 < xLSData[i2].length; i3++) {
                            switch (i3) {
                                case 0:
                                    busLineModel.setBusNo(Integer.parseInt(xLSData[i2][i3].trim()));
                                    break;
                                case 1:
                                    busLineModel.setBusSound(xLSData[i2][i3].trim());
                                    break;
                                case 2:
                                    busLineModel.setBusName(xLSData[i2][i3].trim());
                                    break;
                                case 3:
                                    busLineModel.setLongitude(xLSData[i2][i3].trim());
                                    break;
                                case 4:
                                    busLineModel.setLatitude(xLSData[i2][i3].trim());
                                    break;
                                case 5:
                                    busLineModel.setAngle(xLSData[i2][i3].trim());
                                    break;
                                case 6:
                                    busLineModel.setSiteCode(xLSData[i2][i3].trim());
                                    break;
                                case 7:
                                    busLineModel.setStationAdvert(xLSData[i2][i3].trim());
                                    break;
                                case 8:
                                    busLineModel.setDepartureAdvert(xLSData[i2][i3].trim());
                                    break;
                                case 9:
                                    busLineModel.setStationPrompt(xLSData[i2][i3].trim());
                                    break;
                                case 10:
                                    busLineModel.setDeparturePrompt(xLSData[i2][i3].trim());
                                    break;
                                case 11:
                                    busLineModel.setStationExpansion(xLSData[i2][i3].trim());
                                    break;
                                case 12:
                                    busLineModel.setDepartureExpansion(xLSData[i2][i3].trim());
                                    break;
                                case 13:
                                    busLineModel.setSpeedLimit(xLSData[i2][i3].trim());
                                    break;
                                case 14:
                                    busLineModel.setSpeedLimitInStation(xLSData[i2][i3].trim());
                                    break;
                                case 15:
                                    busLineModel.setMileage(xLSData[i2][i3].trim());
                                    break;
                                case 16:
                                    busLineModel.setiMajorStation(xLSData[i2][i3].trim());
                                    break;
                                case 17:
                                    busLineModel.setVoiceNot(xLSData[i2][i3].trim());
                                    break;
                            }
                        }
                        if (i == 1) {
                            busLineModel.setDirectionName(str2 + "S");
                        } else {
                            busLineModel.setDirectionName(str2 + "X");
                        }
                        busLineModel.setDirection(i);
                        busLineModel.setBusLineName(str2);
                        busLineModel.setBusFilePath(str3);
                        busLineModelDao.insert(busLineModel);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
        } catch (IOException e2) {
            e2.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e2.getMessage());
        }
    }

    public static void readLineInfoXls(String str, String str2, String str3) {
        LogUtils.d(FileUtils.class, "readLineInfoCsv Start filePath:" + str + ",busFilePath" + str2);
        BusLineInfoModelDao busLineInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao();
        busLineInfoModelDao.deleteAll();
        try {
            String[][] xLSData = getXLSData(new File(str), 1);
            if (xLSData != null) {
                for (int i = 0; i < xLSData.length; i++) {
                    BusLineInfoModel busLineInfoModel = new BusLineInfoModel();
                    if (xLSData[i] != null && xLSData[i].length > 0) {
                        for (int i2 = 0; i2 < xLSData[i].length; i2++) {
                            if (i2 == 0) {
                                busLineInfoModel.setLineNo(Integer.parseInt(xLSData[i][i2].trim()));
                            } else if (i2 == 1) {
                                busLineInfoModel.setLineName(xLSData[i][i2].trim());
                            } else if (i2 != 2) {
                                if (i2 != 3) {
                                    if (i2 == 4) {
                                        busLineInfoModel.setLineNumber(xLSData[i][i2].trim());
                                    }
                                } else if (!xLSData[i][i2].trim().equals("")) {
                                    busLineInfoModel.setAttribute(Integer.parseInt(xLSData[i][i2].trim()));
                                } else {
                                    busLineInfoModel.setAttribute(0);
                                }
                            } else if (!xLSData[i][i2].trim().equals("")) {
                                busLineInfoModel.setStrSelect(xLSData[i][i2].trim());
                            } else {
                                busLineInfoModel.setStrSelect("");
                            }
                        }
                        busLineInfoModel.setFilePath(str2);
                        busLineInfoModel.setFileFormat(str3);
                        if ("是".equals(busLineInfoModel.getStrSelect())) {
                            busLineInfoModel.setISelect(true);
                        } else {
                            busLineInfoModel.setISelect(false);
                        }
                        if (busLineInfoModel.getAttribute() == 0) {
                            busLineInfoModel.setAttribute(1);
                        }
                        busLineInfoModelDao.insert(busLineInfoModel);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
        } catch (IOException e2) {
            e2.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e2.getMessage());
        }
    }

    public static void readConfigInfoXls(String str, String str2) {
        LogUtils.d(FileUtils.class, "readConfigInfoCsv Start filePath:" + str + ",busFilePath" + str2);
        ConfigInfoModelDao configInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getConfigInfoModelDao();
        try {
            String[][] xLSData = getXLSData(new File(str), 1);
            if (xLSData != null) {
                for (int i = 0; i < xLSData.length; i++) {
                    ConfigInfoModel configInfoModel = new ConfigInfoModel();
                    if (xLSData[i] != null && xLSData[i].length > 0) {
                        for (int i2 = 0; i2 < xLSData[i].length; i2++) {
                            if (i2 == 0) {
                                configInfoModel.setConfigItem(xLSData[i][i2].trim());
                            } else if (i2 == 1) {
                                configInfoModel.setConfigValue(xLSData[i][i2].trim());
                            } else if (i2 == 2) {
                                configInfoModel.setConfigExplain(xLSData[i][i2]);
                            }
                        }
                        configInfoModel.setFilePath(str2);
                        ConfigInfoModel configInfoModelUnique = configInfoModelDao.queryBuilder().where(ConfigInfoModelDao.Properties.ConfigItem.eq(configInfoModel.getConfigItem()), new WhereCondition[0]).unique();
                        if (configInfoModelUnique != null) {
                            if (configInfoModel.getConfigValue() != null && !configInfoModel.getConfigValue().trim().equals("")) {
                                configInfoModelUnique.setConfigValue(configInfoModel.getConfigValue());
                            }
                            configInfoModelUnique.setConfigExplain(configInfoModel.getConfigExplain());
                            configInfoModelUnique.setFilePath(configInfoModel.getFilePath());
                            if (configInfoModelUnique.getConfigItem().trim().equals("NetDispatchID")) {
                                SPUserInfoUtils.put(AppApplication.getContext(), "NetDispatchID", configInfoModelUnique.getConfigValue());
                            }
                            configInfoModelDao.update(configInfoModelUnique);
                        } else {
                            if (configInfoModel.getConfigValue() == null || configInfoModel.getConfigValue().trim().equals("")) {
                                if (configInfoModel.getConfigItem() != null && !configInfoModel.getConfigItem().trim().equals("")) {
                                    if (configInfoModel.getConfigItem().trim().equals("NetDispatchID")) {
                                        configInfoModel.setConfigValue("1");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchIP")) {
                                        configInfoModel.setConfigValue("211.154.159.34");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchPort")) {
                                        configInfoModel.setConfigValue("7000");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetDdispatchHeartbeatInterval")) {
                                        configInfoModel.setConfigValue("15");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchInfoInterval")) {
                                        configInfoModel.setConfigValue("5");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchSpeedingInterval")) {
                                        configInfoModel.setConfigValue("10");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertID")) {
                                        configInfoModel.setConfigValue("lhxy01");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertIP")) {
                                        configInfoModel.setConfigValue("39.104.66.194");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertPort")) {
                                        configInfoModel.setConfigValue("8081");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertInterfaceInterval")) {
                                        configInfoModel.setConfigValue("10");
                                    } else if (configInfoModel.getConfigItem().trim().equals("IfNetAdvert")) {
                                        configInfoModel.setConfigValue("否");
                                    } else if (configInfoModel.getConfigItem().trim().equals("IfSound")) {
                                        configInfoModel.setConfigValue("是");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertUser")) {
                                        configInfoModel.setConfigValue("admin");
                                    } else if (configInfoModel.getConfigItem().trim().equals("IfEnglish") || configInfoModel.getConfigItem().trim().equals("IfDialect") || configInfoModel.getConfigItem().trim().equals("IfIntegralPoint") || configInfoModel.getConfigItem().trim().equals("IfSpeed") || configInfoModel.getConfigItem().trim().equals("IfAngle")) {
                                        configInfoModel.setConfigValue("否");
                                    } else if (configInfoModel.getConfigItem().trim().equals("InnerVolume") || configInfoModel.getConfigItem().trim().equals("ExternalVolume")) {
                                        configInfoModel.setConfigValue("7");
                                    } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchProtocol") || configInfoModel.getConfigItem().trim().equals("RS232-1Protocol") || configInfoModel.getConfigItem().trim().equals("RS232-2Protocol") || configInfoModel.getConfigItem().trim().equals("RS485Protocol")) {
                                        configInfoModel.setConfigValue("无");
                                    } else if (configInfoModel.getConfigItem().trim().equals("RS232-1Baud") || configInfoModel.getConfigItem().trim().equals("RS232-2Baud") || configInfoModel.getConfigItem().trim().equals("RS485Baud")) {
                                        configInfoModel.setConfigValue("9600");
                                    } else if (configInfoModel.getConfigItem().trim().equals("IfTTSReportStation")) {
                                        configInfoModel.setConfigValue("否");
                                    } else if (configInfoModel.getConfigItem().trim().equals("TTSInnerVolume") || configInfoModel.getConfigItem().trim().equals("TTSExternalVolume")) {
                                        configInfoModel.setConfigValue("7");
                                    } else if (configInfoModel.getConfigItem().trim().equals("ShoutingVolume")) {
                                        configInfoModel.setConfigValue("50");
                                    } else if (configInfoModel.getConfigItem().trim().equals("DispatchVolume")) {
                                        configInfoModel.setConfigValue("7");
                                    } else if (configInfoModel.getConfigItem().trim().equals("LanguageSettings")) {
                                        configInfoModel.setConfigValue("0");
                                    } else if (configInfoModel.getConfigItem().trim().equals("IfConfigEffectiveImmediately")) {
                                        configInfoModel.setConfigValue("是");
                                    }
                                }
                            } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchID")) {
                                SPUserInfoUtils.put(AppApplication.getContext(), "NetDispatchID", configInfoModel.getConfigValue());
                            }
                            configInfoModelDao.insert(configInfoModel);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
        } catch (IOException e2) {
            e2.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e2.getMessage());
        }
    }

    public static void readBusLineFRXls(String str, int i, String str2, String str3, StringBuffer stringBuffer) {
        BusLineFriendRemindModelDao busLineFriendRemindModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineFriendRemindModelDao();
        if (stringBuffer.toString().trim().equals("")) {
            busLineFriendRemindModelDao.deleteAll();
            stringBuffer.append("deleteAll");
        }
        try {
            String[][] xLSData = getXLSData(new File(str), 1);
            if (xLSData != null) {
                for (int i2 = 0; i2 < xLSData.length; i2++) {
                    BusLineFriendRemindModel busLineFriendRemindModel = new BusLineFriendRemindModel();
                    if (xLSData[i2] != null && xLSData[i2].length > 0) {
                        for (int i3 = 0; i3 < xLSData[i2].length; i3++) {
                            switch (i3) {
                                case 0:
                                    busLineFriendRemindModel.setFrNo(Integer.parseInt(xLSData[i2][i3].trim()));
                                    break;
                                case 1:
                                    busLineFriendRemindModel.setFrVoice(xLSData[i2][i3].trim());
                                    break;
                                case 2:
                                    busLineFriendRemindModel.setLongitude(xLSData[i2][i3].trim());
                                    break;
                                case 3:
                                    busLineFriendRemindModel.setLatitude(xLSData[i2][i3].trim());
                                    break;
                                case 4:
                                    busLineFriendRemindModel.setMileage(xLSData[i2][i3].trim());
                                    break;
                                case 5:
                                    busLineFriendRemindModel.setCrossCode(xLSData[i2][i3].trim());
                                    break;
                                case 6:
                                    busLineFriendRemindModel.setCrossPrompt(xLSData[i2][i3].trim());
                                    break;
                                case 7:
                                    busLineFriendRemindModel.setCrossDeparturePrompt(xLSData[i2][i3].trim());
                                    break;
                                case 8:
                                    busLineFriendRemindModel.setCrossExpansion(xLSData[i2][i3].trim());
                                    break;
                                case 9:
                                    busLineFriendRemindModel.setCrossDepartureExpansion(xLSData[i2][i3].trim());
                                    break;
                                case 10:
                                    busLineFriendRemindModel.setCrossSpeedLimit(xLSData[i2][i3].trim());
                                    break;
                                case 11:
                                    busLineFriendRemindModel.setCrossType(xLSData[i2][i3].trim());
                                case 12:
                                    busLineFriendRemindModel.setVoiceNot(xLSData[i2][i3].trim());
                                    break;
                            }
                        }
                        busLineFriendRemindModel.setDirection(i);
                        busLineFriendRemindModel.setBusLineName(str2);
                        busLineFriendRemindModel.setFilePath(str3);
                        if (i == 1) {
                            busLineFriendRemindModel.setDirectionName(str2 + "S");
                        } else {
                            busLineFriendRemindModel.setDirectionName(str2 + "X");
                        }
                        busLineFriendRemindModelDao.insert(busLineFriendRemindModel);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
        } catch (IOException e2) {
            e2.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e2.getMessage());
        }
    }

    public static void readMaintenanceInfoXls(String str, String str2, String str3) {
        LogUtils.d(FileUtils.class, "readMaintenanceInfoXls Start filePath:" + str + ",busFilePath" + str2);
        MaintenanceModelDao maintenanceModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getMaintenanceModelDao();
        maintenanceModelDao.deleteAll();
        try {
            String[][] xLSData = getXLSData(new File(str), 1);
            if (xLSData != null) {
                for (int i = 0; i < xLSData.length; i++) {
                    MaintenanceModel maintenanceModel = new MaintenanceModel();
                    if (xLSData[i] != null && xLSData[i].length > 0) {
                        for (int i2 = 0; i2 < xLSData[i].length; i2++) {
                            if (i2 == 0) {
                                maintenanceModel.setMId(Integer.parseInt(xLSData[i][i2].trim()));
                            } else if (i2 == 1) {
                                maintenanceModel.setContent(xLSData[i][i2].trim());
                            } else if (i2 == 2) {
                                maintenanceModel.setRemarks(xLSData[i][i2].trim());
                            }
                        }
                        maintenanceModel.setFilePath(str2);
                        maintenanceModel.setFileFormat(str3);
                        maintenanceModelDao.insert(maintenanceModel);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static void readMessageInfoXls(String str, String str2, String str3) {
        LogUtils.d(FileUtils.class, "readMessageInfoXls Start filePath:" + str + ",busFilePath" + str2);
        MessageModelDao messageModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getMessageModelDao();
        messageModelDao.deleteAll();
        try {
            String[][] xLSData = getXLSData(new File(str), 1);
            if (xLSData != null) {
                for (int i = 0; i < xLSData.length; i++) {
                    MessageModel messageModel = new MessageModel();
                    if (xLSData[i] != null && xLSData[i].length > 0) {
                        for (int i2 = 0; i2 < xLSData[i].length; i2++) {
                            if (i2 == 0) {
                                messageModel.setMessageNo(Integer.parseInt(xLSData[i][i2].trim()));
                            } else if (i2 == 1) {
                                messageModel.setMessageTime(xLSData[i][i2].trim());
                            } else if (i2 == 2) {
                                messageModel.setMessageContent(xLSData[i][i2].trim());
                            }
                        }
                        messageModel.setFilePath(str2);
                        messageModel.setFileFormat(str3);
                        messageModelDao.insert(messageModel);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static void writeBusLineXls(List<BusLineModel> list, File file) throws Throwable {
        String[] strArr = {"Stop No.", "Play", "Stop name", "Longitude", "Latitude", "Angular", "UID", "Inbound Ad.", "Outbound Ad.", "Inbound hint", "Outbound hint", "Inbound Ex", "Outbound Ex", "InSpeed limit", "Speed limit", "Play point", "L/M stop", "Spkr/not"};
        String strSubstring = file.getPath().substring(file.getPath().lastIndexOf("/") + 1, file.getPath().lastIndexOf("."));
        createXLSExcel(file.getPath(), strSubstring, strArr);
        ArrayList arrayList = new ArrayList();
        for (BusLineModel busLineModel : list) {
            HashMap map = new HashMap();
            map.put(strArr[0], "" + busLineModel.getBusNo());
            map.put(strArr[1], busLineModel.getBusSound());
            map.put(strArr[2], busLineModel.getBusName());
            map.put(strArr[3], busLineModel.getLongitude());
            map.put(strArr[4], busLineModel.getLatitude());
            map.put(strArr[5], busLineModel.getAngle());
            map.put(strArr[6], busLineModel.getSiteCode());
            map.put(strArr[7], busLineModel.getStationAdvert());
            map.put(strArr[8], busLineModel.getDepartureAdvert());
            map.put(strArr[9], busLineModel.getStationPrompt());
            map.put(strArr[10], busLineModel.getDeparturePrompt());
            map.put(strArr[11], busLineModel.getStationExpansion());
            map.put(strArr[12], busLineModel.getDepartureExpansion());
            map.put(strArr[13], busLineModel.getSpeedLimit());
            map.put(strArr[14], busLineModel.getSpeedLimitInStation());
            map.put(strArr[15], busLineModel.getMileage());
            map.put(strArr[16], busLineModel.getIMajorStation());
            map.put(strArr[17], busLineModel.getVoiceNot());
            arrayList.add(map);
        }
        writeToXLSExcel(file.getPath(), strSubstring, arrayList);
    }

    public static void writeBusLineFRXls(List<BusLineFriendRemindModel> list, File file) throws Throwable {
        String[] strArr = {"Stop No.", "Cross", "Longitude", "Latitude", "Cross point", "UID", "CrossInNotice", "CrossOutNotice", "CrossInEx", "CrossOutEx", "SpeedLimit", "CrossType", "Spkr/not"};
        String strSubstring = file.getPath().substring(file.getPath().lastIndexOf("/") + 1, file.getPath().lastIndexOf("."));
        createXLSExcel(file.getPath(), strSubstring, strArr);
        ArrayList arrayList = new ArrayList();
        for (BusLineFriendRemindModel busLineFriendRemindModel : list) {
            HashMap map = new HashMap();
            map.put(strArr[0], "" + busLineFriendRemindModel.getFrNo());
            map.put(strArr[1], busLineFriendRemindModel.getFrVoice());
            map.put(strArr[2], busLineFriendRemindModel.getLongitude());
            map.put(strArr[3], busLineFriendRemindModel.getLatitude());
            map.put(strArr[4], busLineFriendRemindModel.getMileage());
            map.put(strArr[5], busLineFriendRemindModel.getCrossCode());
            map.put(strArr[6], busLineFriendRemindModel.getCrossPrompt());
            map.put(strArr[7], busLineFriendRemindModel.getCrossDeparturePrompt());
            map.put(strArr[8], busLineFriendRemindModel.getCrossExpansion());
            map.put(strArr[9], busLineFriendRemindModel.getCrossDepartureExpansion());
            map.put(strArr[10], busLineFriendRemindModel.getCrossSpeedLimit());
            map.put(strArr[11], busLineFriendRemindModel.getCrossType());
            map.put(strArr[12], busLineFriendRemindModel.getVoiceNot());
            arrayList.add(map);
        }
        writeToXLSExcel(file.getPath(), strSubstring, arrayList);
    }

    public static void writeLineInfoXls(List<BusLineInfoModel> list, File file) throws Throwable {
        String[] strArr = {"NO.", "Line name", "Current line", "Attribute(1:Normal 2:Circle 3:Back)", "Line serial"};
        String strSubstring = file.getPath().substring(file.getPath().lastIndexOf("/") + 1, file.getPath().lastIndexOf("."));
        createXLSExcel(file.getPath(), strSubstring, strArr);
        ArrayList arrayList = new ArrayList();
        for (BusLineInfoModel busLineInfoModel : list) {
            HashMap map = new HashMap();
            map.put(strArr[0], "" + busLineInfoModel.getLineNo());
            map.put(strArr[1], busLineInfoModel.getLineName());
            map.put(strArr[2], busLineInfoModel.getISelect() ? "Y" : "N");
            map.put(strArr[3], "" + busLineInfoModel.getAttribute());
            map.put(strArr[4], busLineInfoModel.getLineNumber());
            arrayList.add(map);
        }
        writeToXLSExcel(file.getPath(), strSubstring, arrayList);
    }

    public static void writeConfigInfoXls(List<ConfigInfoModel> list, File file) throws Throwable {
        String[] strArr = {"CONFIGKEY", "CONFIGVALUE", "ACCOUNT"};
        String strSubstring = file.getPath().substring(file.getPath().lastIndexOf("/") + 1, file.getPath().lastIndexOf("."));
        createXLSExcel(file.getPath(), strSubstring, strArr);
        ArrayList arrayList = new ArrayList();
        for (ConfigInfoModel configInfoModel : list) {
            HashMap map = new HashMap();
            map.put(strArr[0], configInfoModel.getConfigItem());
            map.put(strArr[1], configInfoModel.getConfigValue());
            map.put(strArr[2], configInfoModel.getConfigExplain());
            arrayList.add(map);
        }
        writeToXLSExcel(file.getPath(), strSubstring, arrayList);
    }

    public static void writeMaintenanceInfoXls(List<MaintenanceModel> list, File file) throws Throwable {
        String[] strArr = {"NO.", "Content", "Remarks"};
        LogUtils.d("writeMaintenanceInfoXls", "getPath:" + file.getPath());
        String strSubstring = file.getPath().substring(file.getPath().lastIndexOf("/") + 1, file.getPath().lastIndexOf("."));
        LogUtils.d("writeMaintenanceInfoXls", "exlName:" + strSubstring);
        createXLSExcel(file.getPath(), strSubstring, strArr);
        LogUtils.d("writeMaintenanceInfoXls", "createXLSExcel");
        ArrayList arrayList = new ArrayList();
        for (MaintenanceModel maintenanceModel : list) {
            HashMap map = new HashMap();
            map.put(strArr[0], "" + maintenanceModel.getMId());
            map.put(strArr[1], maintenanceModel.getContent());
            map.put(strArr[2], maintenanceModel.getRemarks());
            arrayList.add(map);
        }
        LogUtils.d("writeMaintenanceInfoXls", "listbusMaintenanceInfoModel");
        writeToXLSExcel(file.getPath(), strSubstring, arrayList);
        LogUtils.d("writeMaintenanceInfoXls", "exlName:" + strSubstring);
    }

    public static void writeMessageModelInfoXls(List<MessageModel> list, File file) throws Throwable {
        String[] strArr = {"NO.", "Date", "Content"};
        LogUtils.d("writeMessageModelInfoXls", "getPath:" + file.getPath());
        String strSubstring = file.getPath().substring(file.getPath().lastIndexOf("/") + 1, file.getPath().lastIndexOf("."));
        LogUtils.d("writeMessageModelInfoXls", "exlName:" + strSubstring);
        createXLSExcel(file.getPath(), strSubstring, strArr);
        LogUtils.d("writeMessageModelInfoXls", "createXLSExcel");
        ArrayList arrayList = new ArrayList();
        for (MessageModel messageModel : list) {
            HashMap map = new HashMap();
            map.put(strArr[0], "" + messageModel.getMessageNo());
            map.put(strArr[1], messageModel.getMessageTime());
            map.put(strArr[2], messageModel.getMessageContent());
            arrayList.add(map);
        }
        LogUtils.d("writeMessageModelInfoXls", "listbusMessageInfoModel");
        writeToXLSExcel(file.getPath(), strSubstring, arrayList);
        LogUtils.d("writeMessageModelInfoXls", "exlName:" + strSubstring);
    }

    public static boolean readBusLineCsv(String str, int i, String str2, String str3, StringBuffer stringBuffer) throws Throwable {
        Throwable th;
        BufferedReader bufferedReader;
        CSVParser cSVParser;
        BusLineModelDao busLineModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao();
        if (stringBuffer.toString().trim().equals("")) {
            busLineModelDao.deleteAll();
            stringBuffer.append("deleteAll");
        }
        CSVParser cSVParser2 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str), MediaPlayer.CHARSET_GBK));
                try {
                    cSVParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                } catch (FileNotFoundException e) {
                    e = e;
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (FileNotFoundException e3) {
            e = e3;
            bufferedReader = null;
        } catch (IOException e4) {
            e = e4;
            bufferedReader = null;
        } catch (Throwable th3) {
            th = th3;
            bufferedReader = null;
        }
        try {
            for (CSVRecord cSVRecord : cSVParser.getRecords()) {
                if (cSVRecord.get("Stop No.") != null && !"".equals(cSVRecord.get("Stop No.").trim())) {
                    BusLineModel busLineModel = new BusLineModel();
                    busLineModel.setBusNo(Integer.parseInt(cSVRecord.get("Stop No.").trim()));
                    busLineModel.setBusSound(cSVRecord.get("Play").trim());
                    busLineModel.setBusName(cSVRecord.get("Stop name").trim());
                    busLineModel.setLongitude(cSVRecord.get("Longitude").trim());
                    busLineModel.setLatitude(cSVRecord.get("Latitude").trim());
                    busLineModel.setAngle(cSVRecord.get("Angular").trim());
                    busLineModel.setSiteCode(cSVRecord.get("UID").trim());
                    busLineModel.setStationAdvert(cSVRecord.get("Inbound Ad.").trim());
                    busLineModel.setDepartureAdvert(cSVRecord.get("Outbound Ad.").trim());
                    busLineModel.setStationPrompt(cSVRecord.get("Inbound hint").trim());
                    busLineModel.setDeparturePrompt(cSVRecord.get("Outbound hint").trim());
                    busLineModel.setStationExpansion(cSVRecord.get("Inbound Ex").trim());
                    busLineModel.setDepartureExpansion(cSVRecord.get("Outbound Ex").trim());
                    busLineModel.setSpeedLimit(cSVRecord.get("InSpeed limit").trim());
                    busLineModel.setSpeedLimitInStation(cSVRecord.get("Speed limit").trim());
                    if (cSVRecord.get("Play point").trim().equals("")) {
                        try {
                            cSVParser.close();
                            bufferedReader.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                        return false;
                    }
                    busLineModel.setMileage(cSVRecord.get("Play point").trim());
                    busLineModel.setIMajorStation(cSVRecord.get("L/M stop").trim());
                    busLineModel.setVoiceNot(cSVRecord.get("Spkr/not").trim());
                    if (i == 1) {
                        busLineModel.setDirectionName(str2 + "S");
                    } else {
                        busLineModel.setDirectionName(str2 + "X");
                    }
                    busLineModel.setDirection(i);
                    busLineModel.setBusLineName(str2);
                    busLineModel.setBusFilePath(str3);
                    busLineModelDao.insert(busLineModel);
                }
            }
            try {
                cSVParser.close();
                bufferedReader.close();
            } catch (IOException e6) {
                e6.printStackTrace();
            }
            return true;
        } catch (FileNotFoundException e7) {
            e = e7;
            cSVParser2 = cSVParser;
            e.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
            if (cSVParser2 != null) {
                try {
                    cSVParser2.close();
                } catch (IOException e8) {
                    e8.printStackTrace();
                    return false;
                }
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            return false;
        } catch (IOException e9) {
            e = e9;
            cSVParser2 = cSVParser;
            e.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
            if (cSVParser2 != null) {
                try {
                    cSVParser2.close();
                } catch (IOException e10) {
                    e10.printStackTrace();
                    return false;
                }
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            return false;
        } catch (Throwable th4) {
            th = th4;
            cSVParser2 = cSVParser;
            if (cSVParser2 != null) {
                try {
                    cSVParser2.close();
                } catch (IOException e11) {
                    e11.printStackTrace();
                    throw th;
                }
            }
            if (bufferedReader == null) {
                throw th;
            }
            bufferedReader.close();
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v10, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r6v11 */
    /* JADX WARN: Type inference failed for: r6v12 */
    /* JADX WARN: Type inference failed for: r6v13 */
    /* JADX WARN: Type inference failed for: r6v14 */
    /* JADX WARN: Type inference failed for: r6v16, types: [java.util.Iterator] */
    /* JADX WARN: Type inference failed for: r6v17 */
    /* JADX WARN: Type inference failed for: r6v18 */
    /* JADX WARN: Type inference failed for: r6v19 */
    /* JADX WARN: Type inference failed for: r6v20 */
    /* JADX WARN: Type inference failed for: r6v21 */
    /* JADX WARN: Type inference failed for: r6v22 */
    /* JADX WARN: Type inference failed for: r6v6 */
    /* JADX WARN: Type inference failed for: r6v7 */
    /* JADX WARN: Type inference failed for: r6v8, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r6v9, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r7v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v10, types: [java.io.BufferedReader, java.io.Reader] */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v3, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r7v4 */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v6 */
    /* JADX WARN: Type inference failed for: r7v7, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r7v8, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r7v9 */
    public static void readLineInfoCsv(String str, String str2, String str3) throws Throwable {
        CSVParser cSVParser;
        ?? bufferedReader = ",busFilePath";
        LogUtils.d(FileUtils.class, "readLineInfoCsv Start filePath:" + str + ",busFilePath" + str2);
        BusLineInfoModelDao busLineInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineInfoModelDao();
        busLineInfoModelDao.deleteAll();
        ?? it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        try {
            try {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str), MediaPlayer.CHARSET_GBK));
                    try {
                        cSVParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                    } catch (FileNotFoundException e) {
                        e = e;
                    } catch (IOException e2) {
                        e = e2;
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            } catch (FileNotFoundException e4) {
                e = e4;
                bufferedReader = 0;
            } catch (IOException e5) {
                e = e5;
                bufferedReader = 0;
            } catch (Throwable th) {
                th = th;
                bufferedReader = 0;
            }
        } catch (Throwable th2) {
            th = th2;
        }
        try {
            it = cSVParser.getRecords().iterator();
            while (it.hasNext()) {
                CSVRecord cSVRecord = (CSVRecord) it.next();
                if (cSVRecord.get("NO.") != null && !"".equals(cSVRecord.get("NO.").trim())) {
                    BusLineInfoModel busLineInfoModel = new BusLineInfoModel();
                    busLineInfoModel.setLineNo(Integer.parseInt(cSVRecord.get("NO.").trim()));
                    busLineInfoModel.setLineName(cSVRecord.get("Line name").trim());
                    busLineInfoModel.setLineNumber(cSVRecord.get("Line serial").trim());
                    busLineInfoModel.setFilePath(str2);
                    busLineInfoModel.setFileFormat(str3);
                    if ("y".equals(cSVRecord.get("Current line").trim().toLowerCase())) {
                        busLineInfoModel.setISelect(true);
                    } else {
                        busLineInfoModel.setISelect(false);
                    }
                    if (cSVRecord.get("Attribute(1:Normal 2:Circle 3:Back)").trim().equals("")) {
                        busLineInfoModel.setAttribute(1);
                    } else {
                        busLineInfoModel.setAttribute(Integer.parseInt(cSVRecord.get("Attribute(1:Normal 2:Circle 3:Back)").trim()));
                    }
                    busLineInfoModelDao.insert(busLineInfoModel);
                }
            }
            cSVParser.close();
            bufferedReader.close();
        } catch (FileNotFoundException e6) {
            e = e6;
            it = cSVParser;
            e.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
            if (it != 0) {
                it.close();
            }
            if (bufferedReader != 0) {
                bufferedReader.close();
            }
        } catch (IOException e7) {
            e = e7;
            it = cSVParser;
            e.printStackTrace();
            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
            if (it != 0) {
                it.close();
            }
            if (bufferedReader != 0) {
                bufferedReader.close();
            }
        } catch (Throwable th3) {
            th = th3;
            it = cSVParser;
            if (it != 0) {
                try {
                    it.close();
                } catch (IOException e8) {
                    e8.printStackTrace();
                    throw th;
                }
            }
            if (bufferedReader != 0) {
                bufferedReader.close();
            }
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v10, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r5v11, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r5v12 */
    /* JADX WARN: Type inference failed for: r5v13 */
    /* JADX WARN: Type inference failed for: r5v14 */
    /* JADX WARN: Type inference failed for: r5v16, types: [java.util.Iterator] */
    /* JADX WARN: Type inference failed for: r5v17 */
    /* JADX WARN: Type inference failed for: r5v18 */
    /* JADX WARN: Type inference failed for: r5v19 */
    /* JADX WARN: Type inference failed for: r5v20 */
    /* JADX WARN: Type inference failed for: r5v21 */
    /* JADX WARN: Type inference failed for: r5v22 */
    /* JADX WARN: Type inference failed for: r5v6 */
    /* JADX WARN: Type inference failed for: r5v7 */
    /* JADX WARN: Type inference failed for: r5v8, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r5v9 */
    /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r6v10, types: [java.io.BufferedReader, java.io.Reader] */
    /* JADX WARN: Type inference failed for: r6v2 */
    /* JADX WARN: Type inference failed for: r6v3, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r6v4 */
    /* JADX WARN: Type inference failed for: r6v5 */
    /* JADX WARN: Type inference failed for: r6v6 */
    /* JADX WARN: Type inference failed for: r6v7 */
    /* JADX WARN: Type inference failed for: r6v8, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r6v9, types: [java.io.BufferedReader] */
    public static void readConfigInfoCsv(String str, String str2) throws Throwable {
        ?? bufferedReader = ",busFilePath";
        LogUtils.d(FileUtils.class, "readConfigInfoCsv Start filePath:" + str + ",busFilePath" + str2);
        ConfigInfoModelDao configInfoModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getConfigInfoModelDao();
        ?? it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        try {
            try {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str), MediaPlayer.CHARSET_GBK));
                    try {
                        CSVParser cSVParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                        try {
                            it = cSVParser.getRecords().iterator();
                            while (it.hasNext()) {
                                CSVRecord cSVRecord = (CSVRecord) it.next();
                                if (cSVRecord.get("CONFIGKEY") != null && !"".equals(cSVRecord.get("CONFIGKEY").trim())) {
                                    ConfigInfoModel configInfoModel = new ConfigInfoModel();
                                    configInfoModel.setConfigItem(cSVRecord.get("CONFIGKEY").trim());
                                    configInfoModel.setConfigValue(cSVRecord.get("CONFIGVALUE").trim());
                                    configInfoModel.setConfigExplain(cSVRecord.get("ACCOUNT"));
                                    configInfoModel.setFilePath(str2);
                                    ConfigInfoModel configInfoModelUnique = configInfoModelDao.queryBuilder().where(ConfigInfoModelDao.Properties.ConfigItem.eq(configInfoModel.getConfigItem()), new WhereCondition[0]).unique();
                                    if (configInfoModelUnique != null) {
                                        if (configInfoModel.getConfigValue() != null && !configInfoModel.getConfigValue().trim().equals("")) {
                                            configInfoModelUnique.setConfigValue(configInfoModel.getConfigValue());
                                        }
                                        configInfoModelUnique.setConfigExplain(configInfoModel.getConfigExplain());
                                        configInfoModelUnique.setFilePath(configInfoModel.getFilePath());
                                        if (configInfoModelUnique.getConfigItem().trim().equals("NetDispatchID")) {
                                            SPUserInfoUtils.put(AppApplication.getContext(), "NetDispatchID", configInfoModelUnique.getConfigValue());
                                        }
                                        configInfoModelDao.update(configInfoModelUnique);
                                    } else {
                                        if (configInfoModel.getConfigValue() == null || configInfoModel.getConfigValue().trim().equals("")) {
                                            if (configInfoModel.getConfigItem() != null && !configInfoModel.getConfigItem().trim().equals("")) {
                                                if (configInfoModel.getConfigItem().trim().equals("NetDispatchID")) {
                                                    configInfoModel.setConfigValue("1");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchIP")) {
                                                    configInfoModel.setConfigValue("39.104.66.194");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchPort")) {
                                                    configInfoModel.setConfigValue("7001");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetDdispatchHeartbeatInterval")) {
                                                    configInfoModel.setConfigValue("15");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchInfoInterval")) {
                                                    configInfoModel.setConfigValue("5");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchSpeedingInterval")) {
                                                    configInfoModel.setConfigValue("10");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertID")) {
                                                    configInfoModel.setConfigValue("lhxy01");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertIP")) {
                                                    configInfoModel.setConfigValue("39.104.66.194");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertPort")) {
                                                    configInfoModel.setConfigValue("8081");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertInterfaceInterval")) {
                                                    configInfoModel.setConfigValue("10");
                                                } else if (configInfoModel.getConfigItem().trim().equals("IfNetAdvert")) {
                                                    configInfoModel.setConfigValue("N");
                                                } else if (configInfoModel.getConfigItem().trim().equals("IfSound")) {
                                                    configInfoModel.setConfigValue("Y");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetAdvertUser")) {
                                                    configInfoModel.setConfigValue("admin");
                                                } else if (configInfoModel.getConfigItem().trim().equals("IfEnglish") || configInfoModel.getConfigItem().trim().equals("IfDialect") || configInfoModel.getConfigItem().trim().equals("IfIntegralPoint") || configInfoModel.getConfigItem().trim().equals("IfSpeed") || configInfoModel.getConfigItem().trim().equals("IfAngle")) {
                                                    configInfoModel.setConfigValue("N");
                                                } else if (configInfoModel.getConfigItem().trim().equals("InnerVolume") || configInfoModel.getConfigItem().trim().equals("ExternalVolume")) {
                                                    configInfoModel.setConfigValue("7");
                                                } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchProtocol") || configInfoModel.getConfigItem().trim().equals("RS232-1Protocol") || configInfoModel.getConfigItem().trim().equals("RS232-2Protocol") || configInfoModel.getConfigItem().trim().equals("RS485Protocol") || configInfoModel.getConfigItem().trim().equals("RS485-2Protocol")) {
                                                    configInfoModel.setConfigValue("N");
                                                } else if (configInfoModel.getConfigItem().trim().equals("RS232-1Baud") || configInfoModel.getConfigItem().trim().equals("RS232-2Baud") || configInfoModel.getConfigItem().trim().equals("RS485Baud") || configInfoModel.getConfigItem().trim().equals("RS485-2Baud")) {
                                                    configInfoModel.setConfigValue("9600");
                                                } else if (configInfoModel.getConfigItem().trim().equals("IfTTSReportStation")) {
                                                    configInfoModel.setConfigValue("N");
                                                } else if (configInfoModel.getConfigItem().trim().equals("TTSInnerVolume") || configInfoModel.getConfigItem().trim().equals("TTSExternalVolume")) {
                                                    configInfoModel.setConfigValue("7");
                                                } else if (configInfoModel.getConfigItem().trim().equals("ShoutingVolume")) {
                                                    configInfoModel.setConfigValue("50");
                                                } else if (configInfoModel.getConfigItem().trim().equals("DispatchVolume")) {
                                                    configInfoModel.setConfigValue("7");
                                                } else if (configInfoModel.getConfigItem().trim().equals("LanguageSettings")) {
                                                    configInfoModel.setConfigValue("0");
                                                } else if (configInfoModel.getConfigItem().trim().equals("IfConfigEffectiveImmediately")) {
                                                    configInfoModel.setConfigValue("Y");
                                                }
                                            }
                                        } else if (configInfoModel.getConfigItem().trim().equals("NetDispatchID")) {
                                            SPUserInfoUtils.put(AppApplication.getContext(), "NetDispatchID", configInfoModel.getConfigValue());
                                        }
                                        configInfoModelDao.insert(configInfoModel);
                                    }
                                }
                            }
                            cSVParser.close();
                            bufferedReader.close();
                        } catch (FileNotFoundException e) {
                            e = e;
                            it = cSVParser;
                            e.printStackTrace();
                            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
                            if (it != 0) {
                                it.close();
                            }
                            if (bufferedReader != 0) {
                                bufferedReader.close();
                            }
                        } catch (IOException e2) {
                            e = e2;
                            it = cSVParser;
                            e.printStackTrace();
                            LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
                            if (it != 0) {
                                it.close();
                            }
                            if (bufferedReader != 0) {
                                bufferedReader.close();
                            }
                        } catch (Throwable th) {
                            th = th;
                            it = cSVParser;
                            if (it != 0) {
                                try {
                                    it.close();
                                } catch (IOException e3) {
                                    e3.printStackTrace();
                                    throw th;
                                }
                            }
                            if (bufferedReader != 0) {
                                bufferedReader.close();
                            }
                            throw th;
                        }
                    } catch (FileNotFoundException e4) {
                        e = e4;
                    } catch (IOException e5) {
                        e = e5;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (FileNotFoundException e6) {
                e = e6;
                bufferedReader = 0;
            } catch (IOException e7) {
                e = e7;
                bufferedReader = 0;
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = 0;
            }
        } catch (IOException e8) {
            e8.printStackTrace();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v1 */
    /* JADX WARN: Type inference failed for: r13v11, types: [java.util.Iterator] */
    /* JADX WARN: Type inference failed for: r13v12 */
    /* JADX WARN: Type inference failed for: r13v13 */
    /* JADX WARN: Type inference failed for: r13v14 */
    /* JADX WARN: Type inference failed for: r13v15 */
    /* JADX WARN: Type inference failed for: r13v16 */
    /* JADX WARN: Type inference failed for: r13v17 */
    /* JADX WARN: Type inference failed for: r13v2 */
    /* JADX WARN: Type inference failed for: r13v3, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r13v4 */
    /* JADX WARN: Type inference failed for: r13v5, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r13v6, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r13v7 */
    /* JADX WARN: Type inference failed for: r13v8 */
    /* JADX WARN: Type inference failed for: r13v9 */
    /* JADX WARN: Type inference failed for: r4v10, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v11, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v12, types: [java.io.BufferedReader, java.io.Reader] */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v15 */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v5, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v8 */
    /* JADX WARN: Type inference failed for: r4v9 */
    public static void readBusLineFRCsv(String str, int i, String str2, String str3, StringBuffer stringBuffer) throws Throwable {
        CSVParser cSVParser;
        BusLineFriendRemindModelDao busLineFriendRemindModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineFriendRemindModelDao();
        boolean zEquals = stringBuffer.toString().trim().equals("");
        ?? bufferedReader = zEquals;
        if (zEquals) {
            busLineFriendRemindModelDao.deleteAll();
            stringBuffer.append("deleteAll");
            bufferedReader = "deleteAll";
        }
        ?? it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        try {
            try {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str), MediaPlayer.CHARSET_GBK));
                    try {
                        cSVParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                    } catch (FileNotFoundException e) {
                        e = e;
                    } catch (IOException e2) {
                        e = e2;
                    }
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    it = cSVParser.getRecords().iterator();
                    while (it.hasNext()) {
                        CSVRecord cSVRecord = (CSVRecord) it.next();
                        if (cSVRecord.get("Stop No.") != null && !"".equals(cSVRecord.get("Stop No.").trim())) {
                            BusLineFriendRemindModel busLineFriendRemindModel = new BusLineFriendRemindModel();
                            busLineFriendRemindModel.setFrNo(Integer.parseInt(cSVRecord.get("Stop No.").trim()));
                            busLineFriendRemindModel.setFrVoice(cSVRecord.get("Cross").trim());
                            busLineFriendRemindModel.setLongitude(cSVRecord.get("Longitude").trim());
                            busLineFriendRemindModel.setLatitude(cSVRecord.get("Latitude").trim());
                            busLineFriendRemindModel.setMileage(cSVRecord.get("Cross point").trim());
                            busLineFriendRemindModel.setCrossCode(cSVRecord.get("UID").trim());
                            busLineFriendRemindModel.setCrossPrompt(cSVRecord.get("CrossInNotice").trim());
                            busLineFriendRemindModel.setCrossDeparturePrompt(cSVRecord.get("CrossOutNotice").trim());
                            busLineFriendRemindModel.setCrossExpansion(cSVRecord.get("CrossInEx").trim());
                            busLineFriendRemindModel.setCrossDepartureExpansion(cSVRecord.get("CrossOutEx").trim());
                            busLineFriendRemindModel.setCrossSpeedLimit(cSVRecord.get("SpeedLimit").trim());
                            busLineFriendRemindModel.setCrossType(cSVRecord.get("CrossType").trim());
                            busLineFriendRemindModel.setVoiceNot(cSVRecord.get("Spkr/not").trim());
                            busLineFriendRemindModel.setDirection(i);
                            busLineFriendRemindModel.setBusLineName(str2);
                            busLineFriendRemindModel.setFilePath(str3);
                            if (i == 1) {
                                busLineFriendRemindModel.setDirectionName(str2 + "S");
                            } else {
                                busLineFriendRemindModel.setDirectionName(str2 + "X");
                            }
                            busLineFriendRemindModelDao.insert(busLineFriendRemindModel);
                        }
                    }
                    cSVParser.close();
                    bufferedReader.close();
                } catch (FileNotFoundException e3) {
                    e = e3;
                    it = cSVParser;
                    e.printStackTrace();
                    LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
                    if (it != 0) {
                        it.close();
                    }
                    if (bufferedReader != 0) {
                        bufferedReader.close();
                    }
                } catch (IOException e4) {
                    e = e4;
                    it = cSVParser;
                    e.printStackTrace();
                    LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
                    if (it != 0) {
                        it.close();
                    }
                    if (bufferedReader != 0) {
                        bufferedReader.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    it = cSVParser;
                    if (it != 0) {
                        try {
                            it.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                            throw th;
                        }
                    }
                    if (bufferedReader != 0) {
                        bufferedReader.close();
                    }
                    throw th;
                }
            } catch (FileNotFoundException e6) {
                e = e6;
                bufferedReader = 0;
            } catch (IOException e7) {
                e = e7;
                bufferedReader = 0;
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = 0;
            }
        } catch (IOException e8) {
            e8.printStackTrace();
        }
    }

    public static boolean readPriceCsv(String str, List<BusLineModel> list) throws Throwable {
        BufferedReader bufferedReader;
        CSVParser cSVParser;
        BusLineModelDao busLineModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusLineModelDao();
        CSVParser cSVParser2 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str), MediaPlayer.CHARSET_GBK));
                try {
                    cSVParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                } catch (FileNotFoundException e) {
                    e = e;
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                for (CSVRecord cSVRecord : cSVParser.getRecords()) {
                    if (cSVRecord.get("序号") != null && !"".equals(cSVRecord.get("序号").trim())) {
                        for (BusLineModel busLineModel : list) {
                            if (JavaUtils.isEmpty(busLineModel.getBusPrice())) {
                                busLineModel.setBusPrice(cSVRecord.get(busLineModel.getBusSound()).trim());
                            } else {
                                busLineModel.setBusPrice(busLineModel.getBusPrice() + "," + cSVRecord.get(busLineModel.getBusSound()).trim());
                            }
                        }
                    }
                }
                busLineModelDao.updateInTx(list);
                try {
                    cSVParser.close();
                    bufferedReader.close();
                    return true;
                } catch (IOException e3) {
                    e3.printStackTrace();
                    return true;
                }
            } catch (FileNotFoundException e4) {
                e = e4;
                cSVParser2 = cSVParser;
                e.printStackTrace();
                LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
                if (cSVParser2 != null) {
                    try {
                        cSVParser2.close();
                    } catch (IOException e5) {
                        e5.printStackTrace();
                        return false;
                    }
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                return false;
            } catch (IOException e6) {
                e = e6;
                cSVParser2 = cSVParser;
                e.printStackTrace();
                LogUtils.d("导入资源", "入库失败，失败原因：" + e.getMessage());
                if (cSVParser2 != null) {
                    try {
                        cSVParser2.close();
                    } catch (IOException e7) {
                        e7.printStackTrace();
                        return false;
                    }
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                return false;
            } catch (Throwable th2) {
                th = th2;
                cSVParser2 = cSVParser;
                if (cSVParser2 != null) {
                    try {
                        cSVParser2.close();
                    } catch (IOException e8) {
                        e8.printStackTrace();
                        throw th;
                    }
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e9) {
            e = e9;
            bufferedReader = null;
        } catch (IOException e10) {
            e = e10;
            bufferedReader = null;
        } catch (Throwable th3) {
            th = th3;
            bufferedReader = null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v10, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r2v11, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v13 */
    /* JADX WARN: Type inference failed for: r2v14 */
    /* JADX WARN: Type inference failed for: r2v16, types: [java.util.Iterator] */
    /* JADX WARN: Type inference failed for: r2v17 */
    /* JADX WARN: Type inference failed for: r2v18 */
    /* JADX WARN: Type inference failed for: r2v19 */
    /* JADX WARN: Type inference failed for: r2v20 */
    /* JADX WARN: Type inference failed for: r2v21 */
    /* JADX WARN: Type inference failed for: r2v22 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v8, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* JADX WARN: Type inference failed for: r3v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r3v10, types: [java.io.BufferedReader, java.io.Reader] */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r3v4 */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARN: Type inference failed for: r3v6 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r3v9, types: [java.io.BufferedReader] */
    public static void readMaintenanceInfoCsv(String str, String str2, String str3) throws Throwable {
        CSVParser cSVParser;
        ?? bufferedReader = ",busFilePath";
        LogUtils.d(FileUtils.class, "readMaintenanceInfoCsv Start filePath:" + str + ",busFilePath" + str2);
        MaintenanceModelDao maintenanceModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getMaintenanceModelDao();
        maintenanceModelDao.deleteAll();
        ?? it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        try {
            try {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str), MediaPlayer.CHARSET_GBK));
                    try {
                        cSVParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                    } catch (FileNotFoundException e) {
                        e = e;
                    } catch (IOException e2) {
                        e = e2;
                    }
                } catch (Throwable th) {
                    th = th;
                }
            } catch (FileNotFoundException e3) {
                e = e3;
                bufferedReader = 0;
            } catch (IOException e4) {
                e = e4;
                bufferedReader = 0;
            } catch (Throwable th2) {
                th = th2;
                bufferedReader = 0;
            }
            try {
                it = cSVParser.getRecords().iterator();
                while (it.hasNext()) {
                    CSVRecord cSVRecord = (CSVRecord) it.next();
                    if (cSVRecord.get("NO.") != null && !"".equals(cSVRecord.get("NO.").trim())) {
                        MaintenanceModel maintenanceModel = new MaintenanceModel();
                        maintenanceModel.setMId(Integer.parseInt(cSVRecord.get("NO.").trim()));
                        maintenanceModel.setContent(cSVRecord.get("Content").trim());
                        maintenanceModel.setRemarks(cSVRecord.get("Remarks").trim());
                        maintenanceModel.setFilePath(str2);
                        maintenanceModel.setFileFormat(str3);
                        maintenanceModelDao.insert(maintenanceModel);
                    }
                }
                cSVParser.close();
                bufferedReader.close();
            } catch (FileNotFoundException e5) {
                e = e5;
                it = cSVParser;
                e.printStackTrace();
                if (it != 0) {
                    it.close();
                }
                if (bufferedReader != 0) {
                    bufferedReader.close();
                }
            } catch (IOException e6) {
                e = e6;
                it = cSVParser;
                e.printStackTrace();
                if (it != 0) {
                    it.close();
                }
                if (bufferedReader != 0) {
                    bufferedReader.close();
                }
            } catch (Throwable th3) {
                th = th3;
                it = cSVParser;
                if (it != 0) {
                    try {
                        it.close();
                    } catch (IOException e7) {
                        e7.printStackTrace();
                        throw th;
                    }
                }
                if (bufferedReader != 0) {
                    bufferedReader.close();
                }
                throw th;
            }
        } catch (IOException e8) {
            e8.printStackTrace();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v10, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r3v11, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r3v12 */
    /* JADX WARN: Type inference failed for: r3v13 */
    /* JADX WARN: Type inference failed for: r3v14 */
    /* JADX WARN: Type inference failed for: r3v16, types: [java.util.Iterator] */
    /* JADX WARN: Type inference failed for: r3v17 */
    /* JADX WARN: Type inference failed for: r3v18 */
    /* JADX WARN: Type inference failed for: r3v19 */
    /* JADX WARN: Type inference failed for: r3v20 */
    /* JADX WARN: Type inference failed for: r3v21 */
    /* JADX WARN: Type inference failed for: r3v22 */
    /* JADX WARN: Type inference failed for: r3v6 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8, types: [org.apache.commons.csv.CSVParser] */
    /* JADX WARN: Type inference failed for: r3v9 */
    /* JADX WARN: Type inference failed for: r4v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v10, types: [java.io.BufferedReader, java.io.Reader] */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v8, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v9, types: [java.io.BufferedReader] */
    public static void readMessageInfoCsv(String str, String str2, String str3) throws Throwable {
        CSVParser cSVParser;
        ?? bufferedReader = ",busFilePath";
        LogUtils.d(FileUtils.class, "readMessageInfoCsv Start filePath:" + str + ",busFilePath" + str2);
        MessageModelDao messageModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getMessageModelDao();
        ?? it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        it = 0;
        try {
            try {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str), MediaPlayer.CHARSET_GBK));
                    try {
                        cSVParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                    } catch (FileNotFoundException e) {
                        e = e;
                    } catch (IOException e2) {
                        e = e2;
                    }
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    it = cSVParser.getRecords().iterator();
                    while (it.hasNext()) {
                        CSVRecord cSVRecord = (CSVRecord) it.next();
                        if (cSVRecord.get("NO.") != null && !"".equals(cSVRecord.get("NO.").trim())) {
                            MessageModel messageModel = new MessageModel();
                            messageModel.setMessageNo(Integer.parseInt(cSVRecord.get("NO.").trim()));
                            messageModel.setMessageTime(cSVRecord.get("Date").trim());
                            messageModel.setMessageContent(cSVRecord.get("Content").trim());
                            messageModel.setFilePath(str2);
                            messageModel.setFileFormat(str3);
                            MessageModel messageModelUnique = messageModelDao.queryBuilder().where(MessageModelDao.Properties.MessageNo.eq(Integer.valueOf(messageModel.getMessageNo())), new WhereCondition[0]).unique();
                            if (messageModelUnique != null) {
                                if (messageModel.getMessageTime() != null && !messageModel.getMessageTime().trim().equals("") && messageModel.getMessageContent() != null && !messageModel.getMessageContent().trim().equals("")) {
                                    messageModelUnique.setMessageTime(messageModel.getMessageTime());
                                    messageModelUnique.setMessageContent(messageModel.getMessageContent());
                                }
                                messageModelUnique.setFilePath(str2);
                                messageModelUnique.setFileFormat(str3);
                                messageModelDao.update(messageModelUnique);
                            }
                        }
                    }
                    cSVParser.close();
                    bufferedReader.close();
                } catch (FileNotFoundException e3) {
                    e = e3;
                    it = cSVParser;
                    e.printStackTrace();
                    if (it != 0) {
                        it.close();
                    }
                    if (bufferedReader != 0) {
                        bufferedReader.close();
                    }
                } catch (IOException e4) {
                    e = e4;
                    it = cSVParser;
                    e.printStackTrace();
                    if (it != 0) {
                        it.close();
                    }
                    if (bufferedReader != 0) {
                        bufferedReader.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    it = cSVParser;
                    if (it != 0) {
                        try {
                            it.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                            throw th;
                        }
                    }
                    if (bufferedReader != 0) {
                        bufferedReader.close();
                    }
                    throw th;
                }
            } catch (FileNotFoundException e6) {
                e = e6;
                bufferedReader = 0;
            } catch (IOException e7) {
                e = e7;
                bufferedReader = 0;
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = 0;
            }
        } catch (IOException e8) {
            e8.printStackTrace();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0139 A[Catch: IOException -> 0x0135, TRY_LEAVE, TryCatch #1 {IOException -> 0x0135, blocks: (B:35:0x0131, B:39:0x0139), top: B:43:0x0131 }] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0131 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:56:? A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void writeBusLineCsv(java.util.List<com.lianhexinye.m90.greendao.gen.BusLineModel> r22, java.io.File r23) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 321
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.common.utils.FileUtils.writeBusLineCsv(java.util.List, java.io.File):void");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0107 A[Catch: IOException -> 0x0103, TRY_LEAVE, TryCatch #1 {IOException -> 0x0103, blocks: (B:35:0x00ff, B:39:0x0107), top: B:43:0x00ff }] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00ff A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:55:? A[SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v12 */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* JADX WARN: Type inference failed for: r1v14 */
    /* JADX WARN: Type inference failed for: r1v15 */
    /* JADX WARN: Type inference failed for: r1v16 */
    /* JADX WARN: Type inference failed for: r1v2, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7 */
    /* JADX WARN: Type inference failed for: r1v8, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void writeBusLineFRCsv(java.util.List<com.lianhexinye.m90.greendao.gen.BusLineFriendRemindModel> r17, java.io.File r18) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 271
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.common.utils.FileUtils.writeBusLineFRCsv(java.util.List, java.io.File):void");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v12 */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* JADX WARN: Type inference failed for: r1v14 */
    /* JADX WARN: Type inference failed for: r1v2, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7, types: [boolean] */
    public static void writeLineInfoCsv(List<BusLineInfoModel> list, File file) throws Throwable {
        BufferedWriter bufferedWriter;
        CSVPrinter cSVPrinter;
        Iterator<BusLineInfoModel> it;
        ?? HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        try {
            try {
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), MediaPlayer.CHARSET_GBK));
                    try {
                        cSVPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT.withHeader("NO.", "Line name", "Current line", "Attribute(1:Normal 2:Circle 3:Back)", "Line serial"));
                    } catch (IOException e) {
                        e = e;
                    }
                } catch (Throwable th) {
                    th = th;
                }
            } catch (IOException e2) {
                e = e2;
                bufferedWriter = null;
            } catch (Throwable th2) {
                th = th2;
                bufferedWriter = null;
            }
            try {
                it = list.iterator();
            } catch (IOException e3) {
                e = e3;
                HasNext = cSVPrinter;
                e.printStackTrace();
                if (HasNext != 0) {
                    HasNext.close();
                }
                if (HasNext != 0) {
                    bufferedWriter.close();
                }
            } catch (Throwable th3) {
                th = th3;
                HasNext = cSVPrinter;
                if (HasNext != 0) {
                    try {
                        HasNext.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                        throw th;
                    }
                }
                if (HasNext != 0) {
                    bufferedWriter.close();
                }
                throw th;
            }
        } catch (IOException e5) {
            e5.printStackTrace();
        }
        while (true) {
            HasNext = it.hasNext();
            if (HasNext == 0) {
                break;
            }
            BusLineInfoModel next = it.next();
            Object[] objArr = new Object[5];
            objArr[0] = "" + next.getLineNo();
            objArr[1] = next.getLineName();
            objArr[2] = next.getISelect() ? "Y" : "N";
            objArr[3] = "" + next.getAttribute();
            objArr[4] = next.getLineNumber();
            cSVPrinter.printRecord(objArr);
        }
        cSVPrinter.printRecord(new Object[0]);
        cSVPrinter.flush();
        cSVPrinter.close();
        bufferedWriter.close();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v2, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r0v3 */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7, types: [boolean] */
    public static void writeConfigInfoCsv(List<ConfigInfoModel> list, File file) throws Throwable {
        BufferedWriter bufferedWriter;
        CSVPrinter cSVPrinter;
        Iterator<ConfigInfoModel> it;
        ?? HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        try {
            try {
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), MediaPlayer.CHARSET_GBK));
                    try {
                        cSVPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT.withHeader("CONFIGKEY", "CONFIGVALUE", "ACCOUNT"));
                    } catch (IOException e) {
                        e = e;
                    }
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    it = list.iterator();
                } catch (IOException e2) {
                    e = e2;
                    HasNext = cSVPrinter;
                    e.printStackTrace();
                    if (HasNext != 0) {
                        HasNext.close();
                    }
                    if (HasNext != 0) {
                        bufferedWriter.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    HasNext = cSVPrinter;
                    if (HasNext != 0) {
                        try {
                            HasNext.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                            throw th;
                        }
                    }
                    if (HasNext != 0) {
                        bufferedWriter.close();
                    }
                    throw th;
                }
            } catch (IOException e4) {
                e = e4;
                bufferedWriter = null;
            } catch (Throwable th3) {
                th = th3;
                bufferedWriter = null;
            }
        } catch (IOException e5) {
            e5.printStackTrace();
        }
        while (true) {
            HasNext = it.hasNext();
            if (HasNext == 0) {
                break;
            }
            ConfigInfoModel next = it.next();
            cSVPrinter.printRecord("" + next.getConfigItem(), next.getConfigValue(), next.getConfigExplain());
        }
        cSVPrinter.printRecord(new Object[0]);
        cSVPrinter.flush();
        cSVPrinter.close();
        bufferedWriter.close();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v2, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r0v3 */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7, types: [boolean] */
    public static void writeMaintenanceInfoCsv(List<MaintenanceModel> list, File file) throws Throwable {
        BufferedWriter bufferedWriter;
        CSVPrinter cSVPrinter;
        Iterator<MaintenanceModel> it;
        ?? HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        try {
            try {
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), MediaPlayer.CHARSET_GBK));
                    try {
                        cSVPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT.withHeader("NO.", "Content", "Remarks"));
                    } catch (IOException e) {
                        e = e;
                    }
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    it = list.iterator();
                } catch (IOException e2) {
                    e = e2;
                    HasNext = cSVPrinter;
                    e.printStackTrace();
                    if (HasNext != 0) {
                        HasNext.close();
                    }
                    if (HasNext != 0) {
                        bufferedWriter.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    HasNext = cSVPrinter;
                    if (HasNext != 0) {
                        try {
                            HasNext.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                            throw th;
                        }
                    }
                    if (HasNext != 0) {
                        bufferedWriter.close();
                    }
                    throw th;
                }
            } catch (IOException e4) {
                e = e4;
                bufferedWriter = null;
            } catch (Throwable th3) {
                th = th3;
                bufferedWriter = null;
            }
        } catch (IOException e5) {
            e5.printStackTrace();
        }
        while (true) {
            HasNext = it.hasNext();
            if (HasNext == 0) {
                break;
            }
            MaintenanceModel next = it.next();
            cSVPrinter.printRecord("" + next.getMId(), next.getContent(), next.getRemarks());
        }
        cSVPrinter.printRecord(new Object[0]);
        cSVPrinter.flush();
        cSVPrinter.close();
        bufferedWriter.close();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v2, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r0v3 */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.apache.commons.csv.CSVPrinter] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7, types: [boolean] */
    public static void writeMessageModelInfoCsv(List<MessageModel> list, File file) throws Throwable {
        BufferedWriter bufferedWriter;
        CSVPrinter cSVPrinter;
        Iterator<MessageModel> it;
        ?? HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        HasNext = 0;
        try {
            try {
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), MediaPlayer.CHARSET_GBK));
                    try {
                        cSVPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT.withHeader("NO.", "Date", "Content"));
                    } catch (IOException e) {
                        e = e;
                    }
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    it = list.iterator();
                } catch (IOException e2) {
                    e = e2;
                    HasNext = cSVPrinter;
                    e.printStackTrace();
                    if (HasNext != 0) {
                        HasNext.close();
                    }
                    if (HasNext != 0) {
                        bufferedWriter.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    HasNext = cSVPrinter;
                    if (HasNext != 0) {
                        try {
                            HasNext.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                            throw th;
                        }
                    }
                    if (HasNext != 0) {
                        bufferedWriter.close();
                    }
                    throw th;
                }
            } catch (IOException e4) {
                e = e4;
                bufferedWriter = null;
            } catch (Throwable th3) {
                th = th3;
                bufferedWriter = null;
            }
        } catch (IOException e5) {
            e5.printStackTrace();
        }
        while (true) {
            HasNext = it.hasNext();
            if (HasNext == 0) {
                break;
            }
            MessageModel next = it.next();
            cSVPrinter.printRecord("" + next.getMessageNo(), next.getMessageTime(), next.getMessageContent());
        }
        cSVPrinter.printRecord(new Object[0]);
        cSVPrinter.flush();
        cSVPrinter.close();
        bufferedWriter.close();
    }

    public static boolean analysisBusFile(String str) throws Throwable {
        File[] fileArr;
        try {
            String str2 = Constants.SD_ROOT + Constants.BUS_RES_PATH;
            delAllFile(str2);
            LogUtils.d("导入资源", "开始解压资源");
            if (ZipUtils.unManage(str, str2)) {
                LogUtils.d("导入资源", "解压资源成功，解压后的路径：" + str2);
                File file = new File(str2 + "/SourceFile/Bus");
                LogUtils.d("导入资源", "导入资源文件目录：" + file.getPath());
                if (!file.exists()) {
                    return true;
                }
                StringBuffer stringBuffer = new StringBuffer();
                StringBuffer stringBuffer2 = new StringBuffer();
                LogUtils.d("导入资源", "开始处理公交资源信息");
                File[] fileArrListFiles = file.listFiles();
                int length = fileArrListFiles.length;
                String str3 = "csv";
                int i = 0;
                while (i < length) {
                    File file2 = fileArrListFiles[i];
                    if (file2.isDirectory()) {
                        File file3 = new File(file2.getPath() + "/" + file2.getName() + "S.csv");
                        if (file3.exists()) {
                            LogUtils.d("导入资源", "开始处理公交上行站点信息，文件路径：" + file3.getPath());
                            readBusLineCsv(file3.getPath(), 1, file2.getName(), file2.getPath(), stringBuffer);
                            LogUtils.d("导入资源", "结束处理公交上行站点信息");
                            fileArr = fileArrListFiles;
                            str3 = "csv";
                        } else {
                            fileArr = fileArrListFiles;
                            File file4 = new File(file2.getPath() + "/" + file2.getName() + "S.xls");
                            if (file4.exists()) {
                                LogUtils.d("导入资源", "开始处理公交上行站点信息，文件路径：" + file4.getPath());
                                readBusLineXls(file4.getPath(), 1, file2.getName(), file2.getPath(), stringBuffer);
                                LogUtils.d("导入资源", "结束处理公交上行站点信息");
                                str3 = "xls";
                            }
                        }
                        File file5 = new File(file2.getPath() + "/" + file2.getName() + "X.csv");
                        if (file5.exists()) {
                            LogUtils.d("导入资源", "开始处理公交下行站点信息，文件路径：" + file5.getPath());
                            readBusLineCsv(file5.getPath(), 2, file2.getName(), file2.getPath(), stringBuffer);
                            LogUtils.d("导入资源", "结束处理公交下行站点信息");
                            str3 = "csv";
                        } else {
                            File file6 = new File(file2.getPath() + "/" + file2.getName() + "X.xls");
                            if (file6.exists()) {
                                LogUtils.d("导入资源", "开始处理公交下行站点信息，文件路径：" + file6.getPath());
                                readBusLineXls(file6.getPath(), 2, file2.getName(), file2.getPath(), stringBuffer);
                                LogUtils.d("导入资源", "结束处理公交下行站点信息");
                                str3 = "xls";
                            }
                        }
                        File file7 = new File(file2.getPath() + "/" + file2.getName() + "SRemind.csv");
                        if (file7.exists()) {
                            LogUtils.d("导入资源", "开始处理公交上行站点提醒信息，文件路径：" + file7.getPath());
                            readBusLineFRCsv(file7.getPath(), 1, file2.getName(), file2.getPath(), stringBuffer2);
                            LogUtils.d("导入资源", "结束处理公交上行站点提醒信息");
                        } else {
                            File file8 = new File(file2.getPath() + "/" + file2.getName() + "SRemind.xls");
                            if (file8.exists()) {
                                LogUtils.d("导入资源", "开始处理公交上行站点提醒信息，文件路径：" + file8.getPath());
                                readBusLineFRXls(file8.getPath(), 1, file2.getName(), file2.getPath(), stringBuffer2);
                                LogUtils.d("导入资源", "结束处理公交上行站点提醒信息");
                            }
                        }
                        File file9 = new File(file2.getPath() + "/" + file2.getName() + "XRemind.csv");
                        if (file9.exists()) {
                            LogUtils.d("导入资源", "开始处理公交下行站点提醒信息，文件路径：" + file9.getPath());
                            readBusLineFRCsv(file9.getPath(), 2, file2.getName(), file2.getPath(), stringBuffer2);
                            LogUtils.d("导入资源", "结束处理公交下行站点提醒信息");
                        } else {
                            File file10 = new File(file2.getPath() + "/" + file2.getName() + "XRemind.xls");
                            if (file10.exists()) {
                                LogUtils.d("导入资源", "开始处理公交下行站点提醒信息，文件路径：" + file10.getPath());
                                readBusLineFRXls(file10.getPath(), 2, file2.getName(), file2.getPath(), stringBuffer2);
                                LogUtils.d("导入资源", "结束处理公交下行站点提醒信息");
                            }
                        }
                    } else {
                        fileArr = fileArrListFiles;
                    }
                    i++;
                    fileArrListFiles = fileArr;
                }
                File file11 = new File(file.getPath() + "/lineInfo.csv");
                if (file11.exists()) {
                    LogUtils.d("导入资源", "开始处理公交lineInfo信息，文件路径：" + file11.getPath());
                    readLineInfoCsv(file11.getPath(), file.getPath(), str3);
                    LogUtils.d("导入资源", "结束处理公交lineInfo信息");
                } else {
                    File file12 = new File(file.getPath() + "/lineInfo.xls");
                    if (file12.exists()) {
                        LogUtils.d("导入资源", "开始处理公交lineInfo信息，文件路径：" + file12.getPath());
                        readLineInfoXls(file12.getPath(), file.getPath(), str3);
                        LogUtils.d("导入资源", "结束处理公交lineInfo信息");
                    }
                }
                File file13 = new File(file.getPath() + "/Vchinfo.csv");
                if (file13.exists()) {
                    readMaintenanceInfoCsv(file13.getPath(), file.getPath(), str3);
                } else {
                    File file14 = new File(file.getPath() + "/Vchinfo.xls");
                    if (file14.exists()) {
                        readMaintenanceInfoXls(file14.getPath(), file.getPath(), str3);
                    }
                }
                File file15 = new File(file.getPath() + "/config.csv");
                if (file15.exists()) {
                    LogUtils.d("导入资源", "开始处理公交config信息，文件路径：" + file15.getPath());
                    readConfigInfoCsv(file15.getPath(), file.getPath());
                    LogUtils.d("导入资源", "结束处理公交config信息");
                } else {
                    File file16 = new File(file.getPath() + "/config.xls");
                    if (file16.exists()) {
                        LogUtils.d("导入资源", "开始处理公交config信息，文件路径：" + file16.getPath());
                        readConfigInfoXls(file16.getPath(), file.getPath());
                        LogUtils.d("导入资源", "结束处理公交config信息");
                    }
                }
                File file17 = new File(file.getPath() + "/Message.csv");
                if (file17.exists()) {
                    LogUtils.d("导入资源", "开始处理平台下发消息message信息，文件路径：" + file17.getPath());
                    readMessageInfoCsv(file17.getPath(), file.getPath(), str3);
                    LogUtils.d("导入资源", "结束处理平台下发消息message信息");
                } else if (new File(file.getPath() + "/Message.xls").exists()) {
                    LogUtils.d("导入资源", "开始处理平台下发消息message信息，文件路径：" + file17.getPath());
                    readMessageInfoXls(file17.getPath(), file.getPath(), str3);
                    LogUtils.d("导入资源", "结束处理平台下发消息message信息");
                }
                LogUtils.d("导入资源", "结束处理公交资源信息");
                return true;
            }
            LogUtils.d("导入资源", "解压资源失败");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("导入资源", "资源导入失败，失败原因：" + e.getMessage());
            return false;
        }
    }
}
