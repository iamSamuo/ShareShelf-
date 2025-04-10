package com.sammy.book_network.book;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book") // controller name on swagger UI

public class BookController {

    private final BookService service;
    // save a book
    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @RequestBody
            @Valid BookRequest request, Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }
}
