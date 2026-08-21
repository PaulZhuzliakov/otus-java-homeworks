# нативному бинарнику не нужна джава - только glibc, поэтому debian-slim
FROM debian:bookworm-slim
COPY build/native/nativeCompile/hw-06-graalvm /app/hw-06-graalvm
EXPOSE 8080
ENTRYPOINT ["/app/hw-06-graalvm"]
