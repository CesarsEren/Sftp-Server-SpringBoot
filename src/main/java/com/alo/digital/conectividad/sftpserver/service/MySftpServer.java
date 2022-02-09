package com.alo.digital.conectividad.sftpserver.service;

import com.alo.digital.conectividad.sftpserver.beans.User;
import com.alo.digital.conectividad.sftpserver.util.LeeFichero;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.alo.digital.conectividad.sftpserver.Properties.SFTP_DIR;
import static com.alo.digital.conectividad.sftpserver.Properties.SFTP_PORT;


@Service
public class MySftpServer {
    private static Logger log = LoggerFactory.getLogger(MySftpServer.class);

    List<User> userList = new ArrayList<>();

    @Autowired
    LeeFichero leeFichero;

    @PostConstruct
    public void startServer() throws IOException {
        start();
    }

    private void start() throws IOException {
        log.info("CARGANDO USUARIOS");
        userList = leeFichero.leerUsuarios();
        log.info("INICIANDO SFTP"+userList.size());
        SshServer sshd = SshServer.setUpDefaultServer();
        //sshd.setHost("localhost");
        sshd.setPort(SFTP_PORT);
        log.info("port [" + sshd.getPort() + "]");
        //log.info("host [" + sshd.getHost() + "]");

        log.info("Creando claves de permiso para [" + SFTP_DIR + "]");
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser")));
        log.info("SFTP acepta todas las claves publicas de authenticación ");
        sshd.setPublickeyAuthenticator((s, publicKey, serverSession) -> true);

        log.info("Permitiendo acceso a la CARPETA [" + SFTP_DIR + "]");
        File f = new File(SFTP_DIR);
        Path dir = Paths.get(f.toURI());

        log.info("Permitiendo acceso a las subcarpetas");
        Files.createDirectories(dir);
        sshd.setFileSystemFactory(new VirtualFileSystemFactory(dir.toAbsolutePath()));
        //"host.ser"
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        log.info("Creando los usuarios.");
        sshd.setPasswordAuthenticator((username, password, session) -> userList.stream().anyMatch(user -> user.getUsuario().equals(username) && user.getPassword().equals(password)));

        sshd.start();
        log.info("SFTP server INICIO");
    }
}
