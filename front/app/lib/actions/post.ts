'use server';

import { revalidatePath } from 'next/cache';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

const SERVER_URL = 'http://localhost:8080';

export async function createPost(formData: FormData) {
  console.log(formData);
  const cookieStore = cookies();

  const response = await fetch(`${SERVER_URL}/posts`, {
    headers: {
      Authorization: `Bearer ${cookieStore.get('tkn')?.value}`,
    },
    method: 'POST',
    body: formData,
  })
    .then((res) => {
      if (res.status === 401 || res.status === 403) {
        throw new Error('인증에 실패했습니다');
      }

      if (!res.ok) throw new Error(`${res.status} status! 확인필요!`);

      return res.json();
    })
    .catch((err) => ({ err: true, message: err.message }));

  console.log(response);

  revalidatePath('/post');
  redirect('/post');
}
