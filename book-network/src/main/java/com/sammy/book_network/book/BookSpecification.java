package com.sammy.book_network.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    // executes complex criteria based queries without using SQL
    public static Specification<Book> withOwnerId(Integer ownerId) {
        // get owner column from book by
        // matching it with owner id
        return (root, query, criteriaBuilder)
                // "owner" is the column
                // "id" is the actual primary key
                -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
