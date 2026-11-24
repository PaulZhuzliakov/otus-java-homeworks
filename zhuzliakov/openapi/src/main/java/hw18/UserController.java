package hw18;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "User", description = "заглушки для работы с пользователем")
@RestController
@RequestMapping("/user")
public class UserController {

    @Operation(summary = "добавить пользователя", description = "принимает произвольный json")
    @ApiResponse(responseCode = "200", description = "пользователь добавлен, возвращает json с его id")
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Map<String, Object> user) {
        Map<String, Object> added = new LinkedHashMap<>();
        added.put("id", 1);
        return added;
    }

    @Operation(summary = "изменить пользователя", description = "принимает произвольный json")
    @ApiResponse(responseCode = "200", description = "пользователь изменён, возвращает json с его id")
    @PostMapping("/edit")
    public Map<String, Object> edit(@Parameter(description = "id пользователя") @RequestParam long id,
                                    @RequestBody Map<String, Object> user) {
        Map<String, Object> edited = new LinkedHashMap<>();
        edited.put("id", id);
        return edited;
    }

    @Operation(summary = "получить пользователя", description = "возвращает произвольный json")
    @ApiResponse(responseCode = "200", description = "данные пользователя")
    @GetMapping("/get")
    public Map<String, Object> get(@Parameter(description = "id пользователя") @RequestParam long id) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", id);
        user.put("login", "ivan");
        user.put("email", "ivan@example.com");
        return user;
    }
}
