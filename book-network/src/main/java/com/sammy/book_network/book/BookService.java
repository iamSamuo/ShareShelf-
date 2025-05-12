package com.sammy.book_network.book;

import com.sammy.book_network.common.PageResponse;
import com.sammy.book_network.exception.OperationNotPermittedException;
import com.sammy.book_network.file.FileStorageService;
import com.sammy.book_network.history.BookTransactionHistory;
import com.sammy.book_network.history.BookTransactionHistoryRepository;
import com.sammy.book_network.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    // inject book mapper and book repo
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;
    private BookRepository bookRepository;
    private BookTransactionHistoryRepository transactionHistoryRepository;

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

    // all books
    public PageResponse<BookResponse> getAllBooks(Integer page, Integer size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal()); // get logged-in user
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream().map(bookMapper::toBookResponse).collect(Collectors.toList());
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getNumberOfElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    // all owner books
    public PageResponse<BookResponse> getAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal()); // get logged-in user
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream().map(bookMapper::toBookResponse).collect(Collectors.toList());
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getNumberOfElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    //  all borrowed books
    public PageResponse<BorrowedBookResponse> getAllBorrowedByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal()); // get logged-in user
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        // get book transaction history
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable,
                user.getId());
        List<BorrowedBookResponse> bookResponse =
                allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getNumberOfElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()

        );

    }

    // all returned books
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal()); // get logged-in user
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        // get book transaction history
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable,
                user.getId());
        List<BorrowedBookResponse> bookResponse =
                allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();

        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getNumberOfElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );

    }

    // update sharable status
    public Integer updateSharableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        User user = ((User) connectedUser.getPrincipal()); // get connected user
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            // throw custom error
            throw new OperationNotPermittedException("You can not change status of a book you don't own!");
        }
        book.setSharable(!book.isSharable());
        // save the changes
        bookRepository.save(book);
        return bookId;

    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            // throw custom error
            throw new OperationNotPermittedException("You can not change status of a book you don't own!");
        }
        book.setArchived(!book.isArchived());
        // save the changes
        bookRepository.save(book);
        return bookId;
    }

    // borrow a book
    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found " +
                        "with ID:: " + bookId));
        // check if book is archived and shareable
        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or " +
                    "not sharable");
        }
        // make sure  that owner is not the same as the user
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not borrow your own book!");
        }
        // check if the book is already borrowed by another user
        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId,
                user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed!");
        }
        // construct the object to be saved
        BookTransactionHistory bookTransactionHistory =
                BookTransactionHistory.builder()
                        .user(user)
                        .book(book)
                        .returned(false)
                        .returnApproved(false).build();

        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }


    // return borrowed book
    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        // check if book exist.
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        // check book sharable and archived status.
        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or" +
                    "not sharable.");
        }
        // check if logged-in use is the book owner.
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not borrow or return your own book!");
        }
        // check if logged-in user borrowed the book
        BookTransactionHistory bookTransactionHistory =
                transactionHistoryRepository.findBookByBookIdAndUserId(bookId, user.getId())
                        .orElseThrow(() -> new OperationNotPermittedException(
                                "Sorry! You can not return a book you haven't yet!"));
        // if all the above checks out
        bookTransactionHistory.setReturned(true);

        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        // find the book
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        // user
        User user = ((User) connectedUser.getPrincipal());
        // use file upload helper service
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);


    }
}
