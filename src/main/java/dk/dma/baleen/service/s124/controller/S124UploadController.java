/*
 * Copyright (c) 2008 Kasper Nielsen.
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
package dk.dma.baleen.service.s124.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.dma.baleen.service.s124.NiordApiCaller;
import dk.dma.baleen.service.s124.service.S124Service;

/**
 *
 */
@RestController
@RequestMapping("/api")
public class S124UploadController {

    String XML="""
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns4:Dataset xmlns:ns1="http://www.opengis.net/gml/3.2" xmlns:ns2="http://www.iho.int/s100gml/5.0" xmlns:ns3="http://www.w3.org/1999/xlink" xmlns:ns4="http://www.iho.int/S124/1.0" ns1:id="D">
    <ns1:boundedBy>
        <ns1:Envelope srsName="EPSG:4326">
            <ns1:lowerCorner>55.5513457 11.1255874</ns1:lowerCorner>
            <ns1:upperCorner>55.5513457 11.1255874</ns1:upperCorner>
        </ns1:Envelope>
    </ns1:boundedBy>
    <ns2:DatasetIdentificationInformation>
        <ns2:encodingSpecification>S100 Part 10b</ns2:encodingSpecification>
        <ns2:encodingSpecificationEdition>1.0</ns2:encodingSpecificationEdition>
        <ns2:productIdentifier>S-124</ns2:productIdentifier>
        <ns2:productEdition>1.0.0</ns2:productEdition>
        <ns2:datasetFileIdentifier>S-124_DMA_D</ns2:datasetFileIdentifier>
        <ns2:datasetTitle>Niord S-124 Dataset</ns2:datasetTitle>
        <ns2:datasetReferenceDate>20250122</ns2:datasetReferenceDate>
        <ns2:datasetLanguage>en</ns2:datasetLanguage>
        <ns2:datasetAbstract>Autogenerated S-124 Dataset for b7428177-df71-4a3d-bc74-7243c044f58c</ns2:datasetAbstract>
    </ns2:DatasetIdentificationInformation>
    <ns4:members>
        <ns4:NAVWARNPreamble ns1:id="DK.NW-004-25">
            <ns4:messageSeriesIdentifier>
                <ns4:agencyResponsibleForProduction>Danish Maritime Authorities</ns4:agencyResponsibleForProduction>
                <ns4:countryName>DK</ns4:countryName>
                <ns4:nameOfSeries>dma-nw</ns4:nameOfSeries>
                <ns4:warningIdentifier>urn:mrn:iho:nw:dk:nw-004-25</ns4:warningIdentifier>
                <ns4:warningNumber>4</ns4:warningNumber>
                <ns4:warningType code="2">Coastal Navigational Warning</ns4:warningType>
                <ns4:year>2025</ns4:year>
            </ns4:messageSeriesIdentifier>
            <ns4:nAVWARNTitle>
                <ns4:language>en</ns4:language>
                <ns4:text>Denmark. The Great Belt. Light buoy missing.</ns4:text>
            </ns4:nAVWARNTitle>
            <ns4:intService>false</ns4:intService>
            <ns4:navwarnTypeGeneral code="AAA">BBB</ns4:navwarnTypeGeneral>
            <ns4:publicationTime>20250122T142952</ns4:publicationTime>
        </ns4:NAVWARNPreamble>
        <ns4:NAVWARNPart ns1:id="urn:mrn:iho:nw:dk:nw-004-25.0">
            <ns1:boundedBy>
                <ns1:Envelope srsName="EPSG:4326">
                    <ns1:lowerCorner>11.1255874 55.5513457</ns1:lowerCorner>
                    <ns1:upperCorner>11.1255874 55.5513457</ns1:upperCorner>
                </ns1:Envelope>
            </ns1:boundedBy>
            <ns4:fixedDateRange>
                <ns4:dateEnd>
                    <ns2:date>20250122</ns2:date>
                </ns4:dateEnd>
                <ns4:dateStart>
                    <ns2:date>20250122</ns2:date>
                </ns4:dateStart>
            </ns4:fixedDateRange>
            <ns4:warningInformation>
                <ns4:information>
                    <ns4:headline>Light buoy missing</ns4:headline>
                    <ns4:language>en</ns4:language>
                    <ns4:text>The light buoy test in pos. 55° 33.1'N - 011° 07.5'E is missing.</ns4:text>
                </ns4:information>
            </ns4:warningInformation>
            <ns4:header ns3:href="#DK.NW-004-25"/>
            <ns4:geometry>
                <ns2:pointProperty>
                    <ns2:Point>
                        <ns1:pos>55.5513457 11.1255874</ns1:pos>
                    </ns2:Point>
                </ns2:pointProperty>
            </ns4:geometry>
        </ns4:NAVWARNPart>
    </ns4:members>
</ns4:Dataset>
            """;

    @Autowired
    S124Service service;

    @Autowired
    NiordApiCaller niordAPI;

    @PostMapping("/upload")
    public String accept(@RequestBody String xmlDataset) throws Exception {
        System.out.println("Recieved " + xmlDataset);
        service.upload(xmlDataset);
        niordAPI.fetchAll();
        return "ok";
    }

    @PostMapping("/uploaddummy")
    public String acceptDummy(String xmlDataset) throws Exception {
        System.out.println("Recieved " + xmlDataset);
        service.upload(XML);
        return "ok";
    }


    @GetMapping("/stuff")
    public String accept() throws Exception {
        return "ok";
    }
}
