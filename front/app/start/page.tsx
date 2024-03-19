import { auth } from '@/app/api/auth/[...nextauth]/auth';
import StartForm from './start-form';
import { redirect } from 'next/navigation';

export default async function Start() {
  const session = await auth();
  // console.log(session);

  if (session.isExists) {
    console.log('이미 가입된 user 를 redirect 합니다.');
    redirect('/');
  }

  return (
    <main className="w-full h-full flex flex-col items-center">
      <div className="sm:w-72">
        <div className="text-center"></div>
        <StartForm session={session} />
      </div>
    </main>
  );
}
