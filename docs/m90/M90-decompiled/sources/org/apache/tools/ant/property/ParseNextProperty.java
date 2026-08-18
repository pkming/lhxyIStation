package org.apache.tools.ant.property;

import java.text.ParsePosition;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public interface ParseNextProperty {
    Project getProject();

    Object parseNextProperty(String str, ParsePosition parsePosition);
}
