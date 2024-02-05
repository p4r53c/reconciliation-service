package com.msr.rnip.reconciliation.configuration;

import com.msr.rnip.reconciliation.adapter.smev2.SMEV2ServiceAdapter;
import com.msr.rnip.reconciliation.adapter.interceptor.SMEV2BaseInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import java.util.HashMap;
import javax.xml.bind.Marshaller;

//@author p4r53c
@Configuration
public class SMEV2AdapterConfig {

    @Value("${smev2.service.endpoint}")
    private String smev2ServiceEndpoint;

    @Bean
    public Jaxb2Marshaller smev2Marshaller() {
        Jaxb2Marshaller smev2Marshaller = new Jaxb2Marshaller();
        smev2Marshaller.setContextPath(
                        "ru.roskazna.gisgmp._02000000.smevgisgmpservice:" +
                        "ru.gosuslugi.smev.rev120315:" +
                        "ru.roskazna.gisgmp.xsd._116.message:" +
                        "ru.roskazna.gisgmp.xsd._116.messagedata:" +
                        "ru.roskazna.gisgmp.xsd._116.pgu_datarequest:" +
                        "ru.roskazna.gisgmp.xsd._116.charge:" +
                        "org.w3._2004._08.xop.include:" +
                        "org.w3._2000._09.xmldsig_:"

        );

        smev2Marshaller.setMarshallerProperties(new HashMap<String, Object>() {{
            //put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            put(Marshaller.JAXB_ENCODING, "UTF-8");
            put(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        }});
        return smev2Marshaller;
    }

    @Bean
    public SMEV2ServiceAdapter smev2ServiceAdapter (Jaxb2Marshaller smev2Marshaller) {
        SMEV2ServiceAdapter smev2ServiceAdapter = new SMEV2ServiceAdapter();
        smev2ServiceAdapter.setDefaultUri(smev2ServiceEndpoint);
        smev2ServiceAdapter.setMarshaller(smev2Marshaller);
        smev2ServiceAdapter.setUnmarshaller(smev2Marshaller);

        ClientInterceptor[] clientInterceptors = {new SMEV2BaseInterceptor()};
        smev2ServiceAdapter.setInterceptors(clientInterceptors);

        return smev2ServiceAdapter;
    }

}
