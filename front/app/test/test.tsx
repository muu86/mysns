'use client';

import { useState } from 'react';
import { createPostDto } from '../lib/actions';

export default function Test() {
  const [image, setImage] = useState();

  const onChange = (e) => {
    setImage(e.target.files[0]);
  };

  const post = async () => {
    const fd = new FormData();
    fd.append('image', image);
    console.log('성공?');
    createPostDto(fd);
  };

  return (
    <form action={createPostDto}>
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
