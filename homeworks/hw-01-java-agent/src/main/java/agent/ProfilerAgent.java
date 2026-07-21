package agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.named;

// Java-агент
public class ProfilerAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            AgentConfig config = AgentConfig.load(agentArgs);
            installTransformers(inst, config);
            ResultWriter.scheduleDump(config.getDurationMillis());
            System.out.println("profiler: отслеживаем " + config.getMethods()
                    + ", результаты будут через " + config.getDurationMillis() / 1000 + " сек");
        } catch (Exception e) {
            System.err.println("profiler: не удалось запустить агент: " + e.getMessage());
        }
    }

    // на каждый метод из конфига вешаем трансформер, который добавляет вызов счётчика
    private static void installTransformers(Instrumentation inst, AgentConfig config) {
        AgentBuilder builder = new AgentBuilder.Default();
        for (String method : config.getMethods()) {
            int dot = method.lastIndexOf('.');
            String className = method.substring(0, dot);
            String methodName = method.substring(dot + 1);
            builder = builder.type(named(className))
                    .transform((b, type, classLoader, module, protectionDomain) ->
                            b.visit(Advice.to(CountAdvice.class).on(named(methodName))));
        }
        builder.installOn(inst);
    }
}
