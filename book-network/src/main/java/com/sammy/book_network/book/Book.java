package com.sammy.book_network.book;

import com.sammy.book_network.common.BaseEntity;
import com.sammy.book_network.feedback.FeedBack;
import com.sammy.book_network.history.BookTransactionHistory;
import com.sammy.book_network.user.User;
import jakarta.persistence.*;
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
    private String bookCover; // the file path to the uploaded picture.
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

    // this is used to mean that this field should not be mapped to the database.
    @Transient
    public double getRate() {
        if (feedBacks == null || feedBacks.isEmpty()) {
            return 0.0;
        }

        // use stream to allow sequential operations(map, filter, reduce).
        var rate = this.feedBacks.stream().mapToDouble(FeedBack::getNote).average().orElse(0.0);
        double roundedRate = Math.round(rate * 10.0) / 10.0;
        return roundedRate;
    }
}
