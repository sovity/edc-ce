import { Asset } from "./asset";
import {Policy} from "../../edc-dmgmt-client";

export interface ContractOffer {
    id: string;
    policy: Policy;
    provider: string;
    consumer: string;
    asset: Asset;
}
