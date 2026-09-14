# Generic Claims Policy (DAPS)

The generic claims policy restricts a data offer to consumers whose DAPS identity token contains a given claim with one of the allowed values.
It lets a dataspace introduce new access rules, for example by country, company group or usage purpose, without a connector release.
Only the DAPS (Keycloak) needs a new claim mapper for the participating connectors.

The policy is available in all sovity dataspaces (`sovity.dataspace.kind=sovity-daps`, `sovity-daps-omejdn` and `iam-mock`),
and the constraint type is offered in the UI for all of them.
It is only useful with a DAPS though, because only there an operator can add claims to a connector's identity token.
It can be used as access policy and as contract policy, and is evaluated for catalog requests, contract negotiations and transfer starts.

## How it works

Every DAPS access token that a consumer connector presents carries a set of claims.
The connector makes all claims of the token available to the policy engine.

A constraint with the left operand `POLICY_CLAIM_<NAME>` is evaluated against the token claim `<NAME>`:

| Part                   | Example                    | Notes                                                                                             |
|------------------------|----------------------------|---------------------------------------------------------------------------------------------------|
| Left operand           | `POLICY_CLAIM_COUNTRY`     | Prefix `POLICY_CLAIM_` plus the claim name. The claim name is matched case-sensitively.           |
| Operator               | `EQ` or `IN`               | `EQ` behaves like `IN`: the constraint is fulfilled if the token claim matches any of the values. |
| Right operand          | `DE, CH` or `["DE", "CH"]` | The allowed values. A single string is split at commas and every value is trimmed.                |
| Token claim (consumer) | `"COUNTRY": "DE"`          | Must be a single non-empty string claim in the consumer's access token.                           |

Evaluation rules:

- The constraint is fulfilled if the consumer's claim value is contained in the list of allowed values.
- A missing claim, an empty claim or a claim that is not a plain string (for example a JSON array or object) denies the constraint. The provider connector logs the denial on debug level together with the consumer's participant ID.
- Only the operators `EQ` and `IN` are supported. Any other operator denies the constraint and is logged on warning level.
- The claim value of the consumer is compared as one literal. A token claim `"COUNTRY": "DE,CH"` is the single value `DE,CH` and does not match the allowed values `DE` or `CH`. Comma separation is only supported on the policy side, not inside the token claim.

## Creating the policy in the UI

In the policy creator, select the constraint type **Consumer's Token Claim**.

- **Claim Name**: the claim name without the `POLICY_CLAIM_` prefix, for example `COUNTRY`.
- **Operator**: `EQ` or `IN`.
- **Allowed Values**: one or more values. Both operators accept multiple values.

The constraint type requires the UI feature `SOVITY_POLICIES`, which is enabled by default for sovity dataspaces.

## Adding a new claim to the dataspace

The claim values are not stored in the connector. They are part of the consumer's identity in the DAPS.
To use a new claim, the DAPS operator (sovity) adds a claim mapper to the Keycloak client of every connector that should carry the claim.
The mapper's token claim name must equal the claim name used in the policy, for example `COUNTRY` for `POLICY_CLAIM_COUNTRY`, and the value must be a single string.

Contact your dataspace operator with the claim name and the value per connector.
No connector configuration or restart is needed on the provider or consumer side.
