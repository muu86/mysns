'use client';

// import { cookies } from 'next/headers';
import { useState } from 'react';

export default function TestButton() {
  const [data, setData] = useState(null);

  // console.log(data);
  const headers = new Headers();
  headers.append('Cookies', document.cookie);
  return (
    <button
      onClick={() => {
        fetch('http://localhost:8080/user', {
          method: 'GET',
          headers,
        })
          .then((res) => res.json)
          .then((res) => setData(res));
      }}
    >
      {data ? data : '버튼'}
    </button>
    // <a href="http://localhost:8080/user">
    //   <p>고고</p>
    // </a>
  );
}
