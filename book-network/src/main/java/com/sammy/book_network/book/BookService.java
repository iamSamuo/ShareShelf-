package com.sammy.book_network.book;

import com.sammy.book_network.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    // inject book mapper
    private final BookMapper bookMapper;
    public Integer save( BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal()); // this is the connected user(loggedIn User)
        // create a method that maps the request to a book.
        Book book = bookMapper.toBook(request);

        return null;
    }
}
