import { WifiIcon } from '@heroicons/react/24/outline';
import { auth } from './api/auth/[...nextauth]/auth';

export default async function Home() {
  const session = await auth();
  console.log(session);
  return (
    <main className="w-full h-full grow overflow-scroll">
      <div className="flex flex-col justify-center items-center">
        <div className="flex flex-row justify-center items-center my-20">
          <WifiIcon className="w-16 h-16" />
          <div className="mx-8">
            <h1 className="text-4xl">오지엄마</h1>
          </div>
        </div>
        <div>
          <p>
            아이 키우는 집이 흔치 않은 지역에 사는 엄마들을 위해 만들었습니다.
          </p>
          <p>위치 기반으로 가까운 지역에 사는 엄마들을 찾을 수 있어요.</p>
        </div>
      </div>
    </main>
  );
}
