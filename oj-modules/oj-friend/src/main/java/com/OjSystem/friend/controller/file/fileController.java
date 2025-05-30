package com.OjSystem.friend.controller.file;

import com.OjSystem.common.file.domain.OSSResult;
import com.OjSystem.friend.service.file.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/file")
@RestController
public class fileController {

    @Autowired
    private IFileService fileService;

    /**
     * 上传文件
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    public OSSResult upload(@RequestPart MultipartFile file){
        return fileService.upload(file);
    }

}
