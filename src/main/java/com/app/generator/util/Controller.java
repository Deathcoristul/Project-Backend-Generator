package com.app.generator.util;

import org.ainslec.picocog.PicoWriter;
import org.apache.commons.lang3.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    private String name;
    private ArrayList<Service> services;

    public Controller(String name, ArrayList<Service> services) {
        this.name = name;
        this.services = services;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
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
        String pckg=parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+"."+parts[parts.length-1];
        PicoWriter picoWriter=new PicoWriter();
        File f=new File(controllersURI+"\\"+StringUtils.capitalize(this.name)+"."+lang);
        if(lang.equals("kt"))
        {
            picoWriter.writeln("package "+pckg);
            picoWriter.writeln("");
            picoWriter.writeln("import org.springframework.stereotype.Controller");
            picoWriter.writeln("import org.springframework.web.bind.annotation.*");
            picoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired");
            picoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".domains.*");
            picoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".services.*");
            picoWriter.writeln("");
            picoWriter.writeln("@Controller");
            picoWriter.writeln_r("class "+StringUtils.capitalize(this.name)+" {");
            for(Service service : services)
            {
                picoWriter.writeln("@Autowired");
                picoWriter.writeln("private lateinit var "+service.getName()+": I"+StringUtils.capitalize(service.getName()));
                picoWriter.writeln("");
            }
        }
        else{
            picoWriter.writeln("package "+pckg+";");
            picoWriter.writeln("");
            picoWriter.writeln("import org.springframework.stereotype.Controller;");
            picoWriter.writeln("import org.springframework.web.bind.annotation.*;");
            picoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired;");
            picoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".domains.*;");
            picoWriter.writeln("import "+parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+".services.*;");
            picoWriter.writeln("");
            picoWriter.writeln("@Controller");
            picoWriter.writeln_r("public class "+StringUtils.capitalize(this.name)+" {");
            for(Service service : services)
            {
                picoWriter.writeln("@Autowired");
                picoWriter.writeln("I"+StringUtils.capitalize(service.getName())+" "+service.getName()+";");
                picoWriter.writeln("");
            }
        }
        picoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }
}
