package com.sammy.book_network.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder // allows this class to be seen by other classes that wish to extend it.
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
// (define a model class tha)defines a base class that provides common properties without being mapped to the database--(class that can be inherited)
@EntityListeners(AuditingEntityListener.class) // listens to changes on createdDate field and LastModifiedDate
public class BaseEntity {
    @Id
    @GeneratedValue()
    @Column(columnDefinition = "INT")
    private Integer id;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime creationDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Integer createdBy; // user id
    @LastModifiedBy
    @Column(insertable = false)
    private Integer lastModifiedBy;// last user to modify
}
