package org.apache.poi.hssf.util;

import java.util.Hashtable;
import org.apache.poi.hssf.record.EscherAggregate;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFColor {
    private static final int DISTINCT_COLOR_COUNT = 46;
    private static final int PALETTE_SIZE = 56;

    public String getHexString() {
        return BLACK.hexString;
    }

    public short getIndex() {
        return (short) 8;
    }

    public static final Hashtable getIndexHash() {
        Hashtable hashtable = new Hashtable(56);
        hashtable.put(new Integer(8), new BLACK());
        hashtable.put(new Integer(60), new BROWN());
        hashtable.put(new Integer(59), new OLIVE_GREEN());
        hashtable.put(new Integer(58), new DARK_GREEN());
        hashtable.put(new Integer(56), new DARK_TEAL());
        hashtable.put(new Integer(18), new DARK_BLUE());
        hashtable.put(new Integer(32), new DARK_BLUE());
        hashtable.put(new Integer(62), new INDIGO());
        hashtable.put(new Integer(63), new GREY_80_PERCENT());
        hashtable.put(new Integer(53), new ORANGE());
        hashtable.put(new Integer(19), new DARK_YELLOW());
        hashtable.put(new Integer(17), new GREEN());
        hashtable.put(new Integer(21), new TEAL());
        hashtable.put(new Integer(38), new TEAL());
        hashtable.put(new Integer(12), new BLUE());
        hashtable.put(new Integer(39), new BLUE());
        hashtable.put(new Integer(54), new BLUE_GREY());
        hashtable.put(new Integer(23), new GREY_50_PERCENT());
        hashtable.put(new Integer(10), new RED());
        hashtable.put(new Integer(52), new LIGHT_ORANGE());
        hashtable.put(new Integer(50), new LIME());
        hashtable.put(new Integer(57), new SEA_GREEN());
        hashtable.put(new Integer(49), new AQUA());
        hashtable.put(new Integer(48), new LIGHT_BLUE());
        hashtable.put(new Integer(20), new VIOLET());
        hashtable.put(new Integer(36), new VIOLET());
        hashtable.put(new Integer(55), new GREY_40_PERCENT());
        hashtable.put(new Integer(14), new PINK());
        hashtable.put(new Integer(33), new PINK());
        hashtable.put(new Integer(51), new GOLD());
        hashtable.put(new Integer(13), new YELLOW());
        hashtable.put(new Integer(34), new YELLOW());
        hashtable.put(new Integer(11), new BRIGHT_GREEN());
        hashtable.put(new Integer(35), new BRIGHT_GREEN());
        hashtable.put(new Integer(15), new TURQUOISE());
        hashtable.put(new Integer(35), new TURQUOISE());
        hashtable.put(new Integer(16), new DARK_RED());
        hashtable.put(new Integer(37), new DARK_RED());
        hashtable.put(new Integer(40), new SKY_BLUE());
        hashtable.put(new Integer(61), new PLUM());
        hashtable.put(new Integer(25), new PLUM());
        hashtable.put(new Integer(22), new GREY_25_PERCENT());
        hashtable.put(new Integer(45), new ROSE());
        hashtable.put(new Integer(43), new LIGHT_YELLOW());
        hashtable.put(new Integer(42), new LIGHT_GREEN());
        hashtable.put(new Integer(41), new LIGHT_TURQUOISE());
        hashtable.put(new Integer(27), new LIGHT_TURQUOISE());
        hashtable.put(new Integer(44), new PALE_BLUE());
        hashtable.put(new Integer(46), new LAVENDER());
        hashtable.put(new Integer(9), new WHITE());
        hashtable.put(new Integer(24), new CORNFLOWER_BLUE());
        hashtable.put(new Integer(26), new LEMON_CHIFFON());
        hashtable.put(new Integer(25), new MAROON());
        hashtable.put(new Integer(28), new ORCHID());
        hashtable.put(new Integer(29), new CORAL());
        hashtable.put(new Integer(30), new ROYAL_BLUE());
        hashtable.put(new Integer(31), new LIGHT_CORNFLOWER_BLUE());
        return hashtable;
    }

    public static final Hashtable getTripletHash() {
        Hashtable hashtable = new Hashtable(46);
        hashtable.put(BLACK.hexString, new BLACK());
        hashtable.put(BROWN.hexString, new BROWN());
        hashtable.put(OLIVE_GREEN.hexString, new OLIVE_GREEN());
        hashtable.put(DARK_GREEN.hexString, new DARK_GREEN());
        hashtable.put(DARK_TEAL.hexString, new DARK_TEAL());
        hashtable.put(DARK_BLUE.hexString, new DARK_BLUE());
        hashtable.put(INDIGO.hexString, new INDIGO());
        hashtable.put(GREY_80_PERCENT.hexString, new GREY_80_PERCENT());
        hashtable.put(ORANGE.hexString, new ORANGE());
        hashtable.put(DARK_YELLOW.hexString, new DARK_YELLOW());
        hashtable.put(GREEN.hexString, new GREEN());
        hashtable.put(TEAL.hexString, new TEAL());
        hashtable.put(BLUE.hexString, new BLUE());
        hashtable.put(BLUE_GREY.hexString, new BLUE_GREY());
        hashtable.put(GREY_50_PERCENT.hexString, new GREY_50_PERCENT());
        hashtable.put(RED.hexString, new RED());
        hashtable.put(LIGHT_ORANGE.hexString, new LIGHT_ORANGE());
        hashtable.put(LIME.hexString, new LIME());
        hashtable.put(SEA_GREEN.hexString, new SEA_GREEN());
        hashtable.put(AQUA.hexString, new AQUA());
        hashtable.put(LIGHT_BLUE.hexString, new LIGHT_BLUE());
        hashtable.put(VIOLET.hexString, new VIOLET());
        hashtable.put(GREY_40_PERCENT.hexString, new GREY_40_PERCENT());
        hashtable.put(PINK.hexString, new PINK());
        hashtable.put(GOLD.hexString, new GOLD());
        hashtable.put(YELLOW.hexString, new YELLOW());
        hashtable.put(BRIGHT_GREEN.hexString, new BRIGHT_GREEN());
        hashtable.put(BRIGHT_GREEN.hexString, new TURQUOISE());
        hashtable.put(DARK_RED.hexString, new DARK_RED());
        hashtable.put(SKY_BLUE.hexString, new SKY_BLUE());
        hashtable.put("9999:3333:6666", new PLUM());
        hashtable.put(GREY_25_PERCENT.hexString, new GREY_25_PERCENT());
        hashtable.put(ROSE.hexString, new ROSE());
        hashtable.put(LIGHT_YELLOW.hexString, new LIGHT_YELLOW());
        hashtable.put(LIGHT_GREEN.hexString, new LIGHT_GREEN());
        hashtable.put(LIGHT_TURQUOISE.hexString, new LIGHT_TURQUOISE());
        hashtable.put(PALE_BLUE.hexString, new PALE_BLUE());
        hashtable.put(LAVENDER.hexString, new LAVENDER());
        hashtable.put(WHITE.hexString, new WHITE());
        hashtable.put(CORNFLOWER_BLUE.hexString, new CORNFLOWER_BLUE());
        hashtable.put(LEMON_CHIFFON.hexString, new LEMON_CHIFFON());
        hashtable.put("9999:3333:6666", new MAROON());
        hashtable.put(ORCHID.hexString, new ORCHID());
        hashtable.put(CORAL.hexString, new CORAL());
        hashtable.put(ROYAL_BLUE.hexString, new ROYAL_BLUE());
        hashtable.put(LIGHT_CORNFLOWER_BLUE.hexString, new LIGHT_CORNFLOWER_BLUE());
        return hashtable;
    }

    public short[] getTriplet() {
        return BLACK.triplet;
    }

    public static final class BLACK extends HSSFColor {
        public static final String hexString = "0:0:0";
        public static final short index = 8;
        public static final short[] triplet = {0, 0, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 8;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class BROWN extends HSSFColor {
        public static final String hexString = "9999:3333:0";
        public static final short index = 60;
        public static final short[] triplet = {EscherAggregate.ST_TEXTCURVEDOWN, 51, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 60;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static class OLIVE_GREEN extends HSSFColor {
        public static final String hexString = "3333:3333:0";
        public static final short index = 59;
        public static final short[] triplet = {51, 51, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 59;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class DARK_GREEN extends HSSFColor {
        public static final String hexString = "0:3333:0";
        public static final short index = 58;
        public static final short[] triplet = {0, 51, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 58;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class DARK_TEAL extends HSSFColor {
        public static final String hexString = "0:3333:6666";
        public static final short index = 56;
        public static final short[] triplet = {0, 51, EscherAggregate.ST_CURVEDRIGHTARROW};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 56;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class DARK_BLUE extends HSSFColor {
        public static final String hexString = "0:0:8080";
        public static final short index = 18;
        public static final short index2 = 32;
        public static final short[] triplet = {0, 0, 128};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 18;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class INDIGO extends HSSFColor {
        public static final String hexString = "3333:3333:9999";
        public static final short index = 62;
        public static final short[] triplet = {51, 51, EscherAggregate.ST_TEXTCURVEDOWN};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 62;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class GREY_80_PERCENT extends HSSFColor {
        public static final String hexString = "3333:3333:3333";
        public static final short index = 63;
        public static final short[] triplet = {51, 51, 51};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 63;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class DARK_RED extends HSSFColor {
        public static final String hexString = "8080:0:0";
        public static final short index = 16;
        public static final short index2 = 37;
        public static final short[] triplet = {128, 0, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 16;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class ORANGE extends HSSFColor {
        public static final String hexString = "FFFF:6666:0";
        public static final short index = 53;
        public static final short[] triplet = {255, EscherAggregate.ST_CURVEDRIGHTARROW, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 53;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class DARK_YELLOW extends HSSFColor {
        public static final String hexString = "8080:8080:0";
        public static final short index = 19;
        public static final short[] triplet = {128, 128, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 19;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class GREEN extends HSSFColor {
        public static final String hexString = "0:8080:0";
        public static final short index = 17;
        public static final short[] triplet = {0, 128, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 17;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class TEAL extends HSSFColor {
        public static final String hexString = "0:8080:8080";
        public static final short index = 21;
        public static final short index2 = 38;
        public static final short[] triplet = {0, 128, 128};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 21;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class BLUE extends HSSFColor {
        public static final String hexString = "0:0:FFFF";
        public static final short index = 12;
        public static final short index2 = 39;
        public static final short[] triplet = {0, 0, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 12;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class BLUE_GREY extends HSSFColor {
        public static final String hexString = "6666:6666:9999";
        public static final short index = 54;
        public static final short[] triplet = {EscherAggregate.ST_CURVEDRIGHTARROW, EscherAggregate.ST_CURVEDRIGHTARROW, EscherAggregate.ST_TEXTCURVEDOWN};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 54;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class GREY_50_PERCENT extends HSSFColor {
        public static final String hexString = "8080:8080:8080";
        public static final short index = 23;
        public static final short[] triplet = {128, 128, 128};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 23;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class RED extends HSSFColor {
        public static final String hexString = "FFFF:0:0";
        public static final short index = 10;
        public static final short[] triplet = {255, 0, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 10;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class LIGHT_ORANGE extends HSSFColor {
        public static final String hexString = "FFFF:9999:0";
        public static final short index = 52;
        public static final short[] triplet = {255, EscherAggregate.ST_TEXTCURVEDOWN, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 52;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class LIME extends HSSFColor {
        public static final String hexString = "9999:CCCC:0";
        public static final short index = 50;
        public static final short[] triplet = {EscherAggregate.ST_TEXTCURVEDOWN, 204, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 50;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class SEA_GREEN extends HSSFColor {
        public static final String hexString = "3333:9999:6666";
        public static final short index = 57;
        public static final short[] triplet = {51, EscherAggregate.ST_TEXTCURVEDOWN, EscherAggregate.ST_CURVEDRIGHTARROW};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 57;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class AQUA extends HSSFColor {
        public static final String hexString = "3333:CCCC:CCCC";
        public static final short index = 49;
        public static final short[] triplet = {51, 204, 204};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 49;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class LIGHT_BLUE extends HSSFColor {
        public static final String hexString = "3333:6666:FFFF";
        public static final short index = 48;
        public static final short[] triplet = {51, EscherAggregate.ST_CURVEDRIGHTARROW, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 48;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class VIOLET extends HSSFColor {
        public static final String hexString = "8080:0:8080";
        public static final short index = 20;
        public static final short index2 = 36;
        public static final short[] triplet = {128, 0, 128};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 20;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class GREY_40_PERCENT extends HSSFColor {
        public static final String hexString = "9696:9696:9696";
        public static final short index = 55;
        public static final short[] triplet = {EscherAggregate.ST_TEXTCIRCLEPOUR, EscherAggregate.ST_TEXTCIRCLEPOUR, EscherAggregate.ST_TEXTCIRCLEPOUR};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 55;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class PINK extends HSSFColor {
        public static final String hexString = "FFFF:0:FFFF";
        public static final short index = 14;
        public static final short index2 = 33;
        public static final short[] triplet = {255, 0, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 14;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class GOLD extends HSSFColor {
        public static final String hexString = "FFFF:CCCC:0";
        public static final short index = 51;
        public static final short[] triplet = {255, 204, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 51;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class YELLOW extends HSSFColor {
        public static final String hexString = "FFFF:FFFF:0";
        public static final short index = 13;
        public static final short index2 = 34;
        public static final short[] triplet = {255, 255, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 13;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class BRIGHT_GREEN extends HSSFColor {
        public static final String hexString = "0:FFFF:0";
        public static final short index = 11;
        public static final short index2 = 35;
        public static final short[] triplet = {0, 255, 0};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 11;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class TURQUOISE extends HSSFColor {
        public static final String hexString = "0:FFFF:FFFF";
        public static final short index = 15;
        public static final short index2 = 35;
        public static final short[] triplet = {0, 255, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 15;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class SKY_BLUE extends HSSFColor {
        public static final String hexString = "0:CCCC:FFFF";
        public static final short index = 40;
        public static final short[] triplet = {0, 204, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 40;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class PLUM extends HSSFColor {
        public static final String hexString = "9999:3333:6666";
        public static final short index = 61;
        public static final short index2 = 25;
        public static final short[] triplet = {EscherAggregate.ST_TEXTCURVEDOWN, 51, EscherAggregate.ST_CURVEDRIGHTARROW};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return "9999:3333:6666";
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 61;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class GREY_25_PERCENT extends HSSFColor {
        public static final String hexString = "C0C0:C0C0:C0C0";
        public static final short index = 22;
        public static final short[] triplet = {192, 192, 192};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 22;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class ROSE extends HSSFColor {
        public static final String hexString = "FFFF:9999:CCCC";
        public static final short index = 45;
        public static final short[] triplet = {255, EscherAggregate.ST_TEXTCURVEDOWN, 204};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 45;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class TAN extends HSSFColor {
        public static final String hexString = "FFFF:CCCC:9999";
        public static final short index = 47;
        public static final short[] triplet = {255, 204, EscherAggregate.ST_TEXTCURVEDOWN};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 47;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class LIGHT_YELLOW extends HSSFColor {
        public static final String hexString = "FFFF:FFFF:9999";
        public static final short index = 43;
        public static final short[] triplet = {255, 255, EscherAggregate.ST_TEXTCURVEDOWN};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 43;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class LIGHT_GREEN extends HSSFColor {
        public static final String hexString = "CCCC:FFFF:CCCC";
        public static final short index = 42;
        public static final short[] triplet = {204, 255, 204};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 42;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class LIGHT_TURQUOISE extends HSSFColor {
        public static final String hexString = "CCCC:FFFF:FFFF";
        public static final short index = 41;
        public static final short index2 = 27;
        public static final short[] triplet = {204, 255, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 41;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class PALE_BLUE extends HSSFColor {
        public static final String hexString = "9999:CCCC:FFFF";
        public static final short index = 44;
        public static final short[] triplet = {EscherAggregate.ST_TEXTCURVEDOWN, 204, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 44;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class LAVENDER extends HSSFColor {
        public static final String hexString = "CCCC:9999:FFFF";
        public static final short index = 46;
        public static final short[] triplet = {204, EscherAggregate.ST_TEXTCURVEDOWN, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 46;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class WHITE extends HSSFColor {
        public static final String hexString = "FFFF:FFFF:FFFF";
        public static final short index = 9;
        public static final short[] triplet = {255, 255, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 9;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class CORNFLOWER_BLUE extends HSSFColor {
        public static final String hexString = "9999:9999:FFFF";
        public static final short index = 24;
        public static final short[] triplet = {EscherAggregate.ST_TEXTCURVEDOWN, EscherAggregate.ST_TEXTCURVEDOWN, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 24;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class LEMON_CHIFFON extends HSSFColor {
        public static final String hexString = "FFFF:FFFF:CCCC";
        public static final short index = 26;
        public static final short[] triplet = {255, 255, 204};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 26;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class MAROON extends HSSFColor {
        public static final String hexString = "9999:3333:6666";
        public static final short index = 25;
        public static final short[] triplet = {EscherAggregate.ST_TEXTCURVEDOWN, 51, EscherAggregate.ST_CURVEDRIGHTARROW};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return "9999:3333:6666";
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 25;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class ORCHID extends HSSFColor {
        public static final String hexString = "6666:0:6666";
        public static final short index = 28;
        public static final short[] triplet = {EscherAggregate.ST_CURVEDRIGHTARROW, 0, EscherAggregate.ST_CURVEDRIGHTARROW};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 28;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class CORAL extends HSSFColor {
        public static final String hexString = "FFFF:8080:8080";
        public static final short index = 29;
        public static final short[] triplet = {255, 128, 128};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 29;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class ROYAL_BLUE extends HSSFColor {
        public static final String hexString = "0:6666:CCCC";
        public static final short index = 30;
        public static final short[] triplet = {0, EscherAggregate.ST_CURVEDRIGHTARROW, 204};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 30;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }

    public static final class LIGHT_CORNFLOWER_BLUE extends HSSFColor {
        public static final String hexString = "CCCC:CCCC:FFFF";
        public static final short index = 31;
        public static final short[] triplet = {204, 204, 255};

        @Override // org.apache.poi.hssf.util.HSSFColor
        public String getHexString() {
            return hexString;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short getIndex() {
            return (short) 31;
        }

        @Override // org.apache.poi.hssf.util.HSSFColor
        public short[] getTriplet() {
            return triplet;
        }
    }
}
