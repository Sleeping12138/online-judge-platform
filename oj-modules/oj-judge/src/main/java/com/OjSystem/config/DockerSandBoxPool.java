package com.OjSystem.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.OjSystem.constants.JudgeConstants;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class DockerSandBoxPool {

    private DockerClient dockerClient;

    private String sandboxImage;

    private String volumeDir;

    private Long memoryLimit;

    private Long memorySwapLimit;

    private Long cpuLimit;

    private int poolSize;

    private String containerNamePrefix;

    private BlockingQueue<String> containerQueue;

    private Map<String, String> containerNameMap;

    /**
     * docker池对象构造方法
     *
     * @param dockerClient
     * @param sandboxImage
     * @param volumeDir
     * @param memoryLimit
     * @param memorySwapLimit
     * @param cpuLimit
     * @param poolSize
     * @param containerNamePrefix
     */
    public DockerSandBoxPool(DockerClient dockerClient, String sandboxImage, String volumeDir, Long memoryLimit,
                             Long memorySwapLimit, Long cpuLimit, int poolSize, String containerNamePrefix) {
        this.dockerClient = dockerClient;
        this.sandboxImage = sandboxImage;
        this.volumeDir = volumeDir;
        this.memoryLimit = memoryLimit;
        this.memorySwapLimit = memorySwapLimit;
        this.cpuLimit = cpuLimit;
        this.poolSize = poolSize;
        this.containerQueue = new ArrayBlockingQueue<>(poolSize);
        this.containerNamePrefix = containerNamePrefix;
        this.containerNameMap = new HashMap<>();
    }


    /**
     * 初始化docker池（就是向docker池对象中的集合中存储创建好的docker容器）
     */
    public void initDockerPool() {
        // 初始化容器池的
        for (int i = 0; i < poolSize; i++) {
            createContainer(containerNamePrefix + "-" + i);
        }
    }


    /**
     * 获取一个docker容器
     *
     * @return
     */
    public String getContainer() {
        try {
            return containerQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 归还docker容器
     *
     * @param containerId
     */
    public void returnContainer(String containerId) {
        containerQueue.add(containerId);
    }


    /**
     * 创建容器
     *
     * @param containerName
     */
    private void createContainer(String containerName) {
        // 判断容器是否已经存在
        List<Container> containerList = dockerClient.listContainersCmd().withShowAll(true).exec();
        if (!CollectionUtil.isEmpty(containerList)) {
            String names = JudgeConstants.JAVA_CONTAINER_PREFIX + containerName;
            for (Container container : containerList) {
                String[] containerNames = container.getNames();
                if (containerNames != null && containerNames.length > 0 && names.equals(containerNames[0])) {
                    if ("created".equals(container.getState()) || "exited".equals(container.getState())) {
                        //启动容器
                        dockerClient.startContainerCmd(container.getId()).exec();
                    }
                    containerQueue.add(container.getId());
                    containerNameMap.put(container.getId(), containerName);
                    return;
                }
            }
        }

        // 不存在
        //拉取镜像
        pullJavaEnvImage();
        //创建容器  限制资源   控制权限
        HostConfig hostConfig = getHostConfig(containerName);
        CreateContainerCmd containerCmd = dockerClient
                .createContainerCmd(JudgeConstants.JAVA_ENV_IMAGE)
                .withName(containerName);
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        //记录容器id
        String containerId = createContainerResponse.getId();
        //启动容器
        dockerClient.startContainerCmd(containerId).exec();

        containerQueue.add(containerId);
        containerNameMap.put(containerId, containerName);
    }


    /**
     * 拉取docker镜像
     */
    private void pullJavaEnvImage() {
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();

        // 判断镜像是否已经来取了
        List<Image> imageList = listImagesCmd.exec();
        for (Image image : imageList) {
            String[] repoTags = image.getRepoTags();
            if (repoTags != null && repoTags.length > 0 && sandboxImage.equals(repoTags[0])) {
                return;
            }
        }
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(sandboxImage);
        try {
            pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 配置docker容器的资源情况
     * @param containerName
     * @return
     */
    private HostConfig getHostConfig(String containerName) {
        HostConfig hostConfig = new HostConfig();

        //设置挂载目录，指定用户代码路径
        String userCodeDir = createContainerDir(containerName);
        hostConfig.setBinds(new Bind(userCodeDir, new Volume(volumeDir)));

        //限制docker容器使用资源
        hostConfig.withMemory(memoryLimit);
        hostConfig.withMemorySwap(memorySwapLimit);
        hostConfig.withCpuCount(cpuLimit);
        hostConfig.withNetworkMode("none");  //禁用网络
        hostConfig.withReadonlyRootfs(true); //禁止在root目录写文件
        return hostConfig;
    }


    /**
     * 获取指定docker容器的代码资源路径
     * @param containerId
     * @return
     */
    public String getCodeDir(String containerId) {
        String containerName = containerNameMap.get(containerId);
        return System.getProperty("user.dir") + File.separator + JudgeConstants.CODE_DIR_POOL + File.separator + containerName;
    }


    /**
     * 创建docker容器的代码资源路径
     * @param containerName
     * @return
     */
    private String createContainerDir(String containerName) {
        //一级目录  存放所有容器的挂载目录
        String codeDir = System.getProperty("user.dir") + File.separator + JudgeConstants.CODE_DIR_POOL;
        if (!FileUtil.exist(codeDir)) {
            FileUtil.mkdir(codeDir);
        }
        return codeDir + File.separator + containerName;
    }
}
