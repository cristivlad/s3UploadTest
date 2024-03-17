package com.example.s3upload.ocp.s2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

  private int id;
  private String name;
  public Item(int id, String name){
    this.id = id;
    this.name = name;
  }
  public String toString(){    // outputs the name
    return name;
  }
}
