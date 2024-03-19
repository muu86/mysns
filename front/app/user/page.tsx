import { getUser } from '@/app/lib/actions/user';
import Image from 'next/image';
import { auth } from '@/app/api/auth/[...nextauth]/auth';

export default async function User() {
  // const userData = await getUser();
  // console.log(userData);

  const session = await auth();
  console.log(session);

  return (
    <main className="w-full h-full">
      <div className="w-full h-full flex flex-col items-center gap-2 px-2">
        <div className="relative border-2 rounded-full border-neutral-600 w-28 h-28 overflow-hidden">
          <Image
            width={200}
            height={200}
            // fill
            src={`https://picsum.photos/200/200?random=${Math.random()}`}
            alt="user-profile"
            draggable={false}
          />
        </div>
        <div>
          <h1>닉네임</h1>
          <p>{session?.user?.name}</p>
        </div>
        <div>
          <h1>동네</h1>
        </div>
        <div>아기 개월</div>
      </div>
    </main>
  );
}
