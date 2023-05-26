package com.app.generator;

import com.app.generator.util.repository.Repository;

import com.app.generator.util.controller.Controller;
import com.app.generator.util.domain.Domain;
import com.app.generator.util.service.Service;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.gradle.*;
import io.spring.initializr.generator.io.IndentingWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.ainslec.picocog.*;
import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.json.*;

import static javax.swing.JOptionPane.showMessageDialog;
public class AppController implements Initializable {
    public AnchorPane mainAnchorPane;
    public AnchorPane domainAnchorPane;
    public AnchorPane repositoryAnchorPane;
    public AnchorPane serviceAnchorPane;
    public AnchorPane controllerAnchorPane;
    public ImageView englishButton;
    public ImageView romaniaButton;
    public Label DependencyLabel;
    public Label RepositoryLabel;
    public Label DomainLabel;
    public Label ServiceLabel;
    public Label ControllerLabel;
    public Button domainCancelButton;
    public Button repositoryCancelButton;
    public Button serviceCancelButton;
    public Button controllerCancelButton;
    public TableView<Pair<String,String>> domainFieldTable;
    public TableColumn<Pair<String,String>,String> columnName;
    public TableColumn<Pair<String,String>,String> columnType;
    public TextField fieldTextField;
    public ComboBox<String> typeCombobox=new ComboBox<>();
    public ComboBox<Domain> domainCombobox=new ComboBox<>();
    public ComboBox<Repository> repositoryCombobox=new ComboBox<>();
    public ComboBox<Service> serviceCombobox=new ComboBox<>();
    public Button button_addRepository;
    public Button button_addService;
    public ListView<Repository> serviceRepositoryList;
    public ListView<Service> controllerServiceList;
    public MenuItem DependencyContextUpdate;
    public MenuItem DependencyContextDelete;
    public MenuItem RepositoryContextUpdate;
    public MenuItem RepositoryContextDelete;
    public MenuItem DomainContextUpdate;
    public MenuItem DomainContextDelete;
    public MenuItem ServiceContextUpdate;
    public MenuItem ServiceContextDelete;
    public MenuItem ControllerContextUpdate;
    public MenuItem ControllerContextDelete;
    public MenuItem DomainContextFieldDelete;
    public MenuItem ServiceContextRepositoryDelete;
    public MenuItem ControllerContextServiceDelete;
    public Button button_addField;
    public TextField RelationField;
    public ComboBox<Domain> relationCombobox=new ComboBox<>();
    public ComboBox<String> SpringBootVersion;
    public ComboBox<String> ProjectManager;
    public ComboBox<String> Language;
    public TextField ProjectName;
    public TextField Group;
    public TextField PackageName;
    public ComboBox<Integer> JavaVersion;
    public TextField Description;
    public TextField Artifact;
    public ComboBox<String> PackageType;
    public Button addDependencyButton;
    public ListView<Dependency> DependenciesList;
    public ListView<Repository> RepositoriesList;
    public ListView<Domain> DomainList;
    public TextField RepositoryField;
    public TextField ServiceField;
    public TextField ControllerField;
    public TextField DomainField;
    public ListView<Service> ServicesList;
    public ListView<Controller> ControllerList;
    public Button GenerateButton;
    public Button addControllerButton;
    public Button addDomainButton;
    public Button addServiceButton;
    public Button addRepositoryButton;
    public TextField DatabaseLink;
    public ComboBox<String> DatabaseType;
    public AnchorPane dependencyAnchorPane;
    public Button dependencyCancelButton;
    public Button dependencyOKButton;
    public TextField dependencyGroupField;
    public TextField dependencyArtifactField;
    public ComboBox<Boolean> dependencyOptionalCombobox;
    public TextField dependencyTypeField;
    public ComboBox<String> dependencyScopeCombobox;
    public TextField dependencyVersionField;
    public TextField searchBar;
    public ListView<JSONObject> dependencySearchListView;
    public MenuItem DomainContextFieldId;
    public TextField usernameField;
    public PasswordField passwordField;

    private String locationURI;
    private String language;
    private boolean mustEdit=false;
    private boolean isJakarta=false,lombok=false;
    private String endChar;
    private String langExtension;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.ProjectManager.getItems().setAll("Gradle","Maven");
        this.PackageType.getItems().setAll("Jar","War");
        this.Language.getItems().setAll("Java","Kotlin");
        this.JavaVersion.getItems().setAll(8,11,17,18);
        this.SpringBootVersion.getItems().setAll("3.0.6","2.7.11");
        this.DatabaseType.getItems().setAll("None","MongoDB","MariaDB");
        this.typeCombobox.getItems().setAll("String","Integer","Long","Boolean","UUID","Double","Date");
        this.dependencyOptionalCombobox.getItems().setAll(true,false);
        this.dependencyScopeCombobox.getItems().setAll("compile","provided","runtime","test");
        this.columnName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        this.columnType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
        this.domainFieldTable.setPlaceholder(new Label(""));
        this.language="ro";
        this.dependencyOptionalCombobox.setButtonCell(new ListCell<>() {//listener-e pentru a reveni la textul prompt al combobox-ului
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(this.getText());
                } else {
                    setText(String.valueOf(item));
                }
            }
        });
        this.dependencyScopeCombobox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(this.getText());
                } else {
                    setText(item);
                }
            }
        });
        this.typeCombobox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(this.getText());
                } else {
                    setText(item);
                }
            }
        });
        this.relationCombobox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Domain item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(this.getText());
                } else {
                    setText(item.getName());
                }
            }
        });
        this.domainCombobox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Domain item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(this.getText());
                } else {
                    setText(item.getName());
                }
            }
        });
        this.repositoryCombobox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Repository item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(this.getText());
                } else {
                    setText(item.getName());
                }
            }
        });
        this.serviceCombobox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Service item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(this.getText());
                } else {
                    setText(item.getName());
                }
            }
        });
    }
    public void onAddDependency() {
        popUp(dependencyAnchorPane);
    }

    public void onAddRepository() {
        popUp(repositoryAnchorPane);
    }

    public void onAddService() {
        popUp(serviceAnchorPane);
    }

    public void onAddDomain() {
        if(DomainList.getItems().size()!=0)
        {
            for(Domain d : DomainList.getItems())
            {
                relationCombobox.getItems().add(d);
            }
        }
        popUp(domainAnchorPane);
    }

    public void onAddController() {
        popUp(controllerAnchorPane);
    }

    public void onGenerate() {
        if(this.language.equals("ro")) {
            if (this.ProjectManager.getValue() == null) {
                showMessageDialog(null, "Proiectul nu are gestionator setat!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.SpringBootVersion.getValue() == null) {
                showMessageDialog(null, "Proiectul nu are versiune de Spring setată!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.PackageType.getValue() == null) {
                showMessageDialog(null, "Proiectul nu are tipul de împachetare setat!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.JavaVersion.getValue() == null) {
                showMessageDialog(null, "Proiectul nu are versiune de mașină virtuală Java setată!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.Language.getValue() == null) {
                showMessageDialog(null, "Proiectul nu are limbajul setat!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(this.DatabaseType.getValue()!=null && this.DatabaseLink.getText().equals("") && !this.DatabaseType.getValue().equals("None") )
            {
                showMessageDialog(null, "Proiectul cu tipul de bază de date setat nu are link aceasta!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(this.SpringBootVersion.getValue().startsWith("3") && this.JavaVersion.getValue()<17)
            {
                showMessageDialog(null, "Pentru versiunea "+this.SpringBootVersion.getValue()+" utilizați o versiune Java de minim 17!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(this.SpringBootVersion.getValue().startsWith("2") && domainsHaveUUID()){
                showMessageDialog(null, "Pentru versiunea "+this.SpringBootVersion.getValue()+", atributele de tip UUID nu vor funcționa la fel de bine ca în versiunea 3!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(this.DatabaseType.getValue().equals("MariaDB"))
            {
                if(this.usernameField.getText().equals("") || this.passwordField.getText().equals("")){
                    showMessageDialog(null, "Lipsesc credențiale pentru MariaDB!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }
        else{
            if (this.ProjectManager.getValue() == null) {
                showMessageDialog(null, "The project has no manager set!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.SpringBootVersion.getValue() == null) {
                showMessageDialog(null, "The project has no Spring version set!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.PackageType.getValue() == null) {
                showMessageDialog(null, "The project has no packaging type set!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.JavaVersion.getValue() == null) {
                showMessageDialog(null, "The project has no Java Virtual Machine version set!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (this.Language.getValue() == null) {
                showMessageDialog(null, "The project does not have the language set!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(this.DatabaseLink.getText().equals("") && !this.DatabaseType.getValue().equals("None") && this.DatabaseType.getValue()!=null)
            {
                showMessageDialog(null, "The project has not a link for the database set!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(this.SpringBootVersion.getValue().startsWith("3") && this.JavaVersion.getValue()<17)
            {
                showMessageDialog(null, "For Spring Boot Version "+this.SpringBootVersion.getValue()+", you must use a Java version at least 17!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(this.SpringBootVersion.getValue().startsWith("2") && domainsHaveUUID()){
                showMessageDialog(null, "For Spring Boot version "+this.SpringBootVersion.getValue()+", UUID attributes won't work well like in version 3!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(this.DatabaseType.getValue().equals("MariaDB"))
            {
                if(this.usernameField.getText().equals("") || this.passwordField.getText().equals("")){
                    showMessageDialog(null, "Missing credentials for MariaDB!", "WARNING", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }
        if(this.DatabaseType.getValue()==null)
            this.DatabaseType.setValue("None");
        langExtension="kt";
        endChar="";
        if(this.Language.getValue().equals("Java")) {
            langExtension = "java";
            endChar=";";
        }
        final DirectoryChooser chooser=new DirectoryChooser();
        chooser.setInitialDirectory(new File("."));
        try{
            File selected=chooser.showDialog(new Stage());
            if(selected!=null)
            {
                locationURI=selected.getAbsolutePath();
                locationURI+="\\"+this.ProjectName.getText();
                File f=new File(locationURI);
                if(f.exists()){
                    deleteRecursively(f);
                }
                f.mkdir();
                if(this.ProjectManager.getValue().equals("Maven"))
                    buildMaven();
                else if(this.ProjectManager.getValue().equals("Gradle"))
                    buildGradle();
                locationURI+="\\src";
                f=new File(locationURI);
                if(!f.exists()){
                    f.mkdir();
                }
                buildMainFiles();
                buildTestFiles();

                if(this.language.equals("ro"))
                    showMessageDialog(null,"Proiectul s-a generat!","SUCCES", JOptionPane.INFORMATION_MESSAGE);
                else
                    showMessageDialog(null,"The project has been generated!","SUCCESS", JOptionPane.INFORMATION_MESSAGE);

                if(this.DatabaseType.getValue().equals("MariaDB"))
                    createTables();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        locationURI="";
        langExtension="";
        lombok=false;
        isJakarta=false;
    }

    private void createTables() {
        if(DomainList.getItems().size()!=0){
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                Connection connection = DriverManager.getConnection(this.DatabaseLink.getText(), usernameField.getText(), passwordField.getText());
                Statement statement = connection.createStatement();
                //Pentru siguranta crearii tabelelor mai intai cream domeniile si apoi relatiile
                for(Domain domain : DomainList.getItems()) {
                    if (!domain.isRelation()) {
                        statement.execute("DROP TABLE IF EXISTS " + domain.getName().toUpperCase() + ";");
                        StringBuilder query = new StringBuilder("CREATE TABLE " + domain.getName().toUpperCase() + "(");
                        ArrayList<Pair<String, String>> fields = domain.getFields();
                        ArrayList<String> mariaDBDataTypes = new ArrayList<>();
                        //vom utiliza tipuri de date MariaDB
                        for (Pair<String, String> pair : fields) {
                            if (pair.getValue().equals("String"))
                                mariaDBDataTypes.add("VARCHAR(255)");
                            else if (pair.getValue().equals("Long"))
                                mariaDBDataTypes.add("BIGINT");
                            else if (pair.getValue().equals("UUID"))
                                mariaDBDataTypes.add("CHAR(36)");
                            else
                                mariaDBDataTypes.add(pair.getValue().toUpperCase());
                        }
                        query.append(fields.get(0).getKey()).append(" ").append(mariaDBDataTypes.get(0)).append(" PRIMARY KEY");
                        for (int i = 1; i < fields.size(); i++) {
                            query.append(",").append(fields.get(i).getKey()).append(" ").append(mariaDBDataTypes.get(i));
                        }

                        query.append(");");

                        statement.execute(query.toString());
                    }
                }
                for(Domain domain : DomainList.getItems())
                {
                    if(domain.isRelation())
                    {
                        statement.execute("DROP TABLE IF EXISTS "+domain.getName().toUpperCase()+";");
                        StringBuilder query= new StringBuilder("CREATE TABLE " + domain.getName().toUpperCase() + "(");
                        ArrayList<Pair<String,String>> fields = domain.getFields();
                        ArrayList<String> mariaDBDataTypes = new ArrayList<>();
                        //vom utiliza tipuri de date MariaDB
                        for(Pair<String,String> pair :fields){
                            if(pair.getValue().equals("String"))
                                mariaDBDataTypes.add("VARCHAR(255)");
                            else if(pair.getValue().equals("Long"))
                                mariaDBDataTypes.add("BIGINT");
                            else if (pair.getValue().equals("UUID"))
                                mariaDBDataTypes.add("CHAR(36)");
                            else
                                mariaDBDataTypes.add(pair.getValue().toUpperCase());
                        }
                        query.append(fields.get(0).getKey()).append(" ").append(mariaDBDataTypes.get(0)).append(",");
                        query.append(fields.get(1).getKey()).append(" ").append(mariaDBDataTypes.get(1)).append(",");
                        query.append("PRIMARY KEY(").append(fields.get(0).getKey()).append(",").append(fields.get(1).getKey()).append("),");
                        Domain domain1 = domain.getFirstDomain();
                        Domain domain2 = domain.getSecondDomain();
                        query.append("FOREIGN KEY (").append(fields.get(0).getKey()).append(") REFERENCES ").append(domain1.getName().toUpperCase()).append("(").append(domain1.getFields().get(0).getKey()).append("),");
                        query.append("FOREIGN KEY (").append(fields.get(1).getKey()).append(") REFERENCES ").append(domain2.getName().toUpperCase()).append("(").append(domain2.getFields().get(0).getKey()).append(")");

                        query.append(");");

                        statement.execute(query.toString());
                    }
                }
                connection.close();
            }
            catch (Exception e)
            {
                if(this.language.equals("ro"))
                    showMessageDialog(null,"Nu s-a putut realiza conexiunea cu baza de date, deci tabelele nu se pot crea!","ATENTIE", JOptionPane.WARNING_MESSAGE);
                else
                    showMessageDialog(null,"The connection to the database could not be established, so the tables cannot be created!","WARNING", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void buildGradle() throws IOException {
        GradleBuild build=new GradleBuild();

        build.plugins().add("org.springframework.boot",plugin -> plugin.setVersion(this.SpringBootVersion.getValue()));
        if(this.SpringBootVersion.getValue().startsWith("3"))
            build.plugins().add("io.spring.dependency-management",plugin -> plugin.setVersion("1.1.0"));
        else
            build.plugins().add("io.spring.dependency-management",plugin -> plugin.setVersion("1.0.15.RELEASE"));
        if(PackageType.getValue().equals("War"))
            build.plugins().add("war");
        if(this.Language.getValue().equals("Kotlin"))
        {
            if(this.SpringBootVersion.getValue().startsWith("3"))
            {
                build.plugins().add("org.jetbrains.kotlin.jvm",plugin -> plugin.setVersion("1.8.0"));
                build.plugins().add("org.jetbrains.kotlin.plugin.spring",plugin -> plugin.setVersion("1.8.0"));
            }
            else{
                build.plugins().add("org.jetbrains.kotlin.jvm",plugin -> plugin.setVersion("1.6.21"));
                build.plugins().add("org.jetbrains.kotlin.plugin.spring",plugin -> plugin.setVersion("1.6.21"));
            }
        }
        else{
            build.plugins().add("java");
        }
        build.settings().sourceCompatibility(this.JavaVersion.getValue().toString());
        build.settings().group(this.Group.getText());
        build.settings().version("0.0.1-SNAPSHOT");
        build.repositories().add(MavenRepository.MAVEN_CENTRAL);
        //vom folosi variabila count in asa fel incat sa putem include in container toate dependintele
        int count =1;
        build.dependencies().add("implementation"+(count++),"org.springframework.boot","spring-boot-starter-web", DependencyScope.COMPILE);
        build.dependencies().add("testImplementation"+(count++),"org.springframework.boot","spring-boot-starter-test", DependencyScope.TEST_COMPILE);
        if(!DatabaseType.getValue().equals("None"))
            build.dependencies().add("implementation"+(count++),"org.springframework.boot","spring-boot-starter-validation", DependencyScope.COMPILE);
        if(DatabaseType.getValue().equals("MongoDB"))
            build.dependencies().add("implementation"+(count++),"org.springframework.boot","spring-boot-starter-data-mongodb", DependencyScope.COMPILE);
        else if(DatabaseType.getValue().equals("MariaDB")){
            build.dependencies().add("implementation"+(count++),"org.springframework.boot","spring-boot-starter-data-jpa", DependencyScope.COMPILE);
            build.dependencies().add("implementation"+(count++),"org.springframework.boot","spring-boot-starter-data-rest", DependencyScope.COMPILE);
            build.dependencies().add("runtimeOnly"+(count++),"org.mariadb.jdbc","mariadb-java-client", DependencyScope.RUNTIME);
        }
        if(!SpringBootVersion.getValue().startsWith("2"))
            isJakarta=true;
        if(PackageType.getValue().equals("War"))
            build.dependencies().add("providedRuntime"+(count++),"org.springframework.boot","spring-boot-starter-tomcat", DependencyScope.RUNTIME);
        for(Dependency d : DependenciesList.getItems())
        {
            if(d.getGroupId().equals("org.projectlombok") && d.getArtifactId().equals("lombok")) {
                lombok = true;
                build.dependencies().add("compileOnly"+(count++),"org.projectlombok","lombok", DependencyScope.COMPILE);
                build.dependencies().add("annotationProcessor"+(count++),"org.projectlombok","lombok", DependencyScope.ANNOTATION_PROCESSOR);
            }
            else{
                DependencyScope scope=DependencyScope.COMPILE;
                String id = "implementation";
                if(d.getScope().equals("runtime")) {
                    scope=DependencyScope.RUNTIME;
                    id="runtimeOnly";
                }
                else if(d.getScope().equals("test"))
                {
                    scope=DependencyScope.TEST_COMPILE;
                    id="testImplementation";
                }
                else if (d.getScope().equals("provided"))
                    id="compileOnly";
                build.dependencies().add(id+(count++),d.getGroupId(),d.getArtifactId()+":"+d.getVersion(),scope);
            }
        }
        // Personalizează task-ul "test"
        if(this.Language.getValue().equals("Kotlin")) {
            build.dependencies().add("implementation","org.jetbrains.kotlin","kotlin-reflect",DependencyScope.COMPILE);
            build.tasks().customizeWithType("KotlinCompile", task -> task.nested("kotlinOptions", customizer -> {
                customizer.attribute("freeCompilerArgs", "['-Xjsr305=strict']");
                customizer.attribute("jvmTarget", this.JavaVersion.getValue().toString());
            }));
        }

        build.tasks().customize("test", task -> task.invoke("useJUnitPlatform"));
        GradleBuildWriter gradleWriter = new GroovyDslGradleBuildWriter();
        Writer writer = new FileWriter(locationURI+"\\build.gradle");
        IndentingWriter indentingWriter = new IndentingWriter(writer);
        gradleWriter.writeTo(indentingWriter,build);
        indentingWriter.close();
        writer.close();
        if(Language.getValue().equals("Kotlin")) {
            //Se va citi tot continutul fisierului
            BufferedReader reader = new BufferedReader(new FileReader(locationURI+"\\build.gradle"));
            StringBuilder existingContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                existingContent.append(line).append("\n");
            }
            reader.close();
            //inseram importul si continutul citit in fisier
            writer = new FileWriter(locationURI + "\\build.gradle", false);
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.print("import org.jetbrains.kotlin.gradle.tasks.KotlinCompile\n"+existingContent);
            printWriter.close();
            writer.close();
        }

        //la versiunea 3.x.x trebuie sa includem si un wrapper pentru a nu avea probleme la descarcare si injectare
        File f=new File(locationURI+"\\gradle");
        f.mkdir();
        f=new File(locationURI+"\\gradle\\wrapper");
        f.mkdir();
        PicoWriter picoWriter=new PicoWriter();
        picoWriter.writeln("distributionBase=GRADLE_USER_HOME");
        picoWriter.writeln("distributionPath=wrapper/dists");
        picoWriter.writeln("distributionUrl=https\\://services.gradle.org/distributions/gradle-7.6.1-bin.zip");
        picoWriter.writeln("zipStoreBase=GRADLE_USER_HOME");
        picoWriter.writeln("zipStorePath=wrapper/dists");
        BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(locationURI+"\\gradle\\wrapper\\gradle-wrapper.properties"));
        bufferedWriter.write(picoWriter.toString());
        bufferedWriter.close();
        //acum vom crea setting.gradle
        picoWriter=new PicoWriter();
        picoWriter.writeln("rootProject.name='"+Artifact.getText()+"'");
        bufferedWriter=new BufferedWriter(new FileWriter(locationURI+"\\setting.gradle"));
        bufferedWriter.write(picoWriter.toString());
        bufferedWriter.close();

    }


    private void deleteRecursively(File file)
    {
        for(File sub: Objects.requireNonNull(file.listFiles()))
        {
            if(sub.isDirectory())
                deleteRecursively(sub);
            boolean delete = sub.delete();//daca nu e director stergem direct
        }
    }
    public void buildMainFiles() throws IOException {
        String tempURI=locationURI+"\\main";
        File f=new File(tempURI);
        if(!f.exists()){
            f.mkdir();
        }
        buildResources(tempURI+"\\resources");
        tempURI+="\\"+this.Language.getValue().toLowerCase();
        f=new File(tempURI);
        if(!f.exists()){
            f.mkdir();
        }
        String[] parts=this.PackageName.getText().split("\\.");
        for(String part:parts)
        {
            tempURI+="\\"+part;
            f=new File(tempURI);
            if(!f.exists()){
                f.mkdir();
            }
        }
        f=new File(tempURI+"\\"+StringUtils.capitalize(this.ProjectName.getText())+"."+langExtension);
        PicoWriter picoWriter=new PicoWriter();
        picoWriter.writeln("package "+this.PackageName.getText()+endChar);
        picoWriter.writeln("");
        picoWriter.writeln("import org.springframework.boot.autoconfigure.SpringBootApplication"+endChar);
        if(langExtension.equals("java"))
        {
            picoWriter.writeln("import org.springframework.boot.SpringApplication;");
            picoWriter.writeln("");
            picoWriter.writeln("@SpringBootApplication");
            picoWriter.writeln_r("public class "+StringUtils.capitalize(this.ProjectName.getText())+" {");
            picoWriter.writeln_r("public static void main(String[] args) {");
            picoWriter.writeln("SpringApplication.run("+StringUtils.capitalize(this.ProjectName.getText())+".class, args);");
            picoWriter.writeln_l("}");
        }
        else{
            picoWriter.writeln("import org.springframework.boot.runApplication");
            picoWriter.writeln("");
            picoWriter.writeln("@SpringBootApplication");
            picoWriter.writeln("class "+StringUtils.capitalize(this.ProjectName.getText()));
            picoWriter.writeln("");
            picoWriter.writeln_r("fun main(args: Array<String>) {");
            picoWriter.writeln("runApplication<"+StringUtils.capitalize(this.ProjectName.getText())+">(*args)");
        }
        picoWriter.writeln_l("}");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
        if(PackageType.getValue().equals("War"))//Daca impachetarea este War, trebuie Servlet
        {
            String servletName=StringUtils.capitalize(this.ProjectName.getText())+"ServletInitializer";
            f=new File(tempURI+"\\"+servletName+"."+langExtension);
            picoWriter=new PicoWriter();
            picoWriter.writeln("package "+this.PackageName.getText()+endChar);
            picoWriter.writeln("");
            if(langExtension.equals("java"))
            {
                picoWriter.writeln("import org.springframework.boot.builder.SpringApplicationBuilder;");
                picoWriter.writeln("import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;");
                picoWriter.writeln("");
                picoWriter.writeln_r("public class "+servletName+" extends SpringBootServletInitializer{");
                picoWriter.writeln("@Override");
                picoWriter.writeln_r("protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {");
                picoWriter.writeln("return application.sources("+StringUtils.capitalize(this.ProjectName.getText())+".class);");
            }
            else{
                picoWriter.writeln("import org.springframework.boot.builder.SpringApplicationBuilder");
                picoWriter.writeln("import org.springframework.boot.web.servlet.support.SpringBootServletInitializer");
                picoWriter.writeln("");
                picoWriter.writeln_r("class "+servletName+": SpringBootServletInitializer() {");
                picoWriter.writeln("");
                picoWriter.writeln_r("override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {");
                picoWriter.writeln("return application.sources("+StringUtils.capitalize(this.ProjectName.getText())+"::class.java)");
            }
            picoWriter.writeln_l("}");
            picoWriter.writeln_l("}");
            if(!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileWriter=new BufferedWriter(new FileWriter(f));
            fileWriter.write(picoWriter.toString());
            fileWriter.close();
        }
        if(!DatabaseType.getValue().equals("None")) {
            if (this.DomainList.getItems().size() != 0) {
                String domainsURI = tempURI + "\\domains";
                f = new File(domainsURI);
                if (!f.exists()) {
                    f.mkdir();
                }
                for (Domain domain : DomainList.getItems())
                    domain.write(domainsURI, langExtension,isJakarta,lombok,DatabaseType.getValue());
            }
            if (this.RepositoriesList.getItems().size() != 0) {
                String repositoriesURI = tempURI + "\\repositories";
                f = new File(repositoriesURI);
                if (!f.exists()) {
                    f.mkdir();
                }
                for (Repository repository : RepositoriesList.getItems())
                    repository.write(repositoriesURI, langExtension, DatabaseType.getValue());
            }
        }
        if (this.ServicesList.getItems().size() != 0) {
            String servicesURI = tempURI + "\\services";
            f = new File(servicesURI);
            if (!f.exists())
                f.mkdir();
            for (Service service : ServicesList.getItems())
                service.write(servicesURI, langExtension);
        }
        if (this.ControllerList.getItems().size() != 0) {
            String controllersURI = tempURI + "\\controllers";
            f = new File(controllersURI);
            if (!f.exists()) {
                f.mkdir();
            }
            for (Controller controller : ControllerList.getItems())
                controller.write(controllersURI, langExtension);
        }
    }

    private void buildResources(String resourceLocation) throws IOException {
        File f=new File(resourceLocation);
        if(!f.exists()){
            f.mkdir();
        }
        f=new File(resourceLocation+"\\application.properties");
        PicoWriter picoWriter=new PicoWriter();
        picoWriter.writeln("server.port=8080");
        if(!this.DatabaseLink.getText().equals("")) {
            if (this.DatabaseType.getValue().equals("MongoDB")) {
                picoWriter.writeln("spring.data.mongodb.uri="+this.DatabaseLink.getText());
                picoWriter.writeln("spring.data.mongodb.database="+this.ProjectName.getText());
            } else if (this.DatabaseType.getValue().equals("MariaDB")) {
                picoWriter.writeln("spring.datasource.url="+this.DatabaseLink.getText());
                picoWriter.writeln("spring.datasource.username="+this.usernameField.getText());
                picoWriter.writeln("spring.datasource.password="+this.passwordField.getText());
                picoWriter.writeln("spring.datasource.driver-class-name=org.mariadb.jdbc.Driver");
                picoWriter.writeln("spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect");
                picoWriter.writeln("spring.jpa.hibernate.ddl-auto=none");
                if(domainsHaveUUID())
                    picoWriter.writeln("spring.jpa.properties.hibernate.type.preferred_uuid_jdbc_type=CHAR");
            }
        }
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }
    private boolean domainsHaveUUID(){
        for(Domain domain : DomainList.getItems())
        {
            for(Pair<String,String> field : domain.getFields()){
                if(field.getValue().equals("UUID"))
                {
                    return true;
                }
            }
        }
        return false;
    }
    public void buildTestFiles() throws IOException {
        String tempURI=locationURI+"\\test";
        File f=new File(tempURI);
        if(!f.exists()){
            f.mkdir();
        }
        tempURI+="\\"+this.Language.getValue().toLowerCase(Locale.ROOT);
        f=new File(tempURI);
        if(!f.exists()){
            f.mkdir();
        }
        String[] parts=this.PackageName.getText().split("\\.");
        for(String part:parts)
        {
            tempURI+="\\"+part;
            f=new File(tempURI);
            if(!f.exists()){
                f.mkdir();
            }
        }
        tempURI+="\\"+StringUtils.capitalize(this.ProjectName.getText())+"Tests."+langExtension;
        f=new File(tempURI);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PicoWriter picoWriter=new PicoWriter();
        picoWriter.writeln("package "+StringUtils.capitalize(this.PackageName.getText())+endChar);
        picoWriter.writeln("");
        picoWriter.writeln("import org.junit.jupiter.api.Test"+endChar);
        picoWriter.writeln("import org.springframework.boot.test.context.SpringBootTest"+endChar);
        picoWriter.writeln("");
        picoWriter.writeln("@SpringBootTest");
        picoWriter.writeln_r("class "+StringUtils.capitalize(this.ProjectName.getText())+"Tests {");
        picoWriter.writeln("");
        picoWriter.writeln("@Test");
        if(langExtension.equals("java"))
            picoWriter.writeln_r("void contextLoads(){");
        else
            picoWriter.writeln_r("fun contextLoads(){");
        picoWriter.writeln_l("}");
        picoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }
    private void buildMaven() throws IOException {
        Model model=new Model();
        Writer writer = new FileWriter(locationURI+"\\pom.xml");
        model.setModelVersion("4.0.0");
        Parent parent = new Parent();
        parent.setGroupId("org.springframework.boot");
        parent.setArtifactId("spring-boot-starter-parent");
        parent.setVersion(this.SpringBootVersion.getValue());
        model.setParent(parent);

        model.setArtifactId(this.Artifact.getText());
        model.setGroupId(this.Group.getText());
        model.setName(this.ProjectName.getText());
        model.setVersion("0.0.1-SNAPSHOT");
        model.setDescription(this.Description.getText().equals("")?"Demo":this.Description.getText());
        model.setPackaging(this.PackageType.getValue().toLowerCase());
        Properties properties = new Properties();
        properties.setProperty("java.version",this.JavaVersion.getValue().toString());

        if(this.Language.getValue().equals("Kotlin"))
        {
            if (this.SpringBootVersion.getValue().startsWith("3"))
                properties.setProperty("kotlin.version","1.8.0");
            else
                properties.setProperty("kotlin.version","1.6.21");
        }

        model.setProperties(properties);

        List<Dependency> dependencyList=new ArrayList<>();
        Dependency dependency=new Dependency();
        dependency.setGroupId("org.springframework.boot");
        dependency.setArtifactId("spring-boot-starter-web");
        dependencyList.add(dependency);
        dependency=new Dependency();
        dependency.setGroupId("org.springframework.boot");
        dependency.setArtifactId("spring-boot-starter-test");
        dependency.setScope("test");
        dependencyList.add(dependency);
        if(PackageType.getValue().equals("War")) {
            dependency = new Dependency();
            dependency.setGroupId("org.springframework.boot");
            dependency.setArtifactId("spring-boot-starter-tomcat");
            dependency.setScope("provided");
            dependencyList.add(dependency);
        }
        if(!DatabaseType.getValue().equals("None"))
        {
            Dependency d=new Dependency();
            d.setGroupId("org.springframework.boot");
            d.setArtifactId("spring-boot-starter-validation");
            dependencyList.add(d);
        }
        if(DatabaseType.getValue().equals("MariaDB"))
        {
            Dependency d=new Dependency();
            d.setGroupId("org.springframework.boot");
            d.setArtifactId("spring-boot-starter-data-jpa");
            dependencyList.add(d);
            d=new Dependency();
            d.setGroupId("org.springframework.boot");
            d.setArtifactId("spring-boot-starter-data-rest");
            dependencyList.add(d);
            d=new Dependency();
            d.setGroupId("org.mariadb.jdbc");
            d.setArtifactId("mariadb-java-client");
            d.setScope("runtime");
            dependencyList.add(d);
        }
        else if(DatabaseType.getValue().equals("MongoDB"))
        {
            Dependency d=new Dependency();
            d.setGroupId("org.springframework.boot");
            d.setArtifactId("spring-boot-starter-data-mongodb");
            dependencyList.add(d);
        }
        if(!SpringBootVersion.getValue().startsWith("2"))
            isJakarta=true;
        for(Dependency d : DependenciesList.getItems())
        {
            if(d.getGroupId().equals("org.projectlombok") && d.getArtifactId().equals("lombok"))
                lombok=true;
            dependencyList.add(d);
        }
        if(langExtension.equals("kt"))
        {
            dependency=new Dependency();
            dependency.setGroupId("org.jetbrains.kotlin");
            dependency.setArtifactId("kotlin-reflect");
            dependencyList.add(dependency);
            dependency=new Dependency();
            dependency.setGroupId("org.jetbrains.kotlin");
            dependency.setArtifactId("kotlin-stdlib-jdk8");
            dependencyList.add(dependency);
            dependency=new Dependency();
            dependency.setGroupId("org.jetbrains.kotlin");
            dependency.setArtifactId("kotlin-stdlib");
            dependency.setVersion("${kotlin.version}");
            dependencyList.add(dependency);
            dependency=new Dependency();
            dependency.setGroupId("org.jetbrains.kotlin");
            dependency.setArtifactId("kotlin-test");
            dependency.setVersion("${kotlin.version}");
            dependency.setScope("test");
            dependencyList.add(dependency);
        }
        model.setDependencies(dependencyList);

        Build build = new Build();

        List<Plugin> pluginList=new ArrayList<>();
        Plugin plugin=new Plugin();
        plugin.setGroupId("org.springframework.boot");
        plugin.setArtifactId("spring-boot-maven-plugin");
        if(lombok)
        {
            Xpp3Dom exclusions=new Xpp3Dom("excludes");
            Xpp3Dom exclusion = new Xpp3Dom("exclude");
            Xpp3Dom groupId=new Xpp3Dom("groupId");
            groupId.setValue("org.projectlombok");
            Xpp3Dom artifactId=new Xpp3Dom("artifactId");
            artifactId.setValue("lombok");
            exclusion.addChild(groupId);
            exclusion.addChild(artifactId);
            exclusions.addChild(exclusion);
            Xpp3Dom config = new Xpp3Dom("configuration");
            config.addChild(exclusions);
            plugin.setConfiguration(config);
        }
        pluginList.add(plugin);

        if(langExtension.equals("kt"))
        {
            build.setSourceDirectory("${project.basedir}/src/main/kotlin");
            build.setTestSourceDirectory("${project.basedir}/src/test/kotlin");
            plugin=new Plugin();
            plugin.setGroupId("org.jetbrains.kotlin");
            plugin.setArtifactId("kotlin-maven-plugin");
            Xpp3Dom config = new Xpp3Dom("configuration");
            Xpp3Dom args=new Xpp3Dom("args");
            Xpp3Dom arg=new Xpp3Dom("arg");
            arg.setValue("-Xjsr305=strict");
            args.addChild(arg);
            config.addChild(args);

            Xpp3Dom compilerPlugins=new Xpp3Dom("compilerPlugins");
            Xpp3Dom compilerPlugin=new Xpp3Dom("plugin");
            compilerPlugin.setValue("spring");
            compilerPlugins.addChild(compilerPlugin);
            config.addChild(compilerPlugins);
            plugin.setConfiguration(config);
            List<Dependency> kotlinDependencies=new ArrayList<>();
            dependency=new Dependency();
            dependency.setGroupId("org.jetbrains.kotlin");
            dependency.setArtifactId("kotlin-maven-allopen");
            dependency.setVersion("${kotlin.version}");
            kotlinDependencies.add(dependency);
            plugin.setDependencies(kotlinDependencies);
            pluginList.add(plugin);
        }
        build.setPlugins(pluginList);
        model.setBuild(build);
        new MavenXpp3Writer().write(writer,model);
        writer.close();

    }
    public void onRemoveDependency() {
        this.DependenciesList.getItems().remove(this.DependenciesList.getSelectionModel().getSelectedItem());
    }

    public void onRemoveRepository() {
        Repository repository = this.RepositoriesList.getSelectionModel().getSelectedItem();
        this.RepositoriesList.getItems().remove(repository);
        this.repositoryCombobox.getItems().remove(repository);
        for(int i=0;i<ServicesList.getItems().size();i++)
        {
            Service service = ServicesList.getItems().get(i);
            boolean hasRepository =false;
            for(Repository repoElem : service.getRepositories()){
                if (repoElem.equals(repository)) {
                    hasRepository = true;
                    break;
                }
            }
            if(hasRepository) {
                Service newService = new Service(service);
                newService.getRepositories().remove(repository);
                for (Controller controller : ControllerList.getItems()) {
                    boolean hasService =false;
                    for(Service servElem : controller.getServices()){
                        if (servElem.equals(service)) {
                            hasService = true;
                            break;
                        }
                    }
                    if(hasService) {
                        int indx = controller.getServices().indexOf(service);
                        controller.getServices().remove(service);
                        controller.getServices().add(indx, newService);
                    }
                }
                ServicesList.getItems().remove(service);
                serviceCombobox.getItems().remove(service);
                ServicesList.getItems().add(i, newService);
                serviceCombobox.getItems().add(i, newService);
            }
        }
    }

    public void onRemoveDomain() {
        Domain domain=this.DomainList.getSelectionModel().getSelectedItem();
        this.DomainList.getItems().remove(domain);
        this.domainCombobox.getItems().remove(domain);

        List<Repository> repositoriesOfDomain = new ArrayList<>();
        for(Repository repository : RepositoriesList.getItems()){
            if(repository.getDomain().equals(domain))
                repositoriesOfDomain.add(repository);
        }

        for(int j=0;j<RepositoriesList.getItems().size();j++)
        {
            Repository repository = RepositoriesList.getItems().get(j);
            if(repository.getDomain().equals(domain)){
                for(int i=0;i<ServicesList.getItems().size();i++) {
                    Service service = ServicesList.getItems().get(i);
                    boolean hasRepository =false;
                    for(Repository repoElem : service.getRepositories()){
                        if (repoElem.equals(repository)) {
                            hasRepository = true;
                            break;
                        }
                    }
                    if (hasRepository) {
                        Service newService = new Service(service);
                        newService.getRepositories().remove(repository);
                        for (Controller controller : ControllerList.getItems()) {
                            boolean hasService =false;
                            for(Service servElem : controller.getServices()){
                                if (servElem.equals(service)) {
                                    hasService = true;
                                    break;
                                }
                            }
                            if (hasService) {
                                int indx = controller.getServices().indexOf(service);
                                controller.getServices().remove(service);
                                controller.getServices().add(indx, newService);
                            }
                        }
                        ServicesList.getItems().remove(service);
                        serviceCombobox.getItems().remove(service);
                        ServicesList.getItems().add(i, newService);
                        serviceCombobox.getItems().add(i, newService);
                    }
                }
            }
        }
        RepositoriesList.getItems().removeAll(repositoriesOfDomain);
        repositoryCombobox.getItems().removeAll(repositoriesOfDomain);


    }

    public void onRemoveService() {
        Service service=this.ServicesList.getSelectionModel().getSelectedItem();
        this.ServicesList.getItems().remove(service);
        this.serviceCombobox.getItems().remove(service);
        for(Controller controller : ControllerList.getItems())
            controller.getServices().remove(service);
    }

    public void onRemoveController() {
        this.ControllerList.getItems().remove(this.ControllerList.getSelectionModel().getSelectedItem());
    }

    public void onGroupOrArtifactTextChanged() {
        this.PackageName.setText(this.Group.getText()+"."+this.Artifact.getText());
        this.ProjectName.setText(this.Artifact.getText());
    }

    public void returnToMain() {
        for(Node n : mainAnchorPane.getChildren())
        {
            n.setDisable(false);
            if(DatabaseType.getValue()==null || DatabaseType.getValue().equals("None")){
                addDomainButton.setDisable(true);
                addRepositoryButton.setDisable(true);
            }
            if(!DatabaseType.getValue().equals("MariaDB")){
                usernameField.setDisable(true);
                passwordField.setDisable(true);
            }
        }
        domainAnchorPane.setVisible(false);
        domainAnchorPane.setDisable(true);
        repositoryAnchorPane.setVisible(false);
        repositoryAnchorPane.setDisable(true);
        serviceAnchorPane.setVisible(false);
        serviceAnchorPane.setDisable(true);
        controllerAnchorPane.setVisible(false);
        controllerAnchorPane.setDisable(true);
        dependencyAnchorPane.setVisible(false);
        dependencyAnchorPane.setDisable(true);
        dependencyGroupField.clear();
        dependencyArtifactField.clear();
        dependencyScopeCombobox.setValue(null);
        dependencyOptionalCombobox.setValue(null);
        dependencyTypeField.clear();
        dependencyVersionField.clear();
        DomainField.clear();
        fieldTextField.clear();
        RelationField.clear();
        typeCombobox.setValue(null);
        domainFieldTable.getItems().clear();
        RepositoryField.clear();
        domainCombobox.setValue(null);
        ServiceField.clear();
        repositoryCombobox.setValue(null);
        serviceRepositoryList.getItems().clear();
        ControllerField.clear();
        serviceCombobox.setValue(null);
        controllerServiceList.getItems().clear();
        relationCombobox.getItems().clear();
        relationCombobox.setValue(null);
        searchBar.clear();
        dependencySearchListView.getItems().clear();
        mustEdit=false;
    }
    private void popUp(AnchorPane pane)
    {
        for(Node n : mainAnchorPane.getChildren())
        {
            n.setDisable(true);
        }
        pane.setDisable(false);
        pane.setVisible(true);
    }
    public void OK_Dependency() {
        if(this.language.equals("ro")) {
            if (dependencyGroupField.getText().equals("")) {
                showMessageDialog(null, "Dependința nu are grupul setat!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (dependencyArtifactField.getText().equals(""))
            {
                showMessageDialog(null, "Dependința nu are artefactul setat!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        else
        {
            if (dependencyGroupField.getText().equals("")) {
                showMessageDialog(null, "The Dependency has no group!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (dependencyArtifactField.getText().equals(""))
            {
                showMessageDialog(null, "The Dependency has no artifact!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        Dependency dependency = new Dependency(){
            @Override
            public String toString(){
                if(this.getVersion()==null)
                    return this.getGroupId()+":"+this.getArtifactId();
                else
                    return this.getGroupId()+":"+this.getArtifactId()+":"+this.getVersion();
            }
        };
        dependency.setGroupId(dependencyGroupField.getText());
        dependency.setArtifactId(dependencyArtifactField.getText());
        if(!dependencyVersionField.getText().equals(""))
            dependency.setVersion(dependencyVersionField.getText());
        if(dependencyOptionalCombobox.getValue()!=null)
            dependency.setOptional(dependencyOptionalCombobox.getValue());
        if(dependencyScopeCombobox.getValue()!=null)
            dependency.setScope(dependencyScopeCombobox.getValue());
        if(!dependencyTypeField.getText().equals(""))
            dependency.setType(dependencyTypeField.getText());
        if(mustEdit){
            this.DependenciesList.getItems().add(DependenciesList.getSelectionModel().getSelectedIndex(),dependency);
            this.DependenciesList.getItems().remove(DependenciesList.getSelectionModel().getSelectedItem());
        }
        else{
            this.DependenciesList.getItems().add(dependency);
        }
        returnToMain();
    }
    public void OK_Domain() {
        ArrayList<String> idTypes=new ArrayList<>(Arrays.asList("String","Integer","Long","UUID"));
        if(this.language.equals("ro")) {
            if (DomainField.getText().equals("")) {
                showMessageDialog(null, "Domeniul nu are nume!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (domainFieldTable.getItems().size()==0)
            {
                showMessageDialog(null, "Domeniul nu are niciun atribut!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(!idTypes.contains(domainFieldTable.getItems().get(0).getValue()))
            {
                showMessageDialog(null, "Identificatorul nu poate fi de tipul "
                        +domainFieldTable.getItems().get(0).getValue()+"!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        else
        {
            if (DomainField.getText().equals("")) {
                showMessageDialog(null, "The Domain has no name!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (domainFieldTable.getItems().size()==0)
            {
                showMessageDialog(null, "The Domain has no fields!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(!idTypes.contains(domainFieldTable.getItems().get(0).getValue()))
            {
                showMessageDialog(null, "The Identifier can't be of "
                        +domainFieldTable.getItems().get(0).getValue()+" type!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        Domain domain = new Domain(DomainField.getText(),new ArrayList<>(domainFieldTable.getItems()));
        if(relationCombobox.getValue()!=null) {
            Domain domain2 = relationCombobox.getValue();
            if(!RelationField.getText().equals("") && this.DatabaseType.getValue().equals("MariaDB")) {
                ArrayList<Pair<String, String>> fields = new ArrayList<Pair<String, String>>();
                Pair<String, String> pair1 = domain.getFields().get(0);
                Pair<String, String> pair2 = domain2.getFields().get(0);
                if (pair1.getKey().equals(pair2.getKey()))//sa evitam confuzii
                {
                    pair1 = new Pair<>(domain.getName() + "_" + pair1.getKey(), pair1.getValue());
                    pair2 = new Pair<>(domain2.getName() + "_" + pair2.getKey(), pair2.getValue());
                }
                fields.add(pair1);
                fields.add(pair2);
                Domain relation = new Domain(RelationField.getText(), fields, true,domain,domain2);
                this.DomainList.getItems().add(relation);
                this.domainCombobox.getItems().add(relation);
            }
            else if(this.DatabaseType.getValue().equals("MongoDB")){
                domain = new Domain(DomainField.getText(),new ArrayList<>(domainFieldTable.getItems()),domain2);
            }
        }
        if(mustEdit) {
            Domain oldDomain=DomainList.getSelectionModel().getSelectedItem();
            this.DomainList.getItems().add(DomainList.getSelectionModel().getSelectedIndex(),domain);
            this.DomainList.getItems().remove(DomainList.getSelectionModel().getSelectedItem());
            this.domainCombobox.getItems().clear();
            for(Domain d:this.DomainList.getItems())
                this.domainCombobox.getItems().add(d);


            for(int j=0;j<RepositoriesList.getItems().size();j++)
            {
                Repository repository = RepositoriesList.getItems().get(j);
                if(repository.getDomain().equals(oldDomain)) {
                    for (int i = 0; i < ServicesList.getItems().size(); i++) {
                        Service service = ServicesList.getItems().get(i);
                        boolean hasRepository =false;
                        for(Repository repoElem : service.getRepositories()){
                            if (repoElem.equals(repository)) {
                                hasRepository = true;
                                break;
                            }
                        }
                        if (hasRepository) {
                            Service newService = new Service(service);
                            Repository newRepository = new Repository(repository.getName(), domain);
                            int indxRepository = newService.getRepositories().indexOf(repository);
                            newService.getRepositories().remove(repository);
                            newService.getRepositories().add(indxRepository, newRepository);
                            for (Controller controller : ControllerList.getItems()) {
                                boolean hasService =false;
                                for(Service servElem : controller.getServices()){
                                    if (servElem.equals(service)) {
                                        hasService = true;
                                        break;
                                    }
                                }
                                if (hasService) {
                                    int indx = controller.getServices().indexOf(service);
                                    controller.getServices().remove(service);
                                    controller.getServices().add(indx, newService);
                                }
                            }
                            ServicesList.getItems().remove(service);
                            serviceCombobox.getItems().remove(service);
                            ServicesList.getItems().add(i, newService);
                            serviceCombobox.getItems().add(i, newService);
                        }
                    }

                    Repository newRepository = new Repository(repository.getName(), domain);
                    RepositoriesList.getItems().remove(repository);
                    RepositoriesList.getItems().add(j, newRepository);
                    repositoryCombobox.getItems().remove(repository);
                    repositoryCombobox.getItems().add(j, newRepository);
                }
            }

        }
        else{
            this.DomainList.getItems().add(domain);
            this.domainCombobox.getItems().add(domain);
        }
        returnToMain();
    }

    public void OK_Repository() {
        if(this.language.equals("ro")) {
            if (RepositoryField.getText().equals("")) {
                showMessageDialog(null, "Repozitoriul nu are nume!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (domainCombobox.getValue()==null)
            {
                showMessageDialog(null, "Nu s-a selectat domeniul pentru repozitoriu!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        else
        {
            if (RepositoryField.getText().equals("")) {
                showMessageDialog(null, "The Repository has no name!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (domainCombobox.getValue()==null)
            {
                showMessageDialog(null, "The Repository has no domain set!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        try {
            Repository repository = new Repository(RepositoryField.getText(), domainCombobox.getValue());
            if (mustEdit) {
                Repository oldRepository = RepositoriesList.getSelectionModel().getSelectedItem();
                this.RepositoriesList.getItems().add(RepositoriesList.getSelectionModel().getSelectedIndex(), repository);
                this.RepositoriesList.getItems().remove(oldRepository);
                this.repositoryCombobox.getItems().clear();

                for(Repository r:RepositoriesList.getItems())
                    this.repositoryCombobox.getItems().add(r);

                for (int i = 0; i < ServicesList.getItems().size(); i++) {
                    Service service = ServicesList.getItems().get(i);
                    boolean hasRepository =false;
                    for(Repository repoElem : service.getRepositories()){
                        if (repoElem.equals(oldRepository)) {
                            hasRepository = true;
                            break;
                        }
                    }
                    if (hasRepository) {
                        Service newService = new Service(service);
                        int indxRepository = newService.getRepositories().indexOf(oldRepository);
                        newService.getRepositories().remove(oldRepository);
                        newService.getRepositories().add(indxRepository, repository);

                        for (Controller controller : ControllerList.getItems()) {
                            boolean hasService =false;
                            for(Service servElem : controller.getServices()){
                                if (servElem.equals(service)) {
                                    hasService = true;
                                    break;
                                }
                            }
                            if (hasService) {
                                int indx = controller.getServices().indexOf(service);
                                controller.getServices().remove(service);
                                controller.getServices().add(indx, newService);
                            }
                        }
                        ServicesList.getItems().remove(service);
                        serviceCombobox.getItems().remove(service);
                        ServicesList.getItems().add(i, newService);
                        serviceCombobox.getItems().add(i, newService);
                    }
                }
            } else {
                this.repositoryCombobox.getItems().add(repository);
                this.RepositoriesList.getItems().add(repository);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        returnToMain();
    }

    public void OK_Service() {
        if(this.language.equals("ro")) {
            if (ServiceField.getText().equals("")) {
                showMessageDialog(null, "Serviciul nu are nume!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        else
        {
            if (ServiceField.getText().equals("")) {
                showMessageDialog(null, "The Service has no name!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        Service service = new Service(ServiceField.getText(),new ArrayList<>(serviceRepositoryList.getItems()));
        if(mustEdit) {
            Service oldService = ServicesList.getSelectionModel().getSelectedItem();
            this.ServicesList.getItems().add(ServicesList.getSelectionModel().getSelectedIndex(),service);
            this.ServicesList.getItems().remove(oldService);

            this.serviceCombobox.getItems().clear();
            for(Service s:ServicesList.getItems())
                this.serviceCombobox.getItems().add(s);

            for (Controller controller : ControllerList.getItems()) {
                boolean hasService =false;
                for(Service servElem : controller.getServices()){
                    if (servElem.equals(oldService)) {
                        hasService = true;
                        break;
                    }
                }
                if(hasService) {
                    int indx = controller.getServices().indexOf(oldService);
                    controller.getServices().remove(oldService);
                    controller.getServices().add(indx, service);
                }
            }
        }
        else {
            this.serviceCombobox.getItems().add(service);
            this.ServicesList.getItems().add(service);
        }
        returnToMain();
    }

    public void OK_Controller() {
        if(this.language.equals("ro")) {
            if (ControllerField.getText().equals("")) {
                showMessageDialog(null, "Controller-ul nu are nume!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        else
        {
            if (ControllerField.getText().equals("")) {
                showMessageDialog(null, "The Controller has no name!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        Controller controller=new Controller(ControllerField.getText(),new ArrayList<>(controllerServiceList.getItems()));
        if(mustEdit) {
            this.ControllerList.getItems().add(ControllerList.getSelectionModel().getSelectedIndex(),controller);
            this.ControllerList.getItems().remove(ControllerList.getSelectionModel().getSelectedItem());
        }
        else
            this.ControllerList.getItems().add(controller);
        returnToMain();
    }

    public void onAddField() {
        if(this.language.equals("ro")) {
            if (typeCombobox.getValue() == null) {
                showMessageDialog(null, "Nu ati setat tipul de data al atributului!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (fieldTextField.getText().equals(""))
            {
                showMessageDialog(null, "Nu ati setat numele atributului!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        else
        {
            if (typeCombobox.getValue() == null) {
                showMessageDialog(null, "You have not set the datatype of the attribute!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (fieldTextField.getText().equals(""))
            {
                showMessageDialog(null, "You have not set the attribute name!", "WARNING", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        Pair<String, String> p = new Pair<String,String>(fieldTextField.getText(),typeCombobox.getValue());
        for (Pair<String, String> pair : domainFieldTable.getItems())
        {
            if(pair.getKey().equals(p.getKey()))
                return;
        }
        if(!domainFieldTable.getItems().contains(p))
            domainFieldTable.getItems().add(p);
    }

    public void onAddRepositoryInService() {
        if(repositoryCombobox.getValue() !=null && !serviceRepositoryList.getItems().contains(repositoryCombobox.getValue()))
            serviceRepositoryList.getItems().add(repositoryCombobox.getValue());
    }

    public void onAddServiceInController() {
        if(serviceCombobox.getValue()!=null && !controllerServiceList.getItems().contains(serviceCombobox.getValue()))
            controllerServiceList.getItems().add(serviceCombobox.getValue());
    }


    public void onUpdateDependency() {
        mustEdit=true;
        Dependency dependency = this.DependenciesList.getSelectionModel().getSelectedItem();
        if(dependency!=null)
        {
            popUp(dependencyAnchorPane);
            dependencyGroupField.setText(dependency.getGroupId());
            dependencyArtifactField.setText(dependency.getArtifactId());
            if(dependency.getType() != null)
                dependencyTypeField.setText(dependency.getType());
            if(dependency.getScope() != null)
                dependencyScopeCombobox.setValue(dependency.getScope());
            if(dependency.getVersion()!=null)
                dependencyVersionField.setText(dependency.getVersion());
            dependencyOptionalCombobox.setValue(dependency.isOptional());
        }
    }

    public void onUpdateRepository() {
        mustEdit=true;
        Repository repository = this.RepositoriesList.getSelectionModel().getSelectedItem();
        if(repository!=null) {
            popUp(repositoryAnchorPane);
            RepositoryField.setText(repository.getName());
            domainCombobox.setValue(repository.getDomain());
        }
    }

    public void onUpdateDomain() {
        mustEdit=true;
        Domain domain = this.DomainList.getSelectionModel().getSelectedItem();
        if(domain!=null) {
            for(Domain d : DomainList.getItems())
            {
                if(!domain.equals(d))
                    relationCombobox.getItems().add(d);
            }
            if(domain.getRelationClass()!=null)
                relationCombobox.setValue(domain.getRelationClass());
            popUp(domainAnchorPane);
            DomainField.setText(domain.getName());
            domainFieldTable.setItems(FXCollections.observableArrayList(domain.getFields()));
        }
    }

    public void onUpdateService() {
        mustEdit=true;
        Service service = this.ServicesList.getSelectionModel().getSelectedItem();
        if(service!=null) {
            popUp(serviceAnchorPane);
            ServiceField.setText(service.getName());
            serviceRepositoryList.setItems(FXCollections.observableArrayList(service.getRepositories()));
        }
    }

    public void onUpdateController() {
        mustEdit=true;
        Controller controller = this.ControllerList.getSelectionModel().getSelectedItem();
        if(controller!=null) {
            popUp(controllerAnchorPane);
            ControllerField.setText(controller.getName());
            controllerServiceList.setItems(FXCollections.observableArrayList(controller.getServices()));
        }
    }

    public void onRemoveServiceFromController() {
        Service service=this.controllerServiceList.getSelectionModel().getSelectedItem();
        this.controllerServiceList.getItems().remove(service);
    }

    public void onRemoveRepositoryFromService() {
        Repository repository=this.serviceRepositoryList.getSelectionModel().getSelectedItem();
        this.serviceRepositoryList.getItems().remove(repository);
    }

    public void onRemoveFieldFromDomain() {
        this.domainFieldTable.getItems().remove(this.domainFieldTable.getSelectionModel().getSelectedItem());
    }
    public void onMakeId() {
        Pair<String,String> field = this.domainFieldTable.getSelectionModel().getSelectedItem();
        this.domainFieldTable.getItems().remove(field);
        this.domainFieldTable.getItems().add(0,field);
    }
    public void onDatabaseTypeChanged() {

        this.RelationField.setDisable(!this.DatabaseType.getValue().equals("MariaDB"));
        this.addDomainButton.setDisable(this.DatabaseType.getValue()!=null && this.DatabaseType.getValue().equals("None"));
        this.addRepositoryButton.setDisable(this.DatabaseType.getValue()!=null && this.DatabaseType.getValue().equals("None"));
        this.usernameField.setDisable(!this.DatabaseType.getValue().equals("MariaDB"));
        this.passwordField.setDisable(!this.DatabaseType.getValue().equals("MariaDB"));
        this.DomainList.getItems().clear();
        this.domainCombobox.getItems().clear();
        this.relationCombobox.getItems().clear();
        this.RepositoriesList.getItems().clear();
        this.repositoryCombobox.getItems().clear();
        this.serviceRepositoryList.getItems().clear();

        if(!ServicesList.getItems().isEmpty())
        {
            for(int i=0;i<ServicesList.getItems().size();i++)
            {
                Service service =ServicesList.getItems().get(i);
                service.getRepositories().clear();
            }
        }
        if(!ControllerList.getItems().isEmpty())
        {
            for(int i =0;i<ControllerList.getItems().size();i++){
                Controller controller=ControllerList.getItems().get(i);
                for(int j=0;j<controller.getServices().size();j++)
                {
                    Service service =controller.getServices().get(j);
                    service.getRepositories().clear();
                }
            }
        }
    }

    public void onEnglishClick() {
        this.language="en";
        this.GenerateButton.setText("Generate");
        this.addControllerButton.setText("Add controller");
        this.addDependencyButton.setText("Add dependency");
        this.addServiceButton.setText("Add service");
        this.addDomainButton.setText("Add domain");
        this.addRepositoryButton.setText("Add repository");
        this.ProjectManager.setPromptText("Project Manager");
        this.DatabaseType.setPromptText("Database Type");
        this.Language.setPromptText("Language");
        this.JavaVersion.setPromptText("Java Version");
        this.PackageType.setPromptText("Package Type");
        this.SpringBootVersion.setPromptText("Spring Version");
        this.Artifact.setPromptText("Artifact");
        this.PackageName.setPromptText("Package Name");
        this.Description.setPromptText("Description");
        this.Group.setPromptText("Group");
        this.ProjectName.setPromptText("Project Name");
        this.DomainField.setPromptText("Domain");
        this.button_addField.setText("Add field");
        this.ServiceField.setPromptText("Service");
        this.RepositoryField.setPromptText("Repository");
        this.DatabaseLink.setPromptText("Database Link");
        this.RepositoryLabel.setText("Repositories");
        this.ControllerLabel.setText("Controllers");
        this.DependencyLabel.setText("Dependencies");
        this.ServiceLabel.setText("Services");
        this.DomainLabel.setText("Domains");
        this.domainCancelButton.setText("Cancel");
        this.repositoryCancelButton.setText("Cancel");
        this.serviceCancelButton.setText("Cancel");
        this.controllerCancelButton.setText("Cancel");
        this.typeCombobox.setPromptText("Type");
        this.fieldTextField.setPromptText("Field Name");
        this.domainCombobox.setPromptText("Domain");
        this.repositoryCombobox.setPromptText("Repository");
        this.button_addRepository.setText("Add repository");
        this.serviceCombobox.setPromptText("Service");
        this.button_addService.setText("Add service");
        this.DependencyContextUpdate.setText("Update");
        this.DependencyContextDelete.setText("Delete");
        this.DomainContextUpdate.setText("Update");
        this.DomainContextDelete.setText("Delete");
        this.RepositoryContextUpdate.setText("Update");
        this.RepositoryContextDelete.setText("Delete");
        this.ServiceContextUpdate.setText("Update");
        this.ServiceContextDelete.setText("Delete");
        this.ControllerContextUpdate.setText("Update");
        this.ControllerContextDelete.setText("Delete");
        this.columnName.setText("Name");
        this.columnType.setText("Type");
        this.ControllerContextServiceDelete.setText("Delete");
        this.ServiceContextRepositoryDelete.setText("Delete");
        this.DomainContextFieldDelete.setText("Delete");
        this.RelationField.setPromptText("Relation Name");
        this.relationCombobox.setPromptText("Relation");
        this.ControllerField.setPromptText("Controller");

        this.dependencyArtifactField.setPromptText("Artifact");
        this.dependencyTypeField.setPromptText("Type");
        this.dependencyScopeCombobox.setPromptText("Scope");
        this.dependencyGroupField.setPromptText("Group");
        this.dependencyVersionField.setPromptText("Version");
        this.dependencyCancelButton.setText("Cancel");
        this.searchBar.setPromptText("Search dependency");
        this.DomainContextFieldId.setText("Make it ID");
        this.usernameField.setPromptText("MariaDB Username");
        this.passwordField.setPromptText("MariaDB Password");
    }

    public void onRomanianClick() {
        this.language="ro";
        this.GenerateButton.setText("Generează");
        this.addControllerButton.setText("Adaugă controller");
        this.addDependencyButton.setText("Adaugă dependință");
        this.addServiceButton.setText("Adaugă serviciu");
        this.addDomainButton.setText("Adaugă domeniu");
        this.addRepositoryButton.setText("Adaugă repozitoriu");
        this.ProjectManager.setPromptText("Gestionar de proiect");
        this.DatabaseType.setPromptText("Tip bază de date");
        this.Language.setPromptText("Limbaj de programare");
        this.JavaVersion.setPromptText("Versiune Java");
        this.PackageType.setPromptText("Împachetare");
        this.SpringBootVersion.setPromptText("Versiune Spring");
        this.Artifact.setPromptText("Artefact");
        this.PackageName.setPromptText("Nume pachet");
        this.Description.setPromptText("Descriere");
        this.Group.setPromptText("Grup");
        this.ProjectName.setPromptText("Nume proiect");
        this.DomainField.setPromptText("Domeniu");
        this.ServiceField.setPromptText("Serviciu");
        this.RepositoryField.setPromptText("Repozitoriu");
        this.DatabaseLink.setPromptText("Link către bază de date");
        this.RepositoryLabel.setText("Repozitorii");
        this.ControllerLabel.setText("Controller-e");
        this.DependencyLabel.setText("Dependințe");
        this.ServiceLabel.setText("Servicii");
        this.DomainLabel.setText("Domenii");
        this.button_addField.setText("Adaugă atribut");
        this.domainCancelButton.setText("Anulează");
        this.repositoryCancelButton.setText("Anulează");
        this.serviceCancelButton.setText("Anulează");
        this.controllerCancelButton.setText("Anulează");
        this.typeCombobox.setPromptText("Tip atribut");
        this.fieldTextField.setPromptText("Nume atribut");
        this.domainCombobox.setPromptText("Domeniu");
        this.repositoryCombobox.setPromptText("Repozitoriu");
        this.button_addRepository.setText("Adaugă repozitoriu");
        this.serviceCombobox.setPromptText("Serviciu");
        this.button_addService.setText("Adaugă serviciu");
        this.DependencyContextUpdate.setText("Modifică");
        this.DependencyContextDelete.setText("Șterge");
        this.DomainContextUpdate.setText("Modifică");
        this.DomainContextDelete.setText("Șterge");
        this.RepositoryContextUpdate.setText("Modifică");
        this.RepositoryContextDelete.setText("Șterge");
        this.ServiceContextUpdate.setText("Modifică");
        this.ServiceContextDelete.setText("Șterge");
        this.ControllerContextUpdate.setText("Modifică");
        this.ControllerContextDelete.setText("Șterge");
        this.columnName.setText("Nume");
        this.columnType.setText("Tip");
        this.ControllerContextServiceDelete.setText("Șterge");
        this.ServiceContextRepositoryDelete.setText("Șterge");
        this.DomainContextFieldDelete.setText("Șterge");
        this.RelationField.setPromptText("Nume Relație");
        this.relationCombobox.setPromptText("Relație");
        this.ControllerField.setPromptText("Nume controller");

        this.dependencyArtifactField.setPromptText("Artefact");
        this.dependencyTypeField.setPromptText("Tip");
        this.dependencyScopeCombobox.setPromptText("Scop");
        this.dependencyGroupField.setPromptText("Grup");
        this.dependencyVersionField.setPromptText("Versiune");
        this.dependencyCancelButton.setText("Anulează");
        this.searchBar.setPromptText("Caută dependința");
        this.DomainContextFieldId.setText("Declară-l identificator unic");
        this.usernameField.setPromptText("Nume de utilizator MariaDB");
        this.passwordField.setPromptText("Parolă MariaDB");
    }

    public void onFinishedSearch() {
        dependencySearchListView.getItems().clear();
        if(!searchBar.getText().equals("")) {
            try {
                URL url = new URL("https://search.maven.org/solrsearch/select?q=" + searchBar.getText()+"&rows=100");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder json= new StringBuilder();
                    int ch;
                    while((ch= reader.read()) != -1)
                        json.append((char)ch);
                    JSONObject obj=new JSONObject(json.toString());
                    JSONObject response=obj.getJSONObject("response");
                    JSONArray array=response.getJSONArray("docs");
                    for (int i=0;i<array.length();i++)
                    {
                        JSONObject dep=new JSONObject(){
                            @Override
                            public String toString(){
                                if(this.getString("latestVersion")==null)
                                    return this.getString("g")+":"+this.getString("a");
                                else
                                    return this.getString("g")+":"+this.getString("a")+":"+this.getString("latestVersion");
                            }
                        };
                        for(String key : array.getJSONObject(i).keySet())
                            dep.put(key,array.getJSONObject(i).get(key));
                        dependencySearchListView.getItems().add(dep);
                    }
                }
            } catch (Exception e) {
                if(this.language.equals("ro"))
                    showMessageDialog(null, "Nu s-a stabilit conexiunea!", "EROARE", JOptionPane.ERROR_MESSAGE);
                else
                    showMessageDialog(null, "No connection!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onSelectDependency() {
        JSONObject dep = dependencySearchListView.getSelectionModel().getSelectedItem();
        this.dependencyGroupField.setText(dep.getString("g"));//group
        this.dependencyArtifactField.setText(dep.getString("a"));//artifact
        this.dependencyVersionField.setText(dep.getString("latestVersion"));
        this.dependencyTypeField.setText(dep.getString("p"));//type/packaging
    }


}