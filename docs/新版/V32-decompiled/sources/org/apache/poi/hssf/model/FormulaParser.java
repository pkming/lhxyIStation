package org.apache.poi.hssf.model;

import android.telephony.PhoneNumberUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import org.apache.poi.hssf.record.formula.AbstractFunctionPtg;
import org.apache.poi.hssf.record.formula.AddPtg;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.AreaPtg;
import org.apache.poi.hssf.record.formula.AttrPtg;
import org.apache.poi.hssf.record.formula.BoolPtg;
import org.apache.poi.hssf.record.formula.ConcatPtg;
import org.apache.poi.hssf.record.formula.DividePtg;
import org.apache.poi.hssf.record.formula.EqualPtg;
import org.apache.poi.hssf.record.formula.FuncVarPtg;
import org.apache.poi.hssf.record.formula.GreaterEqualPtg;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;
import org.apache.poi.hssf.record.formula.IntPtg;
import org.apache.poi.hssf.record.formula.LessEqualPtg;
import org.apache.poi.hssf.record.formula.LessThanPtg;
import org.apache.poi.hssf.record.formula.MultiplyPtg;
import org.apache.poi.hssf.record.formula.NamePtg;
import org.apache.poi.hssf.record.formula.NotEqualPtg;
import org.apache.poi.hssf.record.formula.NumberPtg;
import org.apache.poi.hssf.record.formula.OperationPtg;
import org.apache.poi.hssf.record.formula.ParenthesisPtg;
import org.apache.poi.hssf.record.formula.PowerPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.record.formula.ReferencePtg;
import org.apache.poi.hssf.record.formula.StringPtg;
import org.apache.poi.hssf.record.formula.SubtractPtg;
import org.apache.poi.hssf.record.formula.UnaryMinusPtg;

/* JADX INFO: loaded from: classes3.dex */
public class FormulaParser {
    private static char CR = '\n';
    public static int FORMULA_TYPE_ARRAY = 2;
    public static int FORMULA_TYPE_CELL = 0;
    public static int FORMULA_TYPE_CONDFOMRAT = 3;
    public static int FORMULA_TYPE_NAMEDRANGE = 4;
    public static int FORMULA_TYPE_SHARED = 1;
    private static char TAB = '\t';
    private Workbook book;
    private int formulaLength;
    private String formulaString;
    private char look;
    private int numParen;
    private int pointer;
    private List tokens = new Stack();
    private List functionTokens = new LinkedList();
    private List result = new ArrayList();
    private boolean inFunction = false;

    private boolean IsAddop(char c) {
        return c == '+' || c == '-';
    }

    private boolean IsSpecialChar(char c) {
        return c == '>' || c == '<' || c == '=' || c == '&' || c == '[' || c == ']';
    }

    public FormulaParser(String str, Workbook workbook) {
        this.pointer = 0;
        this.formulaString = str;
        this.pointer = 0;
        this.book = workbook;
        this.formulaLength = str.length();
    }

    private void GetChar() {
        int i = this.pointer;
        if (i == this.formulaLength) {
            this.look = (char) 0;
            return;
        }
        String str = this.formulaString;
        this.pointer = i + 1;
        this.look = str.charAt(i);
    }

    private void Error(String str) {
        System.out.println(new StringBuffer().append("Error: ").append(str).toString());
    }

    private void Abort(String str) {
        Error(str);
        throw new RuntimeException(new StringBuffer().append("Cannot Parse, sorry : ").append(str).toString());
    }

    private void Expected(String str) {
        Abort(new StringBuffer().append(str).append(" Expected").toString());
    }

    private boolean IsAlpha(char c) {
        return Character.isLetter(c) || c == '$';
    }

    private boolean IsDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean IsAlNum(char c) {
        return IsAlpha(c) || IsDigit(c);
    }

    private boolean IsWhite(char c) {
        return c == ' ' || c == TAB;
    }

    private void SkipWhite() {
        while (IsWhite(this.look)) {
            GetChar();
        }
    }

    private void Match(char c) {
        if (this.look != c) {
            Expected(new StringBuffer().append("").append(c).append("").toString());
        } else {
            GetChar();
            SkipWhite();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0023, code lost:
    
        if (r6.look == '\'') goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0025, code lost:
    
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0027, code lost:
    
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0028, code lost:
    
        if (r1 == false) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x002b, code lost:
    
        r0.append(java.lang.Character.toUpperCase(r6.look));
        GetChar();
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0039, code lost:
    
        if (r6.look != '\'') goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x003b, code lost:
    
        Match(android.text.format.DateFormat.QUOTE);
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0040, code lost:
    
        if (r6.look == '\'') goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0028, code lost:
    
        r1 = false;
     */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:18:0x0040 -> B:11:0x0025). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String GetName() {
        /*
            r6 = this;
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r0.<init>()
            char r1 = r6.look
            boolean r1 = r6.IsAlpha(r1)
            r2 = 39
            if (r1 != 0) goto L18
            char r1 = r6.look
            if (r1 == r2) goto L18
            java.lang.String r1 = "Name"
            r6.Expected(r1)
        L18:
            char r1 = r6.look
            if (r1 != r2) goto L43
            r6.Match(r2)
            char r1 = r6.look
            r3 = 1
            r4 = 0
            if (r1 != r2) goto L27
        L25:
            r1 = r3
            goto L28
        L27:
            r1 = r4
        L28:
            if (r1 == 0) goto L2b
            goto L4b
        L2b:
            char r5 = r6.look
            char r5 = java.lang.Character.toUpperCase(r5)
            r0.append(r5)
            r6.GetChar()
            char r5 = r6.look
            if (r5 != r2) goto L28
            r6.Match(r2)
            char r1 = r6.look
            if (r1 == r2) goto L27
            goto L25
        L43:
            char r1 = r6.look
            boolean r1 = r6.IsAlNum(r1)
            if (r1 != 0) goto L53
        L4b:
            r6.SkipWhite()
            java.lang.String r0 = r0.toString()
            return r0
        L53:
            char r1 = r6.look
            char r1 = java.lang.Character.toUpperCase(r1)
            r0.append(r1)
            r6.GetChar()
            goto L43
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.model.FormulaParser.GetName():java.lang.String");
    }

    private String GetNameAsIs() {
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            if (IsAlNum(this.look) || IsWhite(this.look) || IsSpecialChar(this.look)) {
                stringBuffer = stringBuffer.append(this.look);
                GetChar();
            } else {
                return stringBuffer.toString();
            }
        }
    }

    private String GetNum() {
        if (!IsDigit(this.look)) {
            Expected("Integer");
        }
        String string = "";
        while (IsDigit(this.look)) {
            string = new StringBuffer().append(string).append(this.look).toString();
            GetChar();
        }
        SkipWhite();
        return string;
    }

    private void Emit(String str) {
        System.out.print(new StringBuffer().append(TAB).append(str).toString());
    }

    private void EmitLn(String str) {
        Emit(str);
        System.out.println();
    }

    private void Ident() {
        String strGetName = GetName();
        char c = this.look;
        if (c == '(') {
            function(strGetName);
            return;
        }
        if (c == ':') {
            Match(':');
            this.tokens.add(new AreaPtg(new StringBuffer().append(strGetName).append(":").append(GetName()).toString()));
            return;
        }
        if (c == '!') {
            Match('!');
            String strGetName2 = GetName();
            Workbook workbook = this.book;
            short sCheckExternSheet = workbook.checkExternSheet(workbook.getSheetIndex(strGetName));
            if (this.look == ':') {
                Match(':');
                this.tokens.add(new Area3DPtg(new StringBuffer().append(strGetName2).append(":").append(GetName()).toString(), sCheckExternSheet));
                return;
            } else {
                this.tokens.add(new Ref3DPtg(strGetName2, sCheckExternSheet));
                return;
            }
        }
        if (strGetName.equals("TRUE") || strGetName.equals("FALSE")) {
            this.tokens.add(new BoolPtg(strGetName));
        } else {
            this.tokens.add(new ReferencePtg(strGetName));
        }
    }

    private void addArgumentPointer() {
        if (this.functionTokens.size() > 0) {
            ((List) this.functionTokens.get(0)).add(this.tokens.get(r1.size() - 1));
        }
    }

    private void function(String str) {
        this.functionTokens.add(0, new ArrayList(2));
        Match('(');
        int iArguments = Arguments();
        Match(')');
        AbstractFunctionPtg function = getFunction(str, (byte) iArguments);
        this.tokens.add(function);
        if (function.getName().equals("externalflag")) {
            this.tokens.add(new NamePtg(str, this.book));
        }
        this.functionTokens.remove(0);
    }

    private int getPtgSize(int i) {
        ListIterator listIterator = this.tokens.listIterator(i);
        int size = 0;
        while (listIterator.hasNext()) {
            size += ((Ptg) listIterator.next()).getSize();
        }
        return size;
    }

    private int getPtgSize(int i, int i2) {
        ListIterator listIterator = this.tokens.listIterator(i);
        int size = 0;
        while (listIterator.hasNext() && i <= i2) {
            size += ((Ptg) listIterator.next()).getSize();
            i++;
        }
        return size;
    }

    private AbstractFunctionPtg getFunction(String str, byte b) {
        if (str.equals("IF")) {
            FuncVarPtg funcVarPtg = new FuncVarPtg(AbstractFunctionPtg.ATTR_NAME, b);
            List list = (List) this.functionTokens.get(0);
            AttrPtg attrPtg = new AttrPtg();
            attrPtg.setData((short) 7);
            attrPtg.setOptimizedIf(true);
            if (list.size() != 2 && list.size() != 3) {
                throw new IllegalArgumentException(new StringBuffer().append("[").append(list.size()).append("] Arguments Found - An IF formula requires 2 or 3 arguments. IF(CONDITION, TRUE_VALUE, FALSE_VALUE [OPTIONAL]").toString());
            }
            int iIndexOf = this.tokens.indexOf(list.get(0)) + 1;
            this.tokens.add(iIndexOf, attrPtg);
            int iIndexOf2 = this.tokens.indexOf(list.get(1)) + 1;
            AttrPtg attrPtg2 = new AttrPtg();
            attrPtg2.setGoto(true);
            this.tokens.add(iIndexOf2, attrPtg2);
            if (b > 2) {
                AttrPtg attrPtg3 = new AttrPtg();
                attrPtg3.setGoto(true);
                attrPtg3.setData((short) (funcVarPtg.getSize() - 1));
                this.tokens.add(attrPtg3);
            }
            attrPtg.setData((short) getPtgSize(iIndexOf + 1, iIndexOf2));
            int ptgSize = (getPtgSize(iIndexOf2) - attrPtg2.getSize()) + funcVarPtg.getSize();
            if (ptgSize > 32767) {
                throw new RuntimeException("Ptg Size exceeds short when being specified for a goto ptg in an if");
            }
            attrPtg2.setData((short) (ptgSize - 1));
            return funcVarPtg;
        }
        return new FuncVarPtg(str, b);
    }

    private int Arguments() {
        int i;
        if (this.look != ')') {
            Expression();
            addArgumentPointer();
            i = 1;
        } else {
            i = 0;
        }
        while (true) {
            char c = this.look;
            if (c != ',' && c != ';') {
                return i;
            }
            if (c == ',') {
                Match(PhoneNumberUtils.PAUSE);
            } else {
                Match(PhoneNumberUtils.WAIT);
            }
            Expression();
            addArgumentPointer();
            i++;
        }
    }

    private void Factor() {
        char c;
        char c2 = this.look;
        if (c2 == '-') {
            Match('-');
            Factor();
            this.tokens.add(new UnaryMinusPtg());
            return;
        }
        if (c2 == '(') {
            Match('(');
            Expression();
            Match(')');
            this.tokens.add(new ParenthesisPtg());
            return;
        }
        if (IsAlpha(c2) || (c = this.look) == '\'') {
            Ident();
            return;
        }
        if (c == '\"') {
            StringLiteral();
            return;
        }
        String strGetNum = GetNum();
        if (this.look == '.') {
            Match('.');
            if (IsDigit(this.look)) {
                strGetNum = new StringBuffer().append(strGetNum).append(".").append(GetNum()).toString();
            }
            this.tokens.add(new NumberPtg(strGetNum));
            return;
        }
        this.tokens.add(new IntPtg(strGetNum));
    }

    private void StringLiteral() {
        if (this.look != '\"') {
            Expected("\"");
            return;
        }
        GetChar();
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            char c = this.look;
            if (c == '\"') {
                GetChar();
                SkipWhite();
                if (this.look != '\"') {
                    break;
                } else {
                    stringBuffer.append("\"");
                }
            } else {
                if (c == 0) {
                    break;
                }
                stringBuffer.append(c);
                GetChar();
            }
        }
        this.tokens.add(new StringPtg(stringBuffer.toString()));
    }

    private void Multiply() {
        Match('*');
        Factor();
        this.tokens.add(new MultiplyPtg());
    }

    private void Divide() {
        Match('/');
        Factor();
        this.tokens.add(new DividePtg());
    }

    private void Term() {
        Factor();
        while (true) {
            char c = this.look;
            if (c != '*' && c != '/' && c != '^' && c != '&') {
                return;
            }
            if (c == '*') {
                Multiply();
            } else if (c == '/') {
                Divide();
            } else if (c == '^') {
                Power();
            } else if (c == '&') {
                Concat();
            }
        }
    }

    private void Add() {
        Match('+');
        Term();
        this.tokens.add(new AddPtg());
    }

    private void Concat() {
        Match('&');
        Term();
        this.tokens.add(new ConcatPtg());
    }

    private void Equal() {
        Match('=');
        Expression();
        this.tokens.add(new EqualPtg());
    }

    private void Subtract() {
        Match('-');
        Term();
        this.tokens.add(new SubtractPtg());
    }

    private void Power() {
        Match('^');
        Term();
        this.tokens.add(new PowerPtg());
    }

    private void Expression() {
        Term();
        while (IsAddop(this.look)) {
            char c = this.look;
            if (c == '+') {
                Add();
            } else if (c == '-') {
                Subtract();
            }
        }
        char c2 = this.look;
        if (c2 == '=' || c2 == '>' || c2 == '<') {
            if (c2 == '=') {
                Equal();
            } else if (c2 == '>') {
                GreaterThan();
            } else if (c2 == '<') {
                LessThan();
            }
        }
    }

    private void GreaterThan() {
        Match('>');
        if (this.look == '=') {
            GreaterEqual();
        } else {
            Expression();
            this.tokens.add(new GreaterThanPtg());
        }
    }

    private void LessThan() {
        Match('<');
        char c = this.look;
        if (c == '=') {
            LessEqual();
        } else if (c == '>') {
            NotEqual();
        } else {
            Expression();
            this.tokens.add(new LessThanPtg());
        }
    }

    private void GreaterEqual() {
        Match('=');
        Expression();
        this.tokens.add(new GreaterEqualPtg());
    }

    private void LessEqual() {
        Match('=');
        Expression();
        this.tokens.add(new LessEqualPtg());
    }

    private void NotEqual() {
        Match('>');
        Expression();
        this.tokens.add(new NotEqualPtg());
    }

    private void init() {
        GetChar();
        SkipWhite();
    }

    public void parse() {
        synchronized (this.tokens) {
            init();
            Expression();
        }
    }

    public Ptg[] getRPNPtg() {
        return getRPNPtg(FORMULA_TYPE_CELL);
    }

    public Ptg[] getRPNPtg(int i) {
        Node nodeCreateTree = createTree();
        setRootLevelRVA(nodeCreateTree, i);
        setParameterRVA(nodeCreateTree, i);
        return (Ptg[]) this.tokens.toArray(new Ptg[0]);
    }

    private void setRootLevelRVA(Node node, int i) {
        Ptg value = node.getValue();
        if (i == FORMULA_TYPE_NAMEDRANGE) {
            if (value.getDefaultOperandClass() == 0) {
                setClass(node, (byte) 0);
                return;
            } else {
                setClass(node, (byte) 64);
                return;
            }
        }
        setClass(node, (byte) 32);
    }

    private void setParameterRVA(Node node, int i) {
        Ptg value = node.getValue();
        int numChildren = node.getNumChildren();
        int i2 = 0;
        if (!(value instanceof AbstractFunctionPtg)) {
            while (i2 < numChildren) {
                setParameterRVA(node.getChild(i2), i);
                i2++;
            }
        } else {
            while (i2 < numChildren) {
                setParameterRVA(node.getChild(i2), ((AbstractFunctionPtg) value).getParameterClass(i2), i);
                setParameterRVA(node.getChild(i2), i);
                i2++;
            }
        }
    }

    private void setParameterRVA(Node node, int i, int i2) {
        Ptg value = node.getValue();
        if (i == 0) {
            if (value.getDefaultOperandClass() == 0) {
                setClass(node, (byte) 0);
            }
            if (value.getDefaultOperandClass() == 32) {
                if (i2 == FORMULA_TYPE_CELL || i2 == FORMULA_TYPE_SHARED) {
                    setClass(node, (byte) 32);
                } else {
                    setClass(node, (byte) 64);
                }
            }
            if (value.getDefaultOperandClass() == 64) {
                setClass(node, (byte) 64);
                return;
            }
            return;
        }
        if (i == 32) {
            if (i2 == FORMULA_TYPE_NAMEDRANGE) {
                setClass(node, (byte) 64);
                return;
            } else {
                setClass(node, (byte) 32);
                return;
            }
        }
        if (value.getDefaultOperandClass() == 32 && (i2 == FORMULA_TYPE_CELL || i2 == FORMULA_TYPE_SHARED)) {
            setClass(node, (byte) 32);
        } else {
            setClass(node, (byte) 64);
        }
    }

    private void setClass(Node node, byte b) {
        Ptg value = node.getValue();
        if ((value instanceof AbstractFunctionPtg) || !(value instanceof OperationPtg)) {
            value.setClass(b);
            return;
        }
        for (int i = 0; i < node.getNumChildren(); i++) {
            setClass(node.getChild(i), b);
        }
    }

    public static String toFormulaString(Workbook workbook, List list) {
        return (list == null || list.size() == 0) ? "#NAME" : toFormulaString(workbook, (Ptg[]) list.toArray(new Ptg[list.size()]));
    }

    public static String toFormulaString(Workbook workbook, Ptg[] ptgArr) {
        String name;
        if (ptgArr == null || ptgArr.length == 0) {
            return "#NAME";
        }
        Stack stack = new Stack();
        AttrPtg attrPtg = null;
        stack.push(ptgArr[0].toFormulaString(workbook));
        for (int i = 1; i < ptgArr.length; i++) {
            if (!(ptgArr[i] instanceof OperationPtg)) {
                stack.push(ptgArr[i].toFormulaString(workbook));
            } else if ((ptgArr[i] instanceof AttrPtg) && ((AttrPtg) ptgArr[i]).isOptimizedIf()) {
                attrPtg = (AttrPtg) ptgArr[i];
            } else {
                OperationPtg operationPtg = (OperationPtg) ptgArr[i];
                int numberOfOperands = operationPtg.getNumberOfOperands();
                String[] strArr = new String[numberOfOperands];
                while (numberOfOperands > 0) {
                    strArr[numberOfOperands - 1] = (String) stack.pop();
                    numberOfOperands--;
                }
                stack.push(operationPtg.toFormulaString(strArr));
                if ((operationPtg instanceof AbstractFunctionPtg) && (name = ((AbstractFunctionPtg) operationPtg).getName()) != null) {
                    if (attrPtg != null && name.equals(AbstractFunctionPtg.ATTR_NAME)) {
                        stack.push(attrPtg.toFormulaString(new String[]{(String) stack.pop()}));
                    } else if (name.equals("externalflag")) {
                        String str = (String) stack.pop();
                        int iIndexOf = str.indexOf(40);
                        int iIndexOf2 = str.indexOf(44);
                        if (iIndexOf2 == -1) {
                            stack.push(new StringBuffer().append(str.substring(iIndexOf + 1, str.indexOf(41))).append("()").toString());
                        } else {
                            stack.push(new StringBuffer().append(str.substring(iIndexOf + 1, iIndexOf2)).append('(').append(str.substring(iIndexOf2 + 1)).toString());
                        }
                    }
                }
            }
        }
        return (String) stack.pop();
    }

    private Node createTree() {
        Stack stack = new Stack();
        int size = this.tokens.size();
        for (int i = 0; i < size; i++) {
            if (this.tokens.get(i) instanceof OperationPtg) {
                OperationPtg operationPtg = (OperationPtg) this.tokens.get(i);
                int numberOfOperands = operationPtg.getNumberOfOperands();
                Node[] nodeArr = new Node[numberOfOperands];
                for (int i2 = 0; i2 < numberOfOperands; i2++) {
                    nodeArr[(numberOfOperands - i2) - 1] = (Node) stack.pop();
                }
                Node node = new Node(operationPtg);
                node.setChildren(nodeArr);
                stack.push(node);
            } else {
                stack.push(new Node((Ptg) this.tokens.get(i)));
            }
        }
        return (Node) stack.pop();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.tokens.size(); i++) {
            stringBuffer.append(((Ptg) this.tokens.get(i)).toFormulaString(this.book));
            stringBuffer.append(' ');
        }
        return stringBuffer.toString();
    }
}
