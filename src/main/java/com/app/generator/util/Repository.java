package com.app.generator.util;

import org.ainslec.picocog.PicoWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

public class Repository {
    private final String name;
    private final Domain domain;

    public Repository(String name, Domain domain) {
        this.name = name;
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
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
        boolean langFolderFound=false;
        StringBuilder packageBuilder = new StringBuilder();
        for(String s : parts)
        {
            if(langFolderFound)
            {
                packageBuilder.append(s+='.');
            }
            if(lang.equals("kt"))
            {
                if (s.equals("kotlin"))
                    langFolderFound=true;
            }
            else {
                if (s.equals("java"))
                    langFolderFound=true;
            }
        }
        packageBuilder.deleteCharAt(packageBuilder.length()-1);
        String pckg=packageBuilder.toString();
        List<String> packageparts=new ArrayList<>(Arrays.asList(pckg.split("\\.")));
        packageparts.remove(packageparts.size()-1);//eliminam termenul de repositories
        String GroupAndArtefact=String.join(".",packageparts);
        PicoWriter picoWriter=new PicoWriter();
        File f=new File(repositoriesURI+"\\"+StringUtils.capitalize(this.name)+"."+lang);
        if(lang.equals("kt"))
        {
            picoWriter.writeln("package "+pckg);
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*");
            picoWriter.writeln("import "+GroupAndArtefact+".domains.*");
            if(database.equals("MySQL")){
                picoWriter.writeln("import org.springframework.data.jpa.repository.JpaRepository");
                picoWriter.writeln_r("interface "+ StringUtils.capitalize(this.name)+
                        " : JpaRepository<"+StringUtils.capitalize(this.domain.getName())+","+this.domain.getFields().get(0).getValue()+"> {");
            }
            else{
                picoWriter.writeln("import org.springframework.data.mongodb.repository.MongoRepository");
                picoWriter.writeln_r("interface "+ StringUtils.capitalize(this.name)+
                        " : MongoRepository<"+StringUtils.capitalize(this.domain.getName())+","+this.domain.getFields().get(0).getValue()+"> {");
            }
        }
        else{
            picoWriter.writeln("package "+pckg+";");
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*;");
            picoWriter.writeln("import "+GroupAndArtefact+".domains.*;");
            if(database.equals("MySQL")){
                picoWriter.writeln("import org.springframework.data.jpa.repository.JpaRepository;");
                picoWriter.writeln_r("public interface "+ StringUtils.capitalize(this.name)+
                        " extends JpaRepository<"+StringUtils.capitalize(this.domain.getName())+","+this.domain.getFields().get(0).getValue()+"> {");
            }
            else{
                picoWriter.writeln("import org.springframework.data.mongodb.repository.MongoRepository;");
                picoWriter.writeln_r("public interface "+ StringUtils.capitalize(this.name)+
                        " extends MongoRepository<"+StringUtils.capitalize(this.domain.getName())+","+this.domain.getFields().get(0).getValue()+"> {");
            }
        }
        picoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }
}
