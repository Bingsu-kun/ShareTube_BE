package com.takarabako.sharetube;

import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
public class EncryptTest {

  private String encryptPassword = "you shall not pass";

  @Test
  public void contextLoads() throws Exception {
    JasyptEncryptorConfigurationProperties properties = new JasyptEncryptorConfigurationProperties();

    SimpleStringPBEConfig pbeConfig = new SimpleStringPBEConfig();
    pbeConfig.setPassword(encryptPassword);
    pbeConfig.setAlgorithm(properties.getAlgorithm());
    pbeConfig.setKeyObtentionIterations(properties.getKeyObtentionIterations());
    pbeConfig.setPoolSize(properties.getPoolSize());
    pbeConfig.setSaltGeneratorClassName(properties.getSaltGeneratorClassname());
    pbeConfig.setIvGeneratorClassName(properties.getIvGeneratorClassname());
    pbeConfig.setStringOutputType(properties.getStringOutputType());

    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    encryptor.setConfig(pbeConfig);

    log.info("google client secret : " + encryptor.encrypt("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"));
    log.info("jwt client secret : " + encryptor.encrypt("DD08B39A2323170BA2BC68A5888463F4BD875B8A4638F8B17A9CA9B8E1496D0679CB6266DCD96B3CD812FF3C9EDD6E84520507E5A0F51F53F9220BC23B275817"));
    log.info("youtube key : " + encryptor.encrypt("AIzaSyAG5CtIFNO1Lb_Sq0wL8ald6O37no69BYE"));

  }

}
