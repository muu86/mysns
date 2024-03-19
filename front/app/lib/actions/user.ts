'use server';

import { Profile } from 'next-auth';
import { cookies } from 'next/headers';

const SERVER_URL = 'http://localhost:8080';

export async function checkNicknameDuplicated(nickname: string) {
  const response = await fetch(
    `${SERVER_URL}/user/check-nickname?nickname=${nickname}`,
    {
      method: 'GET',
      cache: 'force-cache',
    }
  );
  const data = await response.json();
  return null;
}

export async function getUser(profile: Profile) {
  const response = await fetch(
    `${SERVER_URL}/user?issuer=${profile.iss}&subject=${profile.sub}`,
    {
      method: 'GET',
      cache: 'force-cache',
    }
  );
  if (response.status === 404) {
    return null;
  }
  const user = await response.json();
  return user;
}

export async function createUser(formDate: FormData) {
  if (!profile) return;

  const createUser = {
    sub: profile.sub,
    iss: profile.iss,
    first: profile.given_name,
    last: profile.family_name,
    email: profile.email,
    emailVerified: profile.email_verified,
  };

  const response = await fetch(`${SERVER_URL}/user`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(createUser),
  });

  if (response.status === 400) {
    return {
      err: true,
      errMessage: response.json,
    };
  }
}

// export async function getUser() {
//   const cookieStore = cookies();

//   return await fetch(`${SERVER_URL}/user`, {
//     headers: {
//       Authorization: `Bearer ${cookieStore.get('tkn')?.value}`,
//     },
//   })
//     .then((res) => {
//       if (res.status === 401 || res.status === 403)
//         throw new Error('인증에 실패했습니다');

//       if (!res.ok) throw new Error(`${res.status} status! 확인필요!`);

//       return res.json();
//     })
//     .catch((err) => ({ err: true, message: err.message }));
// }
