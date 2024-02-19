package com.example.s3upload.ocp.s1;

@FunctionalInterface
public interface Evaluate<T> {

  boolean isNegative(T t);
}
