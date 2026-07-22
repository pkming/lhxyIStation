package org.apache.tools.ant.input;

import java.util.LinkedHashSet;
import java.util.Vector;

/* JADX INFO: loaded from: classes3.dex */
public class MultipleChoiceInputRequest extends InputRequest {
    private final LinkedHashSet<String> choices;

    public MultipleChoiceInputRequest(String str, Vector<String> vector) {
        super(str);
        if (vector == null) {
            throw new IllegalArgumentException("choices must not be null");
        }
        this.choices = new LinkedHashSet<>(vector);
    }

    public Vector<String> getChoices() {
        return new Vector<>(this.choices);
    }

    @Override // org.apache.tools.ant.input.InputRequest
    public boolean isInputValid() {
        return this.choices.contains(getInput()) || ("".equals(getInput()) && getDefaultValue() != null);
    }
}
