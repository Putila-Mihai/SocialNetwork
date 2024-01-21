package com.example.socialnetworkapp.repository.paging;

public interface PageInterface<T> {
    Pageable getPageable();

    Pageable getNextPageable();

    Iterable<T> getContent();
}