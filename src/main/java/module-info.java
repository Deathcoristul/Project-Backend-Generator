module com.app.generator {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.desktop;
    requires org.ainslec.picocog;
    requires org.apache.commons.lang3;
    requires initializr.generator;
    requires maven.model;
    requires plexus.utils;
    requires java.sql;
    requires org.mariadb.jdbc;
    opens com.app.generator to javafx.fxml;
    exports com.app.generator;
    exports com.app.generator.util.controller;
    opens com.app.generator.util.controller to javafx.fxml;
    exports com.app.generator.util.domain;
    opens com.app.generator.util.domain to javafx.fxml;
    exports com.app.generator.util.repository;
    opens com.app.generator.util.repository to javafx.fxml;
    exports com.app.generator.util.service;
    opens com.app.generator.util.service to javafx.fxml;
}