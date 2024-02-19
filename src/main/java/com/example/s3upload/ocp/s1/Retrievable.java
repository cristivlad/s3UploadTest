package com.example.s3upload.ocp.s1;

@FunctionalInterface
public interface Retrievable<T> {

    T retrieve();
}
