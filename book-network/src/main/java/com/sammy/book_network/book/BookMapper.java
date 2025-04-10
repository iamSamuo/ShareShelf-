package com.sammy.book_network.book;
import org.springframework.stereotype.Service;

@Service // make book mapper a service
public class BookMapper {
    //map Book Request to Book Entity.
    public Book toBook( BookRequest request){
    return  Book.builder()
            .id(request.id())
            .title(request.title())
            .authorName(request.authorName())
            .synopsis(request.synopsis())
            .archived(false)
            .sharable(request.sharable())
            .build();
    }
}
