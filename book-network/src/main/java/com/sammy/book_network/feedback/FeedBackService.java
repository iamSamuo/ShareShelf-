package com.sammy.book_network.feedback;

import com.sammy.book_network.book.Book;
import com.sammy.book_network.book.BookRepository;
import com.sammy.book_network.common.PageResponse;
import com.sammy.book_network.exception.OperationNotPermittedException;
import com.sammy.book_network.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackService {
    private final BookRepository bookRepository;
    private final FeedbackMapper feedBackMapper;
    private final FeedBackRepository feedBackRepository;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository
                .findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.bookId()));
        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for an archived or not sharable book ");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give a feedback to your own book ");
        }
        FeedBack feedBack = feedBackMapper.toFeedBack(request);
        return feedBackRepository.save(feedBack).getId();
    }

    // get all book feedback
    public PageResponse<FeedBackResponse> getAllFeedBackByBookId(Integer bookId, int page, int size,
                                                                 Authentication connectedUser) {
        // Pagination aspect
        Pageable pageable = PageRequest.of(page, size);
        // get connected user from Principle
        User user = ((User) connectedUser.getPrincipal());
        // find comments by bookId
        Page<FeedBack> feedBacks = feedBackRepository.findAllByBookId(bookId, pageable);
        List<FeedBackResponse> feedBackResponses = feedBacks
                .stream()
                .map(f -> feedBackMapper
                        .tofeedBackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedBackResponses,
                feedBacks.getNumber(),
                feedBacks.getSize(),
                (int) feedBacks.getTotalElements(),
                feedBacks.getTotalPages(),
                feedBacks.isFirst(),
                feedBacks.isLast()
        );
    }
}
