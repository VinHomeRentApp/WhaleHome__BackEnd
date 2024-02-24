package com.example.vinhomeproject.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Date;

@MappedSuperclass
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @LastModifiedBy
    @JsonIgnore
    private String modifiedBy;
    @Column
    @CreatedBy
    @JsonIgnore
    private String createBy;
    @Column
    @CreatedBy
    @JsonIgnore
    private String deleteBy;
    @Column
    @LastModifiedDate
    @JsonIgnore
    private LocalDate modifiedDate;
    @Column
    @CreatedDate
    private LocalDate createDate;
    @Column
    @CreatedDate
    @JsonIgnore
    private LocalDate deleteAt;
    @Column
    @CreatedBy
    @JsonIgnore
    private boolean status = true;

}
