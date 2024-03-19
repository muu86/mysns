'use client';

import { ChangeEvent, ChangeEventHandler, useRef, useState } from 'react';

export default function UserNickname() {
  const [nickname, setNickname] = useState<string>('');
  const nicknameRef = useRef<HTMLInputElement | null>(null);

  const handleNicknameChange: ChangeEventHandler<HTMLInputElement> = (
    event
  ) => {
    console.log(event.target.value);
  };

  return (
    <main className="w-full h-full flex flex-col items-center">
      <div className="sm:w-72">
        <div className="text-center">
          <h1>사용하실 닉네임을 설정해주세요</h1>
        </div>
        <form>
          <input
            onChange={handleNicknameChange}
            ref={nicknameRef}
            type="text"
            name="nickname"
            id="nickname"
            className="block w-full rounded-md border-0 py-1.5 pl-7 pr-20 text-gray-900 ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-neutral-600-600 sm:text-sm sm:leading-6"
          />
          <button>확인</button>
        </form>
      </div>
    </main>
  );
}
