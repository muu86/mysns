'use client';

import { Session } from 'next-auth';
import { ChangeEventHandler, useRef, useState } from 'react';
import { createUser } from '@/app/lib/actions/user';
import { LegalAddress } from '@/app/types/definitions';
import DropdownSearch from '@/app/ui/dropdown-search';
import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';

export type SelectedAddress = {
  sido: string;
  gungu: string;
  eupmyundong: string;
  li: string;
};

type Error = {
  username?: string;
  babyAge?: string;
  address?: string;
};

export default function StartForm({
  address,
}: {
  session: Session;
  address: LegalAddress[];
}) {
  const [username, setUsername] = useState<string>('');
  const [babyAge, setBabyAge] = useState<number>(0);
  const [selectedAddress, setSelectedAddress] = useState<SelectedAddress>({
    sido: '',
    gungu: '',
    eupmyundong: '',
    li: '',
  });
  const [error, setError] = useState<Error>({});

  const usernameRef = useRef<HTMLInputElement | null>(null);
  const babyAgeRef = useRef<HTMLInputElement | null>(null);

  const { update } = useSession();
  const router = useRouter();

  const handleUsernameChange: ChangeEventHandler<HTMLInputElement> = (
    event
  ) => {
    setUsername(event.target.value);
  };

  const handleBabyAgeChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setBabyAge(parseInt(event.target.value));
  };

  // function handleSearchLocation(
  //   event: MouseEvent<HTMLButtonElement, MouseEvent>
  // ): void {
  //   const geo = navigator.geolocation;
  //   console.log(geo);
  //   geo.getCurrentPosition(
  //     (pos) => {
  //       console.log(pos);
  //     },
  //     (err) => {
  //       alert('위치 확인 동의가 필요합니다');
  //     }
  //   );
  // }

  const submitForm = async () => {
    const fd = new FormData();
    fd.append('username', username);
    fd.append('babyAge', babyAge + '');

    const { sido, gungu, eupmyundong, li } = selectedAddress;

    const found = address.find(
      (ad) =>
        ad.sido === sido &&
        ad.gungu === gungu &&
        ad.eupmyundong === eupmyundong &&
        ad.li === li
    );
    if (!found) {
      console.log('주소를 다시 선택해주세요.');
    }
    const selectedCode = found!.code;
    fd.append('legalAddressCode', selectedCode);

    const result = await createUser(fd);
    if (result?.error) {
      setError(result.body);
    }
    if (result?.message === 'success') {
      console.log('this is before call update');
      await update({
        username: username,
      });
      router.push('/');
    }
  };

  return (
    <form action={submitForm} className="flex flex-col gap-4">
      <div className="flex flex-col gap-2">
        <label htmlFor="username">
          <span className="pl-2">닉네임</span>
        </label>
        <input
          onChange={handleUsernameChange}
          ref={usernameRef}
          type="text"
          name="username"
          id="username"
          className="block w-full rounded-md border-0 py-1.5 pl-7 pr-20 ring-1 ring-inset ring-neutral-300 placeholder:text-neutral-400 sm:text-sm sm:leading-6 outline-none"
        />
        {error?.username && <p className="text-red-500">{error?.username}</p>}
      </div>

      <div className="flex flex-col gap-2">
        <label htmlFor="baby-age">
          <span className="pl-2">아이는</span>
        </label>
        <div className="relative">
          <input
            onChange={handleBabyAgeChange}
            ref={babyAgeRef}
            type="number"
            name="baby-age"
            id="baby-age"
            className="block w-full rounded-md border-0 py-1.5 pl-7 pr-7 ring-1 ring-inset ring-gray-300 placeholder:text-neutral-400 sm:text-sm sm:leading-6 outline-none"
            placeholder="0"
          />
          <div className="absolute top-0 right-20 h-full flex items-center justify-center">
            <p className="text-neutral-400">개월이에요</p>
          </div>
        </div>
        {error?.babyAge && <p className="text-red-500">{error?.babyAge}</p>}
      </div>

      <div className="relative w-full flex flex-col gap-2">
        <div className="flex flex-row items-center justify-center">
          <span className="pl-2">동네를 설정해주세요</span>
          <div className="flex-grow"></div>
          <button
            type="button"
            className="border border-neutral-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-full text-sm px-5 py-2.5"
          >
            위치로 확인
          </button>
        </div>
        <DropdownSearch
          address={address}
          depth={'sido'}
          selectedAddress={selectedAddress}
          setSelectedAddress={setSelectedAddress}
        />
        <DropdownSearch
          address={address}
          depth={'gungu'}
          selectedAddress={selectedAddress}
          setSelectedAddress={setSelectedAddress}
        />
        <DropdownSearch
          address={address}
          depth={'eupmyundong'}
          selectedAddress={selectedAddress}
          setSelectedAddress={setSelectedAddress}
        />
        {error?.address && <p className="text-red-500">{error?.address}</p>}
      </div>

      <button
        type="submit"
        className="text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-full text-sm px-5 py-2.5 me-2 mb-2 dark:bg-gray-800 dark:text-white dark:border-gray-600 dark:hover:bg-gray-700 dark:hover:border-gray-600 dark:focus:ring-gray-700"
      >
        확인
      </button>
    </form>
  );
}
