ActiveMQ
=================

> docker部署ActiveMQ 😊

* 查询Docker镜像
```
docker search activemq
```

* 下载Docker镜像
```
docker pull webcenter/activemq
```

* 创建&运行ActiveMQ容器
```
docker run -d --name myactivemq -p 61616:61616 -p 8161:8161 webcenter/activemq
```

* 查看WEB管理界面
```
浏览器输入http://127.0.0.1:8161/, 点击Manage ActiveMQ broker使用默认账号/密码：admin/admin进入查看
```