package agent;

import net.bytebuddy.asm.Advice;

// Код, который ByteBuddy вставляет в начало отслеживаемого метода.
// #t.#m#s = класс.метод(сигнатура), сигнатура нужна чтобы различать перегрузки
public class CountAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin("#t.#m#s") String method) {
        Counters.increment(method);
    }
}
