'use client';

import { useState } from 'react';
import { createPost } from '../lib/actions';

export default function Test() {
  const [image, setImage] = useState();

  const onChange = (e) => {
    setImage(e.target.files[0]);
  };

  // const post = async () => {
  //   const fd = new FormData();
  //   fd.append('image', image);
  //   console.log('성공?');
  //   createPost(fd);
  // };

  const loc = getLocation();
  console.log(loc);

  return (
    <form action={createPost}>
      <input
        type="file"
        accept="image/**"
        name="image"
        multiple
        onChange={onChange}
      />
      <button type="submit">제출</button>
    </form>
  );
}

const getLocation = () => {
  const nav = navigator.geolocation;
  nav.getCurrentPosition((pos) => {
    console.log({ lat: pos.coords.latitude, lon: pos.coords.longitude });
  });
};
