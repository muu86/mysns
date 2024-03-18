'use server';

import { revalidatePath } from 'next/cache';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import { GetFeedsServerResponse, Post } from './definitions';

const SERVER_URL = 'http://localhost:8080';

export async function getUser() {
  const cookieStore = cookies();

  return await fetch(`${SERVER_URL}/user`, {
    headers: {
      Authorization: `Bearer ${cookieStore.get('tkn')?.value}`,
    },
  })
    .then((res) => {
      if (res.status === 401 || res.status === 403)
        throw new Error('인증에 실패했습니다');

      if (!res.ok) throw new Error(`${res.status} status! 확인필요!`);

      return res.json();
    })
    .catch((err) => ({ err: true, message: err.message }));
}

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

export async function getFeeds(
  latitude: number,
  longitude: number,
  offset: number
) {
  const response = await fetch(
    `${SERVER_URL}/feed?latitude=${latitude}&longitude=${longitude}&offset=${offset}`,
    {
      method: 'GET',
      cache: 'force-cache',
    }
  );
  const data: GetFeedsServerResponse[] = await response.json();
  return data.map(
    (d) =>
      ({
        username: d.user.username,
        content: d.content,
        legalAddress: {
          code: d.legalAddress.code,
          sido: d.legalAddress.sido,
          gungu: d.legalAddress.gungu,
          eupmyundong: d.legalAddress.eupmyundong,
          li: d.legalAddress.li,
        },
        createdAt: calculateDate(new Date(d.createdAt)),
        modifiedAt: d.modifiedAt && calculateDate(new Date(d.modifiedAt)),
      } as Post)
  );
}

function calculateDate(date: Date): string {
  const diff = Date.now() - date.getTime();

  const min = Math.floor(diff / (1000 * 60));
  const hour = Math.floor(diff / (1000 * 60 * 60));
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));

  if (min < 60) {
    return min + '분';
  }
  if (hour < 24) {
    return hour + '시간';
  }
  if (days < 7) {
    return days + '일';
  }
  if (days < 30) {
    return Math.floor(days / 7) + '주';
  }
  if (days < 365) {
    return Math.floor(days / 30) + '달';
  }
  return Math.floor(days / 365) + '년';
}
