package demo;

// Демо-приложение для проверки агента: крутит два метода в цикле.
public class App {

    public static void main(String[] args) throws InterruptedException {
        long end = System.currentTimeMillis() + 6000;
        while (System.currentTimeMillis() < end) {
            fastMethod();
            slowMethod();
            Thread.sleep(100);
        }
        System.out.println("приложение завершено");
    }

    static void fastMethod() {
    }

    static void slowMethod() throws InterruptedException {
        Thread.sleep(10);
    }
}
