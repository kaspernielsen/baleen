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

package dk.dma.baleen.controller;

import dk.dma.baleen.service.s124.model.S124DatasetInstanceEntity;
import dk.dma.baleen.service.s124.repository.S124DatasetInstanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/s124-datasets")
public class S124DatasetController {

    private final S124DatasetInstanceRepository repository;

    public S124DatasetController(S124DatasetInstanceRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Page<S124DatasetDto>> getAllDatasets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<S124DatasetInstanceEntity> datasets = repository.findAll(pageRequest);
        Page<S124DatasetDto> dtoPage = datasets.map(S124DatasetDto::fromEntity);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<S124DatasetDto> getDataset(@PathVariable Long id) {
        Optional<S124DatasetInstanceEntity> dataset = repository.findById(id);
        return dataset.map(entity -> ResponseEntity.ok(S124DatasetDto.fromEntity(entity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getDatasetCount() {
        return ResponseEntity.ok(repository.count());
    }

    // DTO class for API responses
    public record S124DatasetDto(
            Long id,
            String mrn,
            String uuid,
            Instant createdAt,
            Instant validFrom,
            Instant validTo,
            String dataProductVersion,
            String geometryWkt,
            List<Long> referencedDatasetIds
    ) {
        public static S124DatasetDto fromEntity(S124DatasetInstanceEntity entity) {
            return new S124DatasetDto(
                    entity.getId(),
                    entity.getMrn(),
                    entity.getUuid() != null ? entity.getUuid().toString() : null,
                    entity.getCreatedAt(),
                    entity.getValidFrom(),
                    entity.getValidTo(),
                    entity.getDataProductVersion(),
                    entity.getGeometry() != null ? entity.getGeometry().toText() : null,
                    entity.getReferences().stream().map(S124DatasetInstanceEntity::getId).toList()
            );
        }
    }
}