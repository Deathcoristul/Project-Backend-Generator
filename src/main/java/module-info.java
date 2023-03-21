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
    requires java.desktop;
    requires org.ainslec.picocog;
    requires org.apache.commons.lang3;
    requires maven.model;

    opens com.app.generator to javafx.fxml;
    exports com.app.generator;
    exports com.app.generator.util;
    opens com.app.generator.util to javafx.fxml;
}