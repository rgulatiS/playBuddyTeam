package pro.play.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.user.dto.UserDto;
import pro.play.user.model.User;
import pro.play.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserDto>> list() {
        List<UserDto> dtos = userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return userRepository.findById(id).map(u -> ResponseEntity.ok(toDto(u))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .mobileNumber(u.getMobileNumber())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .cityId(u.getCity() != null ? u.getCity().getId() : null)
                .build();
    }
}

