package com.sammy.book_network.feedback;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedBackRepository extends JpaRepository<FeedBack, Integer> {
    @Query("""
            SELECT feedback 
            FROM FeedBack feedback
            WHERE feedback.book.id= :bookId
            """)
        // NOTE: JPA will take care of the pageable aspect
    Page<FeedBack> findAllByBookId(Integer bookId, Pageable pageable);

}
