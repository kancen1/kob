2. 配置SpringBoot

Maven仓库地址

Mybatis-Plus官网

在pom.xml文件中添加依赖：

Spring Boot Starter JDBC

Project Lombok

MySQL Connector/J

mybatis-plus-boot-starter

mybatis-plus-generator

spring-boot-starter-security

jjwt-api

jjwt-impl

jjwt-jackson

 

 

实现config.filter.JwtAuthenticationTokenFilter类，用来验证jwt token，如果验证成功，则将User信息注入上下文中

import org.jetbrains.annotations.NotNull; 爆红要加上JetBrains Java Annotations这个依赖

import jakarta.servlet.FilterChain;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;

用新版本springboot的要修改

 

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.kob</groupId>
    <artifactId>backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>backend</name>
    <description>backend</description>

    <properties>
       <java.version>1.8</java.version>
       <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
       <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
       <spring-boot.version>2.3.7.RELEASE</spring-boot.version>
    </properties>

    <dependencies>
       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-jdbc</artifactId>
          <version>2.7.1</version>
       </dependency>

       <dependency>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
          <version>1.18.24</version>
          <scope>provided</scope>
       </dependency>

       <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>8.0.29</version>
       </dependency>

       <dependency>
          <groupId>com.baomidou</groupId>
          <artifactId>mybatis-plus-generator</artifactId>
          <version>3.5.3</version>
       </dependency>


       <dependency>
          <groupId>com.baomidou</groupId>
          <artifactId>mybatis-plus-boot-starter</artifactId>
          <version>3.5.2</version>
       </dependency>

       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-security</artifactId>
          <version>2.7.1</version>
       </dependency>

       <dependency>
          <groupId>io.jsonwebtoken</groupId>
          <artifactId>jjwt-api</artifactId>
          <version>0.11.5</version>
       </dependency>

       <dependency>
          <groupId>io.jsonwebtoken</groupId>
          <artifactId>jjwt-impl</artifactId>
          <version>0.11.5</version>
          <scope>runtime</scope>
       </dependency>

       <dependency>
          <groupId>io.jsonwebtoken</groupId>
          <artifactId>jjwt-jackson</artifactId>
          <version>0.11.5</version>
          <scope>runtime</scope>
       </dependency>

       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-thymeleaf</artifactId>
       </dependency>
       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-web</artifactId>
       </dependency>

       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-test</artifactId>
          <scope>test</scope>
          <exclusions>
             <exclusion>
                <groupId>org.junit.vintage</groupId>
                <artifactId>junit-vintage-engine</artifactId>
             </exclusion>
          </exclusions>
       </dependency>
    </dependencies>

    <dependencyManagement>
       <dependencies>
          <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-dependencies</artifactId>
             <version>${spring-boot.version}</version>
             <type>pom</type>
             <scope>import</scope>
          </dependency>
       </dependencies>
    </dependencyManagement>

    <build>
       <plugins>
          <plugin>
             <groupId>org.apache.maven.plugins</groupId>
             <artifactId>maven-compiler-plugin</artifactId>
             <version>3.8.1</version>
             <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
             </configuration>
          </plugin>
          <plugin>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-maven-plugin</artifactId>
             <version>2.3.7.RELEASE</version>
             <configuration>
                <mainClass>com.kob.backend.BackendApplication</mainClass>
             </configuration>
             <executions>
                <execution>
                   <id>repackage</id>
                   <goals>
                      <goal>repackage</goal>
                   </goals>
                </execution>
             </executions>
          </plugin>
       </plugins>
    </build>

</project>
```



```
<!--添加依赖 如果找不到依赖可以进行清除缓存-->
<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-jdbc -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
    <version>3.3.2</version>
</dependency>

<!--    这个依赖可以简化代码工作量  -->
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.34</version>
    <scope>provided</scope>
</dependency>

<!-- https://mvnrepository.com/artifact/com.mysql/mysql-connector-j -->
<dependency>
    <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
    <version>9.0.0</version>
</dependency>

<!--    默认帮忙写好很多sql语句  -->
<!-- https://mvnrepository.com/artifact/com.baomidou/mybatis-plus-boot-starter -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.7</version>
</dependency>

<!--    可以帮忙自动生成一些函数   -->
<!-- https://mvnrepository.com/artifact/com.baomidou/mybatis-plus-generator -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.5.7</version>
</dependency>

<!--    授权依赖用于管理是否有权限访问这个网页默认用户名user密码在启动说明中没次随机生成 -->
<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>3.3.2</version>
</dependency>

      <!--  jwt验证依赖    -->
<!-- https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-api -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-jackson -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- https://mvnrepository.com/artifact/org.jetbrains/annotations -->
<dependency>
    <groupId>org.jetbrains</groupId>
    <artifactId>annotations</artifactId>
    <version>24.1.0</version>
</dependency>


<!--    以上是sprint boot连接SQL常用依赖-->
```

 

