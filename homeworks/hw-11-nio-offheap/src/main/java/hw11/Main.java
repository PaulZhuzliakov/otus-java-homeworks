package hw11;

import java.io.IOException;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("размер off-heap хранилища (байт): ");
        int capacity = readInt(scanner);

        printPools("пулы до создания хранилищ");

        OffHeapStorage direct;
        try {
            direct = new ByteBufferStorage(capacity);
        } catch (OutOfMemoryError e) {
            System.out.println("не хватило direct-памяти, лимит регулируется -XX:MaxDirectMemorySize: " + e.getMessage());
            return;
        }
        printPools("пулы после создания ByteBufferStorage");

        OffHeapStorage mapped = new MappedByteBufferStorage(capacity);

        printPools("пулы после создания MappedByteBufferStorage");

        while (true) {
            System.out.println();
            System.out.print("файл (пустая строка - выход): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return;
            }
            Path file = Path.of(line);
            try {
                direct.load(file);
                mapped.load(file);
            } catch (NoSuchFileException e) {
                System.out.println("файл не найден: " + file);
                continue;
            } catch (IllegalArgumentException e) {
                System.out.println("ошибка: " + e.getMessage());
                continue;
            } catch (IOException e) {
                System.out.println("ошибка чтения " + file + ": " + e.getMessage());
                continue;
            }

            System.out.println("загружено " + direct.size() + " байт");
            printPools("пулы после загрузки файла");
        }
    }

    private static int readInt(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("нужно целое число, попробуйте ещё: ");
            }
        }
    }

    private static void printPools(String label) {
        System.out.println();
        System.out.println(label + ":");
        for (BufferPoolMXBean pool : ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class)) {
            System.out.println("  pool " + pool.getName() + ": count=" + pool.getCount()
                    + ", used=" + pool.getMemoryUsed() + " байт");
        }
    }
}
