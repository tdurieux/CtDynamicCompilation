package com.github.staticdebug.compiler;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class VirtualSourceFileObject extends SimpleJavaFileObject {

    public VirtualSourceFileObject(String simpleClassName, String sourceContent) {
        super(URI.create(simpleClassName + Kind.SOURCE.extension), Kind.SOURCE);
        this.sourceContent = sourceContent;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceContent;
    }

    public String getSourceContent() {
        return sourceContent;
    }

    private String sourceContent;
}
