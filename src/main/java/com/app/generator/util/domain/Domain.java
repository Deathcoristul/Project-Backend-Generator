package com.app.generator.util.domain;

import javafx.util.Pair;
import org.ainslec.picocog.PicoWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;


public class Domain {
    private final String name;
    private final ArrayList<Pair<String,String>> fields;
    private boolean isRelation=false;
    private Domain relationClass=null;
    private Domain firstDomain=null;

    public Domain getFirstDomain() {
        return firstDomain;
    }

    public Domain getSecondDomain() {
        return secondDomain;
    }

    private Domain secondDomain=null;
    public Domain getRelationClass() {
        return relationClass;
    }


    public Domain(String name, ArrayList<Pair<String, String>> fields, Domain relationClass) {// pentru mongo
        this.name = name;
        this.fields = fields;
        this.relationClass = relationClass;
    }

    public Domain(String name, ArrayList<Pair<String, String>> fields, boolean isRelation,Domain firstDomain,Domain secondDomain) {
        this.name = name;
        this.fields = fields;
        this.isRelation = isRelation;
        this.firstDomain=firstDomain;
        this.secondDomain=secondDomain;
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

    public String getName() {
        return name;
    }
    @Override
    public String toString()
    {
        return getName();//facem override ca sa apara la listview doar numele, fara alte detalii
    }
    public void write(String domainsURI,String lang,boolean isJakarta,boolean lombok,String database) throws IOException {
        String[] parts=domainsURI.split("\\\\");
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
        packageBuilder.deleteCharAt(packageBuilder.length()-1);//eliminam ultimul punct
        String pckg=packageBuilder.toString();
        String persistence="javax";
        if(isJakarta)
            persistence="jakarta";
        String nameCap=StringUtils.capitalize(this.name);
        PicoWriter picoWriter=new PicoWriter();
        File f=new File(domainsURI+"\\"+nameCap+"."+lang);
        if(lang.equals("kt"))
        {
            for(int i=0;i<fields.size();i++)
            {
                Pair<String,String> pair = fields.get(i);
                if (pair.getValue().equals("Integer"))//Pentru a evita warning de la kotlin legat de Integer
                {
                    Pair<String,String> newPair= new Pair<>(pair.getKey(), "Int");
                    fields.set(i,newPair);
                }
            }
            picoWriter.writeln("package "+pckg);
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*");
            picoWriter.writeln("import java.io.Serializable");
            if(database.equals("MariaDB")) {
                picoWriter.writeln("import "+persistence+".persistence.*");
            }
            else {
                picoWriter.writeln("import org.springframework.data.mongodb.core.mapping.Document");
                picoWriter.writeln("import org.springframework.data.mongodb.core.mapping.DocumentReference");
                picoWriter.writeln("import org.springframework.data.annotation.Id");
            }
            picoWriter.writeln("import "+persistence+".validation.constraints.*");
            picoWriter.writeln("");
            if(database.equals("MariaDB")) {
                picoWriter.writeln("@Entity");
                picoWriter.writeln("@Table(name = \"" + this.name.toUpperCase() + "\")");
            }
            else
                picoWriter.writeln("@Document(collection = \""+this.name+"\")");
            if(isRelation())
            {
                picoWriter.writeln("@IdClass("+nameCap+"Id::class)");
                picoWriter.writeln_r("class "+nameCap+"(");
                for (Pair<String, String> field : fields) {
                    if (fields.indexOf(field)==0 || fields.indexOf(field)==1)
                        picoWriter.writeln("@Id");
                    picoWriter.writeln("var " + field.getKey() + ": " + field.getValue() + "? = null,");
                    picoWriter.writeln("");
                }
                picoWriter.writeln_l("): Serializable");
                picoWriter.writeln_r("class "+nameCap+"Id : Serializable{");
                for(int i=0;i<=1;i++)
                {
                    Pair<String,String> field = fields.get(i);
                    picoWriter.writeln("@Id");
                    picoWriter.writeln("var "+field.getKey()+": "+field.getValue()+"? = null");
                }
                picoWriter.writeln_l("}");
            }
            else{
                if(database.equals("MariaDB"))
                    picoWriter.writeln_r("class "+nameCap+"(");
                else {
                    picoWriter.writeln_r("open class " + nameCap + "(");
                }

                picoWriter.writeln("@Id");
                for (Pair<String, String> field : fields) {
                    picoWriter.writeln("var "+field.getKey()+": "+field.getValue()+"? = null,");
                    picoWriter.writeln("");
                }
                if(relationClass!=null)
                {
                    picoWriter.writeln("@DocumentReference");
                    picoWriter.writeln("var "+relationClass.getName().toLowerCase()+": "+StringUtils.capitalize(relationClass.getName())+"? = null");
                }
                picoWriter.writeln_l(")");
            }
        }
        else{
            for(int i=0;i<fields.size();i++)
            {
                Pair<String,String> pair = fields.get(i);
                if (pair.getValue().equals("Int"))
                {
                    Pair<String,String> newPair= new Pair<>(pair.getKey(), "Integer");
                    fields.set(i,newPair);
                }
            }
            picoWriter.writeln("package "+pckg+";");
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*;");
            picoWriter.writeln("import java.io.Serializable;");
            if(database.equals("MariaDB")) {
                picoWriter.writeln("import "+persistence+".persistence.*;");
            }
            else {
                picoWriter.writeln("import org.springframework.data.mongodb.core.mapping.Document;");
                picoWriter.writeln("import org.springframework.data.mongodb.core.mapping.DocumentReference;");
                picoWriter.writeln("import org.springframework.data.annotation.Id;");
            }
            picoWriter.writeln("import "+persistence+".validation.constraints.*;");
            picoWriter.writeln("");
            if(lombok) {
                picoWriter.writeln("import lombok.*;");
                picoWriter.writeln("");
                picoWriter.writeln("@Data");
            }
            if(database.equals("MariaDB")) {
                picoWriter.writeln("@Entity");
                picoWriter.writeln("@Table(name = \"" + this.name.toUpperCase() + "\")");
            }
            else
                picoWriter.writeln("@Document(collection = \""+this.name+"\")");
            if(isRelation())
            {
                picoWriter.writeln("@IdClass("+nameCap+"Id.class)");
                picoWriter.writeln_r("public class "+nameCap+" implements Serializable{");
                for(Pair<String,String> field:fields)
                {
                    if(fields.indexOf(field)==0 || fields.indexOf(field)==1)
                        picoWriter.writeln("@Id");
                    picoWriter.writeln("private "+field.getValue()+" "+field.getKey()+";");
                    picoWriter.writeln("");
                }
                StringBuilder params = new StringBuilder();
                for(Pair<String,String> field : fields)
                {
                    params.append(field.getValue()).append(" ").append(field.getKey()).append(",");
                }
                params.deleteCharAt(params.length()-1);
                picoWriter.writeln_r("public "+nameCap+"("+params+"){");
                for(Pair<String,String> field : fields)
                {
                    picoWriter.writeln("this."+field.getKey()+" = "+field.getKey()+";");
                }
                picoWriter.writeln_l("}");
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
                picoWriter.writeln_r("public "+nameCap+"(){");
                picoWriter.writeln_l("}");
                picoWriter.writeln_l("}");
                picoWriter.writeln("");
                picoWriter.writeln_r("class "+nameCap+"Id implements Serializable{");
                for(int i=0;i<=1;i++)
                {
                    Pair<String,String> field = fields.get(i);
                    picoWriter.writeln("@Id");
                    picoWriter.writeln("private "+field.getValue()+" "+field.getKey()+";");
                    picoWriter.writeln("");
                }
            }
            else{
                picoWriter.writeln_r("public class "+nameCap+"{");
                picoWriter.writeln("@Id");
                for(Pair<String,String> field:fields)
                {
                    picoWriter.writeln("private "+field.getValue()+" "+field.getKey()+";");
                }
                StringBuilder params = new StringBuilder();
                for(Pair<String,String> field : fields)
                {
                    params.append(field.getValue()).append(" ").append(field.getKey()).append(",");
                }
                params.deleteCharAt(params.length()-1);
                picoWriter.writeln_r("public "+nameCap+"("+params+"){");
                for(Pair<String,String> field : fields)
                {
                    picoWriter.writeln("this."+field.getKey()+" = "+field.getKey()+";");
                }
                picoWriter.writeln_l("}");
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
                    String relationName= StringUtils.capitalize(relationClass.getName());
                    String variableName = StringUtils.uncapitalize(relationClass.getName());
                    picoWriter.writeln("@DocumentReference");
                    picoWriter.writeln("private "+relationName+" "+variableName+";");
                    if(!lombok) {
                        picoWriter.writeln_r("public " + relationName + " get" + relationName + "(){");
                        picoWriter.writeln("return this."+variableName+";");
                        picoWriter.writeln_l("}");
                        picoWriter.writeln("");
                        picoWriter.writeln_r("public void set" + relationName+ "("+relationName+" "+variableName+"){");
                        picoWriter.writeln("this."+variableName+"="+variableName+";");
                        picoWriter.writeln_l("}");
                        picoWriter.writeln("");
                    }
                    params.append(",").append(StringUtils.capitalize(relationClass.getName())).append(" ")
                            .append(StringUtils.uncapitalize(relationClass.getName()));
                    picoWriter.writeln_r("public "+nameCap+"("+params+"){");
                    for(Pair<String,String> field : fields)
                    {
                        picoWriter.writeln("this."+field.getKey()+" = "+field.getKey()+";");
                    }
                    picoWriter.writeln("this."+variableName +" = "+variableName+";");
                    picoWriter.writeln_l("}");
                }
                picoWriter.writeln("");
                picoWriter.writeln_r("public "+nameCap+"(){");
                picoWriter.writeln_l("}");
            }
            picoWriter.writeln_l("}");
        }
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Domain domain = (Domain) obj;
        return Objects.equals(name, domain.name) &&
                Objects.equals(fields, domain.fields) &&
                isRelation == domain.isRelation &&
                Objects.equals(relationClass, domain.relationClass);
    }
}
