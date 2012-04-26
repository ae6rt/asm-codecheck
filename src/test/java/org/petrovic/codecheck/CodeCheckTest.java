package org.petrovic.codecheck;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.Assert.fail;

public class CodeCheckTest {

    private final Map<String, Collection<String>> loggerMap = new TreeMap<String, Collection<String>>();
    private final List<String> forbiddenLoggers = new ArrayList<String>() {{
        add("java.util.logging.Logger");
        add("org.apache.log4j.Logger");
    }};

    private final String CLASSFILE_SUFFIX = ".class";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Ignore
    // this test needs to run in the Maven parent's post-compile phase.  otherwise it may see targets that have been
    // fixed, but not yet cleaned and compiled.  msp
    public void testLoggers() throws Exception {
        File parent = new File(System.getProperty("user.dir")).getParentFile();
        Collection<File> classFileCollection = FileUtils.listFiles(parent,
                new IOFileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.getName().endsWith(CLASSFILE_SUFFIX);
                    }

                    @Override
                    public boolean accept(File file, String s) {
                        return s.endsWith(CLASSFILE_SUFFIX);
                    }
                },
                new IOFileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return true;
                    }

                    @Override
                    public boolean accept(File file, String s) {
                        return true;
                    }
                }
        );
//        System.out.printf("logger-scanning parent %s containing %d classfiles...", parent.getAbsolutePath(), classFileCollection.size());
        for (File f : classFileCollection) {
            ClassReader classReader = new ClassReader(new FileInputStream(f));
            TestClassVisitor testClassVisitor = new TestClassVisitor(forbiddenLoggers);
            classReader.accept(testClassVisitor, ClassReader.EXPAND_FRAMES);
            String className = testClassVisitor.className;
            Set<String> hitList = testClassVisitor.hitList;
            if (!hitList.isEmpty()) {
                fail(String.format("The class %s in file %s contains unsupported Loggers %s.  Please use SLF4J exclusively.", className, f.getAbsolutePath(), hitList));
            }
        }
    }


}
