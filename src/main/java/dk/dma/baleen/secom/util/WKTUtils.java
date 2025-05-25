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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * The WKTUtils class.
 * <p/>
 * A helper utility that manipulates the WKT geometry strings.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class WKTUtils {

    /**
     * Converts a WKT geometry into a JTS geometry
     *
     * @param geometryAsWKT The geometry in WKT format
     * @return a JTS geometry
     * @throws ParseException if the WKT geometry was invalid
     */
    public static Geometry convertWKTtoGeometry(String geometryAsWKT) throws ParseException {
        WKTReader wktReader = new WKTReader();
        Geometry geometry = wktReader.read(geometryAsWKT);
        return geometry;
    }

    /**
     * Converts a WKT geometry into GeoJson format, via JTS geometry
     *
     * @param geometryAsWKT The geometry in WKT format
     * @return JsonNode with the geometry expressed in GeoJson format
     * @throws ParseException if the WKT geometry was invalid
     */
    public static JsonNode convertWKTtoGeoJson(String geometryAsWKT) throws ParseException {
        return GeometryJSONConverter.convertFromGeometry(WKTUtils.convertWKTtoGeometry(geometryAsWKT));
    }

}
