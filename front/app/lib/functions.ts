export function calculateDate(date: Date): string {
  const diff = Date.now() - date.getTime();

  const min = Math.floor(diff / (1000 * 60));
  const hour = Math.floor(diff / (1000 * 60 * 60));
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));

  if (min < 60) {
    return min + '분';
  }
  if (hour < 24) {
    return hour + '시간';
  }
  if (days < 7) {
    return days + '일';
  }
  if (days < 30) {
    return Math.floor(days / 7) + '주';
  }
  if (days < 365) {
    return Math.floor(days / 30) + '달';
  }
  return Math.floor(days / 365) + '년';
}
