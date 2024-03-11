'use server';

import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

const SERVER_URL = 'http://localhost:8080';

export async function getUser() {
  const cookieStore = cookies();

  // if (!cookieStore.get('tkn')) {
  //   redirect('/login');
  // }

  return await fetch(`${SERVER_URL}/user`, {
    headers: {
      Authorization: `Bearer ${cookieStore.get('tkn')?.value}`,
    },
  })
    .then((res) => {
      if (`${res.status}`.startsWith('4'))
        throw new Error('인증에 실패했습니다');

      if (!res.ok) throw new Error(`${res.status} status! 확인필요!`);

      return res.json();
    })
    .catch((err) => ({ err: true, message: err.message }));
  // .catch((err) => redirect('/login'));
}

export async function createPostDto(id: FormData, formData: FormData) {
  console.log('server action called!');

  console.log(id);
  console.log(formData);

  // redirect('/');
}
