module com.app.generator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.json;
    requires java.desktop;
    requires org.ainslec.picocog;
    requires org.apache.commons.lang3;
    requires initializr.generator;
    requires maven.model;
    requires plexus.utils;
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