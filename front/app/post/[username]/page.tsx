import { auth } from '@/app/api/auth/[...nextauth]/auth';

export default async function Page() {
  const session = await auth();
  return <div>{session?.user?.username}</div>;
}
