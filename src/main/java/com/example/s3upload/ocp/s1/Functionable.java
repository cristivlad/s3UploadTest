package com.example.s3upload.ocp.s1;

@FunctionalInterface
public interface Functionable<T,R> {

    R applyThis(T t);
}
