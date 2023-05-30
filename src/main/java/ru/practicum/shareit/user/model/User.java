package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank(message = "поле 'ПОЧТА' не может быть пустым")
    private String name;

    @Email
    @NotBlank(message = "поле 'ПОЧТА' не может быть пустым")
    private String email;
}