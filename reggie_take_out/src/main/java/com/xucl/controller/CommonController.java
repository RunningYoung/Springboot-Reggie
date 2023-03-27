package com.xucl.controller;

import com.xucl.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

/**
 * @author xucl
 * @apiNote 文件上传和下载
 * @date 2023/3/24 09:52
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * TODO
     * 文件上传
     * @param file
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是个临时文件，需要转存到其他置顶位置，否则本次请求完成后临时文件会删除
        log.info("文件上传");

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//后缀
        //使用UUID 重新生成文件名，防止文件名重复造成覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        //创建目录对象,如果目录不存在，则创建
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        try {
            //降临时文件转存到其他位置
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            file.transferTo(new File(System.getProperty("user.dir") + basePath + fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * TODO
     * 文件下载
     * @param name
     * @param response
     * @date
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response ){
        String path = System.getProperty("user.dir") + basePath + name;
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fin = new FileInputStream(new File(path));
            //输出流，通过输出流将文件写会浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("images/jpeg");

            byte[] content = new byte[1024];
            int len = 0;
            while ((len = fin.read(content)) != -1) {
                outputStream.write(content, 0, len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fin.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
