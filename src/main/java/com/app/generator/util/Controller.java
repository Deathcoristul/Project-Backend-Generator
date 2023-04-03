package com.app.generator.util;

import org.ainslec.picocog.PicoWriter;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.util.*;

public class Controller {
    private final String name;
    private final ArrayList<Service> services;

    public Controller(String name, ArrayList<Service> services) {
        this.name = name;
        this.services = services;
    }

    public ArrayList<Service> getServices() {
        return services;
    }


    public String getName() {
        return name;
    }
    @Override
    public String toString()
    {
        return name;
    }
    public void write(String controllersURI,String lang) throws IOException {
        String[] parts=controllersURI.split("\\\\");
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
        packageparts.remove(packageparts.size()-1);
        String GroupAndArtefact=String.join(".",packageparts);
        PicoWriter picoWriter=new PicoWriter();
        File f=new File(controllersURI+"\\"+StringUtils.capitalize(this.name)+"."+lang);
        if(lang.equals("kt"))
        {
            picoWriter.writeln("package "+pckg);
            picoWriter.writeln("");
            picoWriter.writeln("import org.springframework.stereotype.Controller");
            picoWriter.writeln("import org.springframework.web.bind.annotation.*");
            picoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired");
            picoWriter.writeln("import "+GroupAndArtefact+".domains.*");
            picoWriter.writeln("import "+GroupAndArtefact+".services.*");
            picoWriter.writeln("");
            picoWriter.writeln("@Controller");
            picoWriter.writeln_r("class "+StringUtils.capitalize(this.name)+" {");
            for(Service service : services)
            {
                picoWriter.writeln("@Autowired");
                picoWriter.writeln("private lateinit var "+service.getName().toLowerCase()+": I"+StringUtils.capitalize(service.getName()));
                picoWriter.writeln("");
            }
        }
        else{
            picoWriter.writeln("package "+pckg+";");
            picoWriter.writeln("");
            picoWriter.writeln("import org.springframework.stereotype.Controller;");
            picoWriter.writeln("import org.springframework.web.bind.annotation.*;");
            picoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired;");
            picoWriter.writeln("import "+GroupAndArtefact+".domains.*;");
            picoWriter.writeln("import "+GroupAndArtefact+".services.*;");
            picoWriter.writeln("");
            picoWriter.writeln("@Controller");
            picoWriter.writeln_r("public class "+StringUtils.capitalize(this.name)+" {");
            if(!services.isEmpty()) {
                for (Service service : services) {
                    picoWriter.writeln("@Autowired");
                    picoWriter.writeln("I" + StringUtils.capitalize(service.getName()) + " " + service.getName().toLowerCase() + ";");
                    picoWriter.writeln("");
                }
            }
        }
        picoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }
}
