package com.musai.musai.entity.exhibition;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "api_fetch_status")
public class ApiFetchStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_page_no", nullable = false)
    private Integer lastPageNo;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ApiFetchStatus() {}

    public ApiFetchStatus(Integer lastPageNo) {
        this.lastPageNo = lastPageNo;
    }

    // getter, setter 생략
}
