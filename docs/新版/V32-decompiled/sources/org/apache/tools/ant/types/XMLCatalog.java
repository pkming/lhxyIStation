package org.apache.tools.ant.types;

import com.unisound.common.r;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: classes3.dex */
public class XMLCatalog extends DataType implements Cloneable, EntityResolver, URIResolver {
    public static final String APACHE_RESOLVER = "org.apache.tools.ant.types.resolver.ApacheCatalogResolver";
    public static final String CATALOG_RESOLVER = "org.apache.xml.resolver.tools.CatalogResolver";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private Path catalogPath;
    private Path classpath;
    private Vector<ResourceLocation> elements = new Vector<>();
    private CatalogResolver catalogResolver = null;

    private interface CatalogResolver extends URIResolver, EntityResolver {
        @Override // javax.xml.transform.URIResolver
        Source resolve(String str, String str2) throws TransformerException;

        @Override // org.xml.sax.EntityResolver
        InputSource resolveEntity(String str, String str2);
    }

    public XMLCatalog() {
        setChecked(false);
    }

    private Vector<ResourceLocation> getElements() {
        return getRef().elements;
    }

    private Path getClasspath() {
        return getRef().classpath;
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
        setChecked(false);
    }

    public void setClasspathRef(Reference reference) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        createClasspath().setRefid(reference);
        setChecked(false);
    }

    public Path createCatalogPath() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.catalogPath == null) {
            this.catalogPath = new Path(getProject());
        }
        setChecked(false);
        return this.catalogPath.createPath();
    }

    public void setCatalogPathRef(Reference reference) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        createCatalogPath().setRefid(reference);
        setChecked(false);
    }

    public Path getCatalogPath() {
        return getRef().catalogPath;
    }

    public void addDTD(ResourceLocation resourceLocation) throws BuildException {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        getElements().addElement(resourceLocation);
        setChecked(false);
    }

    public void addEntity(ResourceLocation resourceLocation) throws BuildException {
        addDTD(resourceLocation);
    }

    public void addConfiguredXMLCatalog(XMLCatalog xMLCatalog) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        getElements().addAll(xMLCatalog.getElements());
        createClasspath().append(xMLCatalog.getClasspath());
        createCatalogPath().append(xMLCatalog.getCatalogPath());
        setChecked(false);
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (!this.elements.isEmpty()) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String str, String str2) throws SAXException, IOException {
        if (isReference()) {
            return getRef().resolveEntity(str, str2);
        }
        dieOnCircularReference();
        log("resolveEntity: '" + str + "': '" + str2 + "'", 4);
        InputSource inputSourceResolveEntity = getCatalogResolver().resolveEntity(str, str2);
        if (inputSourceResolveEntity == null) {
            log("No matching catalog entry found, parser will use: '" + str2 + "'", 4);
        }
        return inputSourceResolveEntity;
    }

    @Override // javax.xml.transform.URIResolver
    public Source resolve(String str, String str2) throws TransformerException {
        URL url;
        if (isReference()) {
            return getRef().resolve(str, str2);
        }
        dieOnCircularReference();
        String strRemoveFragment = removeFragment(str);
        log("resolve: '" + strRemoveFragment + "' with base: '" + str2 + "'", 4);
        SAXSource sAXSource = (SAXSource) getCatalogResolver().resolve(strRemoveFragment, str2);
        if (sAXSource == null) {
            log("No matching catalog entry found, parser will use: '" + str + "'", 4);
            sAXSource = new SAXSource();
            try {
                if (str2 == null) {
                    url = FILE_UTILS.getFileURL(getProject().getBaseDir());
                } else {
                    url = new URL(str2);
                }
                if (strRemoveFragment.length() != 0) {
                    url = new URL(url, strRemoveFragment);
                }
                sAXSource.setInputSource(new InputSource(url.toString()));
            } catch (MalformedURLException unused) {
                sAXSource.setInputSource(new InputSource(strRemoveFragment));
            }
        }
        setEntityResolver(sAXSource);
        return sAXSource;
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            Path path = this.classpath;
            if (path != null) {
                pushAndInvokeCircularReferenceCheck(path, stack, project);
            }
            Path path2 = this.catalogPath;
            if (path2 != null) {
                pushAndInvokeCircularReferenceCheck(path2, stack, project);
            }
            setChecked(true);
        }
    }

    private XMLCatalog getRef() {
        return !isReference() ? this : (XMLCatalog) getCheckedRef(XMLCatalog.class, "xmlcatalog");
    }

    private CatalogResolver getCatalogResolver() {
        if (this.catalogResolver == null) {
            try {
                Class<?> cls = Class.forName(APACHE_RESOLVER, true, Class.forName(CATALOG_RESOLVER, true, Class.forName(APACHE_RESOLVER, true, getProject().createClassLoader(Path.systemClasspath)).getClassLoader()).getClassLoader());
                this.catalogResolver = new ExternalResolver(cls, cls.newInstance());
            } catch (Throwable th) {
                this.catalogResolver = new InternalResolver();
                if (getCatalogPath() != null && getCatalogPath().list().length != 0) {
                    log("Warning: XML resolver not found; external catalogs will be ignored", 1);
                }
                log("Failed to load Apache resolver: " + th, 4);
            }
        }
        return this.catalogResolver;
    }

    private void setEntityResolver(SAXSource sAXSource) throws TransformerException {
        XMLReader xMLReader = sAXSource.getXMLReader();
        if (xMLReader == null) {
            SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
            sAXParserFactoryNewInstance.setNamespaceAware(true);
            try {
                xMLReader = sAXParserFactoryNewInstance.newSAXParser().getXMLReader();
            } catch (ParserConfigurationException e) {
                throw new TransformerException(e);
            } catch (SAXException e2) {
                throw new TransformerException(e2);
            }
        }
        xMLReader.setEntityResolver(this);
        sAXSource.setXMLReader(xMLReader);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ResourceLocation findMatchingEntry(String str) {
        for (ResourceLocation resourceLocation : getElements()) {
            if (resourceLocation.getPublicId().equals(str)) {
                return resourceLocation;
            }
        }
        return null;
    }

    private String removeFragment(String str) {
        int iIndexOf = str.indexOf("#");
        return iIndexOf >= 0 ? str.substring(0, iIndexOf) : str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InputSource filesystemLookup(ResourceLocation resourceLocation) {
        URL fileURL;
        URL fileURL2;
        String strFromURI;
        String strReplace = resourceLocation.getLocation().replace(File.separatorChar, '/');
        if (resourceLocation.getBase() != null) {
            fileURL = resourceLocation.getBase();
        } else {
            try {
                fileURL = FILE_UTILS.getFileURL(getProject().getBaseDir());
            } catch (MalformedURLException unused) {
                throw new BuildException("Project basedir cannot be converted to a URL");
            }
        }
        try {
            fileURL2 = new URL(fileURL, strReplace);
        } catch (MalformedURLException unused2) {
            File file = new File(strReplace);
            if (file.exists() && file.canRead()) {
                log("uri : '" + strReplace + "' matches a readable file", 4);
                try {
                    fileURL2 = FILE_UTILS.getFileURL(file);
                } catch (MalformedURLException unused3) {
                    throw new BuildException("could not find an URL for :" + file.getAbsolutePath());
                }
            } else {
                log("uri : '" + strReplace + "' does not match a readable file", 4);
                fileURL2 = null;
            }
        }
        if (fileURL2 == null || !fileURL2.getProtocol().equals("file") || (strFromURI = FILE_UTILS.fromURI(fileURL2.toString())) == null) {
            return null;
        }
        log("fileName " + strFromURI, 4);
        File file2 = new File(strFromURI);
        if (!file2.exists() || !file2.canRead()) {
            return null;
        }
        try {
            InputSource inputSource = new InputSource(new FileInputStream(file2));
            try {
                String systemId = JAXPUtils.getSystemId(file2);
                inputSource.setSystemId(systemId);
                log("catalog entry matched a readable file: '" + systemId + "'", 4);
            } catch (IOException unused4) {
            }
            return inputSource;
        } catch (IOException unused5) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InputSource classpathLookup(ResourceLocation resourceLocation) {
        Path pathConcatSystemClasspath;
        Path path = this.classpath;
        if (path != null) {
            pathConcatSystemClasspath = path.concatSystemClasspath(Definer.OnError.POLICY_IGNORE);
        } else {
            pathConcatSystemClasspath = new Path(getProject()).concatSystemClasspath(r.E);
        }
        AntClassLoader antClassLoaderCreateClassLoader = getProject().createClassLoader(pathConcatSystemClasspath);
        InputStream resourceAsStream = antClassLoaderCreateClassLoader.getResourceAsStream(resourceLocation.getLocation());
        if (resourceAsStream == null) {
            return null;
        }
        InputSource inputSource = new InputSource(resourceAsStream);
        String externalForm = antClassLoaderCreateClassLoader.getResource(resourceLocation.getLocation()).toExternalForm();
        inputSource.setSystemId(externalForm);
        log("catalog entry matched a resource in the classpath: '" + externalForm + "'", 4);
        return inputSource;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InputSource urlLookup(ResourceLocation resourceLocation) {
        URL fileURL;
        URL url;
        InputStream inputStream;
        String location = resourceLocation.getLocation();
        if (resourceLocation.getBase() != null) {
            fileURL = resourceLocation.getBase();
        } else {
            try {
                fileURL = FILE_UTILS.getFileURL(getProject().getBaseDir());
            } catch (MalformedURLException unused) {
                throw new BuildException("Project basedir cannot be converted to a URL");
            }
        }
        try {
            url = new URL(fileURL, location);
        } catch (MalformedURLException unused2) {
            url = null;
        }
        if (url == null) {
            return null;
        }
        try {
            URLConnection uRLConnectionOpenConnection = url.openConnection();
            if (uRLConnectionOpenConnection != null) {
                uRLConnectionOpenConnection.setUseCaches(false);
                inputStream = uRLConnectionOpenConnection.getInputStream();
            } else {
                inputStream = null;
            }
            if (inputStream == null) {
                return null;
            }
            InputSource inputSource = new InputSource(inputStream);
            try {
                String externalForm = url.toExternalForm();
                inputSource.setSystemId(externalForm);
                log("catalog entry matched as a URL: '" + externalForm + "'", 4);
            } catch (IOException unused3) {
            }
            return inputSource;
        } catch (IOException unused4) {
            return null;
        }
    }

    private class InternalResolver implements CatalogResolver {
        public InternalResolver() {
            XMLCatalog.this.log("Apache resolver library not found, internal resolver will be used", 3);
        }

        @Override // org.apache.tools.ant.types.XMLCatalog.CatalogResolver, org.xml.sax.EntityResolver
        public InputSource resolveEntity(String str, String str2) {
            ResourceLocation resourceLocationFindMatchingEntry = XMLCatalog.this.findMatchingEntry(str);
            if (resourceLocationFindMatchingEntry == null) {
                return null;
            }
            XMLCatalog.this.log("Matching catalog entry found for publicId: '" + resourceLocationFindMatchingEntry.getPublicId() + "' location: '" + resourceLocationFindMatchingEntry.getLocation() + "'", 4);
            InputSource inputSourceFilesystemLookup = XMLCatalog.this.filesystemLookup(resourceLocationFindMatchingEntry);
            if (inputSourceFilesystemLookup == null) {
                inputSourceFilesystemLookup = XMLCatalog.this.classpathLookup(resourceLocationFindMatchingEntry);
            }
            return inputSourceFilesystemLookup == null ? XMLCatalog.this.urlLookup(resourceLocationFindMatchingEntry) : inputSourceFilesystemLookup;
        }

        /* JADX WARN: Removed duplicated region for block: B:12:0x0060  */
        /* JADX WARN: Removed duplicated region for block: B:14:0x0068  */
        /* JADX WARN: Removed duplicated region for block: B:16:0x0070  */
        @Override // org.apache.tools.ant.types.XMLCatalog.CatalogResolver, javax.xml.transform.URIResolver
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public javax.xml.transform.Source resolve(java.lang.String r4, java.lang.String r5) throws javax.xml.transform.TransformerException {
            /*
                r3 = this;
                org.apache.tools.ant.types.XMLCatalog r0 = org.apache.tools.ant.types.XMLCatalog.this
                org.apache.tools.ant.types.ResourceLocation r4 = org.apache.tools.ant.types.XMLCatalog.access$000(r0, r4)
                if (r4 == 0) goto L76
                org.apache.tools.ant.types.XMLCatalog r0 = org.apache.tools.ant.types.XMLCatalog.this
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "Matching catalog entry found for uri: '"
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r2 = r4.getPublicId()
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r2 = "' location: '"
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r2 = r4.getLocation()
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r2 = "'"
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r1 = r1.toString()
                r2 = 4
                r0.log(r1, r2)
                if (r5 == 0) goto L49
                java.net.URL r0 = new java.net.URL     // Catch: java.net.MalformedURLException -> L49
                r0.<init>(r5)     // Catch: java.net.MalformedURLException -> L49
                org.apache.tools.ant.types.ResourceLocation r5 = new org.apache.tools.ant.types.ResourceLocation     // Catch: java.net.MalformedURLException -> L49
                r5.<init>()     // Catch: java.net.MalformedURLException -> L49
                r5.setBase(r0)     // Catch: java.net.MalformedURLException -> L4a
                goto L4a
            L49:
                r5 = r4
            L4a:
                java.lang.String r0 = r4.getPublicId()
                r5.setPublicId(r0)
                java.lang.String r4 = r4.getLocation()
                r5.setLocation(r4)
                org.apache.tools.ant.types.XMLCatalog r4 = org.apache.tools.ant.types.XMLCatalog.this
                org.xml.sax.InputSource r4 = org.apache.tools.ant.types.XMLCatalog.access$100(r4, r5)
                if (r4 != 0) goto L66
                org.apache.tools.ant.types.XMLCatalog r4 = org.apache.tools.ant.types.XMLCatalog.this
                org.xml.sax.InputSource r4 = org.apache.tools.ant.types.XMLCatalog.access$200(r4, r5)
            L66:
                if (r4 != 0) goto L6e
                org.apache.tools.ant.types.XMLCatalog r4 = org.apache.tools.ant.types.XMLCatalog.this
                org.xml.sax.InputSource r4 = org.apache.tools.ant.types.XMLCatalog.access$300(r4, r5)
            L6e:
                if (r4 == 0) goto L76
                javax.xml.transform.sax.SAXSource r5 = new javax.xml.transform.sax.SAXSource
                r5.<init>(r4)
                goto L77
            L76:
                r5 = 0
            L77:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.types.XMLCatalog.InternalResolver.resolve(java.lang.String, java.lang.String):javax.xml.transform.Source");
        }
    }

    private class ExternalResolver implements CatalogResolver {
        private boolean externalCatalogsProcessed = false;
        private Method parseCatalog;
        private Method resolve;
        private Method resolveEntity;
        private Object resolverImpl;
        private Method setXMLCatalog;

        public ExternalResolver(Class<?> cls, Object obj) {
            this.setXMLCatalog = null;
            this.parseCatalog = null;
            this.resolveEntity = null;
            this.resolve = null;
            this.resolverImpl = null;
            this.resolverImpl = obj;
            try {
                this.setXMLCatalog = cls.getMethod("setXMLCatalog", XMLCatalog.class);
                this.parseCatalog = cls.getMethod("parseCatalog", String.class);
                this.resolveEntity = cls.getMethod("resolveEntity", String.class, String.class);
                this.resolve = cls.getMethod("resolve", String.class, String.class);
                XMLCatalog.this.log("Apache resolver library found, xml-commons resolver will be used", 3);
            } catch (NoSuchMethodException e) {
                throw new BuildException(e);
            }
        }

        @Override // org.apache.tools.ant.types.XMLCatalog.CatalogResolver, org.xml.sax.EntityResolver
        public InputSource resolveEntity(String str, String str2) {
            processExternalCatalogs();
            ResourceLocation resourceLocationFindMatchingEntry = XMLCatalog.this.findMatchingEntry(str);
            if (resourceLocationFindMatchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for publicId: '" + resourceLocationFindMatchingEntry.getPublicId() + "' location: '" + resourceLocationFindMatchingEntry.getLocation() + "'", 4);
                InputSource inputSourceFilesystemLookup = XMLCatalog.this.filesystemLookup(resourceLocationFindMatchingEntry);
                if (inputSourceFilesystemLookup == null) {
                    inputSourceFilesystemLookup = XMLCatalog.this.classpathLookup(resourceLocationFindMatchingEntry);
                }
                if (inputSourceFilesystemLookup != null) {
                    return inputSourceFilesystemLookup;
                }
                try {
                    return (InputSource) this.resolveEntity.invoke(this.resolverImpl, str, str2);
                } catch (Exception e) {
                    throw new BuildException(e);
                }
            }
            try {
                return (InputSource) this.resolveEntity.invoke(this.resolverImpl, str, str2);
            } catch (Exception e2) {
                throw new BuildException(e2);
            }
        }

        @Override // org.apache.tools.ant.types.XMLCatalog.CatalogResolver, javax.xml.transform.URIResolver
        public Source resolve(String str, String str2) throws TransformerException {
            ResourceLocation resourceLocation;
            processExternalCatalogs();
            ResourceLocation resourceLocationFindMatchingEntry = XMLCatalog.this.findMatchingEntry(str);
            if (resourceLocationFindMatchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for uri: '" + resourceLocationFindMatchingEntry.getPublicId() + "' location: '" + resourceLocationFindMatchingEntry.getLocation() + "'", 4);
                if (str2 != null) {
                    try {
                        URL url = new URL(str2);
                        resourceLocation = new ResourceLocation();
                        try {
                            resourceLocation.setBase(url);
                        } catch (MalformedURLException unused) {
                        }
                    } catch (MalformedURLException unused2) {
                        resourceLocation = resourceLocationFindMatchingEntry;
                    }
                } else {
                    resourceLocation = resourceLocationFindMatchingEntry;
                }
                resourceLocation.setPublicId(resourceLocationFindMatchingEntry.getPublicId());
                resourceLocation.setLocation(resourceLocationFindMatchingEntry.getLocation());
                InputSource inputSourceFilesystemLookup = XMLCatalog.this.filesystemLookup(resourceLocation);
                if (inputSourceFilesystemLookup == null) {
                    inputSourceFilesystemLookup = XMLCatalog.this.classpathLookup(resourceLocation);
                }
                if (inputSourceFilesystemLookup != null) {
                    return new SAXSource(inputSourceFilesystemLookup);
                }
                try {
                    return (SAXSource) this.resolve.invoke(this.resolverImpl, str, str2);
                } catch (Exception e) {
                    throw new BuildException(e);
                }
            }
            if (str2 == null) {
                try {
                    str2 = XMLCatalog.FILE_UTILS.getFileURL(XMLCatalog.this.getProject().getBaseDir()).toString();
                } catch (MalformedURLException e2) {
                    throw new TransformerException(e2);
                }
            }
            try {
                return (SAXSource) this.resolve.invoke(this.resolverImpl, str, str2);
            } catch (Exception e3) {
                throw new BuildException(e3);
            }
        }

        private void processExternalCatalogs() {
            if (!this.externalCatalogsProcessed) {
                try {
                    this.setXMLCatalog.invoke(this.resolverImpl, XMLCatalog.this);
                    if (XMLCatalog.this.getCatalogPath() != null) {
                        XMLCatalog.this.log("Using catalogpath '" + XMLCatalog.this.getCatalogPath() + "'", 4);
                        for (String str : XMLCatalog.this.getCatalogPath().list()) {
                            File file = new File(str);
                            XMLCatalog.this.log("Parsing " + file, 4);
                            try {
                                this.parseCatalog.invoke(this.resolverImpl, file.getPath());
                            } catch (Exception e) {
                                throw new BuildException(e);
                            }
                        }
                    }
                } catch (Exception e2) {
                    throw new BuildException(e2);
                }
            }
            this.externalCatalogsProcessed = true;
        }
    }
}
