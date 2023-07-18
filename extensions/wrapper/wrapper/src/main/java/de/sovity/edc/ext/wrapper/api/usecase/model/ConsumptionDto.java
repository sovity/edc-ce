package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class ConsumptionDto {
    final ConsumeInputDto input;
    String contractNegotiationId;
    String transferProcessId;
    List<String> errors = new ArrayList<>(); //TODO aggregate errors from CN & TP (if not duplicated with dto)
}
