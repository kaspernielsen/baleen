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

package dk.dma.baleen.s124.service;

import org.springframework.context.annotation.Configuration;

@Configuration
public class H2GisConfig {

//    @PostConstruct
//    public void init() {
//        // Initialize H2GIS
//        try {
//            H2GISFunctions.load();
//        } catch (Exception e) {
//            throw new RuntimeException("Could not initialize H2GIS", e);
//        }
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        return new EmbeddedDatabaseBuilder()
//            .setType(EmbeddedDatabaseType.H2)
//            .addScript("classpath:org/h2gis/functions/spatial.sql")
//            .build();
//    }
}