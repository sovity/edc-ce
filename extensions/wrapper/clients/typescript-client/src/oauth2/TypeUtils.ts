export function isHeaders(object: any): object is Headers {
    return (
        'append' in object &&
        'delete' in object &&
        'get' in object &&
        'has' in object &&
        'set' in object &&
        'forEach' in object
    );
}
