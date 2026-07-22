package android.renderscript;

import android.renderscript.Script;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class ScriptGroup extends BaseObj {
    IO[] mInputs;
    IO[] mOutputs;

    static class IO {
        Allocation mAllocation;
        Script.KernelID mKID;

        IO(Script.KernelID kernelID) {
            this.mKID = kernelID;
        }
    }

    static class ConnectLine {
        Type mAllocationType;
        Script.KernelID mFrom;
        Script.FieldID mToF;
        Script.KernelID mToK;

        ConnectLine(Type type, Script.KernelID kernelID, Script.KernelID kernelID2) {
            this.mFrom = kernelID;
            this.mToK = kernelID2;
            this.mAllocationType = type;
        }

        ConnectLine(Type type, Script.KernelID kernelID, Script.FieldID fieldID) {
            this.mFrom = kernelID;
            this.mToF = fieldID;
            this.mAllocationType = type;
        }
    }

    static class Node {
        int dagNumber;
        Node mNext;
        Script mScript;
        ArrayList<Script.KernelID> mKernels = new ArrayList<>();
        ArrayList<ConnectLine> mInputs = new ArrayList<>();
        ArrayList<ConnectLine> mOutputs = new ArrayList<>();

        Node(Script script) {
            this.mScript = script;
        }
    }

    ScriptGroup(int i, RenderScript renderScript) {
        super(i, renderScript);
    }

    public void setInput(Script.KernelID kernelID, Allocation allocation) {
        int i = 0;
        while (true) {
            IO[] ioArr = this.mInputs;
            if (i < ioArr.length) {
                if (ioArr[i].mKID == kernelID) {
                    this.mInputs[i].mAllocation = allocation;
                    this.mRS.nScriptGroupSetInput(getID(this.mRS), kernelID.getID(this.mRS), this.mRS.safeID(allocation));
                    return;
                }
                i++;
            } else {
                throw new RSIllegalArgumentException("Script not found");
            }
        }
    }

    public void setOutput(Script.KernelID kernelID, Allocation allocation) {
        int i = 0;
        while (true) {
            IO[] ioArr = this.mOutputs;
            if (i < ioArr.length) {
                if (ioArr[i].mKID == kernelID) {
                    this.mOutputs[i].mAllocation = allocation;
                    this.mRS.nScriptGroupSetOutput(getID(this.mRS), kernelID.getID(this.mRS), this.mRS.safeID(allocation));
                    return;
                }
                i++;
            } else {
                throw new RSIllegalArgumentException("Script not found");
            }
        }
    }

    public void execute() {
        this.mRS.nScriptGroupExecute(getID(this.mRS));
    }

    public static final class Builder {
        private int mKernelCount;
        private RenderScript mRS;
        private ArrayList<Node> mNodes = new ArrayList<>();
        private ArrayList<ConnectLine> mLines = new ArrayList<>();

        public Builder(RenderScript renderScript) {
            this.mRS = renderScript;
        }

        private void validateCycle(Node node, Node node2) {
            for (int i = 0; i < node.mOutputs.size(); i++) {
                ConnectLine connectLine = node.mOutputs.get(i);
                if (connectLine.mToK != null) {
                    Node nodeFindNode = findNode(connectLine.mToK.mScript);
                    if (nodeFindNode.equals(node2)) {
                        throw new RSInvalidStateException("Loops in group not allowed.");
                    }
                    validateCycle(nodeFindNode, node2);
                }
                if (connectLine.mToF != null) {
                    Node nodeFindNode2 = findNode(connectLine.mToF.mScript);
                    if (nodeFindNode2.equals(node2)) {
                        throw new RSInvalidStateException("Loops in group not allowed.");
                    }
                    validateCycle(nodeFindNode2, node2);
                }
            }
        }

        private void mergeDAGs(int i, int i2) {
            for (int i3 = 0; i3 < this.mNodes.size(); i3++) {
                if (this.mNodes.get(i3).dagNumber == i2) {
                    this.mNodes.get(i3).dagNumber = i;
                }
            }
        }

        private void validateDAGRecurse(Node node, int i) {
            if (node.dagNumber != 0 && node.dagNumber != i) {
                mergeDAGs(node.dagNumber, i);
                return;
            }
            node.dagNumber = i;
            for (int i2 = 0; i2 < node.mOutputs.size(); i2++) {
                ConnectLine connectLine = node.mOutputs.get(i2);
                if (connectLine.mToK != null) {
                    validateDAGRecurse(findNode(connectLine.mToK.mScript), i);
                }
                if (connectLine.mToF != null) {
                    validateDAGRecurse(findNode(connectLine.mToF.mScript), i);
                }
            }
        }

        private void validateDAG() {
            for (int i = 0; i < this.mNodes.size(); i++) {
                Node node = this.mNodes.get(i);
                if (node.mInputs.size() == 0) {
                    if (node.mOutputs.size() == 0 && this.mNodes.size() > 1) {
                        throw new RSInvalidStateException("Groups cannot contain unconnected scripts");
                    }
                    validateDAGRecurse(node, i + 1);
                }
            }
            int i2 = this.mNodes.get(0).dagNumber;
            for (int i3 = 0; i3 < this.mNodes.size(); i3++) {
                if (this.mNodes.get(i3).dagNumber != i2) {
                    throw new RSInvalidStateException("Multiple DAGs in group not allowed.");
                }
            }
        }

        private Node findNode(Script script) {
            for (int i = 0; i < this.mNodes.size(); i++) {
                if (script == this.mNodes.get(i).mScript) {
                    return this.mNodes.get(i);
                }
            }
            return null;
        }

        private Node findNode(Script.KernelID kernelID) {
            for (int i = 0; i < this.mNodes.size(); i++) {
                Node node = this.mNodes.get(i);
                for (int i2 = 0; i2 < node.mKernels.size(); i2++) {
                    if (kernelID == node.mKernels.get(i2)) {
                        return node;
                    }
                }
            }
            return null;
        }

        public Builder addKernel(Script.KernelID kernelID) {
            if (this.mLines.size() != 0) {
                throw new RSInvalidStateException("Kernels may not be added once connections exist.");
            }
            if (findNode(kernelID) != null) {
                return this;
            }
            this.mKernelCount++;
            Node nodeFindNode = findNode(kernelID.mScript);
            if (nodeFindNode == null) {
                nodeFindNode = new Node(kernelID.mScript);
                this.mNodes.add(nodeFindNode);
            }
            nodeFindNode.mKernels.add(kernelID);
            return this;
        }

        public Builder addConnection(Type type, Script.KernelID kernelID, Script.FieldID fieldID) {
            Node nodeFindNode = findNode(kernelID);
            if (nodeFindNode == null) {
                throw new RSInvalidStateException("From script not found.");
            }
            Node nodeFindNode2 = findNode(fieldID.mScript);
            if (nodeFindNode2 == null) {
                throw new RSInvalidStateException("To script not found.");
            }
            ConnectLine connectLine = new ConnectLine(type, kernelID, fieldID);
            this.mLines.add(new ConnectLine(type, kernelID, fieldID));
            nodeFindNode.mOutputs.add(connectLine);
            nodeFindNode2.mInputs.add(connectLine);
            validateCycle(nodeFindNode, nodeFindNode);
            return this;
        }

        public Builder addConnection(Type type, Script.KernelID kernelID, Script.KernelID kernelID2) {
            Node nodeFindNode = findNode(kernelID);
            if (nodeFindNode == null) {
                throw new RSInvalidStateException("From script not found.");
            }
            Node nodeFindNode2 = findNode(kernelID2);
            if (nodeFindNode2 == null) {
                throw new RSInvalidStateException("To script not found.");
            }
            ConnectLine connectLine = new ConnectLine(type, kernelID, kernelID2);
            this.mLines.add(new ConnectLine(type, kernelID, kernelID2));
            nodeFindNode.mOutputs.add(connectLine);
            nodeFindNode2.mInputs.add(connectLine);
            validateCycle(nodeFindNode, nodeFindNode);
            return this;
        }

        public ScriptGroup create() {
            if (this.mNodes.size() == 0) {
                throw new RSInvalidStateException("Empty script groups are not allowed");
            }
            for (int i = 0; i < this.mNodes.size(); i++) {
                this.mNodes.get(i).dagNumber = 0;
            }
            validateDAG();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int[] iArr = new int[this.mKernelCount];
            int i2 = 0;
            for (int i3 = 0; i3 < this.mNodes.size(); i3++) {
                Node node = this.mNodes.get(i3);
                int i4 = 0;
                while (i4 < node.mKernels.size()) {
                    Script.KernelID kernelID = node.mKernels.get(i4);
                    int i5 = i2 + 1;
                    iArr[i2] = kernelID.getID(this.mRS);
                    boolean z = false;
                    for (int i6 = 0; i6 < node.mInputs.size(); i6++) {
                        if (node.mInputs.get(i6).mToK == kernelID) {
                            z = true;
                        }
                    }
                    boolean z2 = false;
                    for (int i7 = 0; i7 < node.mOutputs.size(); i7++) {
                        if (node.mOutputs.get(i7).mFrom == kernelID) {
                            z2 = true;
                        }
                    }
                    if (!z) {
                        arrayList.add(new IO(kernelID));
                    }
                    if (!z2) {
                        arrayList2.add(new IO(kernelID));
                    }
                    i4++;
                    i2 = i5;
                }
            }
            if (i2 != this.mKernelCount) {
                throw new RSRuntimeException("Count mismatch, should not happen.");
            }
            int[] iArr2 = new int[this.mLines.size()];
            int[] iArr3 = new int[this.mLines.size()];
            int[] iArr4 = new int[this.mLines.size()];
            int[] iArr5 = new int[this.mLines.size()];
            for (int i8 = 0; i8 < this.mLines.size(); i8++) {
                ConnectLine connectLine = this.mLines.get(i8);
                iArr2[i8] = connectLine.mFrom.getID(this.mRS);
                if (connectLine.mToK != null) {
                    iArr3[i8] = connectLine.mToK.getID(this.mRS);
                }
                if (connectLine.mToF != null) {
                    iArr4[i8] = connectLine.mToF.getID(this.mRS);
                }
                iArr5[i8] = connectLine.mAllocationType.getID(this.mRS);
            }
            int iNScriptGroupCreate = this.mRS.nScriptGroupCreate(iArr, iArr2, iArr3, iArr4, iArr5);
            if (iNScriptGroupCreate == 0) {
                throw new RSRuntimeException("Object creation error, should not happen.");
            }
            ScriptGroup scriptGroup = new ScriptGroup(iNScriptGroupCreate, this.mRS);
            scriptGroup.mOutputs = new IO[arrayList2.size()];
            for (int i9 = 0; i9 < arrayList2.size(); i9++) {
                scriptGroup.mOutputs[i9] = (IO) arrayList2.get(i9);
            }
            scriptGroup.mInputs = new IO[arrayList.size()];
            for (int i10 = 0; i10 < arrayList.size(); i10++) {
                scriptGroup.mInputs[i10] = (IO) arrayList.get(i10);
            }
            return scriptGroup;
        }
    }
}
