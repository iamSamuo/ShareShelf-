package com.sammy.book_network.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

// JpaSpecificationExecutor allow building complex query using specification pattern
// executes complex criteria based queries without using SQL
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    // to only show books that the user has not yet borrowed(ready for borrowing)
    @Query("""
            SELECT book
            FROM Book book
            WHERE book.archived =false
            AND book.sharable =true
            AND book.owner.id != :userId
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);
}