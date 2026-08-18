package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.ClasspathUtils;

/* JADX INFO: loaded from: classes3.dex */
public class RegexpFactory extends RegexpMatcherFactory {
    public Regexp newRegexp() throws BuildException {
        return newRegexp(null);
    }

    public Regexp newRegexp(Project project) throws BuildException {
        String property;
        if (project == null) {
            property = System.getProperty(MagicNames.REGEXP_IMPL);
        } else {
            property = project.getProperty(MagicNames.REGEXP_IMPL);
        }
        if (property != null) {
            return createRegexpInstance(property);
        }
        return new Jdk14RegexpRegexp();
    }

    protected Regexp createRegexpInstance(String str) throws BuildException {
        return (Regexp) ClasspathUtils.newInstance(str, RegexpFactory.class.getClassLoader(), Regexp.class);
    }
}
