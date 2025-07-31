package com.sammy.book_network.feedback;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackResponse {
    private Double note;
    private String comment;
    private boolean ownFeedback; // flag added to make sure you like distinguish logged-in user comment, from the
    // rest of the comments
}
