package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;

/* JADX INFO: loaded from: classes3.dex */
public class PathConvert extends Task {
    private static boolean onWindows = Os.isFamily(Os.FAMILY_DOS);
    private boolean preserveDuplicates;
    private Resources path = null;
    private Reference refid = null;
    private String targetOS = null;
    private boolean targetWindows = false;
    private boolean setonempty = true;
    private String property = null;
    private Vector prefixMap = new Vector();
    private String pathSep = null;
    private String dirSep = null;
    private Mapper mapper = null;

    public class MapEntry {
        private String from = null;
        private String to = null;

        public MapEntry() {
        }

        public void setFrom(String str) {
            this.from = str;
        }

        public void setTo(String str) {
            this.to = str;
        }

        public String apply(String str) {
            if (this.from == null || this.to == null) {
                throw new BuildException("Both 'from' and 'to' must be set in a map entry");
            }
            return (PathConvert.onWindows ? str.toLowerCase().replace('\\', '/') : str).startsWith(PathConvert.onWindows ? this.from.toLowerCase().replace('\\', '/') : this.from) ? this.to + str.substring(this.from.length()) : str;
        }
    }

    public static class TargetOs extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{Os.FAMILY_WINDOWS, Os.FAMILY_UNIX, Os.FAMILY_NETWARE, Os.FAMILY_OS2, Os.FAMILY_TANDEM};
        }
    }

    public Path createPath() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        Path path = new Path(getProject());
        add(path);
        return path;
    }

    public void add(ResourceCollection resourceCollection) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        getPath().add(resourceCollection);
    }

    private synchronized Resources getPath() {
        if (this.path == null) {
            Resources resources = new Resources(getProject());
            this.path = resources;
            resources.setCache(true);
        }
        return this.path;
    }

    public MapEntry createMap() {
        MapEntry mapEntry = new MapEntry();
        this.prefixMap.addElement(mapEntry);
        return mapEntry;
    }

    public void setTargetos(String str) {
        TargetOs targetOs = new TargetOs();
        targetOs.setValue(str);
        setTargetos(targetOs);
    }

    public void setTargetos(TargetOs targetOs) {
        String value = targetOs.getValue();
        this.targetOS = value;
        this.targetWindows = (value.equals(Os.FAMILY_UNIX) || this.targetOS.equals(Os.FAMILY_TANDEM)) ? false : true;
    }

    public void setSetonempty(boolean z) {
        this.setonempty = z;
    }

    public void setProperty(String str) {
        this.property = str;
    }

    public void setRefid(Reference reference) {
        if (this.path != null) {
            throw noChildrenAllowed();
        }
        this.refid = reference;
    }

    public void setPathSep(String str) {
        this.pathSep = str;
    }

    public void setDirSep(String str) {
        this.dirSep = str;
    }

    public void setPreserveDuplicates(boolean z) {
        this.preserveDuplicates = z;
    }

    public boolean isPreserveDuplicates() {
        return this.preserveDuplicates;
    }

    public boolean isReference() {
        return this.refid != null;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Resources resources = this.path;
        String str = this.pathSep;
        String str2 = this.dirSep;
        try {
            if (isReference()) {
                Object referencedObject = this.refid.getReferencedObject(getProject());
                if (!(referencedObject instanceof ResourceCollection)) {
                    throw new BuildException("refid '" + this.refid.getRefId() + "' does not refer to a resource collection.");
                }
                getPath().add((ResourceCollection) referencedObject);
            }
            validateSetup();
            String str3 = onWindows ? "\\" : "/";
            StringBuffer stringBuffer = new StringBuffer();
            Iterable union = isPreserveDuplicates() ? this.path : new Union(this.path);
            ArrayList arrayList = new ArrayList();
            Mapper mapper = this.mapper;
            FileNameMapper identityMapper = mapper == null ? new IdentityMapper() : mapper.getImplementation();
            Iterator<Resource> it = union.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String[] strArrMapFileName = identityMapper.mapFileName(String.valueOf(it.next()));
                for (int i = 0; strArrMapFileName != null && i < strArrMapFileName.length; i++) {
                    arrayList.add(strArrMapFileName[i]);
                }
            }
            Iterator it2 = arrayList.iterator();
            boolean z = true;
            while (it2.hasNext()) {
                String strMapElement = mapElement((String) it2.next());
                if (!z) {
                    stringBuffer.append(this.pathSep);
                }
                StringTokenizer stringTokenizer = new StringTokenizer(strMapElement, str3, true);
                while (stringTokenizer.hasMoreTokens()) {
                    String strNextToken = stringTokenizer.nextToken();
                    if (str3.equals(strNextToken)) {
                        strNextToken = this.dirSep;
                    }
                    stringBuffer.append(strNextToken);
                }
                z = false;
            }
            if (this.setonempty || stringBuffer.length() > 0) {
                String string = stringBuffer.toString();
                if (this.property == null) {
                    log(string);
                } else {
                    log("Set property " + this.property + " = " + string, 3);
                    getProject().setNewProperty(this.property, string);
                }
            }
        } finally {
            this.path = resources;
            this.dirSep = str2;
            this.pathSep = str;
        }
    }

    private String mapElement(String str) {
        int size = this.prefixMap.size();
        if (size == 0) {
            return str;
        }
        for (int i = 0; i < size; i++) {
            String strApply = ((MapEntry) this.prefixMap.elementAt(i)).apply(str);
            if (strApply != str) {
                return strApply;
            }
        }
        return str;
    }

    public void addMapper(Mapper mapper) {
        if (this.mapper != null) {
            throw new BuildException(Expand.ERROR_MULTIPLE_MAPPERS);
        }
        this.mapper = mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        Mapper mapper = new Mapper(getProject());
        mapper.add(fileNameMapper);
        addMapper(mapper);
    }

    private void validateSetup() throws BuildException {
        if (this.path == null) {
            throw new BuildException("You must specify a path to convert");
        }
        String str = File.separator;
        String str2 = File.pathSeparator;
        if (this.targetOS != null) {
            boolean z = this.targetWindows;
            str2 = z ? ";" : ":";
            str = z ? "\\" : "/";
        }
        String str3 = this.pathSep;
        if (str3 != null) {
            str2 = str3;
        }
        String str4 = this.dirSep;
        if (str4 != null) {
            str = str4;
        }
        this.pathSep = str2;
        this.dirSep = str;
    }

    private BuildException noChildrenAllowed() {
        return new BuildException("You must not specify nested elements when using the refid attribute.");
    }
}
