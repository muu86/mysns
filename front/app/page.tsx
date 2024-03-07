import { cookies } from 'next/headers';
import Image from 'next/image';
import TestButton from './testbutton';

export default function Home() {
  const cookiesStore = cookies();

  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      <p>{cookiesStore.toString()}</p>
      <p>hi</p>
      <TestButton />
    </main>
  );
}
