export function trimOrEmpty(s: string | null | undefined): string {
  return s?.trim() ?? '';
}

export function everythingBefore(separator: string, s: string): string {
  const index = s.indexOf(separator);
  return index === -1 ? s : s.substring(0, index);
}

export function everythingAfter(separator: string, s: string): string {
  const index = s.indexOf(separator);
  return index === -1 ? '' : s.substring(index + separator.length);
}

export function capitalize(s: string) {
  return s.charAt(0).toUpperCase() + s.slice(1);
}
