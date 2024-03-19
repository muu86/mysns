'use client';

import { useSession } from 'next-auth/react';
import { ChangeEventHandler, useRef, useState } from 'react';
import { createUser } from '../lib/actions/user';

export default function StartForm({ session }) {
  console.log(session);
  const [username, setUsername] = useState<string>('');
  const [babyAge, setBabyAge] = useState<number>(0);
  const [sido, setSido] = useState();
  const [gungu, setGungu] = useState();
  const [dong, setDong] = useState();
  const usernameRef = useRef<HTMLInputElement | null>(null);
  const babyAgeRef = useRef<HTMLInputElement | null>(null);
  const addressRef = useRef<HTMLInputElement | null>(null);

  const handleUsernameChange: ChangeEventHandler<HTMLInputElement> = (
    event
  ) => {
    setUsername(event.target.value);
  };

  const handleBabyAgeChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setBabyAge(event.target.value);
  };

  const handleAddressChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setBabyAge(event.target.value);
  };

  return (
    <form action={createUser}>
      <label>
        닉네임을 정해주세요
        <input
          onChange={handleUsernameChange}
          ref={usernameRef}
          type="text"
          name="username"
          id="username"
          className="block w-full rounded-md border-0 py-1.5 pl-7 pr-20 text-gray-900 ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-neutral-600-600 sm:text-sm sm:leading-6"
        />
      </label>

      <label>
        아이는 몇 개월인가요
        <input
          onChange={handleBabyAgeChange}
          ref={babyAgeRef}
          type="number"
          name="baby-age"
          id="baby-age"
          className="block w-full rounded-md border-0 py-1.5 pl-7 pr-7 text-gray-900 ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-neutral-600-600 sm:text-sm sm:leading-6"
          placeholder="0"
        />
      </label>

      <label>
        동네를 설정해주세요
        <select
          onChange={handleAddressChange}
          ref={usernameRef}
          type="text"
          name="username"
          id="username"
          className="block w-full rounded-md border-0 py-1.5 pl-7 pr-20 text-gray-900 ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-neutral-600-600 sm:text-sm sm:leading-6"
        />
      </label>
      <button type="submit">확인</button>
    </form>
  );
}
