export function needsAuthentication(httpStatus: number) {
    return httpStatus === 401 || httpStatus === 403;
}

export function createUrlEncodedParamsString(obj: any) {
    var formBody = [];
    for (var property in obj) {
        formBody.push(
            `${encodeURIComponent(property)}=${encodeURIComponent(
                obj[property],
            )}`,
        );
    }
    return formBody.join('&');
}
