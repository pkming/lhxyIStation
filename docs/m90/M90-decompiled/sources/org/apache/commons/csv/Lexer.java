package org.apache.commons.csv;

import java.io.Closeable;
import java.io.IOException;
import org.apache.commons.csv.Token;

/* JADX INFO: loaded from: classes3.dex */
final class Lexer implements Closeable {
    private static final char DISABLED = 65534;
    private final char commentStart;
    private final char delimiter;
    private final char escape;
    private String firstEol;
    private final boolean ignoreEmptyLines;
    private final boolean ignoreSurroundingSpaces;
    private final char quoteChar;
    private final ExtendedBufferedReader reader;
    private static final String CR_STRING = Character.toString('\r');
    private static final String LF_STRING = Character.toString('\n');

    boolean isEndOfFile(int i) {
        return i == -1;
    }

    boolean isStartOfLine(int i) {
        return i == 10 || i == 13 || i == -2;
    }

    String getFirstEol() {
        return this.firstEol;
    }

    Lexer(CSVFormat cSVFormat, ExtendedBufferedReader extendedBufferedReader) {
        this.reader = extendedBufferedReader;
        this.delimiter = cSVFormat.getDelimiter();
        this.escape = mapNullToDisabled(cSVFormat.getEscapeCharacter());
        this.quoteChar = mapNullToDisabled(cSVFormat.getQuoteCharacter());
        this.commentStart = mapNullToDisabled(cSVFormat.getCommentMarker());
        this.ignoreSurroundingSpaces = cSVFormat.getIgnoreSurroundingSpaces();
        this.ignoreEmptyLines = cSVFormat.getIgnoreEmptyLines();
    }

    Token nextToken(Token token) throws IOException {
        int lastChar = this.reader.getLastChar();
        int i = this.reader.read();
        boolean endOfLine = readEndOfLine(i);
        if (this.ignoreEmptyLines) {
            while (endOfLine && isStartOfLine(lastChar)) {
                int i2 = this.reader.read();
                endOfLine = readEndOfLine(i2);
                if (isEndOfFile(i2)) {
                    token.type = Token.Type.EOF;
                    return token;
                }
                int i3 = i;
                i = i2;
                lastChar = i3;
            }
        }
        if (isEndOfFile(lastChar) || (!isDelimiter(lastChar) && isEndOfFile(i))) {
            token.type = Token.Type.EOF;
            return token;
        }
        if (isStartOfLine(lastChar) && isCommentStart(i)) {
            String line = this.reader.readLine();
            if (line == null) {
                token.type = Token.Type.EOF;
                return token;
            }
            token.content.append(line.trim());
            token.type = Token.Type.COMMENT;
            return token;
        }
        while (token.type == Token.Type.INVALID) {
            if (this.ignoreSurroundingSpaces) {
                while (isWhitespace(i) && !endOfLine) {
                    i = this.reader.read();
                    endOfLine = readEndOfLine(i);
                }
            }
            if (isDelimiter(i)) {
                token.type = Token.Type.TOKEN;
            } else if (endOfLine) {
                token.type = Token.Type.EORECORD;
            } else if (isQuoteChar(i)) {
                parseEncapsulatedToken(token);
            } else if (isEndOfFile(i)) {
                token.type = Token.Type.EOF;
                token.isReady = true;
            } else {
                parseSimpleToken(token, i);
            }
        }
        return token;
    }

    private Token parseSimpleToken(Token token, int i) throws IOException {
        while (true) {
            if (readEndOfLine(i)) {
                token.type = Token.Type.EORECORD;
                break;
            }
            if (isEndOfFile(i)) {
                token.type = Token.Type.EOF;
                token.isReady = true;
                break;
            }
            if (isDelimiter(i)) {
                token.type = Token.Type.TOKEN;
                break;
            }
            if (isEscape(i)) {
                int escape = readEscape();
                if (escape == -1) {
                    token.content.append((char) i).append((char) this.reader.getLastChar());
                } else {
                    token.content.append((char) escape);
                }
                i = this.reader.read();
            } else {
                token.content.append((char) i);
                i = this.reader.read();
            }
        }
        if (this.ignoreSurroundingSpaces) {
            trimTrailingSpaces(token.content);
        }
        return token;
    }

    private Token parseEncapsulatedToken(Token token) throws IOException {
        int i;
        long currentLineNumber = getCurrentLineNumber();
        while (true) {
            int i2 = this.reader.read();
            if (isEscape(i2)) {
                int escape = readEscape();
                if (escape == -1) {
                    token.content.append((char) i2).append((char) this.reader.getLastChar());
                } else {
                    token.content.append((char) escape);
                }
            } else if (isQuoteChar(i2)) {
                if (isQuoteChar(this.reader.lookAhead())) {
                    token.content.append((char) this.reader.read());
                } else {
                    do {
                        i = this.reader.read();
                        if (isDelimiter(i)) {
                            token.type = Token.Type.TOKEN;
                            return token;
                        }
                        if (isEndOfFile(i)) {
                            token.type = Token.Type.EOF;
                            token.isReady = true;
                            return token;
                        }
                        if (readEndOfLine(i)) {
                            token.type = Token.Type.EORECORD;
                            return token;
                        }
                    } while (isWhitespace(i));
                    throw new IOException("(line " + getCurrentLineNumber() + ") invalid char between encapsulated token and delimiter");
                }
            } else {
                if (isEndOfFile(i2)) {
                    throw new IOException("(startline " + currentLineNumber + ") EOF reached before encapsulated token finished");
                }
                token.content.append((char) i2);
            }
        }
    }

    private char mapNullToDisabled(Character ch) {
        return ch == null ? DISABLED : ch.charValue();
    }

    long getCurrentLineNumber() {
        return this.reader.getCurrentLineNumber();
    }

    long getCharacterPosition() {
        return this.reader.getPosition();
    }

    int readEscape() throws IOException {
        int i = this.reader.read();
        if (i == -1) {
            throw new IOException("EOF whilst processing escape sequence");
        }
        if (i == 98) {
            return 8;
        }
        if (i == 102) {
            return 12;
        }
        if (i == 110) {
            return 10;
        }
        if (i == 114) {
            return 13;
        }
        if (i == 116) {
            return 9;
        }
        if (i != 12 && i != 13) {
            switch (i) {
                case 8:
                case 9:
                case 10:
                    break;
                default:
                    if (isMetaChar(i)) {
                        return i;
                    }
                    return -1;
            }
        }
        return i;
    }

    void trimTrailingSpaces(StringBuilder sb) {
        int length = sb.length();
        while (length > 0) {
            int i = length - 1;
            if (!Character.isWhitespace(sb.charAt(i))) {
                break;
            } else {
                length = i;
            }
        }
        if (length != sb.length()) {
            sb.setLength(length);
        }
    }

    boolean readEndOfLine(int i) throws IOException {
        if (i == 13 && this.reader.lookAhead() == 10) {
            i = this.reader.read();
            if (this.firstEol == null) {
                this.firstEol = "\r\n";
            }
        }
        if (this.firstEol == null) {
            if (i == 10) {
                this.firstEol = LF_STRING;
            } else if (i == 13) {
                this.firstEol = CR_STRING;
            }
        }
        return i == 10 || i == 13;
    }

    boolean isClosed() {
        return this.reader.isClosed();
    }

    boolean isWhitespace(int i) {
        return !isDelimiter(i) && Character.isWhitespace((char) i);
    }

    boolean isDelimiter(int i) {
        return i == this.delimiter;
    }

    boolean isEscape(int i) {
        return i == this.escape;
    }

    boolean isQuoteChar(int i) {
        return i == this.quoteChar;
    }

    boolean isCommentStart(int i) {
        return i == this.commentStart;
    }

    private boolean isMetaChar(int i) {
        return i == this.delimiter || i == this.escape || i == this.quoteChar || i == this.commentStart;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.reader.close();
    }
}
