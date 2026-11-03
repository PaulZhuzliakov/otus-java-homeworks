package org.java.expert.jmeter;

import org.apache.jmeter.util.JMeterUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public final class JMeterHomeConfig {

    private JMeterHomeConfig() {
    }

    public static void init() {
        String jmeterHome = System.getenv("JMETER_HOME");
        if (jmeterHome == null || jmeterHome.isBlank()) {
            throw new IllegalArgumentException("Переменная окружения JMETER_HOME не задана");
        }

        Path properties = Path.of(jmeterHome, "bin", "jmeter.properties");
        if (!Files.isRegularFile(properties)) {
            throw new IllegalArgumentException("Файл не найден: " + properties);
        }

        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(properties.toString());
        JMeterUtils.initLocale();
    }
}
