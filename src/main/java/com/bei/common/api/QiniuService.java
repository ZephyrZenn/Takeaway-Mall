package com.bei.common.api;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
@Slf4j
public class QiniuService {

    @Value("${qiniu.accessKey}")
    private String accessKey;

    @Value("${qiniu.secretKey}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.domain}")
    private String domain;

    /**
     * 上传文件
     * @param file 要上传的二进制文件
     * @param filename 文件名
     * @return 在存储空间的文件名
     * */
    public String uploadFile(MultipartFile file, String filename) {

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        try {
            byte[] uploadBytes = file.getBytes();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(uploadBytes, filename, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
                return putRet.key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                log.error(r.toString());
                try {
                    log.error(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (IOException ex) {
            //ignore
        }
        return "";
    }

    /**
     * 下载文件
     * @param filename 文件名
     * @return 文件url
     * */
    public String download(String filename) throws UnsupportedEncodingException {
        String encodedFileName = URLEncoder.encode(filename, "utf-8").replace("+", "%20");
        String finalUrl = String.format("%s/%s", domain, encodedFileName);
        return "http://" + finalUrl;
    }
}
