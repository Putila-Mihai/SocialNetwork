package com.example.socialnetworkapp.repository.paging;

public class Page<T> implements PageInterface<T>{

    private Pageable pageable;
    private Iterable<T> content;

    private int totalNumberOfElements;

    public Page(Pageable pageable, Iterable<T> content){
        this.pageable = pageable;
        this.content = content;
    }

    @Override
    public Pageable getPageable() {
        return this.pageable;
    }

    @Override
    public Pageable getNextPageable() {
        return new Pageable(this.pageable.getPageNumber() + 1, this.pageable.getPageSize());
    }

    public Pageable getPrevPageable() {
        return new Pageable(this.pageable.getPageNumber() - 1, this.pageable.getPageSize());
    }

    @Override
    public Iterable<T> getContent() {
        return this.content;
}
    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}