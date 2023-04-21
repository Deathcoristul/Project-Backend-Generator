package com.app.generator;

import com.app.generator.util.*;
import com.app.generator.util.Repository;

import io.spring.initializr.generator.buildsystem.gradle.*;
import io.spring.initializr.generator.io.IndentingWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.ainslec.picocog.*;
import javax.swing.*;
import java.io.*;

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
//TODO verificari erori la generare si care ar afecta runtime
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
        this.SpringBootVersion.getItems().setAll("3.0.3-SNAPSHOT","3.0.2","2.7.9-SNAPSHOT","2.7.8");
        this.DatabaseType.getItems().setAll("None","MongoDB","MySQL");
        this.typeCombobox.getItems().setAll("String","Integer","Boolean","UUID","Double","Date");
        this.dependencyOptionalCombobox.getItems().setAll(true,false);
        this.dependencyScopeCombobox.getItems().setAll("compile","provided","runtime","test","system");
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
                showMessageDialog(null, "The project has not a link for the database set!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(this.SpringBootVersion.getValue().startsWith("3") && this.JavaVersion.getValue()<17)
            {
                showMessageDialog(null, "For Spring Boot Version "+this.SpringBootVersion.getValue()+", you must use a Java version at least 17!", "ATENȚIE", JOptionPane.WARNING_MESSAGE);
                return;
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
                    showMessageDialog(null,"Proiectul s-a generat!","SUCCES", JOptionPane.WARNING_MESSAGE);
                else
                    showMessageDialog(null,"The project has been generated!","SUCCES", JOptionPane.WARNING_MESSAGE);
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

    private void buildGradle() {
        //TODO Gradle
        GradleBuild build=new GradleBuild();
        //Gradle
        StandardGradlePlugin plugin=new StandardGradlePlugin("org.springframework.boot");
        plugin.setVersion(this.SpringBootVersion.getValue());
        build.properties().property("version","0.0.1-SNAPSHOT");
        //IndentingWriter indentingWriter = new IndentingWriter();
        GroovyDslGradleBuildWriter writer = new GroovyDslGradleBuildWriter();
        //writer.writeTo(build,new File("build.gradle"));
        //build
    }


    private void deleteRecursively(File file)
    {
        for(File sub: Objects.requireNonNull(file.listFiles()))
        {
            if(sub.isDirectory())
                deleteRecursively(sub);
            sub.delete();//daca nu e director stergem direct
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
                picoWriter.writeln_r("public class "+servletName+" extends extends SpringBootServletInitializer{");
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
            } else if (this.DatabaseType.getValue().equals("MySQL")) {
                picoWriter.writeln("spring.datasource.url="+this.DatabaseLink.getText());
                picoWriter.writeln("spring.datasource.username={username}");
                picoWriter.writeln("spring.datasource.password={password}");
                picoWriter.writeln("spring.datasource.driver-class-name=org.mariadb.jdbc.Driver");
                picoWriter.writeln("spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect");
                picoWriter.writeln("spring.jpa.hibernate.ddl-auto=none");
            }
        }
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
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
        picoWriter.writeln("package "+StringUtils.capitalize(this.ProjectName.getText())+endChar);
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
            properties.setProperty("kotlin.version","1.8.0");
        model.setProperties(properties);

        List<Dependency> dependencyList=new ArrayList<>();
        Dependency dependency=new Dependency();
        dependency.setGroupId("org.springframework.boot");
        dependency.setArtifactId("spring-boot-starter");
        dependencyList.add(dependency);
        dependency=new Dependency();
        dependency.setGroupId("org.springframework.boot");
        dependency.setArtifactId("spring-boot-starter-web");
        dependencyList.add(dependency);
        dependency=new Dependency();
        dependency.setGroupId("org.springframework.boot");
        dependency.setArtifactId("spring-boot-starter-test");
        dependency.setScope("test");
        dependencyList.add(dependency);
        if(!DatabaseType.getValue().equals("None"))
        {
            Dependency d=new Dependency();
            d.setGroupId("org.springframework.boot");
            d.setArtifactId("spring-boot-starter-validation");
            dependencyList.add(d);
        }
        if(DatabaseType.getValue().equals("MySQL"))
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
            if(d.getGroupId().equals("org.projectlombok") && d.getArtifactId().equals("lombok") && d.isOptional())
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
            Xpp3Dom cplugin=new Xpp3Dom("plugin");
            cplugin.setValue("spring");
            compilerPlugins.addChild(cplugin);
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
    }
    public void onRemoveDependency() {
        this.DependenciesList.getItems().remove(this.DependenciesList.getSelectionModel().getSelectedItem());
    }

    public void onRemoveRepository() {
        Repository repository = this.RepositoriesList.getSelectionModel().getSelectedItem();
        this.RepositoriesList.getItems().remove(repository);
        this.repositoryCombobox.getItems().remove(repository);
    }

    public void onRemoveDomain() {
        Domain domain=this.DomainList.getSelectionModel().getSelectedItem();
        this.DomainList.getItems().remove(domain);
        this.domainCombobox.getItems().remove(domain);
    }

    public void onRemoveService() {
        Service service=this.ServicesList.getSelectionModel().getSelectedItem();
        this.ServicesList.getItems().remove(service);
        this.serviceCombobox.getItems().remove(service);
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
    public void OK_Dependency(ActionEvent actionEvent) {
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
    public void OK_Domain(ActionEvent actionEvent) {
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
        }
        Domain domain = new Domain(DomainField.getText(),new ArrayList<>(domainFieldTable.getItems()));
        if(relationCombobox.getValue()!=null) {
            Domain domain2 = relationCombobox.getValue();
            if(!RelationField.getText().equals("") && this.DatabaseType.getValue().equals("MySQL")) {
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
                Domain relation = new Domain(RelationField.getText(), fields, true);
                this.DomainList.getItems().add(relation);
                this.domainCombobox.getItems().add(relation);
            }
            else if(this.DatabaseType.getValue().equals("MongoDB")){
                domain = new Domain(DomainField.getText(),new ArrayList<>(domainFieldTable.getItems()),domain2);
            }
        }
        if(mustEdit) {
            this.DomainList.getItems().add(DomainList.getSelectionModel().getSelectedIndex(),domain);
            this.DomainList.getItems().remove(DomainList.getSelectionModel().getSelectedItem());
            this.domainCombobox.getItems().clear();
            for(Domain d:this.DomainList.getItems())
                this.domainCombobox.getItems().add(d);
        }
        else{
            this.DomainList.getItems().add(domain);
            this.domainCombobox.getItems().add(domain);
        }

        returnToMain();
    }

    public void OK_Repository(ActionEvent actionEvent) {
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
        Repository repository = new Repository(RepositoryField.getText(),domainCombobox.getValue());
        if(mustEdit) {
            this.RepositoriesList.getItems().add(RepositoriesList.getSelectionModel().getSelectedIndex(),repository);
            this.RepositoriesList.getItems().remove(RepositoriesList.getSelectionModel().getSelectedItem());
            this.repositoryCombobox.setItems(this.RepositoriesList.getItems());
        }
        else{
            this.repositoryCombobox.getItems().add(repository);
            this.RepositoriesList.getItems().add(repository);
        }

        returnToMain();
    }

    public void OK_Service(ActionEvent actionEvent) {
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
            this.ServicesList.getItems().add(ServicesList.getSelectionModel().getSelectedIndex(),service);
            this.ServicesList.getItems().remove(ServicesList.getSelectionModel().getSelectedItem());
            this.serviceCombobox.setItems(this.ServicesList.getItems());
        }
        else {
            this.serviceCombobox.getItems().add(service);
            this.ServicesList.getItems().add(service);
        }
        returnToMain();
    }

    public void OK_Controller(ActionEvent actionEvent) {
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

    public void onAddField(ActionEvent actionEvent) {
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

    public void onAddRepositoryInService(ActionEvent actionEvent) {
        if(repositoryCombobox.getValue() !=null && !serviceRepositoryList.getItems().contains(repositoryCombobox.getValue()))
            serviceRepositoryList.getItems().add(repositoryCombobox.getValue());
    }

    public void onAddServiceInController(ActionEvent actionEvent) {
        if(serviceCombobox.getValue()!=null && !controllerServiceList.getItems().contains(serviceCombobox.getValue()))
            controllerServiceList.getItems().add(serviceCombobox.getValue());
    }


    public void onUpdateDependency(ActionEvent actionEvent) {
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

    public void onUpdateRepository(ActionEvent actionEvent) {
        mustEdit=true;
        Repository repository = this.RepositoriesList.getSelectionModel().getSelectedItem();
        if(repository!=null) {
            popUp(repositoryAnchorPane);
            RepositoryField.setText(repository.getName());
            domainCombobox.setValue(repository.getDomain());
        }
    }

    public void onUpdateDomain(ActionEvent actionEvent) {
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

    public void onUpdateService(ActionEvent actionEvent) {
        mustEdit=true;
        Service service = this.ServicesList.getSelectionModel().getSelectedItem();
        if(service!=null) {
            popUp(serviceAnchorPane);
            ServiceField.setText(service.getName());
            serviceRepositoryList.setItems(FXCollections.observableArrayList(service.getRepositories()));
        }
    }

    public void onUpdateController(ActionEvent actionEvent) {
        mustEdit=true;
        Controller controller = this.ControllerList.getSelectionModel().getSelectedItem();
        if(controller!=null) {
            popUp(controllerAnchorPane);
            ControllerField.setText(controller.getName());
            controllerServiceList.setItems(FXCollections.observableArrayList(controller.getServices()));
        }
    }

    public void onRemoveServiceFromController(ActionEvent actionEvent) {
        Service service=this.controllerServiceList.getSelectionModel().getSelectedItem();
        this.controllerServiceList.getItems().remove(service);
    }

    public void onRemoveRepositoryFromService(ActionEvent actionEvent) {
        Repository repository=this.serviceRepositoryList.getSelectionModel().getSelectedItem();
        this.serviceRepositoryList.getItems().remove(repository);
    }

    public void onRemoveFieldFromDomain(ActionEvent actionEvent) {
        this.domainFieldTable.getItems().remove(this.domainFieldTable.getSelectionModel().getSelectedItem());
    }

    public void onDatabaseTypeChanged(ActionEvent actionEvent) {

        this.RelationField.setDisable(!this.DatabaseType.getValue().equals("MySQL"));
        this.addDomainButton.setDisable(this.DatabaseType.getValue()!=null && this.DatabaseType.getValue().equals("None"));
        this.addRepositoryButton.setDisable(this.DatabaseType.getValue()!=null && this.DatabaseType.getValue().equals("None"));

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
                Service oldS =ServicesList.getItems().get(i);
                Service newS =new Service(ServicesList.getItems().get(i).getName());
                ServicesList.getItems().remove(oldS);
                controllerServiceList.getItems().remove(oldS);
                ServicesList.getItems().add(i,newS);
                controllerServiceList.getItems().add(i,newS);
            }
        }
        if(!ControllerList.getItems().isEmpty())
        {
            for(int i =0;i<ControllerList.getItems().size();i++){
                Controller oldC=ControllerList.getItems().get(i);
                Controller newC=new Controller(oldC.getName());
                ControllerList.getItems().remove(oldC);
                ControllerList.getItems().add(i,newC);
            }
        }
    }

    public void onEnglishClick(MouseEvent mouseEvent) {
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
    }

    public void onRomanianClick(MouseEvent mouseEvent) {
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
    }

    public void onFinishedSearch(ActionEvent mouseEvent) {
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

    public void onSelectDependency(MouseEvent mouseEvent) {
        JSONObject dep = dependencySearchListView.getSelectionModel().getSelectedItem();
        this.dependencyGroupField.setText(dep.getString("g"));
        this.dependencyArtifactField.setText(dep.getString("a"));
        this.dependencyVersionField.setText(dep.getString("latestVersion"));
    }
}