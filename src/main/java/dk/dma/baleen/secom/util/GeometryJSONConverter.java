/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.dma.baleen.secom.util;

import java.io.IOException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The type Geometry JSON converter.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class GeometryJSONConverter {

    /**
     * Convert from geometry to a JSON node.
     *
     * @param geometry the geometry
     * @return the json node
     */
    public static JsonNode convertFromGeometry(Geometry geometry) {
        if (geometry == null) {
            return null;
        }

        ObjectMapper om = new ObjectMapper();
        try {
            JsonNode node = om.readTree(new GeoJsonWriter().write(geometry));
            return node;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Convert from a JSON node to geometry.
     *
     * @param jsonNode the json node
     * @return the geometry
     */
    public static Geometry convertToGeometry(JsonNode jsonNode) {
        if (jsonNode == null  || jsonNode.toString() == "null" || jsonNode.asText() == "null") {
            return null;
        }

        try {
            return new GeoJsonReader().read(jsonNode.toString());
        } catch (ParseException e) {
            return null;
        }
    }

}
