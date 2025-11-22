package com.classiverse.backend.domain.reading.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingProgressId implements Serializable {
    private Long user;
    private Long story;
    private Long character;
}