package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.GreedyInputHandler;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;
import org.apache.tools.ant.input.MultipleChoiceInputRequest;
import org.apache.tools.ant.input.PropertyFileInputHandler;
import org.apache.tools.ant.input.SecureInputHandler;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Input extends Task {
    private boolean messageAttribute;
    private String validargs = null;
    private String message = "";
    private String addproperty = null;
    private String defaultvalue = null;
    private Handler handler = null;

    public class Handler extends DefBase {
        private String refid = null;
        private HandlerType type = null;
        private String classname = null;

        public Handler() {
        }

        public void setRefid(String str) {
            this.refid = str;
        }

        public String getRefid() {
            return this.refid;
        }

        public void setClassname(String str) {
            this.classname = str;
        }

        public String getClassname() {
            return this.classname;
        }

        public void setType(HandlerType handlerType) {
            this.type = handlerType;
        }

        public HandlerType getType() {
            return this.type;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public InputHandler getInputHandler() {
            HandlerType handlerType = this.type;
            if (handlerType == null) {
                if (this.refid != null) {
                    try {
                        return (InputHandler) getProject().getReference(this.refid);
                    } catch (ClassCastException e) {
                        throw new BuildException(this.refid + " does not denote an InputHandler", e);
                    }
                }
                String str = this.classname;
                if (str != null) {
                    return (InputHandler) ClasspathUtils.newInstance(str, createLoader(), InputHandler.class);
                }
                throw new BuildException("Must specify refid, classname or type");
            }
            return handlerType.getInputHandler();
        }
    }

    public static class HandlerType extends EnumeratedAttribute {
        private static final String[] VALUES = {"default", "propertyfile", "greedy", "secure"};
        private static final InputHandler[] HANDLERS = {new DefaultInputHandler(), new PropertyFileInputHandler(), new GreedyInputHandler(), new SecureInputHandler()};

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return VALUES;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public InputHandler getInputHandler() {
            return HANDLERS[getIndex()];
        }
    }

    public void setValidargs(String str) {
        this.validargs = str;
    }

    public void setAddproperty(String str) {
        this.addproperty = str;
    }

    public void setMessage(String str) {
        this.message = str;
        this.messageAttribute = true;
    }

    public void setDefaultvalue(String str) {
        this.defaultvalue = str;
    }

    public void addText(String str) {
        if (this.messageAttribute && "".equals(str.trim())) {
            return;
        }
        this.message += getProject().replaceProperties(str);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        InputRequest inputRequest;
        String str;
        if (this.addproperty != null && getProject().getProperty(this.addproperty) != null) {
            log("skipping " + getTaskName() + " as property " + this.addproperty + " has already been set.");
            return;
        }
        String str2 = this.validargs;
        if (str2 != null) {
            inputRequest = new MultipleChoiceInputRequest(this.message, StringUtils.split(str2, 44));
        } else {
            inputRequest = new InputRequest(this.message);
        }
        inputRequest.setDefaultValue(this.defaultvalue);
        Handler handler = this.handler;
        (handler == null ? getProject().getInputHandler() : handler.getInputHandler()).handleInput(inputRequest);
        String input = inputRequest.getInput();
        if ((input == null || input.trim().length() == 0) && (str = this.defaultvalue) != null) {
            input = str;
        }
        if (this.addproperty == null || input == null) {
            return;
        }
        getProject().setNewProperty(this.addproperty, input);
    }

    public Handler createHandler() {
        if (this.handler != null) {
            throw new BuildException("Cannot define > 1 nested input handler");
        }
        Handler handler = new Handler();
        this.handler = handler;
        return handler;
    }
}
