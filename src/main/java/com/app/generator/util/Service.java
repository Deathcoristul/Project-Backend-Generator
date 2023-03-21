package com.app.generator.util;

import org.ainslec.picocog.PicoWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Service {
    private String name;
    private ArrayList<Repository> repositories;

    public Service(String name, ArrayList<Repository> repositories) {
        this.name = name;
        this.repositories = repositories;
    }

    public ArrayList<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(ArrayList<Repository> repositories) {
        this.repositories = repositories;
    }

    public String getName() {
        return name;
    }
    @Override
    public String toString()
    {
        return name;
    }
    public void write(String servicesURI,String lang) throws IOException {
        String[] parts=servicesURI.split("\\\\");
        String pckg=parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+"."+parts[parts.length-1];
        PicoWriter interfacepicoWriter=new PicoWriter();
        PicoWriter classpicoWriter =new PicoWriter();
        File f=new File(servicesURI+"\\"+StringUtils.capitalize(this.name)+"."+lang);
        if(lang.equals("kt"))
        {
            classpicoWriter.writeln("package "+pckg);
            classpicoWriter.writeln("");
            classpicoWriter.writeln("import org.springframework.stereotype.Service");
            classpicoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired");
            classpicoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".domains.*");
            classpicoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".repositories.*");
            classpicoWriter.writeln("");
            classpicoWriter.writeln("@Service");
            classpicoWriter.writeln_r("class "+ StringUtils.capitalize(this.name)+": I"+StringUtils.capitalize(this.name)+" {");
            for(Repository repository : repositories)
            {
                classpicoWriter.writeln("@Autowired");
                classpicoWriter.writeln("private lateinit var "+repository.getName()+": "+StringUtils.capitalize(repository.getName()));
                classpicoWriter.writeln("");
            }
        }
        else{
            classpicoWriter.writeln("package "+pckg+";");
            classpicoWriter.writeln("");
            classpicoWriter.writeln("import org.springframework.stereotype.Service;");
            classpicoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired;");
            classpicoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".domains.*;");
            classpicoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".repositories.*;");
            classpicoWriter.writeln("");
            classpicoWriter.writeln("@Service");
            classpicoWriter.writeln_r("public class "+StringUtils.capitalize(this.name)+" implements I"+StringUtils.capitalize(this.name)+" {");
            for(Repository repository : repositories)
            {
                classpicoWriter.writeln("@Autowired");
                classpicoWriter.writeln(StringUtils.capitalize(repository.getName())+" "+repository.getName()+";");
                classpicoWriter.writeln("");
            }
        }
        classpicoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(classpicoWriter.toString());
        fileWriter.close();
        f=new File(servicesURI+"\\I"+name+"."+lang);
        if(lang.equals("kt"))
        {
            interfacepicoWriter.writeln("package "+pckg);
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".domains.*");
            interfacepicoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".repositories.*");
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln_r("interface I"+ StringUtils.capitalize(this.name)+" {");
        }
        else{
            interfacepicoWriter.writeln("package "+pckg+";");
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".domains.*;");
            interfacepicoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".repositories.*;");
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln_r("public interface I"+StringUtils.capitalize(this.name)+" {");
        }
        interfacepicoWriter.writeln_l("}");
        BufferedWriter interfacefileWriter=new BufferedWriter(new FileWriter(f));
        interfacefileWriter.write(interfacepicoWriter.toString());
        interfacefileWriter.close();
    }
}
