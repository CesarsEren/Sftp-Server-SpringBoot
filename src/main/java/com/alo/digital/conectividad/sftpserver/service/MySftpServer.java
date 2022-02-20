package com.alo.digital.conectividad.sftpserver.service;

import com.alo.digital.conectividad.sftpserver.beans.User;
import com.alo.digital.conectividad.sftpserver.util.LeeFichero;
import org.apache.sshd.common.AttributeStore;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.channel.ChannelListener;
import org.apache.sshd.common.channel.throttle.ChannelStreamPacketWriterResolver;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.io.PacketWriter;
import org.apache.sshd.common.kex.KexProposalOption;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    public static final AttributeStore.AttributeKey<String> STR_KEY = new AttributeStore.AttributeKey<>();
    public static final AttributeStore.AttributeKey<Long> LONG_KEY = new AttributeStore.AttributeKey<>();

    private void start() throws IOException {
        log.info("CARGANDO USUARIOS");
        userList = leeFichero.leerUsuarios();
        log.info("INICIANDO SFTP" + userList.size());
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
        SftpSubsystemFactory sftpSubsystemFactory = new SftpSubsystemFactory();
        sftpSubsystemFactory.addSftpEventListener(new AbstractSftpEventListenerAdapter() {
            @Override
            public void initialized(ServerSession session, int version) {
                log.info("USUARIO INICIO {}", session.getUsername());
                super.initialized(session, version);
            }

            @Override
            public void destroying(ServerSession session) {
                super.destroying(session);
            }

            @Override
            public void writing(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) throws IOException {
                super.writing(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen);
            }

            @Override
            public void written(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, Throwable thrown) throws IOException {
                log.info("[{}] SE ENTREGA ARCHIVO :{}",session.getUsername(), localHandle);
                super.written(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen, thrown);
            }

            @Override
            public void close(ServerSession session, String remoteHandle, Handle localHandle) {
                super.close(session, remoteHandle, localHandle);
            }

            @Override
            public void creating(ServerSession session, Path path, Map<String, ?> attrs) throws IOException {
                super.creating(session, path, attrs);
            }

            @Override
            public void created(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) throws IOException {
                super.created(session, path, attrs, thrown);
            }

            @Override
            public void moving(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts) throws IOException {

                super.moving(session, srcPath, dstPath, opts);
            }

            @Override
            public void moved(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts, Throwable thrown) throws IOException {
                log.info("[{}] SE MUEVE EL ARCHIVO de:{} hasta :{}",session.getUsername(),srcPath, dstPath);
                super.moved(session, srcPath, dstPath, opts, thrown);
            }

            @Override
            public void removing(ServerSession session, Path path) throws IOException {
                super.removing(session, path);
            }

            @Override
            public void removed(ServerSession session, Path path, Throwable thrown) throws IOException {
                log.info("[{}] SE ELIMINA EL ARCHIVO : {}",session.getUsername(),path);
                super.removed(session, path, thrown);
            }

        });


        sshd.setSubsystemFactories(Collections.singletonList(sftpSubsystemFactory));
        log.info("Creando los usuarios.");
        sshd.setPasswordAuthenticator((username, password, session) -> userList.stream().anyMatch(user -> user.getUsuario().equals(username) && user.getPassword().equals(password)));


        sshd.start();
        log.info("SFTP server INICIO");
    }
}
