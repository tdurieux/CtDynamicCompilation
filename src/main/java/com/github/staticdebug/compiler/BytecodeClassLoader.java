package com.github.staticdebug.compiler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class BytecodeClassLoader extends URLClassLoader {

    private HashMap<String, VirtualCompiledSourceFileObject> bytecodes;

    protected BytecodeClassLoader(URL[] classpath, Map<String, VirtualCompiledSourceFileObject> bytecodes) {
        super(classpath);
        this.bytecodes = new HashMap<>(bytecodes);
    }

    @Override
    public Class<?> findClass(String qualifiedName) throws
			ClassNotFoundException {
        if (containsBytecodesFor(qualifiedName)) {
            byte[] bytes = bytecodesFor(qualifiedName);
            return defineClass(qualifiedName, bytes, 0, bytes.length);
        }
        return super.findClass(qualifiedName);
    }

    @Override
    public InputStream getResourceAsStream(String resourceName) {
        if (resourceName.endsWith(".class")) {
            String qualifiedName = resourceName.substring(0, resourceName.length() - 6).replace('/', '.');
            if (containsBytecodesFor(qualifiedName)) {
                return new ByteArrayInputStream(bytecodesFor(qualifiedName));
            }
        }
        return super.getResourceAsStream(resourceName);
    }

    private boolean containsBytecodesFor(String qualifiedName) {
        return bytecodes.containsKey(qualifiedName);
    }

    private byte[] bytecodesFor(String qualifiedName) {
        return bytecodes.get(qualifiedName).getByteCodes();
    }
}
