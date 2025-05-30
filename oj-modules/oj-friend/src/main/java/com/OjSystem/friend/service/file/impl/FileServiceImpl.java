package com.OjSystem.friend.service.file.impl;

import com.OjSystem.common.core.errorcode.modules.ojfriend.FriendServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.file.domain.OSSResult;
import com.OjSystem.common.file.service.OSSService;
import com.OjSystem.friend.service.file.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileServiceImpl implements IFileService {

    @Autowired
    private OSSService ossService;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @Override
    public OSSResult upload(MultipartFile file) {
        try {
            return ossService.uploadFile(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FriendServiceException(FriendServiceErrorCode.UPLOAD_PHOTO_IS_ERROR);
        }
    }
}
