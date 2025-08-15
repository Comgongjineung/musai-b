package com.musai.musai.entity.arts;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "met")
public class Met {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "met_id")
    private Long metId;

    @Column(name = "objectId")
    private Long objectId;

    public Met() {}

    public Met(Long objectId) {
        this.objectId = objectId;
    }

}
