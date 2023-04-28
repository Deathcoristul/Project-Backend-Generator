package com.app.generator.util.service;

import com.app.generator.util.domain.Domain;
import com.app.generator.util.repository.Repository;
import javafx.util.Pair;
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
            if(!repositories.isEmpty())
            {
                classpicoWriter.writeln("import "+GroupAndArtefact+".domains.*");
                classpicoWriter.writeln("import "+GroupAndArtefact+".repositories.*");
            }
            classpicoWriter.writeln("import java.util.*");
            classpicoWriter.writeln("");
            classpicoWriter.writeln("@Service");
            classpicoWriter.writeln_r("class "+ StringUtils.capitalize(this.name)+": I"+StringUtils.capitalize(this.name)+" {");
            if(!repositories.isEmpty()) {
                for(Repository repository : repositories)
                {
                    String repositoryName=repository.getName().toLowerCase();
                    classpicoWriter.writeln("@Autowired");
                    classpicoWriter.writeln("private lateinit var "+repositoryName+": "+StringUtils.capitalize(repository.getName()));
                    classpicoWriter.writeln("");
                }
                for(Repository repository : repositories) {
                    Domain dom = repository.getDomain();
                    String repositoryName=repository.getName().toLowerCase();
                    ArrayList<Pair<String,String>> fields = dom.getFields();
                    String domainNameCap =StringUtils.capitalize(dom.getName());
                    classpicoWriter.writeln_r("override fun get"+domainNameCap+"ById(id :"+fields.get(0).getValue()+"):"+domainNameCap+"?{");
                    classpicoWriter.writeln("return "+repositoryName+".get"+domainNameCap+"ById(id)");
                    classpicoWriter.writeln_l("}");
                    classpicoWriter.writeln_r("override fun getAll"+domainNameCap+"():List<"+domainNameCap+">{");
                    classpicoWriter.writeln("return "+repositoryName+".findAll()");
                    classpicoWriter.writeln_l("}");

                    classpicoWriter.writeln_r("override fun create"+domainNameCap+"("+dom.getName().toLowerCase()+":"+domainNameCap+"){");
                    classpicoWriter.writeln(repositoryName+".save("+dom.getName().toLowerCase()+")");
                    classpicoWriter.writeln_l("}");
                    classpicoWriter.writeln_r("override fun update"+domainNameCap+"(idOld : "+fields.get(0).getValue()+","+dom.getName().toLowerCase()+":"+domainNameCap+"){");
                    classpicoWriter.writeln("val new"+dom.getName().toLowerCase()+":"+domainNameCap+" = "+repositoryName+".get"+domainNameCap+"ById(idOld)");
                    for(Pair<String,String> field : fields)
                    {
                        if (!field.getKey().equals(fields.get(0).getKey()))
                            classpicoWriter.writeln("new"+dom.getName().toLowerCase()+"."+field.getKey()+"="+dom.getName().toLowerCase()+"."+field.getKey());
                    }
                    if(dom.getRelationClass()!=null)
                    {
                        String relationName=StringUtils.capitalize(dom.getRelationClass().getName());
                        classpicoWriter.writeln("new"+dom.getName().toLowerCase()+"."+relationName.toLowerCase()+"="+dom.getName().toLowerCase()+"."+relationName.toLowerCase());
                    }
                    classpicoWriter.writeln(repositoryName+".save(new"+dom.getName().toLowerCase()+")");
                    classpicoWriter.writeln_l("}");
                    classpicoWriter.writeln_r("override fun delete"+domainNameCap+"("+fields.get(0).getKey() +":"+fields.get(0).getValue() +"){");
                    classpicoWriter.writeln("val "+dom.getName().toLowerCase()+":"+domainNameCap+" = "+repositoryName+".get"+domainNameCap+"ById("+fields.get(0).getKey() +")");
                    classpicoWriter.writeln(repositoryName+".delete("+dom.getName().toLowerCase()+")");
                    classpicoWriter.writeln_l("}");
                }
            }
        }
        else{
            classpicoWriter.writeln("package "+pckg+";");
            classpicoWriter.writeln("");
            classpicoWriter.writeln("import org.springframework.stereotype.Service;");
            classpicoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired;");
            if(!repositories.isEmpty())
            {
                classpicoWriter.writeln("import "+GroupAndArtefact+".domains.*;");
                classpicoWriter.writeln("import "+GroupAndArtefact+".repositories.*;");
            }
            classpicoWriter.writeln("import java.util.*;");
            classpicoWriter.writeln("");
            classpicoWriter.writeln("@Service");
            classpicoWriter.writeln_r("public class "+StringUtils.capitalize(this.name)+" implements I"+StringUtils.capitalize(this.name)+" {");
            if(!repositories.isEmpty()) {
                for (Repository repository : repositories) {
                    String repositoryName=repository.getName().toLowerCase();
                    classpicoWriter.writeln("@Autowired");
                    classpicoWriter.writeln(StringUtils.capitalize(repository.getName()) + " " + repositoryName + ";");
                    classpicoWriter.writeln("");
                }
                for (Repository repository : repositories){
                    String repositoryName=repository.getName().toLowerCase();
                    Domain dom = repository.getDomain();
                    ArrayList<Pair<String,String>> fields = dom.getFields();
                    String domainNameCap =StringUtils.capitalize(dom.getName());
                    classpicoWriter.writeln("@Override");
                    classpicoWriter.writeln_r("public "+domainNameCap+" get"+domainNameCap+"ById("+fields.get(0).getValue()+" id){");
                    classpicoWriter.writeln("return "+repositoryName+".get"+domainNameCap+"ById(id);");
                    classpicoWriter.writeln_l("}");

                    classpicoWriter.writeln("@Override");
                    classpicoWriter.writeln_r("public List<"+domainNameCap+"> getAll"+domainNameCap+"(){");
                    classpicoWriter.writeln("return "+repositoryName+".findAll();");
                    classpicoWriter.writeln_l("}");

                    classpicoWriter.writeln("@Override");
                    classpicoWriter.writeln_r("public void create"+domainNameCap+"("+ domainNameCap+" "+dom.getName().toLowerCase()+"){");
                    classpicoWriter.writeln(repositoryName+".save("+dom.getName().toLowerCase()+");");
                    classpicoWriter.writeln_l("}");

                    classpicoWriter.writeln("@Override");
                    classpicoWriter.writeln_r("public void update"+domainNameCap+"("+fields.get(0).getValue()+" idOld,"+ domainNameCap+" "+dom.getName().toLowerCase()+"){");
                    classpicoWriter.writeln(domainNameCap+" new"+dom.getName().toLowerCase()+" = "+repositoryName+".get"+domainNameCap+"ById(idOld);");
                    for(Pair<String,String> field : fields)
                    {
                        if (!field.getKey().equals(fields.get(0).getKey())) {
                            String capField = StringUtils.capitalize(field.getKey());
                            classpicoWriter.writeln("new"+dom.getName().toLowerCase()+".set"+ capField +"("+dom.getName().toLowerCase()+".get"+ capField +"());");
                        }
                    }
                    if(dom.getRelationClass()!=null)
                    {
                        String relationName=StringUtils.capitalize(dom.getRelationClass().getName());
                        classpicoWriter.writeln("new"+dom.getName().toLowerCase()+".set"+relationName+"("+dom.getName().toLowerCase()+".get"+relationName+"());");
                    }
                    classpicoWriter.writeln(repositoryName+".save(new"+dom.getName().toLowerCase()+");");
                    classpicoWriter.writeln_l("}");

                    classpicoWriter.writeln("@Override");
                    classpicoWriter.writeln_r("public void delete"+domainNameCap+"("+fields.get(0).getValue() +" "+fields.get(0).getKey() +"){");
                    classpicoWriter.writeln(domainNameCap+" "+dom.getName().toLowerCase()+" = "+repositoryName+".get"+domainNameCap+"ById("+fields.get(0).getKey() +");");
                    classpicoWriter.writeln(repositoryName+".delete("+dom.getName().toLowerCase()+");");
                    classpicoWriter.writeln_l("}");
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
            if(!repositories.isEmpty())
            {
                interfacepicoWriter.writeln("import "+GroupAndArtefact+".domains.*");
            }
            interfacepicoWriter.writeln("import java.util.*");
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln_r("interface I"+ StringUtils.capitalize(this.name)+" {");
            if(!getRepositories().isEmpty()) {
                for (Repository rep : getRepositories()) {
                    Domain dom = rep.getDomain();
                    ArrayList<Pair<String,String>> fields = dom.getFields();
                    String domainNameCap =StringUtils.capitalize(dom.getName());
                    interfacepicoWriter.writeln("fun get"+domainNameCap+"ById(id :"+fields.get(0).getValue()+"):"+domainNameCap+"?");
                    interfacepicoWriter.writeln("fun getAll"+domainNameCap+"():List<"+domainNameCap+">");
                    interfacepicoWriter.writeln("fun create"+domainNameCap+"("+dom.getName().toLowerCase()+":"+domainNameCap+")");
                    interfacepicoWriter.writeln("fun update"+domainNameCap+"(idOld :"+fields.get(0).getValue()+","+dom.getName().toLowerCase()+":"+domainNameCap+")");
                    interfacepicoWriter.writeln("fun delete"+domainNameCap+"("+fields.get(0).getKey() +":"+fields.get(0).getValue() +")");
                }
            }
        }
        else{
            interfacepicoWriter.writeln("package "+pckg+";");
            interfacepicoWriter.writeln("");
            if(!repositories.isEmpty())
            {
                interfacepicoWriter.writeln("import "+GroupAndArtefact+".domains.*;");
            }
            interfacepicoWriter.writeln("import java.util.*;");
            interfacepicoWriter.writeln("");
            interfacepicoWriter.writeln_r("public interface I"+StringUtils.capitalize(this.name)+" {");
            if(!getRepositories().isEmpty()) {
                for (Repository rep : getRepositories()) {
                    Domain dom = rep.getDomain();
                    ArrayList<Pair<String,String>> fields = dom.getFields();
                    String domainNameCap =StringUtils.capitalize(dom.getName());
                    interfacepicoWriter.writeln(domainNameCap+" get"+domainNameCap+"ById("+fields.get(0).getValue()+" id);");
                    interfacepicoWriter.writeln("List<"+domainNameCap+"> getAll"+domainNameCap+"();");
                    interfacepicoWriter.writeln("void create"+domainNameCap+"("+domainNameCap+" "+dom.getName().toLowerCase()+");");
                    interfacepicoWriter.writeln("void update"+domainNameCap+"("+fields.get(0).getValue()+" idOld,"+domainNameCap+" "+dom.getName().toLowerCase()+");");
                    interfacepicoWriter.writeln("void delete"+domainNameCap+"("+fields.get(0).getValue() +" "+fields.get(0).getKey() +");");
                }
            }
        }
        interfacepicoWriter.writeln_l("}");
        BufferedWriter interfacefileWriter=new BufferedWriter(new FileWriter(f));
        interfacefileWriter.write(interfacepicoWriter.toString());
        interfacefileWriter.close();
    }
}
