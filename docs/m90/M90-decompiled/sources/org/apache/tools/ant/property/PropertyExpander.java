package org.apache.tools.ant.property;

import java.text.ParsePosition;
import org.apache.tools.ant.PropertyHelper;

/* JADX INFO: loaded from: classes3.dex */
public interface PropertyExpander extends PropertyHelper.Delegate {
    String parsePropertyName(String str, ParsePosition parsePosition, ParseNextProperty parseNextProperty);
}
