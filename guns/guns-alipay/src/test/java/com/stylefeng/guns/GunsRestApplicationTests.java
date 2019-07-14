package com.stylefeng.guns;

import com.stylefeng.guns.rest.AlipayApplication;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AlipayApplication.class)
public class GunsRestApplicationTests {

	@Autowired
	private FTPUtil ftpUtil;

	@Test
	public void contextLoads() throws IOException {

		String fileStrByAddress = ftpUtil.getFileStrByAddress("sources/seats/cgs.json");
		System.out.println(fileStrByAddress);
	}

}
