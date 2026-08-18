package org.apache.tools.ant.input;

/* JADX INFO: loaded from: classes3.dex */
public class InputRequest {
    private String defaultValue;
    private String input;
    private final String prompt;

    public boolean isInputValid() {
        return true;
    }

    public InputRequest(String str) {
        if (str == null) {
            throw new IllegalArgumentException("prompt must not be null");
        }
        this.prompt = str;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public void setInput(String str) {
        this.input = str;
    }

    public String getInput() {
        return this.input;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String str) {
        this.defaultValue = str;
    }
}
