package demo;

// Демо-приложение для проверки агента: крутит методы в цикле.
public class App {

    public static void main(String[] args) throws InterruptedException {
        long end = System.currentTimeMillis() + 6000;
        while (System.currentTimeMillis() < end) {
            fastMethod();
            slowMethod();
            overloadedMethod(1);
            overloadedMethod("a");
            Thread.sleep(100);
        }
        System.out.println("приложение завершено");
    }

    static void fastMethod() {
    }

    static void slowMethod() throws InterruptedException {
        Thread.sleep(10);
    }

    // перегрузки, в конфиге задаются с сигнатурой
    static void overloadedMethod(int i) {
    }

    static void overloadedMethod(String s) {
    }
}
