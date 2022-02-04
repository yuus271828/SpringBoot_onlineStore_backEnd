package com.ohiji;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.ohiji.OnlineStoreOhijiBackEndApplication.class)
public class OnlineStoreOhijiBackEndApplicationTests {

	@Test
	public void contextLoads(ApplicationContext context) {
		assertThat(context).isNotNull();
	}

}
