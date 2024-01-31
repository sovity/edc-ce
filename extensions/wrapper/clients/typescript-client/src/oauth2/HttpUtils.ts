export function needsAuthentication(httpStatus: number) {
    return httpStatus === 401 || httpStatus === 403;
}
