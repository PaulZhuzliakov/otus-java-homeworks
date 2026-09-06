package ru.otus;

import org.apache.jmeter.util.JMeterUtils;

import java.io.File;

public class JmeterHomeConfig {
    public static void init() {
        String jmeterHome = System.getenv("JMETER_HOME");
        if (jmeterHome == null || jmeterHome.isEmpty()) {
            throw new IllegalArgumentException("JMETER_HOME environment variable is not set");
        }

        File jmeterHomeDir = new File(jmeterHome);
        if (!jmeterHomeDir.exists()) {
            throw new IllegalArgumentException("jmeterHomeDir[" + jmeterHomeDir.getAbsolutePath() + "] does not exist");
        }

        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(jmeterHome + "/bin/jmeter.properties");
    }
}
