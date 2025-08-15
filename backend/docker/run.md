
#  Docker Compose 安装中间件 MySQL、Redis、Minio、Xxl-Job

## 安装

```bash
# windows 环境
docker-compose -f ./docker-compose_win.yml -p youlai-boot up -d

#linux  mac 环境
docker-compose -f ./docker-compose_linux.yml -p youlai-boot up -d

```

- p youlai-boot 指定命名空间，避免与其他容器冲突，这里方便管理，统一管理和卸载

## 卸载
```bash
docker-compose -f ./docker-compose.yml -p youlai-boot down
```

