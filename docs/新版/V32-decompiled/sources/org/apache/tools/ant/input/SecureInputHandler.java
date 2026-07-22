package org.apache.tools.ant.input;

import java.util.Arrays;
import org.apache.tools.ant.util.ReflectUtil;

/* JADX INFO: loaded from: classes3.dex */
public class SecureInputHandler extends DefaultInputHandler {
    @Override // org.apache.tools.ant.input.DefaultInputHandler, org.apache.tools.ant.input.InputHandler
    public void handleInput(InputRequest inputRequest) throws Throwable {
        String prompt = getPrompt(inputRequest);
        try {
            Object objInvokeStatic = ReflectUtil.invokeStatic(System.class, "console");
            do {
                char[] cArr = (char[]) ReflectUtil.invoke(objInvokeStatic, "readPassword", String.class, prompt, Object[].class, (Object[]) null);
                inputRequest.setInput(new String(cArr));
                Arrays.fill(cArr, ' ');
            } while (!inputRequest.isInputValid());
        } catch (Exception unused) {
            super.handleInput(inputRequest);
        }
    }
}
