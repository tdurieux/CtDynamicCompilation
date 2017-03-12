package com.github.staticdebug.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class VirtualClassFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream byteCodes;

    public VirtualClassFileObject(String qualifiedName, Kind kind) {
        super(URI.create(qualifiedName), kind);
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(byteCodes.toByteArray());
    }

    @Override
    public OutputStream openOutputStream() {
        byteCodes = new ByteArrayOutputStream();
        return byteCodes;
    }

    public byte[] getByteCodes() {
        return byteCodes.toByteArray();
    }
}
