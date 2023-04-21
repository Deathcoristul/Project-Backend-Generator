package com.app.generator.util;

import javafx.util.Pair;
import org.ainslec.picocog.PicoWriter;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.util.*;

public class Controller {
    private final String name;
    private ArrayList<Service> services;

    public Controller(String name, ArrayList<Service> services) {
        this.name = name;
        this.services = services;
    }

    public Controller(String name) {
        this.name = name;
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
            picoWriter.writeln("import org.springframework.http.*");
            picoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired");
            if(!services.isEmpty()) {
                picoWriter.writeln("import " + GroupAndArtefact + ".services.*");
                if(!services.get(0).getRepositories().isEmpty())
                    picoWriter.writeln("import " + GroupAndArtefact + ".domains.*");
            }
            picoWriter.writeln("");
            picoWriter.writeln("@Controller");
            picoWriter.writeln("@RequestMapping(\"/"+this.name.toLowerCase()+"\")");
            picoWriter.writeln_r("class "+StringUtils.capitalize(this.name)+" {");
            if(!services.isEmpty()) {
                for (Service service : services) {
                    picoWriter.writeln("@Autowired");
                    picoWriter.writeln("private lateinit var " + service.getName().toLowerCase() + ": I" + StringUtils.capitalize(service.getName()));
                    picoWriter.writeln("");
                }
                for (Service service : services) {
                    String serviceName=service.getName().toLowerCase();
                    for(Repository repository: service.getRepositories())
                    {
                        String repositoryName=repository.getName().toLowerCase();
                        Domain dom = repository.getDomain();
                        ArrayList<Pair<String,String>> fields = dom.getFields();
                        String domainNameCap =StringUtils.capitalize(dom.getName());

                        picoWriter.writeln("@GetMapping(\""+serviceName+"/"+repositoryName+"/{id}\")");
                        Pair<String, String> idField = fields.get(0);
                        picoWriter.writeln_r("fun get"+domainNameCap+"ById(@PathVariable(\"id\") id:"+ idField.getValue()+"):ResponseEntity<"+domainNameCap+">{");
                        picoWriter.writeln("val "+domainNameCap.toLowerCase()+":"+domainNameCap+"?="+serviceName+".get"+domainNameCap+"ById(id)");
                        picoWriter.writeln_r("return if("+domainNameCap.toLowerCase()+"==null)");
                        picoWriter.writeln("ResponseEntity("+domainNameCap.toLowerCase()+", HttpStatus.NOT_FOUND)");
                        picoWriter.writeln_lr("else");
                        picoWriter.writeln("ResponseEntity("+domainNameCap.toLowerCase()+", HttpStatus.OK)");
                        picoWriter.writeln_l("");
                        picoWriter.writeln_l("}");

                        picoWriter.writeln("@GetMapping(\""+serviceName+"/"+repositoryName+"\")");
                        picoWriter.writeln_r("fun getAll"+domainNameCap+"() : ResponseEntity<List<"+domainNameCap+">>{");
                        picoWriter.writeln("val "+domainNameCap.toLowerCase()+"List : List<"+domainNameCap+"> ="+serviceName+".getAll"+domainNameCap+"()");
                        picoWriter.writeln_r("return if("+domainNameCap.toLowerCase()+"List.isEmpty())");
                        picoWriter.writeln("ResponseEntity("+domainNameCap.toLowerCase()+"List, HttpStatus.NOT_FOUND)");
                        picoWriter.writeln_lr("else");
                        picoWriter.writeln("ResponseEntity("+domainNameCap.toLowerCase()+"List, HttpStatus.OK)");
                        picoWriter.writeln_l("");
                        picoWriter.writeln_l("}");

                        picoWriter.writeln("@PostMapping(\""+serviceName+"/"+repositoryName+"\")");
                        picoWriter.writeln_r("fun create"+domainNameCap+"(@RequestBody "+dom.getName().toLowerCase()+":"+domainNameCap+"): ResponseEntity<Unit>{");
                        picoWriter.writeln(serviceName+".create"+domainNameCap+"("+dom.getName().toLowerCase()+")");
                        picoWriter.writeln("return ResponseEntity(null,HttpStatus.CREATED)");
                        picoWriter.writeln_l("}");

                        picoWriter.writeln("@PutMapping(\""+serviceName+"/"+repositoryName+"/{idOld}\")");
                        picoWriter.writeln_r("fun update"+domainNameCap+"(@PathVariable(\"idOld\") idOld:"+idField.getValue()+",@RequestBody "+ dom.getName().toLowerCase()+":"+domainNameCap +"): ResponseEntity<Unit>{");
                        picoWriter.writeln("val old"+dom.getName().toLowerCase()+":"+domainNameCap+"? = "+serviceName+".get"+domainNameCap+"ById(idOld)");
                        picoWriter.writeln_r("if(old"+domainNameCap.toLowerCase()+"==null)");
                        picoWriter.writeln("return ResponseEntity(null, HttpStatus.NO_CONTENT)");
                        picoWriter.writeln_lr("else");
                        picoWriter.writeln(serviceName+".update"+domainNameCap+"(idOld,"+dom.getName().toLowerCase()+")");
                        picoWriter.writeln("return ResponseEntity(null, HttpStatus.ACCEPTED)");
                        picoWriter.writeln_l("");
                        picoWriter.writeln_l("}");

                        picoWriter.writeln("@DeleteMapping(\""+serviceName+"/"+repositoryName+"/{"+ idField.getKey()+"}\")");
                        picoWriter.writeln_r("fun delete"+domainNameCap+"(@PathVariable(\""+ idField.getKey()+"\") "+ idField.getKey() +":"+ idField.getValue() +"): ResponseEntity<Unit>{");
                        picoWriter.writeln("val "+dom.getName().toLowerCase()+":"+domainNameCap+"? = "+serviceName+".get"+domainNameCap+"ById("+ idField.getKey() +")");
                        picoWriter.writeln_r("if("+domainNameCap.toLowerCase()+"==null)");
                        picoWriter.writeln("return ResponseEntity(null, HttpStatus.NO_CONTENT)");
                        picoWriter.writeln_lr("else");
                        picoWriter.writeln(serviceName+".delete"+domainNameCap+"("+idField.getKey()+")");
                        picoWriter.writeln("return ResponseEntity(null, HttpStatus.OK)");
                        picoWriter.writeln_l("");
                        picoWriter.writeln_l("}");
                        picoWriter.writeln("");
                    }
                }
            }
        }
        else{
            picoWriter.writeln("package "+pckg+";");
            picoWriter.writeln("");
            picoWriter.writeln("import org.springframework.stereotype.Controller;");
            picoWriter.writeln("import org.springframework.web.bind.annotation.*;");
            picoWriter.writeln("import org.springframework.http.*;");
            picoWriter.writeln("import org.springframework.beans.factory.annotation.Autowired;");

            if(!services.isEmpty()) {
                picoWriter.writeln("import " + GroupAndArtefact + ".services.*;");
                if(!services.get(0).getRepositories().isEmpty())
                    picoWriter.writeln("import " + GroupAndArtefact + ".domains.*;");
            }
            picoWriter.writeln("");
            picoWriter.writeln("@Controller");
            picoWriter.writeln("@RequestMapping(\"/"+this.name.toLowerCase()+"\")");
            picoWriter.writeln_r("public class "+StringUtils.capitalize(this.name)+" {");
            if(!services.isEmpty()) {
                for (Service service : services) {
                    picoWriter.writeln("@Autowired");
                    picoWriter.writeln("I" + StringUtils.capitalize(service.getName()) + " " + service.getName().toLowerCase() + ";");
                    picoWriter.writeln("");
                }
                for (Service service : services) {
                    String serviceName=service.getName().toLowerCase();
                    for(Repository repository: service.getRepositories())
                    {
                        String repositoryName=repository.getName().toLowerCase();
                        Domain dom = repository.getDomain();
                        ArrayList<Pair<String,String>> fields = dom.getFields();
                        String domainNameCap =StringUtils.capitalize(dom.getName());
                        picoWriter.writeln("@GetMapping(\""+serviceName+"/"+repositoryName+"/{id}\")");
                        Pair<String, String> idField = fields.get(0);
                        picoWriter.writeln_r("ResponseEntity<"+domainNameCap+"> get"+domainNameCap+"ById(@PathVariable(\"id\") "+ idField.getValue()+" id){");
                        picoWriter.writeln(domainNameCap+" "+domainNameCap.toLowerCase()+"="+serviceName+".get"+domainNameCap+"ById(id);");
                        picoWriter.writeln_r("if("+domainNameCap.toLowerCase()+"==null)");
                        picoWriter.writeln("return new ResponseEntity("+domainNameCap.toLowerCase()+", HttpStatus.NOT_FOUND);");
                        picoWriter.writeln_lr("else");
                        picoWriter.writeln("return new ResponseEntity("+domainNameCap.toLowerCase()+", HttpStatus.OK);");
                        picoWriter.writeln_l("");
                        picoWriter.writeln_l("}");

                        picoWriter.writeln("@GetMapping(\""+serviceName+"/"+repositoryName+"\")");
                        picoWriter.writeln_r("ResponseEntity<List<"+domainNameCap+">> getAll"+domainNameCap+"(){");
                        picoWriter.writeln("List<"+domainNameCap+"> "+domainNameCap.toLowerCase()+"List="+serviceName+".getAll"+domainNameCap+"();");
                        picoWriter.writeln_r("if("+domainNameCap.toLowerCase()+"List.isEmpty())");
                        picoWriter.writeln("return new ResponseEntity("+domainNameCap.toLowerCase()+"List, HttpStatus.NOT_FOUND);");
                        picoWriter.writeln_lr("else");
                        picoWriter.writeln("return new ResponseEntity("+domainNameCap.toLowerCase()+"List, HttpStatus.OK);");
                        picoWriter.writeln_l("");
                        picoWriter.writeln_l("}");

                        picoWriter.writeln("@PostMapping(\""+serviceName+"/"+repositoryName+"\")");
                        picoWriter.writeln_r("ResponseEntity create"+domainNameCap+"(@RequestBody "+ domainNameCap+" "+dom.getName().toLowerCase()+"){");
                        picoWriter.writeln(serviceName+".create"+domainNameCap+"("+dom.getName().toLowerCase()+");");
                        picoWriter.writeln("return new ResponseEntity(null,HttpStatus.CREATED);");
                        picoWriter.writeln_l("}");

                        picoWriter.writeln("@PutMapping(\""+serviceName+"/"+repositoryName+"/{idOld}\")");
                        picoWriter.writeln_r("ResponseEntity update"+domainNameCap+"(@PathVariable(\"idOld\") "+ idField.getValue() +" idOld,@RequestBody "+ domainNameCap+" "+dom.getName().toLowerCase()+"){");
                        picoWriter.writeln(domainNameCap+" old"+dom.getName().toLowerCase()+" = "+serviceName+".get"+domainNameCap+"ById(idOld);");
                        picoWriter.writeln_r("if(old"+domainNameCap.toLowerCase()+"==null)");
                        picoWriter.writeln("return new ResponseEntity(null, HttpStatus.NO_CONTENT);");
                        picoWriter.writeln_lr("else");
                        picoWriter.writeln(serviceName+".update"+domainNameCap+"(idOld,"+dom.getName().toLowerCase()+");");
                        picoWriter.writeln("return new ResponseEntity(null, HttpStatus.ACCEPTED);");
                        picoWriter.writeln_l("");
                        picoWriter.writeln_l("}");

                        picoWriter.writeln("@DeleteMapping(\""+serviceName+"/"+repositoryName+"/{"+ idField.getKey()+"}\")");
                        picoWriter.writeln_r("ResponseEntity delete"+domainNameCap+"(@PathVariable(\""+ idField.getKey()+"\") "+ idField.getValue() +" "+ idField.getKey() +"){");
                        picoWriter.writeln(domainNameCap+" "+dom.getName().toLowerCase()+" = "+serviceName+".get"+domainNameCap+"ById("+ idField.getKey() +");");
                        picoWriter.writeln_r("if("+domainNameCap.toLowerCase()+"==null)");
                        picoWriter.writeln("return new ResponseEntity(null, HttpStatus.NO_CONTENT);");
                        picoWriter.writeln_lr("else");
                        picoWriter.writeln(serviceName+".delete"+domainNameCap+"("+idField.getKey()+");");
                        picoWriter.writeln("return new ResponseEntity(null, HttpStatus.OK);");
                        picoWriter.writeln_l("");
                        picoWriter.writeln_l("}");
                        picoWriter.writeln("");
                    }
                }
            }
        }
        picoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }
}
