package org.apache.poi.hssf.dev;

import java.io.FileInputStream;
import java.util.List;
import org.apache.poi.hssf.model.FormulaParser;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.formula.ExpPtg;
import org.apache.poi.hssf.record.formula.FuncPtg;
import org.apache.poi.hssf.record.formula.OperationPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/* JADX INFO: loaded from: classes3.dex */
public class FormulaViewer {
    private String file;
    private boolean list = false;

    public void run() throws Exception {
        List listCreateRecords = RecordFactory.createRecords(new POIFSFileSystem(new FileInputStream(this.file)).createDocumentInputStream("Workbook"));
        for (int i = 0; i < listCreateRecords.size(); i++) {
            Record record = (Record) listCreateRecords.get(i);
            if (record.getSid() == 6) {
                if (this.list) {
                    listFormula((FormulaRecord) record);
                } else {
                    parseFormulaRecord((FormulaRecord) record);
                }
            }
        }
    }

    private void listFormula(FormulaRecord formulaRecord) {
        String strValueOf;
        List parsedExpression = formulaRecord.getParsedExpression();
        int numberOfExpressionTokens = formulaRecord.getNumberOfExpressionTokens();
        if (parsedExpression != null) {
            int i = numberOfExpressionTokens - 1;
            Ptg ptg = (Ptg) parsedExpression.get(i);
            if (ptg instanceof FuncPtg) {
                strValueOf = String.valueOf(i);
            } else {
                strValueOf = String.valueOf(-1);
            }
            StringBuffer stringBuffer = new StringBuffer();
            if (ptg instanceof ExpPtg) {
                return;
            }
            stringBuffer.append(((OperationPtg) ptg).toFormulaString((Workbook) null));
            stringBuffer.append("~");
            byte ptgClass = ptg.getPtgClass();
            if (ptgClass == 0) {
                stringBuffer.append("REF");
            } else if (ptgClass == 32) {
                stringBuffer.append("VALUE");
            } else if (ptgClass == 64) {
                stringBuffer.append("ARRAY");
            }
            stringBuffer.append("~");
            if (numberOfExpressionTokens > 1) {
                byte ptgClass2 = ((Ptg) parsedExpression.get(numberOfExpressionTokens - 2)).getPtgClass();
                if (ptgClass2 == 0) {
                    stringBuffer.append("REF");
                } else if (ptgClass2 == 32) {
                    stringBuffer.append("VALUE");
                } else if (ptgClass2 == 64) {
                    stringBuffer.append("ARRAY");
                }
            } else {
                stringBuffer.append("VALUE");
            }
            stringBuffer.append("~");
            stringBuffer.append(strValueOf);
            System.out.println(stringBuffer.toString());
            return;
        }
        System.out.println("#NAME");
    }

    public void parseFormulaRecord(FormulaRecord formulaRecord) {
        System.out.println("==============================");
        System.out.print(new StringBuffer().append("row = ").append(formulaRecord.getRow()).toString());
        System.out.println(new StringBuffer().append(", col = ").append((int) formulaRecord.getColumn()).toString());
        System.out.println(new StringBuffer().append("value = ").append(formulaRecord.getValue()).toString());
        System.out.print(new StringBuffer().append("xf = ").append((int) formulaRecord.getXFIndex()).toString());
        System.out.print(new StringBuffer().append(", number of ptgs = ").append(formulaRecord.getNumberOfExpressionTokens()).toString());
        System.out.println(new StringBuffer().append(", options = ").append((int) formulaRecord.getOptions()).toString());
        System.out.println(new StringBuffer().append("RPN List = ").append(formulaString(formulaRecord)).toString());
        System.out.println(new StringBuffer().append("Formula text = ").append(composeFormula(formulaRecord)).toString());
    }

    private String formulaString(FormulaRecord formulaRecord) {
        int numberOfExpressionTokens = formulaRecord.getNumberOfExpressionTokens();
        List parsedExpression = formulaRecord.getParsedExpression();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < numberOfExpressionTokens; i++) {
            Ptg ptg = (Ptg) parsedExpression.get(i);
            stringBuffer.append(ptg.toFormulaString((Workbook) null));
            byte ptgClass = ptg.getPtgClass();
            if (ptgClass == 0) {
                stringBuffer.append("(R)");
            } else if (ptgClass == 32) {
                stringBuffer.append("(V)");
            } else if (ptgClass == 64) {
                stringBuffer.append("(A)");
            }
            stringBuffer.append(' ');
        }
        return stringBuffer.toString();
    }

    private String composeFormula(FormulaRecord formulaRecord) {
        return FormulaParser.toFormulaString((Workbook) null, formulaRecord.getParsedExpression());
    }

    public void setFile(String str) {
        this.file = str;
    }

    public void setList(boolean z) {
        this.list = z;
    }

    public static void main(String[] strArr) {
        if (strArr == null || strArr.length > 2 || strArr[0].equals("--help")) {
            System.out.println("FormulaViewer .8 proof that the devil lies in the details (or just in BIFF8 files in general)");
            System.out.println("usage: Give me a big fat file name");
            return;
        }
        if (strArr[0].equals("--listFunctions")) {
            try {
                FormulaViewer formulaViewer = new FormulaViewer();
                formulaViewer.setFile(strArr[1]);
                formulaViewer.setList(true);
                formulaViewer.run();
                return;
            } catch (Exception e) {
                System.out.println("Whoops!");
                e.printStackTrace();
                return;
            }
        }
        try {
            FormulaViewer formulaViewer2 = new FormulaViewer();
            formulaViewer2.setFile(strArr[0]);
            formulaViewer2.run();
        } catch (Exception e2) {
            System.out.println("Whoops!");
            e2.printStackTrace();
        }
    }
}
