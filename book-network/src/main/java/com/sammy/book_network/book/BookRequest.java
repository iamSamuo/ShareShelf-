package com.sammy.book_network.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

// all the fields contained in a record are final(meaning a record is immutable)
public record BookRequest(
        Integer id, // if id is null create a new book--else update a book.
        @NotEmpty(message = "100") // handle front-end error messages
        @NotNull(message = "100")
        String title,
        @NotEmpty(message = "101")
        @NotNull(message = "101")
        String authorName,
        @NotEmpty(message = "102")
        @NotNull(message = "102")
        String isbn,
        @NotEmpty(message = "103")
        @NotNull(message = "103")
        String synopsis,
        boolean sharable
) {
}
