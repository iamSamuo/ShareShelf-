package com.sammy.book_network.feedback;

import com.sammy.book_network.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedBackController {
    private final FeedBackService feedBackService;

    // add feedback
    @PostMapping
    public ResponseEntity<Integer> saveFeedBack(
            @Valid
            @RequestBody FeedbackRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedBackService.save(request, connectedUser));
    }

    // get all feedback by book
    @GetMapping("book/{book_id}")
    public ResponseEntity<PageResponse<FeedBackResponse>> getFeedBackByBookId(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedBackService.getAllFeedBackByBookId(bookId, page, size, connectedUser));
    }

}
