package com.musai.musai.entity.arts;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "arts")
public class Art {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer art_id;

    @Lob
    private String primaryImageSmall;

    private String name;
    private String department;
    private String title;
    private String culture;
    private String period;
    private String objectDate;
    private Integer objectBeginDate;
    private Integer objectEndDate;

    @Column(name = "object_id")
    private Integer objectID;

    private String classification;
    private String style;
    private String objectName;
}
