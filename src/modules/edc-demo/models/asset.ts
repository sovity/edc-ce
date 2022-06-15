
export class Asset {
    private static readonly PROPERTY_ID:string = "asset:prop:id";
    private static readonly PROPERTY_NAME = "asset:prop:name";
    private static readonly PROPERTY_VERSION = "asset:prop:version";
    private static readonly PROPERTY_CONTENT_TYPE = "asset:prop:contenttype";
    private static readonly PROPERTY_POLICY_ID = "asset:prop:policy-id";
    private static readonly PROPERTY_ORIGINATOR = "asset:prop:originator";
    private static readonly PROPERTY_TYPE = "type";
    private static readonly KNOWN_PROPERTY_KEYS = [
        Asset.PROPERTY_ID,
        Asset.PROPERTY_NAME,
        Asset.PROPERTY_VERSION,
        Asset.PROPERTY_CONTENT_TYPE,
        Asset.PROPERTY_POLICY_ID,
        Asset.PROPERTY_TYPE,
        Asset.PROPERTY_ORIGINATOR
    ];

    constructor(public properties: { [key: string]: string; }) {
    }

    public get id() {
        return this.properties[Asset.PROPERTY_ID];
    }

    public set id(value: string) {
        this.properties[Asset.PROPERTY_ID] = value;
    }

    public get name() {
        return this.properties[Asset.PROPERTY_NAME];
    }

    public set name(value: string) {
        this.properties[Asset.PROPERTY_NAME] = value;
    }

    public get version() {
        return this.properties[Asset.PROPERTY_VERSION];
    }

    public set version(value: string) {
        this.properties[Asset.PROPERTY_VERSION] = value;
    }

    public get contentType() {
        return this.properties[Asset.PROPERTY_CONTENT_TYPE];
    }

    public set contentType(value: string) {
        this.properties[Asset.PROPERTY_CONTENT_TYPE] = value;
    }

    public get policyId() {
        return this.properties[Asset.PROPERTY_POLICY_ID];
    }

    public set policyId(value: string) {
        this.properties[Asset.PROPERTY_POLICY_ID] = value;
    }

    public get type() {
        return this.properties[Asset.PROPERTY_TYPE];
    }

    public set type(value: string) {
        this.properties[Asset.PROPERTY_TYPE] = value;
    }

    public get originator() {
        return this.properties[Asset.PROPERTY_ORIGINATOR];
    }

    public set originator(value: string) {
        this.properties[Asset.PROPERTY_ORIGINATOR] = value;
    }

    public get additionalPropertyKeys() {
        return Object.keys(this.properties)
            .filter(propertyKey => !Asset.KNOWN_PROPERTY_KEYS.includes(propertyKey));
    }

    public get isAsync() {
        return this.type !== 'http'
    }
}
