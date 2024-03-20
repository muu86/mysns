'use server';

import { GetFeedsResponse } from '@/app/types/res-definition';
import { Post } from '@/app/types/definitions';
import { calculateDate } from '@/app/lib/functions';

const SERVER_URL = 'http://localhost:8080';

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
  const data: GetFeedsResponse[] = await response.json();
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
