package com.app.generator.util;

import javafx.util.Pair;
import org.ainslec.picocog.PicoWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Domain {
    private String name;
    private ArrayList<Pair<String,String>> fields;
    private boolean isRelation=false;
    private Domain relationClass=null;

    public Domain getRelationClass() {
        return relationClass;
    }

    public void setRelationClass(Domain relationClass) {
        this.relationClass = relationClass;
    }

    public Domain(String name, ArrayList<Pair<String, String>> fields, Domain relationClass) {// pentru mongo
        this.name = name;
        this.fields = fields;
        this.relationClass = relationClass;
    }

    public Domain(String name, ArrayList<Pair<String, String>> fields, boolean isRelation) {
        this.name = name;
        this.fields = fields;
        this.isRelation = isRelation;
    }

    public boolean isRelation() {
        return isRelation;
    }

    public Domain(String name, ArrayList<Pair<String, String>> fields) {
        this.name=name;
        this.fields = fields;
    }
    public ArrayList<Pair<String, String>> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Pair<String, String>> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }
    @Override
    public String toString()
    {
        return name;//facem override ca sa apara la listview doar numele, fara alte detalii
    }

    public void write(String domainsURI,String lang,boolean isJakarta,boolean lombok,String database) throws IOException {
        String[] parts=domainsURI.split("\\\\");
        String pckg=parts[parts.length-4]+"."+parts[parts.length-3]+"."+parts[parts.length-2]+"."+parts[parts.length-1];
        String persistence="javax";
        if(isJakarta)
            persistence="jakarta";
        PicoWriter picoWriter=new PicoWriter();
        File f=new File(domainsURI+"\\"+StringUtils.capitalize(this.name)+"."+lang);
        if(lang.equals("kt"))
        {
            picoWriter.writeln("package "+pckg);
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*");
            picoWriter.writeln("import java.io.Serializable");
            picoWriter.writeln("import org.springframework.hateoas.RepresentationModel");
            picoWriter.writeln("import org.springframework.data.mongodb.core.mapping.Document");
            picoWriter.writeln("import "+persistence+".persistence.*");
            picoWriter.writeln("import "+persistence+".validation.constraints.*");
            picoWriter.writeln("");
            picoWriter.writeln("@Entity");
            if(database.equals("MySQL"))
                picoWriter.writeln("@Table(name = \""+this.name.toUpperCase()+"\")");
            else
                picoWriter.writeln("@Document(collection = \""+this.name+"\")");
            if(isRelation())
            {
                picoWriter.writeln("@IdClass("+StringUtils.capitalize(this.name)+"Id.class)");
                picoWriter.writeln_r("class "+StringUtils.capitalize(this.name)+" : Serializable{");
                for(Pair<String,String> field:fields)
                {
                    picoWriter.writeln("@NotNull");
                    picoWriter.writeln("@Id");
                    picoWriter.writeln("var "+field.getKey()+": "+field.getValue()+"? = null");
                    picoWriter.writeln("");
                }
                picoWriter.writeln_l("}");
                picoWriter.writeln_r("class "+StringUtils.capitalize(this.name)+"Id : Serializable{");
                for(Pair<String,String> field:fields)
                {
                    picoWriter.writeln("@NotNull");
                    picoWriter.writeln("@Id");
                    picoWriter.writeln("var "+field.getKey()+": "+field.getValue()+"? = null");
                }
            }
            else{
                if(database.equals("MySQL"))
                    picoWriter.writeln_r("class "+StringUtils.capitalize(this.name)+" : RepresentationModel<"+StringUtils.capitalize(this.name)+">{");
                else {
                    picoWriter.writeln_r("class " + StringUtils.capitalize(this.name) + "{");
                    picoWriter.writeln("@NotNull");
                }

                picoWriter.writeln("@Id");
                for (Pair<String, String> field : fields) {
                    picoWriter.writeln("var "+field.getKey()+": "+field.getValue()+"? = null");
                    picoWriter.writeln("");
                }
                if(relationClass!=null)
                {
                    picoWriter.writeln("@DocumentReference(lazy = true)");
                    picoWriter.writeln("var "+StringUtils.uncapitalize(relationClass.getName())+": "+StringUtils.capitalize(relationClass.getName())+"? = null");
                }
            }
        }
        else{
            picoWriter.writeln("package "+pckg+";");
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*;");
            picoWriter.writeln("import java.io.Serializable;");
            picoWriter.writeln("import org.springframework.hateoas.RepresentationModel;");
            picoWriter.writeln("import org.springframework.data.mongodb.core.mapping.Document;");
            picoWriter.writeln("import "+persistence+".persistence.*;");
            picoWriter.writeln("import "+persistence+".validation.constraints.*;");
            picoWriter.writeln("");
            if(lombok) {
                picoWriter.writeln("import lombok.*;");
                picoWriter.writeln("");
                picoWriter.writeln("@Data");
            }
            picoWriter.writeln("@Entity");
            if(database.equals("MySQL"))
                picoWriter.writeln("@Table(name = \""+this.name.toUpperCase()+"\")");
            else
                picoWriter.writeln("@Document(collection = \""+this.name+"\")");
            if(isRelation())
            {
                picoWriter.writeln("@IdClass("+StringUtils.capitalize(this.name)+"Id.class)");
                picoWriter.writeln_r("public class "+StringUtils.capitalize(this.name)+" implements Serializable{");
                for(Pair<String,String> field:fields)
                {
                    picoWriter.writeln("@Id");
                    picoWriter.writeln("private "+field.getValue()+" "+field.getKey()+";");
                    picoWriter.writeln("");
                }
                if(!lombok) {
                    for (Pair<String, String> field : fields) {
                        picoWriter.writeln_r("public " + field.getValue() + " get" + StringUtils.capitalize(field.getKey()) + "(){");
                        picoWriter.writeln("return this."+field.getKey()+";");
                        picoWriter.writeln_l("}");
                        picoWriter.writeln("");
                        picoWriter.writeln_r("public void set" + StringUtils.capitalize(field.getKey())
                                + "("+field.getValue()+" "+field.getKey()+"){");
                        picoWriter.writeln("this."+field.getKey()+"="+field.getKey()+";");
                        picoWriter.writeln_l("}");
                        picoWriter.writeln("");
                    }
                }
                picoWriter.writeln_r("public "+StringUtils.capitalize(this.name)+"(){");
                picoWriter.writeln_l("}");
                picoWriter.writeln_l("}");
                picoWriter.writeln("");
                picoWriter.writeln_r("class "+StringUtils.capitalize(this.name)+"Id implements Serializable{");
                for(Pair<String,String> field:fields)
                {
                    picoWriter.writeln("@Id");
                    picoWriter.writeln("private "+field.getValue()+" "+field.getKey()+";");
                    picoWriter.writeln("");
                }
            }
            else{
                if(database.equals("MySQL"))
                    picoWriter.writeln_r("public class "+StringUtils.capitalize(this.name)+" extends RepresentationModel<"+StringUtils.capitalize(this.name)+">{");
                else
                    picoWriter.writeln_r("public class "+StringUtils.capitalize(this.name)+"{");
                picoWriter.writeln("@Id");
                for(Pair<String,String> field:fields)
                {
                    picoWriter.writeln("private "+field.getValue()+" "+field.getKey()+";");
                }
                if(!lombok) {
                    for (Pair<String, String> field : fields) {
                        picoWriter.writeln_r("public " + field.getValue() + " get" + StringUtils.capitalize(field.getKey()) + "(){");
                        picoWriter.writeln("return this."+field.getKey()+";");
                        picoWriter.writeln_l("}");
                        picoWriter.writeln("");
                        picoWriter.writeln_r("public void set" + StringUtils.capitalize(field.getKey())
                                + "("+field.getValue()+" "+field.getKey()+"){");
                        picoWriter.writeln("this."+field.getKey()+"="+field.getKey()+";");
                        picoWriter.writeln_l("}");
                        picoWriter.writeln("");
                    }
                }
                if(relationClass!=null)
                {
                    picoWriter.writeln("@DocumentReference(lazy = true)");
                    picoWriter.writeln("private "+StringUtils.capitalize(relationClass.getName())+" "+StringUtils.uncapitalize(relationClass.getName())+";");
                }
                picoWriter.writeln("");
                picoWriter.writeln_r("public "+StringUtils.capitalize(this.name)+"(){");
                picoWriter.writeln_l("}");
            }
        }
        picoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }
}
