package com.stylefeng.guns;

import com.stylefeng.guns.rest.AlipayApplication;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
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

		File file = new File("/Users/dander/Desktop/qr-c7fa59be8a104f628a7ee9dfa1a42116.png");
		boolean uploadFile = ftpUtil.uploadFile("qr-c7fa59be8a104f628a7ee9dfa1a42116.png", file);
		System.out.println("上传是否成功 + " + uploadFile);
	}

}
