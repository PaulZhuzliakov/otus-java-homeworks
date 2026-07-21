package cache.menu;

import cache.Cache;
import cache.SoftReferenceCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Emulator {

    private final Cache<String, String> cache;
    private Path directory;

    public Emulator() {
        this(new SoftReferenceCache<>());
    }

    public Emulator(Cache<String, String> cache) {
        this.cache = cache;
    }

    public void setDirectory(String path) {
        Path dir = Path.of(path);
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("не директория: " + path);
        }
        this.directory = dir;
    }

    public void putFile(String fileName) throws IOException {
        checkDirectory();
        cache.put(fileName, readFile(fileName));
    }

    public String getFile(String fileName) throws IOException {
        checkDirectory();
        String content = cache.get(fileName);
        if (content == null) {
            System.out.println("(в кэше нет — читаю с диска и кладу в кэш)");
            content = readFile(fileName);
            cache.put(fileName, content);
        } else {
            System.out.println("(из кэша)");
        }
        return content;
    }

    private String readFile(String fileName) throws IOException {
        return Files.readString(directory.resolve(fileName));
    }

    private void checkDirectory() {
        if (directory == null) {
            throw new IllegalStateException("сначала укажите директорию");
        }
    }

    public static void main(String[] args) {
        Emulator emulator = new Emulator();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("1 - указать кэшируемую директорию");
            System.out.println("2 - загрузить содержимое файла в кэш");
            System.out.println("3 - получить содержимое файла из кэша");
            System.out.println("0 - выход");
            System.out.print("> ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.print("путь к директории: ");
                    try {
                        emulator.setDirectory(scanner.nextLine().trim());
                        System.out.println("ок");
                    } catch (IllegalArgumentException e) {
                        System.out.println("ошибка: " + e.getMessage());
                    }
                }
                case "2" -> {
                    System.out.print("имя файла: ");
                    try {
                        emulator.putFile(scanner.nextLine().trim());
                        System.out.println("(файл загружен в кэш)");
                    } catch (Exception e) {
                        System.out.println("ошибка: " + e.getMessage());
                    }
                }
                case "3" -> {
                    System.out.print("имя файла: ");
                    try {
                        System.out.println(emulator.getFile(scanner.nextLine().trim()));
                    } catch (Exception e) {
                        System.out.println("ошибка: " + e.getMessage());
                    }
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("неизвестная команда");
            }
        }
    }
}
