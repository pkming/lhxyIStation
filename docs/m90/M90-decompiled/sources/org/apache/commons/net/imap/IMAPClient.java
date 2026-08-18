package org.apache.commons.net.imap;

import java.io.IOException;
import org.apache.commons.net.imap.IMAP;

/* JADX INFO: loaded from: classes3.dex */
public class IMAPClient extends IMAP {

    public enum FETCH_ITEM_NAMES {
        ALL,
        FAST,
        FULL,
        BODY,
        BODYSTRUCTURE,
        ENVELOPE,
        FLAGS,
        INTERNALDATE,
        RFC822,
        UID
    }

    public enum SEARCH_CRITERIA {
        ALL,
        ANSWERED,
        BCC,
        BEFORE,
        BODY,
        CC,
        DELETED,
        DRAFT,
        FLAGGED,
        FROM,
        HEADER,
        KEYWORD,
        LARGER,
        NEW,
        NOT,
        OLD,
        ON,
        OR,
        RECENT,
        SEEN,
        SENTBEFORE,
        SENTON,
        SENTSINCE,
        SINCE,
        SMALLER,
        SUBJECT,
        TEXT,
        TO,
        UID,
        UNANSWERED,
        UNDELETED,
        UNDRAFT,
        UNFLAGGED,
        UNKEYWORD,
        UNSEEN
    }

    public enum STATUS_DATA_ITEMS {
        MESSAGES,
        RECENT,
        UIDNEXT,
        UIDVALIDITY,
        UNSEEN
    }

    public boolean capability() throws IOException {
        return doCommand(IMAPCommand.CAPABILITY);
    }

    public boolean noop() throws IOException {
        return doCommand(IMAPCommand.NOOP);
    }

    public boolean logout() throws IOException {
        return doCommand(IMAPCommand.LOGOUT);
    }

    public boolean login(String str, String str2) throws IOException {
        if (getState() != IMAP.IMAPState.NOT_AUTH_STATE || !doCommand(IMAPCommand.LOGIN, str + " " + str2)) {
            return false;
        }
        setState(IMAP.IMAPState.AUTH_STATE);
        return true;
    }

    public boolean select(String str) throws IOException {
        return doCommand(IMAPCommand.SELECT, str);
    }

    public boolean examine(String str) throws IOException {
        return doCommand(IMAPCommand.EXAMINE, str);
    }

    public boolean create(String str) throws IOException {
        return doCommand(IMAPCommand.CREATE, str);
    }

    public boolean delete(String str) throws IOException {
        return doCommand(IMAPCommand.DELETE, str);
    }

    public boolean rename(String str, String str2) throws IOException {
        return doCommand(IMAPCommand.RENAME, str + " " + str2);
    }

    public boolean subscribe(String str) throws IOException {
        return doCommand(IMAPCommand.SUBSCRIBE, str);
    }

    public boolean unsubscribe(String str) throws IOException {
        return doCommand(IMAPCommand.UNSUBSCRIBE, str);
    }

    public boolean list(String str, String str2) throws IOException {
        return doCommand(IMAPCommand.LIST, str + " " + str2);
    }

    public boolean lsub(String str, String str2) throws IOException {
        return doCommand(IMAPCommand.LSUB, str + " " + str2);
    }

    public boolean status(String str, String[] strArr) throws IOException {
        if (strArr == null || strArr.length < 1) {
            throw new IllegalArgumentException("STATUS command requires at least one data item name");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" (");
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(strArr[i]);
        }
        sb.append(")");
        return doCommand(IMAPCommand.STATUS, sb.toString());
    }

    public boolean append(String str, String str2, String str3) throws IOException {
        if (str2 != null) {
            str = str + " " + str2;
        }
        if (str3 != null) {
            if (str3.charAt(0) == '{') {
                str = str + " " + str3;
            } else {
                str = str + " {" + str3 + "}";
            }
        }
        return doCommand(IMAPCommand.APPEND, str);
    }

    public boolean append(String str) throws IOException {
        return append(str, null, null);
    }

    public boolean check() throws IOException {
        return doCommand(IMAPCommand.CHECK);
    }

    public boolean close() throws IOException {
        return doCommand(IMAPCommand.CLOSE);
    }

    public boolean expunge() throws IOException {
        return doCommand(IMAPCommand.EXPUNGE);
    }

    public boolean search(String str, String str2) throws IOException {
        return doCommand(IMAPCommand.SEARCH, (str != null ? "CHARSET " + str : "") + str2);
    }

    public boolean search(String str) throws IOException {
        return search(null, str);
    }

    public boolean fetch(String str, String str2) throws IOException {
        return doCommand(IMAPCommand.FETCH, str + " " + str2);
    }

    public boolean store(String str, String str2, String str3) throws IOException {
        return doCommand(IMAPCommand.STORE, str + " " + str2 + " " + str3);
    }

    public boolean copy(String str, String str2) throws IOException {
        return doCommand(IMAPCommand.COPY, str + " " + str2);
    }

    public boolean uid(String str, String str2) throws IOException {
        return doCommand(IMAPCommand.UID, str + " " + str2);
    }
}
