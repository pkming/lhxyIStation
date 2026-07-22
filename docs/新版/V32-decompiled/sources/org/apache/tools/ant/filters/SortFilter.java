package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;

/* JADX INFO: loaded from: classes3.dex */
public final class SortFilter extends BaseParamFilterReader implements ChainableReader {
    private static final String COMPARATOR_KEY = "comparator";
    private static final String REVERSE_KEY = "reverse";
    private Comparator<? super String> comparator;
    private Iterator<String> iterator;
    private String line;
    private List<String> lines;
    private boolean reverse;

    public SortFilter() {
        this.comparator = null;
        this.line = null;
        this.iterator = null;
    }

    public SortFilter(Reader reader) {
        super(reader);
        this.comparator = null;
        this.line = null;
        this.iterator = null;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }
        String str = this.line;
        if (str != null) {
            char cCharAt = str.charAt(0);
            if (this.line.length() == 1) {
                this.line = null;
                return cCharAt;
            }
            this.line = this.line.substring(1);
            return cCharAt;
        }
        if (this.lines == null) {
            this.lines = new ArrayList();
            while (true) {
                this.line = readLine();
                String str2 = this.line;
                if (str2 == null) {
                    break;
                }
                this.lines.add(str2);
            }
            sort();
            this.iterator = this.lines.iterator();
        }
        if (this.iterator.hasNext()) {
            this.line = this.iterator.next();
        } else {
            this.line = null;
            this.lines = null;
            this.iterator = null;
        }
        if (this.line != null) {
            return read();
        }
        return -1;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        SortFilter sortFilter = new SortFilter(reader);
        sortFilter.setReverse(isReverse());
        sortFilter.setComparator(getComparator());
        sortFilter.setInitialized(true);
        return sortFilter;
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public void setReverse(boolean z) {
        this.reverse = z;
    }

    public Comparator<? super String> getComparator() {
        return this.comparator;
    }

    public void setComparator(Comparator<? super String> comparator) {
        this.comparator = comparator;
    }

    public void add(Comparator<? super String> comparator) {
        if (this.comparator != null && comparator != null) {
            throw new BuildException("can't have more than one comparator");
        }
        setComparator(comparator);
    }

    private void initialize() throws IOException {
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                String name = parameters[i].getName();
                if (REVERSE_KEY.equals(name)) {
                    setReverse(Boolean.valueOf(parameters[i].getValue()).booleanValue());
                } else if (COMPARATOR_KEY.equals(name)) {
                    try {
                        setComparator((Comparator) Class.forName(parameters[i].getValue()).newInstance());
                    } catch (ClassCastException unused) {
                        throw new BuildException("Value of comparator attribute should implement java.util.Comparator interface");
                    } catch (ClassNotFoundException e) {
                        throw new BuildException(e);
                    } catch (IllegalAccessException e2) {
                        throw new BuildException(e2);
                    } catch (InstantiationException e3) {
                        throw new BuildException(e3);
                    } catch (Exception e4) {
                        throw new BuildException(e4);
                    }
                } else {
                    continue;
                }
            }
        }
    }

    private void sort() {
        Comparator<? super String> comparator = this.comparator;
        if (comparator == null) {
            if (this.reverse) {
                Collections.sort(this.lines, new Comparator<String>() { // from class: org.apache.tools.ant.filters.SortFilter.1
                    @Override // java.util.Comparator
                    public int compare(String str, String str2) {
                        return -str.compareTo(str2);
                    }
                });
                return;
            } else {
                Collections.sort(this.lines);
                return;
            }
        }
        Collections.sort(this.lines, comparator);
    }
}
