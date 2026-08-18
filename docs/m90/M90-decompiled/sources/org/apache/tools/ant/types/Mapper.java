package org.apache.tools.ant.types;

import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.CompositeMapper;
import org.apache.tools.ant.util.ContainerMapper;
import org.apache.tools.ant.util.FileNameMapper;

/* JADX INFO: loaded from: classes3.dex */
public class Mapper extends DataType implements Cloneable {
    protected MapperType type = null;
    protected String classname = null;
    protected Path classpath = null;
    protected String from = null;
    protected String to = null;
    private ContainerMapper container = null;

    public Mapper(Project project) {
        setProject(project);
    }

    public void setType(MapperType mapperType) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.type = mapperType;
    }

    public void addConfigured(FileNameMapper fileNameMapper) {
        add(fileNameMapper);
    }

    public void add(FileNameMapper fileNameMapper) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.container == null) {
            if (this.type == null && this.classname == null) {
                this.container = new CompositeMapper();
            } else {
                FileNameMapper implementation = getImplementation();
                if (implementation instanceof ContainerMapper) {
                    this.container = (ContainerMapper) implementation;
                } else {
                    throw new BuildException(String.valueOf(implementation) + " mapper implementation does not support nested mappers!");
                }
            }
        }
        this.container.add(fileNameMapper);
        setChecked(false);
    }

    public void addConfiguredMapper(Mapper mapper) {
        add(mapper.getImplementation());
    }

    public void setClassname(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.classname = str;
    }

    public void setClasspath(Path path) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createClasspath() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        setChecked(false);
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        createClasspath().setRefid(reference);
    }

    public void setFrom(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.from = str;
    }

    public void setTo(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.to = str;
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (this.type != null || this.from != null || this.to != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    public FileNameMapper getImplementation() throws BuildException {
        if (isReference()) {
            dieOnCircularReference();
            Reference refid = getRefid();
            Object referencedObject = refid.getReferencedObject(getProject());
            if (referencedObject instanceof FileNameMapper) {
                return (FileNameMapper) referencedObject;
            }
            if (referencedObject instanceof Mapper) {
                return ((Mapper) referencedObject).getImplementation();
            }
            throw new BuildException((referencedObject == null ? "null" : referencedObject.getClass().getName()) + " at reference '" + refid.getRefId() + "' is not a valid mapper reference.");
        }
        MapperType mapperType = this.type;
        if (mapperType == null && this.classname == null && this.container == null) {
            throw new BuildException("nested mapper or one of the attributes type or classname is required");
        }
        ContainerMapper containerMapper = this.container;
        if (containerMapper != null) {
            return containerMapper;
        }
        if (mapperType != null && this.classname != null) {
            throw new BuildException("must not specify both type and classname attribute");
        }
        try {
            FileNameMapper fileNameMapperNewInstance = getImplementationClass().newInstance();
            Project project = getProject();
            if (project != null) {
                project.setProjectReference(fileNameMapperNewInstance);
            }
            fileNameMapperNewInstance.setFrom(this.from);
            fileNameMapperNewInstance.setTo(this.to);
            return fileNameMapperNewInstance;
        } catch (BuildException e) {
            throw e;
        } catch (Throwable th) {
            throw new BuildException(th);
        }
    }

    protected Class<? extends FileNameMapper> getImplementationClass() throws ClassNotFoundException {
        String implementation = this.classname;
        MapperType mapperType = this.type;
        if (mapperType != null) {
            implementation = mapperType.getImplementation();
        }
        return Class.forName(implementation, true, this.classpath == null ? getClass().getClassLoader() : getProject().createClassLoader(this.classpath)).asSubclass(FileNameMapper.class);
    }

    protected Mapper getRef() {
        return (Mapper) getCheckedRef(Mapper.class, getDataTypeName());
    }

    public static class MapperType extends EnumeratedAttribute {
        private Properties implementations;

        public MapperType() {
            Properties properties = new Properties();
            this.implementations = properties;
            properties.put("identity", "org.apache.tools.ant.util.IdentityMapper");
            this.implementations.put("flatten", "org.apache.tools.ant.util.FlatFileNameMapper");
            this.implementations.put("glob", "org.apache.tools.ant.util.GlobPatternMapper");
            this.implementations.put("merge", "org.apache.tools.ant.util.MergingMapper");
            this.implementations.put(RegularExpression.DATA_TYPE_NAME, "org.apache.tools.ant.util.RegexpPatternMapper");
            this.implementations.put("package", "org.apache.tools.ant.util.PackageNameMapper");
            this.implementations.put("unpackage", "org.apache.tools.ant.util.UnPackageNameMapper");
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"identity", "flatten", "glob", "merge", RegularExpression.DATA_TYPE_NAME, "package", "unpackage"};
        }

        public String getImplementation() {
            return this.implementations.getProperty(getValue());
        }
    }
}
