package com.eal.appturnosbarberia.models;

public class Data {
  private String field1;
  private String field2;
  private String field3;

  // Constructor
  public Data(String field1, String field2, String field3) {
    this.field1 = field1;
    this.field2 = field2;
    this.field3 = field3;
  }

  // Getters y setters
  public String getField1() {
    return field1;
  }
  public void setField1(String field1) {
    this.field1 = field1;
  }
  public String getField2() {
    return field2;
  }
  public void setField2(String field2) {
    this.field2 = field2;
  }
  public String getField3() {
    return field3;
  }
  public void setField3(String field3) {
    this.field3 = field3;
  }
}
