package com.sammy.book_network.feedback;

import com.sammy.book_network.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service // mappers should be services
public class FeedbackMapper {
    public FeedBack toFeedBack(FeedbackRequest request) {
        return FeedBack
                .builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book
                        .builder()
                        .id(request.bookId())
                        .archived(false) // not required and has no impact( just to satisfy lombok.)
                        .sharable(false) // not required and has no impact( just to satisfy lombok.)
                        .build())
                .build();
    }

    // map feedback to feedBack response
    public FeedBackResponse tofeedBackResponse(FeedBack feedBack, Integer userId) {
        return FeedBackResponse.builder()
                .note(feedBack.getNote())
                .comment(feedBack.getComment())
                .ownFeedback(Objects.equals(feedBack.getCreatedBy(), userId))
                .build();

    }
}
