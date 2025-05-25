package dk.dma.baleen.secom.controllers;

import dk.dma.baleen.secom.spi.AuthenticatedMcpNode;

public record SecomNode(String mrn) implements AuthenticatedMcpNode {

}