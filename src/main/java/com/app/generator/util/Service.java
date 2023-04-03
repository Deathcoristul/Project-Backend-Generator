package com.app.generator.util;

import org.ainslec.picocog.PicoWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

public class Service {
    private final String name;
    private ArrayList<Repository> repositories=new ArrayList<>();

    public Service(String name, ArrayList<Repository> repositories) {
        this.name = name;
        this.repositories = repositories;
    }

    public Service(String name) {
        this.name = name;
    }

    public ArrayList<Repository> getRepositories() {
        return repositories;
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

        PicoWriter interfacepicoWriter=new PicoWriter();
        PicoWriter classpicoWriter =new PicoWriter();
        File f=new File(servicesURI+"\\"+StringUtils.capitalize(this.name)+"."+lang);
        if(lang.equals("kt"))
        {
            classpicoWriter.writeln("package "+pckg);
            classpicoWriter.writeln("");
            classpicoWriter.writeln("import org.springframework.stereotype.Service");
            classpicoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired");
            classpicoWriter.writeln("import "+GroupAndArtefact+".domains.*");
            classpicoWriter.writeln("import "+GroupAndArtefact+".repositories.*");
            classpicoWriter.writeln("");
            classpicoWriter.writeln("@Service");
            classpicoWriter.writeln_r("class "+ StringUtils.capitalize(this.name)+": I"+StringUtils.capitalize(this.name)+" {");
            if(!repositories.isEmpty()) {
                for(Repository repository : repositories)
                {
                    classpicoWriter.writeln("@Autowired");
                    classpicoWriter.writeln("private lateinit var "+repository.getName().toLowerCase()+": "+StringUtils.capitalize(repository.getName()));
                    classpicoWriter.writeln("");
                }
            }
        }
        else{
            classpicoWriter.writeln("package "+pckg+";");
            classpicoWriter.writeln("");
            classpicoWriter.writeln("import org.springframework.stereotype.Service;");
            classpicoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired;");
            classpicoWriter.writeln("import "+GroupAndArtefact+".domains.*;");
            classpicoWriter.writeln("import "+GroupAndArtefact+".repositories.*;");
            classpicoWriter.writeln("");
            classpicoWriter.writeln("@Service");
            classpicoWriter.writeln_r("public class "+StringUtils.capitalize(this.name)+" implements I"+StringUtils.capitalize(this.name)+" {");
            if(!repositories.isEmpty()) {
                for (Repository repository : repositories) {
                    classpicoWriter.writeln("@Autowired");
                    classpicoWriter.writeln(StringUtils.capitalize(repository.getName()) + " " + repository.getName().toLowerCase() + ";");
                    classpicoWriter.writeln("");
                }
            }
        }
        classpicoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(classpicoWriter.toString());
        fileWriter.close();
        f=new File(servicesURI+"\\I"+StringUtils.capitalize(this.name)+"."+lang);
        if(lang.equals("kt"))
        {
            interfacepicoWriter.writeln("package "+pckg);
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln("import "+GroupAndArtefact+".domains.*");
            interfacepicoWriter.writeln("import "+GroupAndArtefact+".repositories.*");
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln_r("interface I"+ StringUtils.capitalize(this.name)+" {");
        }
        else{
            interfacepicoWriter.writeln("package "+pckg+";");
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln("import "+GroupAndArtefact+".domains.*;");
            interfacepicoWriter.writeln("import "+GroupAndArtefact+".repositories.*;");
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln_r("public interface I"+StringUtils.capitalize(this.name)+" {");
        }
        interfacepicoWriter.writeln_l("}");
        BufferedWriter interfacefileWriter=new BufferedWriter(new FileWriter(f));
        interfacefileWriter.write(interfacepicoWriter.toString());
        interfacefileWriter.close();
    }
}
