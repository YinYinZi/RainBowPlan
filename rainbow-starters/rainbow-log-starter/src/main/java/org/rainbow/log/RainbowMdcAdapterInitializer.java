package org.rainbow.log;

import org.slf4j.RainbowMdcAdapter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author K
 * @date 2021/2/14  12:37
 */
public class RainbowMdcAdapterInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public RainbowMdcAdapterInitializer() {}

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        RainbowMdcAdapter.getInstance();
    }
}
