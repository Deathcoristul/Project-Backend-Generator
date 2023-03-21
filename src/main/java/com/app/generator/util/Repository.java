package com.app.generator.util;

import org.ainslec.picocog.PicoWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Repository {
    private String name;
    private Domain domain;

    public Repository(String name, Domain domain) {
        this.name = name;
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString()
    {
        return name;
    }
    public void write(String repositoriesURI,String lang,String database) throws IOException {
        String[] parts=repositoriesURI.split("\\\\");
        String pckg=parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+"."+parts[parts.length-1];
        PicoWriter picoWriter=new PicoWriter();
        File f=new File(repositoriesURI+"\\"+StringUtils.capitalize(this.name)+"."+lang);
        if(lang.equals("kt"))
        {
            picoWriter.writeln("package "+pckg);
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*");
            picoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".domains.*");
            if(database.equals("MySQL")){
                picoWriter.writeln("import org.springframework.data.jpa.repository.JpaRepository");
                picoWriter.writeln_r("interface "+ StringUtils.capitalize(this.name)+
                        " : JpaRepository<"+this.domain.getName()+","+this.domain.getFields().get(0).getValue()+"> {");
            }
            else{
                picoWriter.writeln("import org.springframework.data.mongodb.repository.MongoRepository");
                picoWriter.writeln_r("interface "+ StringUtils.capitalize(this.name)+
                        " : MongoRepository<"+this.domain.getName()+","+this.domain.getFields().get(0).getValue()+"> {");
            }
        }
        else{
            picoWriter.writeln("package "+pckg+";");
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*;");
            picoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".domains.*;");
            if(database.equals("MySQL")){
                picoWriter.writeln("import org.springframework.data.jpa.repository.JpaRepository;");
                picoWriter.writeln_r("public interface "+ StringUtils.capitalize(this.name)+
                        " extends JpaRepository<"+this.domain.getName()+","+this.domain.getFields().get(0).getValue()+"> {");
            }
            else{
                picoWriter.writeln("import org.springframework.data.mongodb.repository.MongoRepository;");
                picoWriter.writeln_r("public interface "+ StringUtils.capitalize(this.name)+
                        " extends MongoRepository<"+this.domain.getName()+","+this.domain.getFields().get(0).getValue()+"> {");
            }
        }
        picoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }
}
