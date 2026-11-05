package org.java.expert.jmeter;

import org.apache.jmeter.engine.StandardJMeterEngine;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CreateUserLoadTest {

    private CreateUserLoadTest() {
    }

    public static void main(String[] args) throws Exception {
        JMeterHomeConfig.init();
        TestParams params = TestParamProvider.get();

        Path result = Path.of(params.resultPath()).toAbsolutePath().normalize();
        Files.createDirectories(result.getParent());

        var jmeter = new StandardJMeterEngine();
        jmeter.configure(JMeterConfiguration.create(params));
        jmeter.run();

        System.out.println("Готово. Результаты: " + result);
    }
}
