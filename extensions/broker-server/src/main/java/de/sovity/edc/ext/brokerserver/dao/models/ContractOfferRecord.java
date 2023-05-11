package de.sovity.edc.ext.brokerserver.dao.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ContractOfferRecord {
    String id;
}
