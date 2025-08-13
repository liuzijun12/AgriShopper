package com.youlai.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 应用启动类
 *
 * @author Ray.Hao
 * @since 0.0.1
 */
@SpringBootApplication
@ConfigurationPropertiesScan // 开启配置属性绑定
@MapperScan("com.youlai.boot.**.mapper") // 扫描Mapper接口
public class YouLaiBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouLaiBootApplication.class, args);
    }

}
