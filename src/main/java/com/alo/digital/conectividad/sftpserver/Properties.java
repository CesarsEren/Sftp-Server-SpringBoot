package com.alo.digital.conectividad.sftpserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {

    public static String SFTP_DIR;
    public static int SFTP_PORT;

    @Value("${app.sftp.dir}")
    public void setSftpDir(String urlPos) {
        Properties.SFTP_DIR = urlPos;
    }

    @Value("${app.sftp.port}")
    public void setSftpPort(int s) {
        Properties.SFTP_PORT = s;
    }
}
