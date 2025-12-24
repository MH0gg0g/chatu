package com.example.chatu.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupMessageDTO {

    @NotBlank
    private String groupId;

    @NotBlank
    @Size(max = 2000)
    private String content;
}
