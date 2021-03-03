# 1.Swagger

## 1.1 简介

Swagger 是一个规范且完整的框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务。

Swagger 的目标是对 REST API 定义一个标准且和语言无关的接口，可以让人和计算机拥有无须访问源码、文档或网络流量监测就可以发现和理解服务的能力。当通过 Swagger 进行正确定义，用户可以理解远程服务并使用最少实现逻辑与远程服务进行交互。与为底层编程所实现的接口类似，Swagger 消除了调用服务时可能会有的猜测。

__Swagger 的优势__

- 支持 API 自动生成同步的在线文档：使用 Swagger 后可以直接通过代码生成文档，不再需要自己手动编写接口文档了，对程序员来说非常方便，可以节约写文档的时间去学习新技术。
- 提供 Web 页面在线测试 API：光有文档还不够，Swagger 生成的文档还支持在线测试。参数和格式都定好了，直接在界面上输入参数对应的值即可在线测试接口。

__[Swagger官网地址](https://swagger.io/)__

## 1.2 在项目中使用Swagger

在项目中使用Swagger需要SpringBox

- Swagger2
- Swaggerui

SpringBoot也可以集成Swagger

1. 新建一个SpringBoot项目 SpringBoot-web
2. 导入相关依赖

```xml
<!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
<!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger2 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
```

3. 编写一个Hello工程

```java
package com.example.swaggerdemo.controller;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello (){
        return "Hello, SpringBoot";
    };
}
```

4. 默认配置Swagger

```java
package com.example.swaggerdemo.config;

@Configuration
@EnableSwagger2   //开启Swagger2
public class SwaggerConfig {

}

```

5. 访问8080:/swagger-ui.html


## 1.3 详细配置Swagger

Swagger的bean实例Docker

```java
package com.example.swaggerdemo.config;

@Configuration
@EnableSwagger2   //开启Swagger2
public class SwaggerConfig {
    //配置了Swagger的bean实例
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        return new ApiInfo(
                "Hashqi Swagger", 
                "Api Documentation",
                "1.0",
                "http://github.com/sqsqsqw",
                new Contact("Hashqi", "http://github.com/sqsqsqw", "123@123.com"),
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
    }
}

```

## 1.4 Swagger 配置扫描接口

docket.select()

```java
package com.example.swaggerdemo.config;

@Configuration
@EnableSwagger2   //开启Swagger2
public class SwaggerConfig {
    //配置了Swagger的bean实例
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //enable变成false时会关闭swagger2
                .enable(false)
                .select()
                //apis()用于过滤api
                //  RequestHandlerSelectors,配置要扫描接口的方式
                //      basePackage 指定要扫描的包
                //      any 扫描全部
                //      none 不扫描
                //      withMethodAnnotation 通过方法注解扫描
                //      withClassAnnotation 通过类注解扫描
                //.apis(RequestHandlerSelectors.basePackage("com.example.controller"))
                //.apis(RequestHandlerSelectors.withClassAnnotation(Controller.class))
                .apis(RequestHandlerSelectors.withMethodAnnotation(GetMapping.class))

                //paths()用于过滤路径
                //.paths(PathSelectors.ant("/example/swaggerdemo/config/**"))
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfo(
                "Hashqi Swagger",
                "Api Documentation",
                "1.0",
                "http://github.com/sqsqsqw",
                new Contact("Hashqi", "http://github.com/sqsqsqw", "123@123.com"),
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
    }
}

```

假设Swagger只能在生产环境中使用，在其他环境不使用：

- 判断是不是生产环境
- 注入enable()

```java
//SwaggerConfig
package com.example.swaggerdemo.config;

@Configuration
@EnableSwagger2   //开启Swagger2
public class SwaggerConfig {
    //配置了Swagger的bean实例
    @Bean
    public Docket docket(Environment environment) {

        Profiles profiles = Profiles.of("dev");

        //获得项目的环境
        boolean flag = environment.acceptsProfiles(profiles);

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //enable变成false时会关闭swagger2
                .enable(flag)
                .select()
                //apis()用于过滤api
                //  RequestHandlerSelectors,配置要扫描接口的方式
                //      basePackage 指定要扫描的包
                //      any 扫描全部
                //      none 不扫描
                //      withMethodAnnotation 通过方法注解扫描
                //      withClassAnnotation 通过类注解扫描
                //.apis(RequestHandlerSelectors.basePackage("com.example.controller"))
                //.apis(RequestHandlerSelectors.withClassAnnotation(Controller.class))
                .apis(RequestHandlerSelectors.withMethodAnnotation(GetMapping.class))

                //paths()用于过滤路径
                //.paths(PathSelectors.ant("/example/swaggerdemo/config/**"))
                .build();
    }

    private ApiInfo apiInfo(){...}
}

//application.properties
spring.profiles.active=pro

//application-dev.properties
server.port=8081

//application-pro.properties
server.port=8082
```

__配置API文档的分组__

.groupName("Hashqi")

```java
//多人配置
    @Bean
    public Docket docket1(Environment environment) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("1");
    }

    @Bean
    public Docket docket2(Environment environment) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("2");
    }
    @Bean
    public Docket docket3(Environment environment) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("3");
    }
```

__实体类配置__

```java
@RestController
public class HelloController {

    @ApiOperation("Hello访问接口")
    @GetMapping("/hello")
    public String hello (){
        return "Hello, SpringBoot";
    };

    //只要我们的接口的返回值中存在实体类，实体就会被扫描到Swagger中
    @PostMapping("/user")
    public User user(){
        return new User();
    }
}
```

```java
//或者在实体中添加注解进行注释
@ApiModel("用户")
public class User {
    @ApiModelProperty("用户名")
    public String username;
    @ApiModelProperty("用户密码")
    public String password;
}
```
