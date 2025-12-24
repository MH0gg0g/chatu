package com.example.chatu.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PrivateMessageDTO {

    @NotBlank
    private String to;

    @NotBlank
    @Size(max = 2000)
    private String content;
}
