import { Asset } from "./asset";
import {Policy} from "../../mgmt-api-client";

export interface ContractOffer {
    id: string;
    policy: Policy;
    provider: string;
    consumer: string;
    asset: Asset;
}
