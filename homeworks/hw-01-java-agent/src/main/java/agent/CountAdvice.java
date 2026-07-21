package agent;

import net.bytebuddy.asm.Advice;

// Код, который ByteBuddy вставляет в начало отслеживаемого метода
public class CountAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin("#t.#m") String method) {
        Counters.increment(method);
    }
}
