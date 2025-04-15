package com.sammy.book_network.book;

import com.sammy.book_network.common.PageRespose;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book") // controller name on swagger UI

public class BookController {

    private final BookService service;
    private final BookService bookService;

    // save a book
    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @RequestBody
            @Valid BookRequest request, Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    // find all the books(except the ones the connected user has)
    // implement the paging functionality
    @GetMapping()
    public ResponseEntity<PageRespose<BookResponse>> getAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size, connectedUser));
    }

    // get books by owner
    @GetMapping("/owner")
    public ResponseEntity<PageRespose<BookResponse>> getAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.getAllBooksByOwner(page, size, connectedUser));
    }

    // find book by a specific id
    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> getBook(
            @PathVariable("book-id")
            Integer bookId) {
        return ResponseEntity.ok(service.findById(bookId));
    }

}
