package com.OjSystem.common.file.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.util.StrUtil;
import com.OjSystem.common.core.errorcode.modules.ojfriend.FriendServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.file.config.OSSProperties;
import com.OjSystem.common.file.domain.OSSResult;
import com.OjSystem.common.redis.service.RedisService;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
@RefreshScope
public class OSSService {

    @Autowired
    private OSSProperties prop;

    @Autowired
    private OSSClient ossClient;

    @Autowired
    private RedisService redisService;

    @Value("${file.max-time}")
    private int maxTime;


    /**
     * 上传文件
     * @param file
     * @return
     * @throws Exception
     */
    public OSSResult uploadFile(MultipartFile file) throws Exception {
        InputStream inputStream = null;
        try {
            String fileName;
            if (file.getOriginalFilename() != null) {
                fileName = file.getOriginalFilename().toLowerCase();
            } else {
                fileName = "a.png";
            }
            String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
            inputStream = file.getInputStream();
            return upload(extName, inputStream);
        } catch (Exception e) {
            log.error("OSS upload file error", e);
            throw new FriendServiceException(FriendServiceErrorCode.UPLOAD_PHOTO_IS_ERROR);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private OSSResult upload(String fileType, InputStream inputStream) {
        // key pattern: file/id.xxx, cannot start with /
        String key = prop.getPathPrefix() + ObjectId.next() + "." + fileType;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setObjectAcl(CannedAccessControlList.PublicRead);
        PutObjectRequest request = new PutObjectRequest(prop.getBucketName(), key, inputStream, objectMetadata);
        PutObjectResult putObjectResult;
        try {
            putObjectResult = ossClient.putObject(request);
        } catch (Exception e) {
            log.error("OSS put object error: {}", ExceptionUtil.stacktraceToOneLineString(e, 500));
            throw new FriendServiceException(FriendServiceErrorCode.UPLOAD_PHOTO_IS_ERROR);
        }
        return assembleOSSResult(key, putObjectResult);
    }

    private OSSResult assembleOSSResult(String key, PutObjectResult putObjectResult) {
        OSSResult ossResult = new OSSResult();
        if (putObjectResult == null || StrUtil.isBlank(putObjectResult.getRequestId())) {
            ossResult.setSuccess(false);
        } else {
            ossResult.setSuccess(true);
            ossResult.setName(FileUtil.getName(key));
        }
        return ossResult;
    }
}
