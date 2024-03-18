'use client';

import { Post } from '@/app/lib/definitions';
import { notoSerifKorean } from '@/app/ui/fonts';
import clsx from 'clsx';
import Image from 'next/image';
import { useState } from 'react';

const picsum = 'https://picsum.photos/1200?random=';

export const Feed = ({ post }: { post: Post }) => {
  const { content, legalAddress, createdAt } = post;
  const [profileLoaded, setProfileLoaded] = useState(false);
  const [imageLoaded, setImageLoaded] = useState(false);

  return (
    <article className="w-full h-full py-2 px-1 md:w-[600px] mx-auto border-b-2 border-dashed grid grid-cols-12 grid-rows-12 border-neutral-200 rounded-md">
      <div className="row-start-1 row-span-1 col-span-full">
        <div className="w-full h-full flex flex-row items-center gap-2 px-2">
          <div className="relative border-2 border-neutral-600 rounded-full w-8 h-8 overflow-hidden object-cover">
            <Image
              width={100}
              height={100}
              src={`https://picsum.photos/400?random=${Math.random()}`}
              alt="user-profile"
              draggable={false}
            />
          </div>
          <p className="font-bold">{post.username}</p>
          <div className="flex-1"></div>
          <p
            className={clsx(
              'font-bold text-neutral-400',
              notoSerifKorean.className
            )}
          >
            {legalAddress.gungu} {legalAddress.eupmyundong}
          </p>
          <div className={clsx('text-neutral-300', notoSerifKorean.className)}>
            <p>{createdAt}</p>
          </div>
        </div>
      </div>
      <div className="row-start-2 row-span-6 col-span-full">
        <div className="relative w-full h-full overflow-hidden object-cover">
          <Image
            width={600}
            height={600}
            src={`${picsum}${Math.random()}`}
            alt="hi"
            draggable={false}
          />
        </div>
      </div>
      <div className="row-start-9 col-start-2 col-span-10">
        <p className="flex-1 pt-6 pb-2">{content}</p>
      </div>
    </article>
  );
};
