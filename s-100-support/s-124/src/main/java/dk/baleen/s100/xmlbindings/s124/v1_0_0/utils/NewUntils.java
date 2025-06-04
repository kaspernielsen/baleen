/*
 * Copyright (c) 2024 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.baleen.s100.xmlbindings.s124.v1_0_0.utils;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import dk.dma.baleen.s100.xmlbindings.s124.v2_0_0.Dataset;
import dk.dma.baleen.s100.xmlbindings.s124.v2_0_0.impl.DatasetImpl;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBIntrospector;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.util.JAXBSource;

/**
 *
 */
public class NewUntils {

    /** The Schema. */
    public static final Schema SCHEMA;

    /** The Schema. */
    private static final String SCHEMA_LOCATION = "/xsd/S-124.xsd";

    static {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            URL schemaResource = S124GmlValidator.class.getResource(SCHEMA_LOCATION);
            SCHEMA = schemaFactory.newSchema(schemaResource);
        } catch (SAXException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Validator createAndConfigureValidator(List<SchemaValidationError> validationErrors) {
        Validator validator = SCHEMA.newValidator();

        validator.setErrorHandler(new ErrorHandler() {
            @Override
            public void error(SAXParseException e) {
                validationErrors.add(new SchemaValidationError("ERROR", e.getMessage(), e.getLineNumber(), e.getColumnNumber()));
            }

            @Override
            public void fatalError(SAXParseException e) {
                validationErrors.add(new SchemaValidationError("FATAL", e.getMessage(), e.getLineNumber(), e.getColumnNumber()));
            }

            @Override
            public void warning(SAXParseException e) {
                validationErrors.add(new SchemaValidationError("WARNING", e.getMessage(), e.getLineNumber(), e.getColumnNumber()));
            }
        });
        return validator;
    }

    public void printXml(JAXBElement<?> jaxbElement, OutputStream out) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(jaxbElement.getValue().getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(jaxbElement, out);
    }

    public List<SchemaValidationError> validateAgainstSchema(JAXBElement<?> jaxbElement) throws JAXBException {
        requireNonNull(jaxbElement);

        JAXBContext jaxbContext = JAXBContext.newInstance(jaxbElement.getValue().getClass());
        JAXBSource source = new JAXBSource(jaxbContext, jaxbElement);

        List<SchemaValidationError> validationErrors = new ArrayList<>();

        try {
            Validator validator = createAndConfigureValidator(validationErrors);
            validator.validate(source);
        } catch (SAXException e) {
            validationErrors.add(new SchemaValidationError("UNKNOWN", e.getMessage(), null, null));
        } catch (IOException e) {
            validationErrors.add(new SchemaValidationError("IO", e.getMessage(), null, null));
        }

        return validationErrors;
    }

    /**
     * Converts a XML string input into a Dataset object.
     *
     * @param xml
     *            the XML representation of a dataset
     * @return The unmarshalled Dataset object
     * @throws JAXBException
     *             if an error was encountered while unmarshalling the XML
     */
    public static Dataset toDataset(String xml) throws JAXBException {
        // Manipulate the class loader for the JAXBContext
        Thread thread = Thread.currentThread();
        ClassLoader originalClassLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(DatasetImpl.class.getClassLoader());

        try {
            // Create a jaxb context and unmarshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(DatasetImpl.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Create an inputstream from the XML and unmarshal it
            ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
            return (Dataset) JAXBIntrospector.getValue(unmarshaller.unmarshal(is));
        } finally {
            // Replace the original context loader
            thread.setContextClassLoader(originalClassLoader);
        }
    }

    public record SchemaValidationError(String type, String message, Integer lineNumber, Integer columnNumber) {}
}
