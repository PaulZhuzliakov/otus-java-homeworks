package agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;
import java.util.StringJoiner;

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

    // на каждый метод из конфига вешаем трансформер, который добавляет вызов счётчика.
    // метод в конфиге: "класс.метод" или "класс.метод(типы,аргументов)", если нужна конкретная перегрузка
    private static void installTransformers(Instrumentation inst, AgentConfig config) {
        AgentBuilder builder = new AgentBuilder.Default();
        for (String method : config.getMethods()) {
            String classAndMethod = method;
            String signature = "()";
            int paren = method.indexOf('(');
            if (paren >= 0) {
                classAndMethod = method.substring(0, paren);
                signature = method.substring(paren);
            }
            int dot = classAndMethod.lastIndexOf('.');
            String className = classAndMethod.substring(0, dot);
            String methodName = classAndMethod.substring(dot + 1);
            // регистрируем сразу - тогда невызванные методы попадут в результат с нулём
            Counters.register(className + "." + methodName + signature);
            builder = addTransformer(builder, className, methodName, signature);
        }
        builder.installOn(inst);
    }

    private static AgentBuilder addTransformer(AgentBuilder builder, String className, String methodName, String signature) {
        return builder.type(named(className))
                .transform((b, type, classLoader, module, protectionDomain) ->
                        b.visit(Advice.to(CountAdvice.class).on(named(methodName).and(hasSignature(signature)))));
    }

    // сигнатуру сравниваем в том же виде, в каком её отдаёт advice (#s): (тип,тип)
    private static ElementMatcher<MethodDescription> hasSignature(String signature) {
        return target -> {
            StringJoiner joiner = new StringJoiner(",", "(", ")");
            target.getParameters().asTypeList().asErasures().forEach(param -> joiner.add(param.getName()));
            return joiner.toString().equals(signature);
        };
    }
}
