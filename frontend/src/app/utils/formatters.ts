const dateFormatter = new Intl.DateTimeFormat('es-PE', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
});

const currencyFormatter = new Intl.NumberFormat('es-PE', {
  style: 'currency',
  currency: 'PEN',
  maximumFractionDigits: 2,
});

const decimalFormatter = new Intl.NumberFormat('es-PE', {
  maximumFractionDigits: 1,
});

export function formatLocalDate(value: string | null | undefined): string {
  if (!value) return 'Sin fecha';

  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) return value;

  return dateFormatter.format(date);
}

export function formatSoles(value: number | null | undefined): string {
  return currencyFormatter.format(Number(value ?? 0));
}

export function formatPercent(value: number | null | undefined): string {
  return `${decimalFormatter.format(Number(value ?? 0))}%`;
}

export function getInitials(name: string | null | undefined): string {
  const initials = (name ?? '')
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase();

  return initials || 'E';
}
