package org.refarch.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@ComponentScan({ "org.refarch" })
@EnableWebMvc
//@Import({ HikariPersistenceConfig.class })
public class AppConf {
	
}
