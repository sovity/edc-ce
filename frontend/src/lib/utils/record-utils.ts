// Transform flat keys to nested structure
export const unflattenRecord = (
  flatMessages: Record<string, string>,
  separator: string,
): Record<string, unknown> => {
  const nested: Record<string, unknown> = {};
  Object.entries(flatMessages).forEach(([key, value]) => {
    const parts = key.split(separator);
    const property = parts.pop()!;

    let current = nested;
    parts.forEach((part) => {
      if (!current.hasOwnProperty(part)) {
        current[part] = {};
      }
      current = current[part] as Record<string, unknown>;
    });

    current[property] = value;
  });
  return nested;
};
