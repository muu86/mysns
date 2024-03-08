import { cookies } from 'next/headers';

export default function Home() {
  const cookiesStore = cookies();

  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      <div className="w-72 px-3 rounded-md border-solid border-2 border-gray-400 flex-col items-center justify-center">
        <div className="py-3 text-center">
          <p className="font-bold">home</p>
        </div>
        <div className="py-3 text-center break-words">
          <p className="font-bold">현재 쿠키 정보</p>
          <p className="py-3">{cookiesStore.toString()}</p>
        </div>
      </div>
    </main>
  );
}
