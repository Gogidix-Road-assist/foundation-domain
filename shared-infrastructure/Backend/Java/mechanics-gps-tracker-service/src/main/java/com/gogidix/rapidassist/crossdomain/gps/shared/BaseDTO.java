package com.gogidix.rapidassist.crossdomain.gps.shared;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class BaseDTO {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
