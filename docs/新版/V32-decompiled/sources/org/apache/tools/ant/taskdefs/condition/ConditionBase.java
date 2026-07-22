package org.apache.tools.ant.taskdefs.condition;

import android.nfc.cardemulation.CardEmulation;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Available;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.ant.taskdefs.UpToDate;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ConditionBase extends ProjectComponent {
    private Vector conditions = new Vector();
    private String taskName;

    protected ConditionBase() {
        this.taskName = "condition";
        this.taskName = CardEmulation.EXTRA_SERVICE_COMPONENT;
    }

    protected ConditionBase(String str) {
        this.taskName = "condition";
        this.taskName = str;
    }

    protected int countConditions() {
        return this.conditions.size();
    }

    protected final Enumeration getConditions() {
        return this.conditions.elements();
    }

    public void setTaskName(String str) {
        this.taskName = str;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void addAvailable(Available available) {
        this.conditions.addElement(available);
    }

    public void addChecksum(Checksum checksum) {
        this.conditions.addElement(checksum);
    }

    public void addUptodate(UpToDate upToDate) {
        this.conditions.addElement(upToDate);
    }

    public void addNot(Not not) {
        this.conditions.addElement(not);
    }

    public void addAnd(And and) {
        this.conditions.addElement(and);
    }

    public void addOr(Or or) {
        this.conditions.addElement(or);
    }

    public void addEquals(Equals equals) {
        this.conditions.addElement(equals);
    }

    public void addOs(Os os) {
        this.conditions.addElement(os);
    }

    public void addIsSet(IsSet isSet) {
        this.conditions.addElement(isSet);
    }

    public void addHttp(Http http) {
        this.conditions.addElement(http);
    }

    public void addSocket(Socket socket) {
        this.conditions.addElement(socket);
    }

    public void addFilesMatch(FilesMatch filesMatch) {
        this.conditions.addElement(filesMatch);
    }

    public void addContains(Contains contains) {
        this.conditions.addElement(contains);
    }

    public void addIsTrue(IsTrue isTrue) {
        this.conditions.addElement(isTrue);
    }

    public void addIsFalse(IsFalse isFalse) {
        this.conditions.addElement(isFalse);
    }

    public void addIsReference(IsReference isReference) {
        this.conditions.addElement(isReference);
    }

    public void addIsFileSelected(IsFileSelected isFileSelected) {
        this.conditions.addElement(isFileSelected);
    }

    public void add(Condition condition) {
        this.conditions.addElement(condition);
    }
}
