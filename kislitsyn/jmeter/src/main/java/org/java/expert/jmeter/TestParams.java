package org.java.expert.jmeter;

public record TestParams(
        String host,
        int port,
        String endPoint,
        int threads,
        int duration,
        double rps,
        String resultPath
) {
}
