package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtil {

    // 地址 端口 用户名 密码
    private String hostName;
    private Integer port;
    private String userName;
    private String password;
    private String serverqr;

    private FTPClient ftpClient = null;

    private void initFTPClient() {
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName,port);
            ftpClient.login(userName,password);
        } catch (Exception e) {
            log.error("初始化TFP异常", e);
        }
    }

    // 输入一个路径 将路径文件转换为字符串 返回
    public String getFileStrByAddress(String fileAddress) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            initFTPClient();
            bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            ftpClient.retrieveFileStream(fileAddress)
                    )
            );

            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                String lineStr = bufferedReader.readLine();
                if (lineStr==null) {
                    break;
                }
                stringBuffer.append(lineStr);
            }
            ftpClient.logout();
            return stringBuffer.toString();
        } catch (Exception e) {
            log.error("初始化FTP异常", e);
        } finally {
            bufferedReader.close();
        }
        return null;
    }

    public boolean uploadFile(String fileName, File file) {

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);

            initFTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            ftpClient.changeWorkingDirectory(this.getServerqr());
            ftpClient.storeFile(fileName, fileInputStream);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传失败", e);
            return false;
        } finally {
            try {
                fileInputStream.close();
                ftpClient.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        FTPUtil ftpUtil = new FTPUtil();
        String fileStrByAddress = ftpUtil.getFileStrByAddress("sources/seats/cgs.json");
        System.out.println(fileStrByAddress);
    }
}
