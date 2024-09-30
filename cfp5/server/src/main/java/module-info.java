module server {
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.boot.starter.web;
  requires spring.core;
  requires spring.boot.starter.logging;
  requires spring.context;
  requires spring.web;
  requires spring.webmvc;
  requires spring.beans;
  requires spring.boot.starter;
  requires org.slf4j;
  requires com.google.gson;
  requires io.swagger.v3.oas.annotations;
  requires io.swagger.v3.oas.models;
  requires jakarta.validation;
  requires org.apache.commons.lang3;
  requires com.google.auth.oauth2;
  requires firebase.admin;
  requires com.google.auth;
  requires com.google.api.apicommon;
  requires com.google.common;
  requires java.net.http;

  exports de.unimannheim.swt.pse.server.game.map;
  exports de.unimannheim.swt.pse.server.game.exceptions;
  exports de.unimannheim.swt.pse.server.game.state;
  exports de.unimannheim.swt.pse.server.controller;
  exports de.unimannheim.swt.pse.server.controller.data;

  exports de.unimannheim.swt.pse.server.database.controller;
  exports de.unimannheim.swt.pse.server.database.service;
  exports de.unimannheim.swt.pse.server.database.dao;
  exports de.unimannheim.swt.pse.server.database.model;

  opens de.unimannheim.swt.pse.server to spring.core, spring.beans, spring.context, spring.web, spring.webmvc;
  exports de.unimannheim.swt.pse.server;
  exports de.unimannheim.swt.pse.server.database;
  exports de.unimannheim.swt.pse.server.authentication;
}