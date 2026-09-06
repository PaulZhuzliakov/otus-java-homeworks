package ru.otus;

import lombok.Builder;

@Builder
public record TestParams(
        String host,
        String port,
        String endPoint,
        String threads,
        String ramp,
        String loop,
        String duration,
        String resultPath
) {
}
