package com.blackbox.vero.common;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
public class ChildEntity extends BaseEntity {

    protected Integer idx;
    
}
