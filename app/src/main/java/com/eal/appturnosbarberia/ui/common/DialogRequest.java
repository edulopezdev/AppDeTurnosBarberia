package com.eal.appturnosbarberia.ui.common;

public class DialogRequest {
  private final String title;
  private final String message;
  private final String positiveLabel;
  private final String negativeLabel;
  private final String action;

  public DialogRequest(String title, String message, String positiveLabel, String negativeLabel, String action) {
    this.title = title;
    this.message = message;
    this.positiveLabel = positiveLabel;
    this.negativeLabel = negativeLabel;
    this.action = action;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public String getPositiveLabel() {
    return positiveLabel;
  }

  public String getNegativeLabel() {
    return negativeLabel;
  }

  public String getAction() {
    return action;
  }
}
