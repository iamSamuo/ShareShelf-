package com.sammy.book_network.feedback;


import com.sammy.book_network.book.Book;
import com.sammy.book_network.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FeedBack extends BaseEntity {
    // inherit the id column from thr BaseEntity
    private Double note; // 1-5 stars(rating)
    private String comment;
    // auditing columns (inherit from BaseEntity)
    //relationship with book
    @ManyToOne
    @JoinColumn(name = "book_id") // this will be the foreign key column on this table
    private Book book;


}
