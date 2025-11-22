package com.classiverse.backend.domain.closeness.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClosenessId implements Serializable {
    private Long user;
    private Long character;
}