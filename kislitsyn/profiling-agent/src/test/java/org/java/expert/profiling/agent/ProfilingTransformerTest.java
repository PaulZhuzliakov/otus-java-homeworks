package org.java.expert.profiling.agent;

import org.java.expert.profiling.fixture.TestService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProfilingTransformerTest {

    @Test
    void instrumentsMethodAndCountsItsCalls() throws Exception {
        String className = TestService.class.getName();
        String methodId = className + "#process";
        byte[] originalBytecode = readClassBytes(TestService.class);
        ByteArrayClassLoader loader = new ByteArrayClassLoader(getClass().getClassLoader());

        ProfilingTransformer transformer =
                new ProfilingTransformer(className, "process", loader);

        byte[] transformedBytecode = transformer.transform(
                TestService.class.getModule(),
                loader,
                className.replace('.', '/'),
                null,
                TestService.class.getProtectionDomain(),
                originalBytecode
        );

        assertFalse(Arrays.equals(originalBytecode, transformedBytecode));

        ProfilingRuntime.configure(Duration.ofMinutes(1));

        Class<?> transformedClass = loader.define(className, transformedBytecode);
        Object instance = transformedClass.getConstructor().newInstance();
        Method process = transformedClass.getMethod("process");

        process.invoke(instance);
        process.invoke(instance);

        assertEquals(2, MethodCallCounter.get(methodId));
    }

    private static byte[] readClassBytes(Class<?> type) throws IOException {
        String resourceName = "/" + type.getName().replace('.', '/') + ".class";

        try (InputStream input = type.getResourceAsStream(resourceName)) {
            assertNotNull(input);
            return input.readAllBytes();
        }
    }

    private static final class ByteArrayClassLoader extends ClassLoader {

        private ByteArrayClassLoader(ClassLoader parent) {
            super(parent);
        }

        private Class<?> define(String className, byte[] bytecode) {
            return defineClass(className, bytecode, 0, bytecode.length);
        }
    }
}
