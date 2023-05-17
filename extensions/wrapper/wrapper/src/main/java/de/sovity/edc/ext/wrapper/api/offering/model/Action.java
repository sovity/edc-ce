package de.sovity.edc.ext.wrapper.api.offering.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Test")
public class Action {
    String type;
    String includedIn;
    AtomicConstraint atomicConstraint;
}
