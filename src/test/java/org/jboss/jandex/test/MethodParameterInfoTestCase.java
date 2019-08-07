package org.jboss.jandex.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import org.junit.Before;
import org.junit.Test;

public class MethodParameterInfoTestCase {

    @Retention(RetentionPolicy.RUNTIME)
    static @interface Anno0 {
    }
    @Retention(RetentionPolicy.RUNTIME)
    static @interface AnnoA {
    }
    @Retention(RetentionPolicy.RUNTIME)
    static @interface AnnoB {
    }
    @Retention(RetentionPolicy.RUNTIME)
    static @interface AnnoC {
    }
    @Retention(RetentionPolicy.RUNTIME)
    static @interface AnnoD {
    }

    static class MethodParameterInfoTestClass {
        @SuppressWarnings("unused")
        @Anno0
        public void getMethodParameterInfos(@Deprecated @AnnoA Object arg1,
                                            @Deprecated @AnnoB int arg2,
                                            @Deprecated @AnnoC String arg3,
                                            @Deprecated @AnnoD Long[] arg4) {
            return;
        }
    }

    private static InputStream tcclGetResourceAsStream(String path) {
        return Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);
    }

    protected static void index(Indexer indexer, String resName) {
        try {
            InputStream stream = tcclGetResourceAsStream(resName);
            indexer.index(stream);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    protected static String pathOf(Class<?> clazz) {
        return clazz.getName().replace('.', '/').concat(".class");
    }

    protected static Index indexOf(Class<?>... classes) {
        Indexer indexer = new Indexer();

        for (Class<?> klazz : classes) {
            index(indexer, pathOf(klazz));
        }

        return indexer.complete();
    }

    MethodParameterInfo p1;
    MethodParameterInfo p2;
    MethodParameterInfo p3;
    MethodParameterInfo p4;

    @Before
    public void setup() {
        Index index = indexOf(MethodParameterInfoTestClass.class,
                              Anno0.class,
                              AnnoA.class,
                              AnnoB.class,
                              AnnoC.class,
                              AnnoD.class);

        ClassInfo classInfo = index.getClassByName(DotName.createSimple(MethodParameterInfoTestClass.class.getName()));
        MethodInfo methodInfo = classInfo.firstMethod("getMethodParameterInfos");

        for (AnnotationInstance annotation : methodInfo.annotations()) {
            if (annotation.target().kind() != Kind.METHOD_PARAMETER) {
                continue;
            }

            MethodParameterInfo param = annotation.target().asMethodParameter();

            if ("arg1".equals(param.name())) {
                p1 = param;
            } else if ("arg2".equals(param.name())) {
                p2 = param;
            } else if ("arg3".equals(param.name())) {
                p3 = param;
            } else if ("arg4".equals(param.name())) {
                p4 = param;
            }
        }
    }

    @Test
    public void testMethodParameterInfoAnnotations() {
        assertEquals(2, p1.annotations().size());
        assertEquals(2, p2.annotations().size());
        assertEquals(2, p3.annotations().size());
        assertEquals(2, p4.annotations().size());
    }

    @Test
    public void testMethodParameterInfoHasAnnotation() {
        assertTrue(p1.hasAnnotation(DotName.createSimple(AnnoA.class.getName())));
        assertFalse(p1.hasAnnotation(DotName.createSimple(Anno0.class.getName())));

        assertTrue(p2.hasAnnotation(DotName.createSimple(AnnoB.class.getName())));
        assertFalse(p2.hasAnnotation(DotName.createSimple(Anno0.class.getName())));

        assertTrue(p3.hasAnnotation(DotName.createSimple(AnnoC.class.getName())));
        assertFalse(p3.hasAnnotation(DotName.createSimple(Anno0.class.getName())));

        assertTrue(p4.hasAnnotation(DotName.createSimple(AnnoD.class.getName())));
        assertFalse(p4.hasAnnotation(DotName.createSimple(Anno0.class.getName())));
    }
}
