package com.bei.controller;

import com.bei.common.CommonResult;
import com.bei.common.api.QiniuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@RequestMapping("/common")
@RestController
public class CommonController {

    @Autowired
    private QiniuService qiniuService;

    @PostMapping("/upload")
    public CommonResult uploadFile(MultipartFile file) {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID() + suffix;
        String name = qiniuService.uploadFile(file, filename);
        return CommonResult.success(name);
    }

    @GetMapping("/download")
    public CommonResult download(String name, HttpServletResponse response) {
        try {
            String picUrl = qiniuService.download(name);
            URL url = new URL(picUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000*5);
            urlConnection.connect();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            OutputStream out = response.getOutputStream();
            byte[] buf = new byte[1024];
            int size;
            while ((size = in.read(buf)) != -1) {
                out.write(buf, 0, size);
            }
            in.close();
            out.close();
        } catch (UnsupportedEncodingException e) {
            return CommonResult.error("文件格式错误");
        } catch (MalformedURLException e) {
            return CommonResult.error("url获取错误");
        } catch (IOException e) {
            return CommonResult.error("文件读取错误");
        }
        return CommonResult.success("传输成功");
    }
}
