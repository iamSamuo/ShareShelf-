package com.sammy.book_network.book;

import org.springframework.stereotype.Service;

@Service // make book mapper a service
public class BookMapper {
    private final BookRepository bookRepository;

    public BookMapper(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    //map Book Request to Book Entity.
    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false)
                .sharable(request.sharable())
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .synopsis(book.getSynopsis())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isSharable())
                .owner(book.getOwner().fullName())
//                .cover() // TODO: implement this later
                .build();
    }
}
