# 代码生成器



## 1 使用说明

```properties
heima.url=jdbc:mysql://localhost:3306/mp?useUnicode=true&useSSL=false&characterEncoding=utf8
#heima.url=jdbc:mysql://192.168.211.136:3306/leadnews_search?useUnicode=true&useSSL=false&characterEncoding=utf8
heima.username=root
heima.password=123456
heima.driver=com.mysql.jdbc.Driver
# 配置模块名称 一个模块名即可 例如：admin,user 分别表示 admin微服务,user 微服务
heima.moduleName=search
# 父包名 一般设置为两层 使用之后，生成的路径为 com.itheima.user.xxx
heima.parent=com.itheima
# controller继承的类的全路径
heima.superController=com.itheima.core.controller.AbstractCoreController
# 是否开启controller继承
heima.superControllerFlag=true
# 是否开启swagger2生成
heima.swagger=true

#生成feign
heima.superFeign=com.itheima.core.feign.CoreFeign
#是否开启生成Feign接口
heima.superFeignFlag=true
# 设置微服务的名称
heima.application.name=leadnews-search
# 系统工程路径 请填写你自己所在的代码生成器所在的工程目录
heima.projectPath=C:\\Users\\admin\\IdeaProjects\\codeTemplatePlus
# 是否开启系统工程路径获取
heima.enableProject=true
```

