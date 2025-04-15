package com.sammy.book_network.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRespose<T> {
    // actual book content
    private List<T> content;
    //track which page number we are on
    private int number;
    private int size;
    private int totalElements;
    // total pages
    private int totalPages;
    // is the page first
    private boolean first;
    //is the page last
    private boolean last;

}
