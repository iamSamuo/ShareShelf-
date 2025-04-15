package com.sammy.book_network.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private int id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String owner;
    private byte[] cover; // stores image bytes
    private double rate;
    // average of all the feedbacks to the book multiplied by the number of feedback
    private boolean archived;
    private boolean shareable;
}
