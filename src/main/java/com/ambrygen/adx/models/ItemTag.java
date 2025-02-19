package com.ambrygen.adx.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_tags")
@Getter
@Setter
@NoArgsConstructor
public class ItemTag extends Auditable<String> {
    @Column
    private String itemId;

    @Column
    private String tagId;


    public ItemTag(String itemId, String tagId) {

        this.itemId = itemId;
        this.tagId = tagId;
    }
}
