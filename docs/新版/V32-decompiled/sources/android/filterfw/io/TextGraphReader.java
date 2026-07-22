package android.filterfw.io;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterFactory;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.KeyValueMap;
import android.filterfw.core.ProtocolException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class TextGraphReader extends GraphReader {
    private KeyValueMap mBoundReferences;
    private ArrayList<Command> mCommands = new ArrayList<>();
    private Filter mCurrentFilter;
    private FilterGraph mCurrentGraph;
    private FilterFactory mFactory;
    private KeyValueMap mSettings;

    private interface Command {
        void execute(TextGraphReader textGraphReader) throws GraphIOException;
    }

    private class ImportPackageCommand implements Command {
        private String mPackageName;

        public ImportPackageCommand(String str) {
            this.mPackageName = str;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) throws GraphIOException {
            try {
                textGraphReader.mFactory.addPackage(this.mPackageName);
            } catch (IllegalArgumentException e) {
                throw new GraphIOException(e.getMessage());
            }
        }
    }

    private class AddLibraryCommand implements Command {
        private String mLibraryName;

        public AddLibraryCommand(String str) {
            this.mLibraryName = str;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) {
            FilterFactory unused = textGraphReader.mFactory;
            FilterFactory.addFilterLibrary(this.mLibraryName);
        }
    }

    private class AllocateFilterCommand implements Command {
        private String mClassName;
        private String mFilterName;

        public AllocateFilterCommand(String str, String str2) {
            this.mClassName = str;
            this.mFilterName = str2;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) throws GraphIOException {
            try {
                textGraphReader.mCurrentFilter = textGraphReader.mFactory.createFilterByClassName(this.mClassName, this.mFilterName);
            } catch (IllegalArgumentException e) {
                throw new GraphIOException(e.getMessage());
            }
        }
    }

    private class InitFilterCommand implements Command {
        private KeyValueMap mParams;

        public InitFilterCommand(KeyValueMap keyValueMap) {
            this.mParams = keyValueMap;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) throws GraphIOException {
            try {
                textGraphReader.mCurrentFilter.initWithValueMap(this.mParams);
                textGraphReader.mCurrentGraph.addFilter(TextGraphReader.this.mCurrentFilter);
            } catch (ProtocolException e) {
                throw new GraphIOException(e.getMessage());
            }
        }
    }

    private class ConnectCommand implements Command {
        private String mSourceFilter;
        private String mSourcePort;
        private String mTargetFilter;
        private String mTargetName;

        public ConnectCommand(String str, String str2, String str3, String str4) {
            this.mSourceFilter = str;
            this.mSourcePort = str2;
            this.mTargetFilter = str3;
            this.mTargetName = str4;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) {
            textGraphReader.mCurrentGraph.connect(this.mSourceFilter, this.mSourcePort, this.mTargetFilter, this.mTargetName);
        }
    }

    @Override // android.filterfw.io.GraphReader
    public FilterGraph readGraphString(String str) throws GraphIOException {
        FilterGraph filterGraph = new FilterGraph();
        reset();
        this.mCurrentGraph = filterGraph;
        parseString(str);
        applySettings();
        executeCommands();
        reset();
        return filterGraph;
    }

    private void reset() {
        this.mCurrentGraph = null;
        this.mCurrentFilter = null;
        this.mCommands.clear();
        this.mBoundReferences = new KeyValueMap();
        this.mSettings = new KeyValueMap();
        this.mFactory = new FilterFactory();
    }

    private void parseString(String str) throws GraphIOException {
        Pattern pattern;
        String str2;
        String strEat;
        Pattern pattern2;
        PatternScanner patternScanner;
        Pattern pattern3;
        Pattern pattern4;
        Pattern pattern5;
        Pattern pattern6;
        Pattern pattern7;
        String str3;
        Pattern pattern8;
        Pattern pattern9;
        char c;
        Pattern patternCompile = Pattern.compile("@[a-zA-Z]+");
        Pattern patternCompile2 = Pattern.compile("\\}");
        Pattern patternCompile3 = Pattern.compile("\\{");
        Pattern patternCompile4 = Pattern.compile("(\\s+|//[^\\n]*\\n)+");
        Pattern patternCompile5 = Pattern.compile("[a-zA-Z\\.]+");
        Pattern patternCompile6 = Pattern.compile("[a-zA-Z\\./:]+");
        Pattern patternCompile7 = Pattern.compile("\\[[a-zA-Z0-9\\-_]+\\]");
        Pattern patternCompile8 = Pattern.compile("=>");
        String str4 = ";";
        Pattern patternCompile9 = Pattern.compile(";");
        Pattern patternCompile10 = Pattern.compile("[a-zA-Z0-9\\-_]+");
        PatternScanner patternScanner2 = new PatternScanner(str, patternCompile4);
        String str5 = null;
        String strEat2 = null;
        String strSubstring = null;
        String strEat3 = null;
        char c2 = 0;
        while (true) {
            Pattern pattern10 = patternCompile;
            if (!patternScanner2.atEnd()) {
                switch (c2) {
                    case 0:
                        pattern = patternCompile9;
                        str2 = str4;
                        PatternScanner patternScanner3 = patternScanner2;
                        strEat = str5;
                        pattern2 = patternCompile6;
                        patternScanner = patternScanner3;
                        Pattern pattern11 = patternCompile5;
                        pattern3 = patternCompile10;
                        pattern4 = pattern11;
                        pattern5 = pattern10;
                        String strEat4 = patternScanner.eat(pattern5, "<command>");
                        if (strEat4.equals("@import")) {
                            c2 = 1;
                        } else if (strEat4.equals("@library")) {
                            c2 = 2;
                        } else if (strEat4.equals("@filter")) {
                            c2 = 3;
                        } else if (strEat4.equals("@connect")) {
                            c2 = '\b';
                        } else if (strEat4.equals("@set")) {
                            c2 = '\r';
                        } else if (strEat4.equals("@external")) {
                            c2 = 14;
                        } else {
                            if (!strEat4.equals("@setting")) {
                                throw new GraphIOException("Unknown command '" + strEat4 + "'!");
                            }
                            c2 = 15;
                        }
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner4 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner4;
                        Pattern pattern12 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern12;
                        break;
                    case 1:
                        pattern = patternCompile9;
                        Pattern pattern13 = patternCompile5;
                        str2 = str4;
                        pattern3 = patternCompile10;
                        PatternScanner patternScanner5 = patternScanner2;
                        strEat = str5;
                        pattern2 = patternCompile6;
                        patternScanner = patternScanner5;
                        pattern4 = pattern13;
                        this.mCommands.add(new ImportPackageCommand(patternScanner.eat(pattern4, "<package-name>")));
                        pattern5 = pattern10;
                        c2 = 16;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner42 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner42;
                        Pattern pattern122 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern122;
                        break;
                    case 2:
                        pattern = patternCompile9;
                        Pattern pattern14 = patternCompile5;
                        Pattern pattern15 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        strEat = str5;
                        pattern2 = pattern15;
                        this.mCommands.add(new AddLibraryCommand(patternScanner.eat(pattern2, "<library-name>")));
                        pattern5 = pattern10;
                        pattern4 = pattern14;
                        c2 = 16;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner422 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner422;
                        Pattern pattern1222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern1222;
                        break;
                    case 3:
                        pattern = patternCompile9;
                        pattern6 = patternCompile5;
                        pattern7 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        strEat = patternScanner.eat(pattern3, "<class-name>");
                        c2 = 4;
                        pattern5 = pattern10;
                        pattern4 = pattern6;
                        pattern2 = pattern7;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner4222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner4222;
                        Pattern pattern12222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern12222;
                        break;
                    case 4:
                        pattern = patternCompile9;
                        pattern6 = patternCompile5;
                        pattern7 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        strEat = str5;
                        this.mCommands.add(new AllocateFilterCommand(strEat, patternScanner.eat(pattern3, "<filter-name>")));
                        c2 = 5;
                        pattern5 = pattern10;
                        pattern4 = pattern6;
                        pattern2 = pattern7;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner42222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner42222;
                        Pattern pattern122222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern122222;
                        break;
                    case 5:
                        str3 = str5;
                        pattern = patternCompile9;
                        pattern8 = patternCompile5;
                        pattern9 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        patternScanner.eat(patternCompile3, "{");
                        c2 = 6;
                        pattern5 = pattern10;
                        pattern4 = pattern8;
                        pattern2 = pattern9;
                        strEat = str3;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner422222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner422222;
                        Pattern pattern1222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern1222222;
                        break;
                    case 6:
                        str3 = str5;
                        pattern = patternCompile9;
                        pattern8 = patternCompile5;
                        pattern9 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        this.mCommands.add(new InitFilterCommand(readKeyValueAssignments(patternScanner, patternCompile2)));
                        c2 = 7;
                        pattern5 = pattern10;
                        pattern4 = pattern8;
                        pattern2 = pattern9;
                        strEat = str3;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner4222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner4222222;
                        Pattern pattern12222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern12222222;
                        break;
                    case 7:
                        str3 = str5;
                        pattern = patternCompile9;
                        pattern8 = patternCompile5;
                        pattern9 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        patternScanner.eat(patternCompile2, "}");
                        c2 = 0;
                        pattern5 = pattern10;
                        pattern4 = pattern8;
                        pattern2 = pattern9;
                        strEat = str3;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner42222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner42222222;
                        Pattern pattern122222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern122222222;
                        break;
                    case '\b':
                        str3 = str5;
                        pattern = patternCompile9;
                        pattern8 = patternCompile5;
                        pattern9 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        c = '\t';
                        strEat2 = patternScanner.eat(pattern3, "<source-filter-name>");
                        c2 = c;
                        pattern5 = pattern10;
                        pattern4 = pattern8;
                        pattern2 = pattern9;
                        strEat = str3;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner422222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner422222222;
                        Pattern pattern1222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern1222222222;
                        break;
                    case '\t':
                        str3 = str5;
                        pattern = patternCompile9;
                        pattern8 = patternCompile5;
                        pattern9 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        String strEat5 = patternScanner.eat(patternCompile7, "[<source-port-name>]");
                        c = '\n';
                        strSubstring = strEat5.substring(1, strEat5.length() - 1);
                        c2 = c;
                        pattern5 = pattern10;
                        pattern4 = pattern8;
                        pattern2 = pattern9;
                        strEat = str3;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner4222222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner4222222222;
                        Pattern pattern12222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern12222222222;
                        break;
                    case '\n':
                        str3 = str5;
                        pattern = patternCompile9;
                        pattern8 = patternCompile5;
                        pattern9 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        patternScanner.eat(patternCompile8, "=>");
                        c2 = 11;
                        pattern5 = pattern10;
                        pattern4 = pattern8;
                        pattern2 = pattern9;
                        strEat = str3;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner42222222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner42222222222;
                        Pattern pattern122222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern122222222222;
                        break;
                    case 11:
                        str3 = str5;
                        pattern = patternCompile9;
                        pattern8 = patternCompile5;
                        pattern9 = patternCompile6;
                        str2 = str4;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        c = '\f';
                        strEat3 = patternScanner.eat(pattern3, "<target-filter-name>");
                        c2 = c;
                        pattern5 = pattern10;
                        pattern4 = pattern8;
                        pattern2 = pattern9;
                        strEat = str3;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner422222222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner422222222222;
                        Pattern pattern1222222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern1222222222222;
                        break;
                    case '\f':
                        str2 = str4;
                        Pattern pattern16 = patternCompile5;
                        String str6 = str5;
                        Pattern pattern17 = patternCompile6;
                        patternScanner = patternScanner2;
                        pattern3 = patternCompile10;
                        pattern = patternCompile9;
                        this.mCommands.add(new ConnectCommand(strEat2, strSubstring, strEat3, patternScanner2.eat(patternCompile7, "[<target-port-name>]").substring(1, r0.length() - 1)));
                        pattern5 = pattern10;
                        pattern4 = pattern16;
                        pattern2 = pattern17;
                        strEat = str6;
                        c2 = 16;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner4222222222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner4222222222222;
                        Pattern pattern12222222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern12222222222222;
                        break;
                    case '\r':
                        this.mBoundReferences.putAll(readKeyValueAssignments(patternScanner2, patternCompile9));
                        pattern = patternCompile9;
                        str2 = str4;
                        pattern5 = pattern10;
                        c2 = 16;
                        PatternScanner patternScanner6 = patternScanner2;
                        strEat = str5;
                        pattern2 = patternCompile6;
                        patternScanner = patternScanner6;
                        Pattern pattern18 = patternCompile5;
                        pattern3 = patternCompile10;
                        pattern4 = pattern18;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner42222222222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner42222222222222;
                        Pattern pattern122222222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern122222222222222;
                        break;
                    case 14:
                        bindExternal(patternScanner2.eat(patternCompile10, "<external-identifier>"));
                        pattern = patternCompile9;
                        str2 = str4;
                        pattern5 = pattern10;
                        c2 = 16;
                        PatternScanner patternScanner62 = patternScanner2;
                        strEat = str5;
                        pattern2 = patternCompile6;
                        patternScanner = patternScanner62;
                        Pattern pattern182 = patternCompile5;
                        pattern3 = patternCompile10;
                        pattern4 = pattern182;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner422222222222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner422222222222222;
                        Pattern pattern1222222222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern1222222222222222;
                        break;
                    case 15:
                        this.mSettings.putAll(readKeyValueAssignments(patternScanner2, patternCompile9));
                        pattern = patternCompile9;
                        str2 = str4;
                        pattern5 = pattern10;
                        c2 = 16;
                        PatternScanner patternScanner622 = patternScanner2;
                        strEat = str5;
                        pattern2 = patternCompile6;
                        patternScanner = patternScanner622;
                        Pattern pattern1822 = patternCompile5;
                        pattern3 = patternCompile10;
                        pattern4 = pattern1822;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner4222222222222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner4222222222222222;
                        Pattern pattern12222222222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern12222222222222222;
                        break;
                    case 16:
                        patternScanner2.eat(patternCompile9, str4);
                        pattern = patternCompile9;
                        str2 = str4;
                        c2 = 0;
                        pattern5 = pattern10;
                        PatternScanner patternScanner6222 = patternScanner2;
                        strEat = str5;
                        pattern2 = patternCompile6;
                        patternScanner = patternScanner6222;
                        Pattern pattern18222 = patternCompile5;
                        pattern3 = patternCompile10;
                        pattern4 = pattern18222;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner42222222222222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner42222222222222222;
                        Pattern pattern122222222222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern122222222222222222;
                        break;
                    default:
                        pattern = patternCompile9;
                        str2 = str4;
                        pattern5 = pattern10;
                        PatternScanner patternScanner62222 = patternScanner2;
                        strEat = str5;
                        pattern2 = patternCompile6;
                        patternScanner = patternScanner62222;
                        Pattern pattern182222 = patternCompile5;
                        pattern3 = patternCompile10;
                        pattern4 = pattern182222;
                        patternCompile = pattern5;
                        str4 = str2;
                        patternCompile9 = pattern;
                        PatternScanner patternScanner422222222222222222 = patternScanner;
                        patternCompile6 = pattern2;
                        str5 = strEat;
                        patternScanner2 = patternScanner422222222222222222;
                        Pattern pattern1222222222222222222 = pattern3;
                        patternCompile5 = pattern4;
                        patternCompile10 = pattern1222222222222222222;
                        break;
                }
            } else {
                if (c2 != 16 && c2 != 0) {
                    throw new GraphIOException("Unexpected end of input!");
                }
                return;
            }
        }
    }

    @Override // android.filterfw.io.GraphReader
    public KeyValueMap readKeyValueAssignments(String str) throws GraphIOException {
        return readKeyValueAssignments(new PatternScanner(str, Pattern.compile("\\s+")), null);
    }

    private KeyValueMap readKeyValueAssignments(PatternScanner patternScanner, Pattern pattern) throws GraphIOException {
        Pattern patternCompile = Pattern.compile("=");
        Pattern patternCompile2 = Pattern.compile(";");
        Pattern patternCompile3 = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9]*");
        Pattern patternCompile4 = Pattern.compile("'[^']*'|\\\"[^\\\"]*\\\"");
        Pattern patternCompile5 = Pattern.compile("[0-9]+");
        Pattern patternCompile6 = Pattern.compile("[0-9]*\\.[0-9]+f?");
        Pattern patternCompile7 = Pattern.compile("\\$[a-zA-Z]+[a-zA-Z0-9]");
        Pattern patternCompile8 = Pattern.compile("true|false");
        KeyValueMap keyValueMap = new KeyValueMap();
        char c = 0;
        String strEat = null;
        while (!patternScanner.atEnd() && (pattern == null || !patternScanner.peek(pattern))) {
            char c2 = 2;
            if (c == 0) {
                strEat = patternScanner.eat(patternCompile3, "<identifier>");
                c2 = 1;
            } else if (c == 1) {
                patternScanner.eat(patternCompile, "=");
            } else if (c == 2) {
                String strTryEat = patternScanner.tryEat(patternCompile4);
                if (strTryEat != null) {
                    keyValueMap.put(strEat, strTryEat.substring(1, strTryEat.length() - 1));
                } else {
                    String strTryEat2 = patternScanner.tryEat(patternCompile7);
                    if (strTryEat2 != null) {
                        String strSubstring = strTryEat2.substring(1, strTryEat2.length());
                        KeyValueMap keyValueMap2 = this.mBoundReferences;
                        Object obj = keyValueMap2 != null ? keyValueMap2.get(strSubstring) : null;
                        if (obj == null) {
                            throw new GraphIOException("Unknown object reference to '" + strSubstring + "'!");
                        }
                        keyValueMap.put(strEat, obj);
                    } else {
                        String strTryEat3 = patternScanner.tryEat(patternCompile8);
                        if (strTryEat3 != null) {
                            keyValueMap.put(strEat, Boolean.valueOf(Boolean.parseBoolean(strTryEat3)));
                        } else {
                            String strTryEat4 = patternScanner.tryEat(patternCompile6);
                            if (strTryEat4 != null) {
                                keyValueMap.put(strEat, Float.valueOf(Float.parseFloat(strTryEat4)));
                            } else {
                                String strTryEat5 = patternScanner.tryEat(patternCompile5);
                                if (strTryEat5 != null) {
                                    keyValueMap.put(strEat, Integer.valueOf(Integer.parseInt(strTryEat5)));
                                } else {
                                    throw new GraphIOException(patternScanner.unexpectedTokenMessage("<value>"));
                                }
                            }
                        }
                    }
                }
                c2 = 3;
            } else if (c != 3) {
                c2 = c;
            } else {
                patternScanner.eat(patternCompile2, ";");
                c2 = 0;
            }
            c = c2;
        }
        if (c == 0 || c == 3) {
            return keyValueMap;
        }
        throw new GraphIOException("Unexpected end of assignments on line " + patternScanner.lineNo() + "!");
    }

    private void bindExternal(String str) throws GraphIOException {
        if (this.mReferences.containsKey(str)) {
            this.mBoundReferences.put(str, this.mReferences.get(str));
            return;
        }
        throw new GraphIOException("Unknown external variable '" + str + "'! You must add a reference to this external in the host program using addReference(...)!");
    }

    private void checkReferences() throws GraphIOException {
        for (String str : this.mReferences.keySet()) {
            if (!this.mBoundReferences.containsKey(str)) {
                throw new GraphIOException("Host program specifies reference to '" + str + "', which is not declared @external in graph file!");
            }
        }
    }

    private void applySettings() throws GraphIOException {
        for (String str : this.mSettings.keySet()) {
            Object obj = this.mSettings.get(str);
            if (str.equals("autoBranch")) {
                expectSettingClass(str, obj, String.class);
                if (obj.equals("synced")) {
                    this.mCurrentGraph.setAutoBranchMode(1);
                } else if (obj.equals("unsynced")) {
                    this.mCurrentGraph.setAutoBranchMode(2);
                } else if (obj.equals("off")) {
                    this.mCurrentGraph.setAutoBranchMode(0);
                } else {
                    throw new GraphIOException("Unknown autobranch setting: " + obj + "!");
                }
            } else if (str.equals("discardUnconnectedOutputs")) {
                expectSettingClass(str, obj, Boolean.class);
                this.mCurrentGraph.setDiscardUnconnectedOutputs(((Boolean) obj).booleanValue());
            } else {
                throw new GraphIOException("Unknown @setting '" + str + "'!");
            }
        }
    }

    private void expectSettingClass(String str, Object obj, Class cls) throws GraphIOException {
        if (obj.getClass() != cls) {
            throw new GraphIOException("Setting '" + str + "' must have a value of type " + cls.getSimpleName() + ", but found a value of type " + obj.getClass().getSimpleName() + "!");
        }
    }

    private void executeCommands() throws GraphIOException {
        Iterator<Command> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().execute(this);
        }
    }
}
