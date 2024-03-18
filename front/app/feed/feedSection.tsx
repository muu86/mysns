'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import { getFeeds } from '../lib/actions';
import { Post } from '../lib/definitions';
import { ScrollTrigger } from './ScrollTrigger';
import { Feed } from './feed';

const picsum = 'https://picsum.photos/300/random=';
const lorem = `https://baconipsum.com/api/?type=all-meat&sentences=1&paras=30`;

export default function FeedSection() {
  const [coords, setCoords] = useState<GeolocationCoordinates | null>(null);
  const [posts, setPosts] = useState<Post[] | null>(null);
  const [offset, setOffset] = useState<number>(0);

  const sectionRef = useRef<HTMLElement | null>(null);

  useEffect(() => {
    (async () => {
      const geo = navigator.geolocation;
      geo.getCurrentPosition(async (pos) => {
        setCoords(pos.coords);
      });
    })();
  }, []);

  const fetchFeeds = useCallback(
    async (offset: number) => {
      if (!coords) return;
      const response = await getFeeds(
        coords?.latitude,
        coords?.longitude,
        offset
      );

      setPosts((posts) => {
        if (!posts) return [...response];
        return [...posts, ...response];
      });
      setOffset((prevOffset) => prevOffset + 10);
    },
    [coords]
  );

  useEffect(() => {
    if (!coords) return;
    fetchFeeds(0);
  }, [coords, fetchFeeds]);

  return (
    <section ref={sectionRef} className="w-full h-full">
      {posts && posts.map((post, i) => <Feed key={i} post={post} />)}
      {posts && <ScrollTrigger fetchNext={() => fetchFeeds(offset)} />}
    </section>
  );
}
