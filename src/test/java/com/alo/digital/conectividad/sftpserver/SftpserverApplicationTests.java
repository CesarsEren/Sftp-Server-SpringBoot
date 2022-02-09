package com.alo.digital.conectividad.sftpserver;

import com.alo.digital.conectividad.sftpserver.beans.User;
import com.alo.digital.conectividad.sftpserver.util.LeeFichero;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SftpserverApplicationTests {

	@Test
	void contextLoads() {
		LeeFichero leeFichero = new LeeFichero();
		List<User> userList = leeFichero.leerUsuarios();
		System.out.println(userList.size());
	}

}
