package org.java.expert.jmeter;

public final class TestParamProvider {

    private TestParamProvider() {
    }

    public static TestParams get() {
        TestParams params = new TestParams(
                System.getProperty("host", "localhost"),
                Integer.parseInt(System.getProperty("port", "8080")),
                System.getProperty("endPoint", "/api/users"),
                Integer.parseInt(System.getProperty("threads", "10")),
                Integer.parseInt(System.getProperty("duration", "30")),
                Double.parseDouble(System.getProperty("rps", "5")),
                System.getProperty("resultPath", "jmeter/jmeter-results/result.jtl")
        );

        System.out.println("Параметры теста: " + params);
        return params;
    }
}
