package com.smartgridz;

import com.smartgridz.controller.dto.UserDto;
import com.smartgridz.domain.RoleType;
import com.smartgridz.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class SmartGridzApplicationTests {

	@Autowired
	UserService userService;

	private static final String adminLogin    = "bigdog";
	private static final String adminUserName = "Forest Gump";

	private static final String adminEmail    = "forest.gump@smartgridz.com";

	@AfterEach
	public void tearDown() throws Exception {
		try {
			userService.deleteUserByLogin(adminLogin);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void testGetUserByLogin() throws Exception {
		//arrange
		UserDto admin = createUser();

		//act
		userService.saveUser(admin);

		//assert
		Assert.isTrue(userService.findUserByLogin(adminLogin) != null, "Unable to find user by login.");
	}

	@Test
	public void testGetUserByUserName() throws Exception {
		//arrange
		UserDto admin = createUser();

		//act
		userService.saveUser(admin);

		//assert
		Assert.isTrue(userService.findUserByUserName(adminUserName) != null, "Unable to find user by username.");
	}

	private UserDto createUser() {
		UserDto admin = new UserDto();
		admin.setLogin(adminLogin);
		admin.setUserName(adminUserName);
		admin.setEmail(adminEmail);

		admin.setPassword("noop");
		admin.setRole(RoleType.ADMINISTRATOR.name());

		return admin;
	}

}
