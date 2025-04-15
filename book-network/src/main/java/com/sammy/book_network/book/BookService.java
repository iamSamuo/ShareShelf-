package com.sammy.book_network.book;

import com.sammy.book_network.common.PageRespose;
import com.sammy.book_network.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    // inject book mapper and book repo
    private final BookMapper bookMapper;
    private BookRepository bookRepository;

    public Integer save(BookRequest request, Authentication connectedUser) {
        // this is the connected user(loggedIn User)
        User user = ((User) connectedUser.getPrincipal());
        // create a method that maps the request to a book.
        Book book = bookMapper.toBook(request);
        // set the owner of the book
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    // Book mapper response
    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No found with ID:: " + bookId));
    }

    public PageRespose<BookResponse> getAllBooks(Integer page, Integer size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal()); // get logged-in user
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream().map(bookMapper::toBookResponse).collect(Collectors.toList());
        return new PageRespose<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getNumberOfElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageRespose<BookResponse> getAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal()); // get logged-in user
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream().map(bookMapper::toBookResponse).collect(Collectors.toList());
        return new PageRespose<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getNumberOfElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
}
