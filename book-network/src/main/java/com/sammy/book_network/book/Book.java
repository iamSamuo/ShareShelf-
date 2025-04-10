package com.sammy.book_network.book;

import com.sammy.book_network.common.BaseEntity;
import com.sammy.book_network.feedback.FeedBack;
import com.sammy.book_network.history.BookTransactionHistory;
import com.sammy.book_network.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntity {
    // inherit ID column from BaseEntity

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean sharable;
    // auditing columns will be obtained from BaseEntity and this class extends it (they are used to track changes that happen on a record on the DB )
// book user relation
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    // book feedback relation
    @OneToMany(mappedBy = "book")
    private List<FeedBack> feedBacks;
    // book transaction relation
    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

}
