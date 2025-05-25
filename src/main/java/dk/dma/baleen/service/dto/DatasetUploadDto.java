package dk.dma.baleen.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DatasetUploadDto(@JsonProperty("gmlDatasets") List<DatasetUploadGmlDto> gmlDatasets) {


}