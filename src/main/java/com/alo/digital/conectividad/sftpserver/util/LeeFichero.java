package com.alo.digital.conectividad.sftpserver.util;

import com.alo.digital.conectividad.sftpserver.beans.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class LeeFichero {

    public List<User> leerUsuarios() {
        List<User> users = new ArrayList<>();
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            archivo = new ClassPathResource("usuarios.txt").getFile();
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] data = linea.split("\\|");
                if(data.length>1){
                    users.add(new User(data[0], data[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return users;
    }
}
