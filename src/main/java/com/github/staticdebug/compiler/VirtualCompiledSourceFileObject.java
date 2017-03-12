package com.github.staticdebug.compiler;

public class VirtualCompiledSourceFileObject extends VirtualClassFileObject {

    private String source;

    public VirtualCompiledSourceFileObject(String qualifiedName, String source) {
        super(qualifiedName, Kind.CLASS);
        this.source = source;
    }

    public String getSourceContent() {
        return source;
    }
}
