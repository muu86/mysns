export async function getLegalAddresses() {
  const response = await fetch(`${process.env.SERVER_BASE_URL}/loc/legal`, {
    cache: 'force-cache',
  });
  const data = await response.json();
  return data;
}
