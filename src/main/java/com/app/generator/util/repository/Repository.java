package com.app.generator.util.repository;

import com.app.generator.util.domain.Domain;
import javafx.util.Pair;
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

    public void write(String repositoriesURI, String lang, String database) throws IOException {
        String[] parts=repositoriesURI.split("\\\\");
        boolean langFolderFound=false;
        StringBuilder packageBuilder = new StringBuilder();
        for(String s : parts)
        {
            if(langFolderFound)
            {
                packageBuilder.append(s).append('.');
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
        String nameCap=StringUtils.capitalize(this.name);
        String domainNameCap =StringUtils.capitalize(this.domain.getName());
        Pair<String,String> idField=this.domain.getFields().get(0);
        PicoWriter picoWriter=new PicoWriter();
        File f=new File(repositoriesURI+"\\"+nameCap+"."+lang);
        if(lang.equals("kt"))
        {
            picoWriter.writeln("package "+pckg);
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*");
            picoWriter.writeln("import "+GroupAndArtefact+".domains.*");
            if(database.equals("MariaDB")){
                picoWriter.writeln("import org.springframework.data.jpa.repository.*");
                picoWriter.writeln_r("interface "+ nameCap+
                        " : JpaRepository<"+domainNameCap+","+idField.getValue()+"> {");
                if(domain.isRelation())
                    picoWriter.writeln("@Query(value=\"SELECT * FROM "+this.domain.getName().toUpperCase()+" WHERE "+idField.getKey()+"=?1 AND "+domain.getFields().get(1).getKey()+"=?2\",nativeQuery=true)");
                else
                    picoWriter.writeln("@Query(value=\"SELECT * FROM "+this.domain.getName().toUpperCase()+" WHERE "+idField.getKey()+"=?1\",nativeQuery=true)");
            }
            else{
                picoWriter.writeln("import org.springframework.data.mongodb.repository.*");
                picoWriter.writeln_r("interface "+ nameCap+
                        " : MongoRepository<"+domainNameCap+","+idField.getValue()+"> {");
                picoWriter.writeln("@Query(\"{'"+idField.getKey()+ "':?0" +"}\")");
            }
            if(domain.isRelation())
                picoWriter.writeln("fun get"+domainNameCap+"ById(id : "+idField.getValue()+",id2 : "+domain.getFields().get(1).getValue()+") : "+domainNameCap);
            else
                picoWriter.writeln("fun get"+domainNameCap+"ById(id : "+idField.getValue()+") : "+domainNameCap);
        }
        else{
            picoWriter.writeln("package "+pckg+";");
            picoWriter.writeln("");
            picoWriter.writeln("import java.util.*;");
            picoWriter.writeln("import "+GroupAndArtefact+".domains.*;");
            if(database.equals("MariaDB")){
                picoWriter.writeln("import org.springframework.data.jpa.repository.*;");
                picoWriter.writeln_r("public interface "+ nameCap+
                        " extends JpaRepository<"+domainNameCap+","+idField.getValue()+"> {");
                if(domain.isRelation())
                    picoWriter.writeln("@Query(value=\"SELECT * FROM "+this.domain.getName().toUpperCase()+" WHERE "+idField.getKey()+"=?1 AND "+domain.getFields().get(1).getKey()+"=?2\",nativeQuery=true)");
                else
                    picoWriter.writeln("@Query(value=\"SELECT * FROM "+this.domain.getName().toUpperCase()+" WHERE "+idField.getKey()+"=?1\",nativeQuery=true)");
            }
            else{
                picoWriter.writeln("import org.springframework.data.mongodb.repository.*;");
                picoWriter.writeln_r("public interface "+ nameCap+
                        " extends MongoRepository<"+domainNameCap+","+idField.getValue()+"> {");
                picoWriter.writeln("@Query(\"{'"+idField.getKey()+ "':?0" +"}\")");
            }
            if(domain.isRelation())
                picoWriter.writeln(domainNameCap+" get"+domainNameCap+"ById("+idField.getValue()+" id,"+domain.getFields().get(1).getValue()+" id2)"+";");
            else
                picoWriter.writeln(domainNameCap+" get"+domainNameCap+"ById("+idField.getValue()+" id)"+";");
        }
        picoWriter.writeln_l("}");
        BufferedWriter fileWriter=new BufferedWriter(new FileWriter(f));
        fileWriter.write(picoWriter.toString());
        fileWriter.close();
    }
}
