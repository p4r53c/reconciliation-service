package com.msr.rnip.reconciliation.utils;

import org.dom4j.io.DocumentSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.util.JAXBSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

//@author p4r53c
@Component
public class XMLHelper {

    private final static Logger logger = LoggerFactory.getLogger(XMLHelper.class);

    @Autowired
    private Jaxb2Marshaller smev2Marshaller;


    /**
     * Метод конвертирующий {@link JAXBElement<T>} объект в {@link String}.
     *
     * @param obj Типизированный JAXB-объект
     * @param <T> Тип JAXB-объекта.
     * @return XML в виде неформатированной стоки.
     * @throws IOException В случае отказа маршалера выброс из {@link StreamResult} и {@link StringWriter}
     */
    public <T> String jaxbToXmlString(JAXBElement<T> obj) throws IOException {
        try {
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            smev2Marshaller.marshal(obj, result);
            String outputXml = stringWriter.toString();
            stringWriter.close();
            return outputXml;

        } catch ( IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T xmlStringToJaxb(final String xml) throws IOException, SAXException, ParserConfigurationException {
        try {
            DocumentBuilderFactory abstractFactory = DocumentBuilderFactory.newInstance();
            abstractFactory.setNamespaceAware(true);
            DocumentBuilder	documentBuilder	= abstractFactory.newDocumentBuilder();
            final Document doc = documentBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
            final Element element = doc.getDocumentElement();
            JAXBElement<T> object = (JAXBElement<T>) smev2Marshaller.unmarshal(new DOMSource(element));
            //logger.info("Object class: {}", (T) object.getClass());
            return object.getValue();
        }
        catch (IOException | SAXException | ParserConfigurationException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Метод формирующий SAOP-Envelope.
     *
     * @param message Сообщение, которое нужно обернуть.
     * @return {@link String} обернутое сообщение.
     */
    public String createSoapEnvelope (String message) {
        final String soapEnvelope =
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                        "<SOAP-ENV:Header /><SOAP-ENV:Body>%s</SOAP-ENV:Body></SOAP-ENV:Envelope>";
        return String.format(soapEnvelope, message);

    }
}
