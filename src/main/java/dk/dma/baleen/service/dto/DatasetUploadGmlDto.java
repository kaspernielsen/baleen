package dk.dma.baleen.service.dto;

// Hmm, vi kunne have data produktet med.
// Saa kan vi ogsaa bruge den som output
public record DatasetUploadGmlDto(

        String dataProduct,

        String dataProductVersion,

        String gml) {}
