package com.ambrygen.adx.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag extends Auditable<String> {
    @Column
    private String title;


    public Tag(String title) {
        this.title = title;
    }
}
