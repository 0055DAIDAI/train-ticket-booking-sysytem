package org.example.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyuncs.auth.EnvironmentVariableCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class AliyunOSSOperator {

    //    方法1
//    @Value("${aliyun.oss.endpoint}")
//    private String endpoint;
//    @Value("${aliyun.oss.bucketName}")
//    private String bucketName;
//    @Value("${aliyun.oss.region}")
//    private String region;
//    法2
    @Autowired
    AliyunOSSProperties aliyunOSSProperties;

    public  String upload(byte[] content,String originalFilename) throws Exception{
// 从环境变量中获取阿里云账号的AccessKey ID和AccessKey Secret
        EnvironmentVariableCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
//        完整的object路径: xxxxx/xxxxx.jpg
//        获取当前系统日期的字符串，格式为yyyy/MM
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
//        生成一个新的不重复的文件名
        String NewFileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = dir + "/" + NewFileName;
//        创建OSSClient实例
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        // 设置签名算法版本为 V4
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        // 设置使用 HTTPS 协议访问 OSS，保证传输安全性
        clientBuilderConfiguration.setProtocol(Protocol.HTTPS);

        OSS ossClient = OSSClientBuilder.create()
                // 以华东1（杭州）地域的外网访问域名为例，Endpoint填写为oss-cn- hangzhou.aliyuncs.com
                .endpoint("oss-cn-guangzhou.aliyuncs.com")
                // 从环境变量中获取访问凭证（需提前配置 OSS_ACCESS_KEY_ID 和 OSS_ACCESS_KEY_SECRET）
                .credentialsProvider(CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider())
                // 设置客户端配置
                .clientConfiguration(clientBuilderConfiguration)
                // 以华东1（杭州）地域为例，Region填写为cn-hangzhou
                .region("cn-guangzhou")
                .build();
        try {
            ossClient.putObject(aliyunOSSProperties.getBucketName(), objectName, new ByteArrayInputStream(content));
        }finally {
            ossClient.shutdown();
        }


        //
        return "https://" + aliyunOSSProperties.getBucketName() + "." + aliyunOSSProperties.getEndpoint() + "/" + objectName;

    }
}