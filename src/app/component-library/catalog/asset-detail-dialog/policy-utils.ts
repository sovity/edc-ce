import {PolicyDto as BrokerPolicyDto} from '@sovity.de/broker-server-client';
import {PolicyDto} from '@sovity.de/edc-client';

/**
 * Maps our API Wrapper Policy to the Core EDC Policy JSON.
 *
 * {@link PolicyDto} is part of our API wrapper. We want to display the ODRL Policy JSON though, and our JSON dialog only
 * accepts objects, so we need to parse it.
 * @param policyDto our policy dto
 * @returns core edc policy
 */
export function getLegacyPolicy(policyDto: BrokerPolicyDto): any {
  return JSON.parse(policyDto.legacyPolicy ?? 'null');
}
