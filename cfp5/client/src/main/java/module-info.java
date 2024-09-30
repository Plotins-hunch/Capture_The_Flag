module client {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires com.google.gson;
  requires server;
  requires ai;
  requires java.prefs; // This line is crucial
  requires spring.web;
  requires spring.beans;
  requires spring.context;
  requires spring.core;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.webmvc;

  // Add any additional requirements depending on your specific setup

  requires java.net.http;
  requires javafx.media;
  requires java.datatransfer;
  requires java.desktop;

  exports de.unimannheim.swt.pse.client;
  exports de.unimannheim.swt.pse.client.controller;
  opens de.unimannheim.swt.pse.client to javafx.fxml; // Adjust if you have FXML files in your package
  opens de.unimannheim.swt.pse.client.controller to javafx.fxml; // Adjust if you have FXML files in your package
}