package org.apache.commons.net.nntp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.io.DotTerminatedMessageReader;
import org.apache.commons.net.io.DotTerminatedMessageWriter;
import org.apache.commons.net.io.Util;

/* JADX INFO: loaded from: classes3.dex */
public class NNTPClient extends NNTP {
    private void __parseArticlePointer(String str, ArticleInfo articleInfo) throws MalformedServerReplyException {
        String[] strArrSplit = str.split(" ");
        if (strArrSplit.length >= 3) {
            try {
                articleInfo.articleNumber = Long.parseLong(strArrSplit[1]);
                articleInfo.articleId = strArrSplit[2];
                return;
            } catch (NumberFormatException unused) {
            }
        }
        throw new MalformedServerReplyException("Could not parse article pointer.\nServer reply: " + str);
    }

    private static void __parseGroupReply(String str, NewsgroupInfo newsgroupInfo) throws MalformedServerReplyException {
        String[] strArrSplit = str.split(" ");
        if (strArrSplit.length >= 5) {
            try {
                newsgroupInfo._setArticleCount(Long.parseLong(strArrSplit[1]));
                newsgroupInfo._setFirstArticle(Long.parseLong(strArrSplit[2]));
                newsgroupInfo._setLastArticle(Long.parseLong(strArrSplit[3]));
                newsgroupInfo._setNewsgroup(strArrSplit[4]);
                newsgroupInfo._setPostingPermission(0);
                return;
            } catch (NumberFormatException unused) {
            }
        }
        throw new MalformedServerReplyException("Could not parse newsgroup info.\nServer reply: " + str);
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x006b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static org.apache.commons.net.nntp.NewsgroupInfo __parseNewsgroupListEntry(java.lang.String r12) {
        /*
            java.lang.String r0 = " "
            java.lang.String[] r12 = r12.split(r0)
            int r0 = r12.length
            r1 = 0
            r2 = 4
            if (r0 >= r2) goto Lc
            return r1
        Lc:
            org.apache.commons.net.nntp.NewsgroupInfo r0 = new org.apache.commons.net.nntp.NewsgroupInfo
            r0.<init>()
            r2 = 0
            r3 = r12[r2]
            r0._setNewsgroup(r3)
            r3 = 1
            r4 = r12[r3]     // Catch: java.lang.NumberFormatException -> L6f
            long r4 = java.lang.Long.parseLong(r4)     // Catch: java.lang.NumberFormatException -> L6f
            r6 = 2
            r7 = r12[r6]     // Catch: java.lang.NumberFormatException -> L6f
            long r7 = java.lang.Long.parseLong(r7)     // Catch: java.lang.NumberFormatException -> L6f
            r0._setFirstArticle(r7)     // Catch: java.lang.NumberFormatException -> L6f
            r0._setLastArticle(r4)     // Catch: java.lang.NumberFormatException -> L6f
            r9 = 0
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L39
            int r11 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r11 != 0) goto L39
            r0._setArticleCount(r9)     // Catch: java.lang.NumberFormatException -> L6f
            goto L40
        L39:
            long r4 = r4 - r7
            r7 = 1
            long r4 = r4 + r7
            r0._setArticleCount(r4)     // Catch: java.lang.NumberFormatException -> L6f
        L40:
            r1 = 3
            r12 = r12[r1]
            char r12 = r12.charAt(r2)
            r4 = 77
            if (r12 == r4) goto L6b
            r4 = 78
            if (r12 == r4) goto L67
            r4 = 89
            if (r12 == r4) goto L63
            r4 = 121(0x79, float:1.7E-43)
            if (r12 == r4) goto L63
            r4 = 109(0x6d, float:1.53E-43)
            if (r12 == r4) goto L6b
            r3 = 110(0x6e, float:1.54E-43)
            if (r12 == r3) goto L67
            r0._setPostingPermission(r2)
            goto L6e
        L63:
            r0._setPostingPermission(r6)
            goto L6e
        L67:
            r0._setPostingPermission(r1)
            goto L6e
        L6b:
            r0._setPostingPermission(r3)
        L6e:
            return r0
        L6f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.net.nntp.NNTPClient.__parseNewsgroupListEntry(java.lang.String):org.apache.commons.net.nntp.NewsgroupInfo");
    }

    static Article __parseArticleEntry(String str) {
        Article article = new Article();
        article.setSubject(str);
        String[] strArrSplit = str.split("\t");
        if (strArrSplit.length > 6) {
            try {
                article.setArticleNumber(Long.parseLong(strArrSplit[0]));
                article.setSubject(strArrSplit[1]);
                article.setFrom(strArrSplit[2]);
                article.setDate(strArrSplit[3]);
                article.setArticleId(strArrSplit[4]);
                article.addReference(strArrSplit[5]);
            } catch (NumberFormatException unused) {
            }
        }
        return article;
    }

    private NewsgroupInfo[] __readNewsgroupListing() throws IOException {
        DotTerminatedMessageReader dotTerminatedMessageReader = new DotTerminatedMessageReader(this._reader_);
        Vector vector = new Vector(2048);
        while (true) {
            try {
                String line = dotTerminatedMessageReader.readLine();
                if (line != null) {
                    NewsgroupInfo newsgroupInfo__parseNewsgroupListEntry = __parseNewsgroupListEntry(line);
                    if (newsgroupInfo__parseNewsgroupListEntry != null) {
                        vector.addElement(newsgroupInfo__parseNewsgroupListEntry);
                    } else {
                        throw new MalformedServerReplyException(line);
                    }
                } else {
                    dotTerminatedMessageReader.close();
                    int size = vector.size();
                    if (size < 1) {
                        return new NewsgroupInfo[0];
                    }
                    NewsgroupInfo[] newsgroupInfoArr = new NewsgroupInfo[size];
                    vector.copyInto(newsgroupInfoArr);
                    return newsgroupInfoArr;
                }
            } catch (Throwable th) {
                dotTerminatedMessageReader.close();
                throw th;
            }
        }
    }

    private BufferedReader __retrieve(int i, String str, ArticleInfo articleInfo) throws IOException {
        if (str != null) {
            if (!NNTPReply.isPositiveCompletion(sendCommand(i, str))) {
                return null;
            }
        } else if (!NNTPReply.isPositiveCompletion(sendCommand(i))) {
            return null;
        }
        if (articleInfo != null) {
            __parseArticlePointer(getReplyString(), articleInfo);
        }
        return new DotTerminatedMessageReader(this._reader_);
    }

    private BufferedReader __retrieve(int i, long j, ArticleInfo articleInfo) throws IOException {
        if (!NNTPReply.isPositiveCompletion(sendCommand(i, Long.toString(j)))) {
            return null;
        }
        if (articleInfo != null) {
            __parseArticlePointer(getReplyString(), articleInfo);
        }
        return new DotTerminatedMessageReader(this._reader_);
    }

    public BufferedReader retrieveArticle(String str, ArticleInfo articleInfo) throws IOException {
        return __retrieve(0, str, articleInfo);
    }

    public Reader retrieveArticle(String str) throws IOException {
        return retrieveArticle(str, (ArticleInfo) null);
    }

    public Reader retrieveArticle() throws IOException {
        return retrieveArticle((String) null);
    }

    public BufferedReader retrieveArticle(long j, ArticleInfo articleInfo) throws IOException {
        return __retrieve(0, j, articleInfo);
    }

    public BufferedReader retrieveArticle(long j) throws IOException {
        return retrieveArticle(j, (ArticleInfo) null);
    }

    public BufferedReader retrieveArticleHeader(String str, ArticleInfo articleInfo) throws IOException {
        return __retrieve(3, str, articleInfo);
    }

    public Reader retrieveArticleHeader(String str) throws IOException {
        return retrieveArticleHeader(str, (ArticleInfo) null);
    }

    public Reader retrieveArticleHeader() throws IOException {
        return retrieveArticleHeader((String) null);
    }

    public BufferedReader retrieveArticleHeader(long j, ArticleInfo articleInfo) throws IOException {
        return __retrieve(3, j, articleInfo);
    }

    public BufferedReader retrieveArticleHeader(long j) throws IOException {
        return retrieveArticleHeader(j, (ArticleInfo) null);
    }

    public BufferedReader retrieveArticleBody(String str, ArticleInfo articleInfo) throws IOException {
        return __retrieve(1, str, articleInfo);
    }

    public Reader retrieveArticleBody(String str) throws IOException {
        return retrieveArticleBody(str, (ArticleInfo) null);
    }

    public Reader retrieveArticleBody() throws IOException {
        return retrieveArticleBody((String) null);
    }

    public BufferedReader retrieveArticleBody(long j, ArticleInfo articleInfo) throws IOException {
        return __retrieve(1, j, articleInfo);
    }

    public BufferedReader retrieveArticleBody(long j) throws IOException {
        return retrieveArticleBody(j, (ArticleInfo) null);
    }

    public boolean selectNewsgroup(String str, NewsgroupInfo newsgroupInfo) throws IOException {
        if (!NNTPReply.isPositiveCompletion(group(str))) {
            return false;
        }
        if (newsgroupInfo == null) {
            return true;
        }
        __parseGroupReply(getReplyString(), newsgroupInfo);
        return true;
    }

    public boolean selectNewsgroup(String str) throws IOException {
        return selectNewsgroup(str, null);
    }

    public String listHelp() throws IOException {
        if (!NNTPReply.isInformational(help())) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        DotTerminatedMessageReader dotTerminatedMessageReader = new DotTerminatedMessageReader(this._reader_);
        Util.copyReader(dotTerminatedMessageReader, stringWriter);
        dotTerminatedMessageReader.close();
        stringWriter.close();
        return stringWriter.toString();
    }

    public String[] listOverviewFmt() throws IOException {
        if (!NNTPReply.isPositiveCompletion(sendCommand("LIST", "OVERVIEW.FMT"))) {
            return null;
        }
        DotTerminatedMessageReader dotTerminatedMessageReader = new DotTerminatedMessageReader(this._reader_);
        ArrayList arrayList = new ArrayList();
        while (true) {
            String line = dotTerminatedMessageReader.readLine();
            if (line != null) {
                arrayList.add(line);
            } else {
                dotTerminatedMessageReader.close();
                return (String[]) arrayList.toArray(new String[arrayList.size()]);
            }
        }
    }

    public boolean selectArticle(String str, ArticleInfo articleInfo) throws IOException {
        if (str != null) {
            if (!NNTPReply.isPositiveCompletion(stat(str))) {
                return false;
            }
        } else if (!NNTPReply.isPositiveCompletion(stat())) {
            return false;
        }
        if (articleInfo == null) {
            return true;
        }
        __parseArticlePointer(getReplyString(), articleInfo);
        return true;
    }

    public boolean selectArticle(String str) throws IOException {
        return selectArticle(str, (ArticleInfo) null);
    }

    public boolean selectArticle(ArticleInfo articleInfo) throws IOException {
        return selectArticle((String) null, articleInfo);
    }

    public boolean selectArticle(long j, ArticleInfo articleInfo) throws IOException {
        if (!NNTPReply.isPositiveCompletion(stat(j))) {
            return false;
        }
        if (articleInfo == null) {
            return true;
        }
        __parseArticlePointer(getReplyString(), articleInfo);
        return true;
    }

    public boolean selectArticle(long j) throws IOException {
        return selectArticle(j, (ArticleInfo) null);
    }

    public boolean selectPreviousArticle(ArticleInfo articleInfo) throws IOException {
        if (!NNTPReply.isPositiveCompletion(last())) {
            return false;
        }
        if (articleInfo == null) {
            return true;
        }
        __parseArticlePointer(getReplyString(), articleInfo);
        return true;
    }

    public boolean selectPreviousArticle() throws IOException {
        return selectPreviousArticle((ArticleInfo) null);
    }

    public boolean selectNextArticle(ArticleInfo articleInfo) throws IOException {
        if (!NNTPReply.isPositiveCompletion(next())) {
            return false;
        }
        if (articleInfo == null) {
            return true;
        }
        __parseArticlePointer(getReplyString(), articleInfo);
        return true;
    }

    public boolean selectNextArticle() throws IOException {
        return selectNextArticle((ArticleInfo) null);
    }

    public NewsgroupInfo[] listNewsgroups() throws IOException {
        if (NNTPReply.isPositiveCompletion(list())) {
            return __readNewsgroupListing();
        }
        return null;
    }

    public Iterable<String> iterateNewsgroupListing() throws IOException {
        if (NNTPReply.isPositiveCompletion(list())) {
            return new ReplyIterator(this._reader_);
        }
        throw new IOException("LIST command failed: " + getReplyString());
    }

    public Iterable<NewsgroupInfo> iterateNewsgroups() throws IOException {
        return new NewsgroupIterator(iterateNewsgroupListing());
    }

    public NewsgroupInfo[] listNewsgroups(String str) throws IOException {
        if (NNTPReply.isPositiveCompletion(listActive(str))) {
            return __readNewsgroupListing();
        }
        return null;
    }

    public Iterable<String> iterateNewsgroupListing(String str) throws IOException {
        if (NNTPReply.isPositiveCompletion(listActive(str))) {
            return new ReplyIterator(this._reader_);
        }
        throw new IOException("LIST ACTIVE " + str + " command failed: " + getReplyString());
    }

    public Iterable<NewsgroupInfo> iterateNewsgroups(String str) throws IOException {
        return new NewsgroupIterator(iterateNewsgroupListing(str));
    }

    public NewsgroupInfo[] listNewNewsgroups(NewGroupsOrNewsQuery newGroupsOrNewsQuery) throws IOException {
        if (NNTPReply.isPositiveCompletion(newgroups(newGroupsOrNewsQuery.getDate(), newGroupsOrNewsQuery.getTime(), newGroupsOrNewsQuery.isGMT(), newGroupsOrNewsQuery.getDistributions()))) {
            return __readNewsgroupListing();
        }
        return null;
    }

    public Iterable<String> iterateNewNewsgroupListing(NewGroupsOrNewsQuery newGroupsOrNewsQuery) throws IOException {
        if (NNTPReply.isPositiveCompletion(newgroups(newGroupsOrNewsQuery.getDate(), newGroupsOrNewsQuery.getTime(), newGroupsOrNewsQuery.isGMT(), newGroupsOrNewsQuery.getDistributions()))) {
            return new ReplyIterator(this._reader_);
        }
        throw new IOException("NEWGROUPS command failed: " + getReplyString());
    }

    public Iterable<NewsgroupInfo> iterateNewNewsgroups(NewGroupsOrNewsQuery newGroupsOrNewsQuery) throws IOException {
        return new NewsgroupIterator(iterateNewNewsgroupListing(newGroupsOrNewsQuery));
    }

    public String[] listNewNews(NewGroupsOrNewsQuery newGroupsOrNewsQuery) throws IOException {
        if (!NNTPReply.isPositiveCompletion(newnews(newGroupsOrNewsQuery.getNewsgroups(), newGroupsOrNewsQuery.getDate(), newGroupsOrNewsQuery.getTime(), newGroupsOrNewsQuery.isGMT(), newGroupsOrNewsQuery.getDistributions()))) {
            return null;
        }
        Vector vector = new Vector();
        DotTerminatedMessageReader dotTerminatedMessageReader = new DotTerminatedMessageReader(this._reader_);
        while (true) {
            try {
                String line = dotTerminatedMessageReader.readLine();
                if (line == null) {
                    break;
                }
                vector.addElement(line);
            } catch (Throwable th) {
                dotTerminatedMessageReader.close();
                throw th;
            }
        }
        dotTerminatedMessageReader.close();
        int size = vector.size();
        if (size < 1) {
            return new String[0];
        }
        String[] strArr = new String[size];
        vector.copyInto(strArr);
        return strArr;
    }

    public Iterable<String> iterateNewNews(NewGroupsOrNewsQuery newGroupsOrNewsQuery) throws IOException {
        if (NNTPReply.isPositiveCompletion(newnews(newGroupsOrNewsQuery.getNewsgroups(), newGroupsOrNewsQuery.getDate(), newGroupsOrNewsQuery.getTime(), newGroupsOrNewsQuery.isGMT(), newGroupsOrNewsQuery.getDistributions()))) {
            return new ReplyIterator(this._reader_);
        }
        throw new IOException("NEWNEWS command failed: " + getReplyString());
    }

    public boolean completePendingCommand() throws IOException {
        return NNTPReply.isPositiveCompletion(getReply());
    }

    public Writer postArticle() throws IOException {
        if (NNTPReply.isPositiveIntermediate(post())) {
            return new DotTerminatedMessageWriter(this._writer_);
        }
        return null;
    }

    public Writer forwardArticle(String str) throws IOException {
        if (NNTPReply.isPositiveIntermediate(ihave(str))) {
            return new DotTerminatedMessageWriter(this._writer_);
        }
        return null;
    }

    public boolean logout() throws IOException {
        return NNTPReply.isPositiveCompletion(quit());
    }

    public boolean authenticate(String str, String str2) throws IOException {
        if (authinfoUser(str) != 381 || authinfoPass(str2) != 281) {
            return false;
        }
        this._isAllowedToPost = true;
        return true;
    }

    private BufferedReader __retrieveArticleInfo(String str) throws IOException {
        if (NNTPReply.isPositiveCompletion(xover(str))) {
            return new DotTerminatedMessageReader(this._reader_);
        }
        return null;
    }

    public BufferedReader retrieveArticleInfo(long j) throws IOException {
        return __retrieveArticleInfo(Long.toString(j));
    }

    public BufferedReader retrieveArticleInfo(long j, long j2) throws IOException {
        return __retrieveArticleInfo(j + "-" + j2);
    }

    public Iterable<Article> iterateArticleInfo(long j, long j2) throws IOException {
        BufferedReader bufferedReaderRetrieveArticleInfo = retrieveArticleInfo(j, j2);
        if (bufferedReaderRetrieveArticleInfo == null) {
            throw new IOException("XOVER command failed: " + getReplyString());
        }
        return new ArticleIterator(new ReplyIterator(bufferedReaderRetrieveArticleInfo, false));
    }

    private BufferedReader __retrieveHeader(String str, String str2) throws IOException {
        if (NNTPReply.isPositiveCompletion(xhdr(str, str2))) {
            return new DotTerminatedMessageReader(this._reader_);
        }
        return null;
    }

    public BufferedReader retrieveHeader(String str, long j) throws IOException {
        return __retrieveHeader(str, Long.toString(j));
    }

    public BufferedReader retrieveHeader(String str, long j, long j2) throws IOException {
        return __retrieveHeader(str, j + "-" + j2);
    }

    @Deprecated
    public Reader retrieveHeader(String str, int i, int i2) throws IOException {
        return retrieveHeader(str, i, i2);
    }

    @Deprecated
    public Reader retrieveArticleInfo(int i, int i2) throws IOException {
        return retrieveArticleInfo(i, i2);
    }

    @Deprecated
    public Reader retrieveHeader(String str, int i) throws IOException {
        return retrieveHeader(str, i);
    }

    @Deprecated
    public boolean selectArticle(int i, ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        boolean zSelectArticle = selectArticle(i, articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return zSelectArticle;
    }

    @Deprecated
    public Reader retrieveArticleInfo(int i) throws IOException {
        return retrieveArticleInfo(i);
    }

    @Deprecated
    public boolean selectArticle(int i) throws IOException {
        return selectArticle(i);
    }

    @Deprecated
    public Reader retrieveArticleHeader(int i) throws IOException {
        return retrieveArticleHeader(i);
    }

    @Deprecated
    public Reader retrieveArticleHeader(int i, ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        BufferedReader bufferedReaderRetrieveArticleHeader = retrieveArticleHeader(i, articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return bufferedReaderRetrieveArticleHeader;
    }

    @Deprecated
    public Reader retrieveArticleBody(int i) throws IOException {
        return retrieveArticleBody(i);
    }

    @Deprecated
    public Reader retrieveArticle(int i, ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        BufferedReader bufferedReaderRetrieveArticle = retrieveArticle(i, articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return bufferedReaderRetrieveArticle;
    }

    @Deprecated
    public Reader retrieveArticle(int i) throws IOException {
        return retrieveArticle(i);
    }

    @Deprecated
    public Reader retrieveArticleBody(int i, ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        BufferedReader bufferedReaderRetrieveArticleBody = retrieveArticleBody(i, articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return bufferedReaderRetrieveArticleBody;
    }

    @Deprecated
    public Reader retrieveArticle(String str, ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        BufferedReader bufferedReaderRetrieveArticle = retrieveArticle(str, articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return bufferedReaderRetrieveArticle;
    }

    @Deprecated
    public Reader retrieveArticleBody(String str, ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        BufferedReader bufferedReaderRetrieveArticleBody = retrieveArticleBody(str, articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return bufferedReaderRetrieveArticleBody;
    }

    @Deprecated
    public Reader retrieveArticleHeader(String str, ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        BufferedReader bufferedReaderRetrieveArticleHeader = retrieveArticleHeader(str, articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return bufferedReaderRetrieveArticleHeader;
    }

    @Deprecated
    public boolean selectArticle(String str, ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        boolean zSelectArticle = selectArticle(str, articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return zSelectArticle;
    }

    @Deprecated
    public boolean selectArticle(ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        boolean zSelectArticle = selectArticle(articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return zSelectArticle;
    }

    @Deprecated
    public boolean selectNextArticle(ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        boolean zSelectNextArticle = selectNextArticle(articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return zSelectNextArticle;
    }

    @Deprecated
    public boolean selectPreviousArticle(ArticlePointer articlePointer) throws IOException {
        ArticleInfo articleInfo__ap2ai = __ap2ai(articlePointer);
        boolean zSelectPreviousArticle = selectPreviousArticle(articleInfo__ap2ai);
        __ai2ap(articleInfo__ap2ai, articlePointer);
        return zSelectPreviousArticle;
    }

    private ArticleInfo __ap2ai(ArticlePointer articlePointer) {
        if (articlePointer == null) {
            return null;
        }
        return new ArticleInfo();
    }

    private void __ai2ap(ArticleInfo articleInfo, ArticlePointer articlePointer) {
        if (articlePointer != null) {
            articlePointer.articleId = articleInfo.articleId;
            articlePointer.articleNumber = (int) articleInfo.articleNumber;
        }
    }
}
