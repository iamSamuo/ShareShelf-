package com.sammy.book_network.user;

import com.sammy.book_network.book.Book;
import com.sammy.book_network.history.BookTransactionHistory;
import com.sammy.book_network.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users") // we used users since "user" is a reserved keyword in postgres
@EntityListeners(AuditingEntityListener.class) // tracks creation and updated timestamps
public class User implements UserDetails, Principal {
    @Id
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    @Column(unique = true) // email must be unique
    private String email;
    private String password;
    private boolean accountLocked;
    private boolean enabled;
    //private List<Role> --- this is considered a foreign key that comes from roles table
    @ManyToMany(fetch = FetchType.EAGER)
    // this means when a user is fetched, its roles as also fetched at the same time.
    private List<Role> roles;
    // user book relationship
    @OneToMany(mappedBy = "owner")
    private List<Book> books;
    // relation with transaction history
    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistory> histories;
    // tracking account creation date and update dates.
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate lastModifiedDate;

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String fullName() {
        return firstname + " " + lastname;
    } // return fullName
}
