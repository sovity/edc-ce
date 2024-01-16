/**
 * Trimmed string or null if blank
 */
export function trimmedOrNull(s?: string | null): string | null {
  const trimmed = s?.trim();
  return trimmed ? trimmed : null;
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
