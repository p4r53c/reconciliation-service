package com.msr.rnip.reconciliation.configuration;

import com.msr.rnip.reconciliation.adapter.interceptor.JINNBaseInterceptor;
import com.msr.rnip.reconciliation.adapter.jinn.JINNServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import javax.xml.bind.Marshaller;
import java.util.HashMap;

//@author p4r53c
@Configuration
@PropertySource("classpath:crypto.properties")
public class JINNAdapterConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.msr.rnip.reconciliation.types.jinn");
        marshaller.setMarshallerProperties(new HashMap<String, Object>() {{
            //put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            put(Marshaller.JAXB_ENCODING, "UTF-8");
            put(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        }});
        return marshaller;
    }

    @Bean
    @Autowired
    public JINNServiceAdapter jinnServiceAdapter (Jaxb2Marshaller marshaller) {
        JINNServiceAdapter jinnSigningServiceClient = new JINNServiceAdapter();
        jinnSigningServiceClient.setMarshaller(marshaller);
        jinnSigningServiceClient.setUnmarshaller(marshaller);

        ClientInterceptor[] clientInterceptors = {new JINNBaseInterceptor()};
        jinnSigningServiceClient.setInterceptors(clientInterceptors);

        return jinnSigningServiceClient;
    }

}
