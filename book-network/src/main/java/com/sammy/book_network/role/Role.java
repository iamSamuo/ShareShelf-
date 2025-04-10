package com.sammy.book_network.role;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sammy.book_network.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class )
public class Role  {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore // prevents it from being serialized as a response prevent loop fetching.
    private List<User> users;
    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDate createdDate;

}
