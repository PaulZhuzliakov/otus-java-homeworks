package ru.otus;

public class TestParamProvider {
    public static TestParams get() {
        TestParams testParams = TestParams.builder()
                .host(System.getProperty("host", "localhost"))
                .port(System.getProperty("port", "8080"))
                .endPoint(System.getProperty("endPoint", "/api/users"))
                .threads(System.getProperty("threads", "1"))
                .ramp(System.getProperty("ramp", "1"))
                .loop(System.getProperty("loop", "1"))
                .duration(System.getProperty("duration", "1"))
                .resultPath(System.getProperty("resultPath", "/results"))
                .build();

        System.out.println("Сформированы параметры=" + testParams);

        return testParams;
    }
}
