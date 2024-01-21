package com.example.socialnetworkapp.repository.paging;

public class Pageable {

    int pageNumber;
    int pageSize;

    public Pageable(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber(){ return pageNumber;}
    public int getPageSize() {return pageSize;}

}