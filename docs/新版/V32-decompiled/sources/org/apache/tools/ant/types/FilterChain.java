package org.apache.tools.ant.types;

import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.filters.ClassConstants;
import org.apache.tools.ant.filters.EscapeUnicode;
import org.apache.tools.ant.filters.ExpandProperties;
import org.apache.tools.ant.filters.HeadFilter;
import org.apache.tools.ant.filters.LineContains;
import org.apache.tools.ant.filters.LineContainsRegExp;
import org.apache.tools.ant.filters.PrefixLines;
import org.apache.tools.ant.filters.ReplaceTokens;
import org.apache.tools.ant.filters.StripJavaComments;
import org.apache.tools.ant.filters.StripLineBreaks;
import org.apache.tools.ant.filters.StripLineComments;
import org.apache.tools.ant.filters.SuffixLines;
import org.apache.tools.ant.filters.TabsToSpaces;
import org.apache.tools.ant.filters.TailFilter;
import org.apache.tools.ant.filters.TokenFilter;

/* JADX INFO: loaded from: classes3.dex */
public class FilterChain extends DataType implements Cloneable {
    private Vector<Object> filterReaders = new Vector<>();

    public void addFilterReader(AntFilterReader antFilterReader) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(antFilterReader);
    }

    public Vector<Object> getFilterReaders() {
        if (isReference()) {
            return ((FilterChain) getCheckedRef()).getFilterReaders();
        }
        dieOnCircularReference();
        return this.filterReaders;
    }

    public void addClassConstants(ClassConstants classConstants) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(classConstants);
    }

    public void addExpandProperties(ExpandProperties expandProperties) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(expandProperties);
    }

    public void addHeadFilter(HeadFilter headFilter) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(headFilter);
    }

    public void addLineContains(LineContains lineContains) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(lineContains);
    }

    public void addLineContainsRegExp(LineContainsRegExp lineContainsRegExp) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(lineContainsRegExp);
    }

    public void addPrefixLines(PrefixLines prefixLines) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(prefixLines);
    }

    public void addSuffixLines(SuffixLines suffixLines) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(suffixLines);
    }

    public void addReplaceTokens(ReplaceTokens replaceTokens) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(replaceTokens);
    }

    public void addStripJavaComments(StripJavaComments stripJavaComments) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(stripJavaComments);
    }

    public void addStripLineBreaks(StripLineBreaks stripLineBreaks) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(stripLineBreaks);
    }

    public void addStripLineComments(StripLineComments stripLineComments) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(stripLineComments);
    }

    public void addTabsToSpaces(TabsToSpaces tabsToSpaces) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(tabsToSpaces);
    }

    public void addTailFilter(TailFilter tailFilter) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(tailFilter);
    }

    public void addEscapeUnicode(EscapeUnicode escapeUnicode) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(escapeUnicode);
    }

    public void addTokenFilter(TokenFilter tokenFilter) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(tokenFilter);
    }

    public void addDeleteCharacters(TokenFilter.DeleteCharacters deleteCharacters) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(deleteCharacters);
    }

    public void addContainsRegex(TokenFilter.ContainsRegex containsRegex) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(containsRegex);
    }

    public void addReplaceRegex(TokenFilter.ReplaceRegex replaceRegex) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(replaceRegex);
    }

    public void addTrim(TokenFilter.Trim trim) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(trim);
    }

    public void addReplaceString(TokenFilter.ReplaceString replaceString) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(replaceString);
    }

    public void addIgnoreBlank(TokenFilter.IgnoreBlank ignoreBlank) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(ignoreBlank);
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (!this.filterReaders.isEmpty()) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    public void add(ChainableReader chainableReader) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        setChecked(false);
        this.filterReaders.addElement(chainableReader);
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            for (Object obj : this.filterReaders) {
                if (obj instanceof DataType) {
                    pushAndInvokeCircularReferenceCheck((DataType) obj, stack, project);
                }
            }
            setChecked(true);
        }
    }
}
