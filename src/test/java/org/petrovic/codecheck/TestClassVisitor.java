package org.petrovic.codecheck;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author petrovic -- 12/17/11 5:13 AM
 */
public class TestClassVisitor implements ClassVisitor {
    private final Collection<String> loggerList;
    public String className;
    public final Set<String> hitList = new HashSet<String>();

    public TestClassVisitor(Collection<String> loggerList) {
        this.loggerList = loggerList;
    }

    @Override
    public void visit(int version, int accessFlags, String className, String signature, String superName, String[] interfaces) {
        this.className = className.replaceAll("/", ".");
    }

    @Override
    public FieldVisitor visitField(int fieldAccessFlags, String fieldName, String fieldType, String signature, Object fieldInitialValue) {
        if (fieldType.startsWith("L")) {
            String fieldClassName = fieldType.substring(1, fieldType.indexOf(";")).replaceAll("/", ".");
            if (loggerList.contains(fieldClassName)) {
                hitList.add(fieldClassName);
            }
        }
        return null;
    }

    @Override
    public void visitSource(String s, String s1) {
    }

    @Override
    public void visitOuterClass(String s, String s1, String s2) {
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        return null;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
    }

    @Override
    public void visitInnerClass(String s, String s1, String s2, int i) {
    }

    @Override
    public MethodVisitor visitMethod(int i, final String methodName, String s1, String s2, String[] strings) {
        // stubbed out until we need it
        return new TestMethodVisitor();
    }

    @Override
    public void visitEnd() {
    }
}
