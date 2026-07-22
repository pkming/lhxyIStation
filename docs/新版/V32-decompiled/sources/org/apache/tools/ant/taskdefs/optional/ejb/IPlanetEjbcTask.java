package org.apache.tools.ant.taskdefs.optional.ejb;

import com.unisound.common.r;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc;
import org.apache.tools.ant.types.Path;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes3.dex */
public class IPlanetEjbcTask extends Task {
    private Path classpath;
    private File dest;
    private File ejbdescriptor;
    private File iasdescriptor;
    private File iashome;
    private boolean keepgenerated = false;
    private boolean debug = false;

    public void setEjbdescriptor(File file) {
        this.ejbdescriptor = file;
    }

    public void setIasdescriptor(File file) {
        this.iasdescriptor = file;
    }

    public void setDest(File file) {
        this.dest = file;
    }

    public void setClasspath(Path path) {
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setKeepgenerated(boolean z) {
        this.keepgenerated = z;
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }

    public void setIashome(File file) {
        this.iashome = file;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        checkConfiguration();
        executeEjbc(getParser());
    }

    private void checkConfiguration() throws BuildException {
        File file = this.ejbdescriptor;
        if (file == null) {
            throw new BuildException("The standard EJB descriptor must be specified using the \"ejbdescriptor\" attribute.", getLocation());
        }
        if (!file.exists() || !this.ejbdescriptor.isFile()) {
            throw new BuildException("The standard EJB descriptor (" + this.ejbdescriptor + ") was not found or isn't a file.", getLocation());
        }
        File file2 = this.iasdescriptor;
        if (file2 == null) {
            throw new BuildException("The iAS-speific XML descriptor must be specified using the \"iasdescriptor\" attribute.", getLocation());
        }
        if (!file2.exists() || !this.iasdescriptor.isFile()) {
            throw new BuildException("The iAS-specific XML descriptor (" + this.iasdescriptor + ") was not found or isn't a file.", getLocation());
        }
        File file3 = this.dest;
        if (file3 == null) {
            throw new BuildException("The destination directory must be specified using the \"dest\" attribute.", getLocation());
        }
        if (!file3.exists() || !this.dest.isDirectory()) {
            throw new BuildException("The destination directory (" + this.dest + ") was not found or isn't a directory.", getLocation());
        }
        File file4 = this.iashome;
        if (file4 != null && !file4.isDirectory()) {
            throw new BuildException("If \"iashome\" is specified, it must be a valid directory (it was set to " + this.iashome + ").", getLocation());
        }
    }

    private SAXParser getParser() throws BuildException {
        try {
            SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
            sAXParserFactoryNewInstance.setValidating(true);
            return sAXParserFactoryNewInstance.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new BuildException("Unable to create a SAXParser: " + e.getMessage(), e, getLocation());
        } catch (SAXException e2) {
            throw new BuildException("Unable to create a SAXParser: " + e2.getMessage(), e2, getLocation());
        }
    }

    private void executeEjbc(SAXParser sAXParser) throws BuildException {
        IPlanetEjbc iPlanetEjbc = new IPlanetEjbc(this.ejbdescriptor, this.iasdescriptor, this.dest, getClasspath().toString(), sAXParser);
        iPlanetEjbc.setRetainSource(this.keepgenerated);
        iPlanetEjbc.setDebugOutput(this.debug);
        File file = this.iashome;
        if (file != null) {
            iPlanetEjbc.setIasHomeDir(file);
        }
        try {
            iPlanetEjbc.execute();
        } catch (IOException e) {
            throw new BuildException("An IOException occurred while trying to read the XML descriptor file: " + e.getMessage(), e, getLocation());
        } catch (IPlanetEjbc.EjbcException e2) {
            throw new BuildException("An exception occurred while trying to run the ejbc utility: " + e2.getMessage(), e2, getLocation());
        } catch (SAXException e3) {
            throw new BuildException("A SAXException occurred while trying to read the XML descriptor file: " + e3.getMessage(), e3, getLocation());
        }
    }

    private Path getClasspath() {
        Path path = this.classpath;
        if (path == null) {
            return new Path(getProject()).concatSystemClasspath(r.E);
        }
        return path.concatSystemClasspath(Definer.OnError.POLICY_IGNORE);
    }
}
