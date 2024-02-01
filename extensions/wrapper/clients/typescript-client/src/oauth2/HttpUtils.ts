export function needsAuthentication(httpStatus: number) {
    return httpStatus === 401 || httpStatus === 403;
}

export function createUrlEncodedParamsString(obj: Record<string, string>) {
    return Object.entries(obj)
        .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
        .join('&');
}
