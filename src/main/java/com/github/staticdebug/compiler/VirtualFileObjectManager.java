package com.github.staticdebug.compiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VirtualFileObjectManager extends ForwardingJavaFileManager<JavaFileManager> {
    private Map<String, VirtualSourceFileObject> sourceFiles;
    private Map<String, VirtualCompiledSourceFileObject> classFiles;

    protected VirtualFileObjectManager(JavaFileManager fileManager) {
        super(fileManager);
        classFiles = new HashMap<>();
        sourceFiles = new HashMap<>();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind, FileObject outputFile) throws  IOException {
        VirtualCompiledSourceFileObject classFile = new VirtualCompiledSourceFileObject(qualifiedName, sourceFiles.get(qualifiedName).getSourceContent());
        classFiles.put(qualifiedName, classFile);
        return classFile;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (VirtualSourceFileObject.class.isInstance(file)
                || VirtualClassFileObject.class.isInstance(file)
                || VirtualCompiledSourceFileObject.class.isInstance(file)) {
            return file.getName();
        }
        return super.inferBinaryName(location, file);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
        Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);
        List<JavaFileObject> files = new ArrayList<>();
        if (location == StandardLocation.CLASS_PATH && kinds.contains(
				JavaFileObject.Kind.CLASS)) {
            addMatchingFiles(Kind.CLASS, packageName, classFiles.values(), files);
        } else if (location == StandardLocation.SOURCE_PATH && kinds.contains(JavaFileObject.Kind.SOURCE)) {
            addMatchingFiles(Kind.SOURCE, packageName, sourceFiles.values(), files);
        }
        for (JavaFileObject next : result) {
            files.add(next);
        }
        return files;
    }

    private void addMatchingFiles(Kind kind, String packageName, Collection<? extends JavaFileObject> files, Collection<JavaFileObject> destination) {
        for (JavaFileObject file : files) {
            if (file.getKind() == kind && file.getName().startsWith(packageName)) {
                destination.add(file);
            }
        }
    }

    public void setSourceFiles(Map<String, JavaFileObject> sourceFiles) {
        this.sourceFiles = new HashMap<>();
        for (String qualifiedName : sourceFiles.keySet()) {
            JavaFileObject fileObject = sourceFiles.get(qualifiedName);
            if (fileObject instanceof VirtualSourceFileObject) {
                this.sourceFiles.put(qualifiedName, (VirtualSourceFileObject) fileObject);
            }
        }
    }

    private URI uriFor(Location location, String packageName, String simpleClassName) {
        String uriScheme = location.getName() + '/' + packageName + '/' + simpleClassName + ".java";
        return URI.create(uriScheme);
    }

    public Map<String, VirtualCompiledSourceFileObject> getClassFiles() {
        return classFiles;
    }
}
