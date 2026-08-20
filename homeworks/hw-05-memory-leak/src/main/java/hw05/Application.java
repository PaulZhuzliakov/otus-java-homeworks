package hw05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // нагрузка: регистрируем пользователей в цикле сами, чтобы не крутить curl руками
    @Bean
    CommandLineRunner load(RegistrationService service) {
        return args -> {
            for (long n = 1; ; n++) {
                service.register("user" + n, "pass" + n);
                if (n % 25 == 0) {
                    System.out.println("registered " + n);
                }
                Thread.sleep(600);
            }
        };
    }
}
