package com.example.s3upload.contentNegotiation;

import java.util.List;

public class ListParam<T> {
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
