'use client';

import Link from 'next/link';

export default function Page() {
  // const login = async () => {
  //   // console.log('hi');
  //   fetch('http://localhost:8080/oauth2/authorization/keycloak')
  //     .then((res) => res.json())
  //     .then((res) => console.log(res));
  // };
  return (
    <div className="w-72 flex-col items-center justify-center">
      <div>
        <p>login</p>
        <Link href="http://localhost:8080/oauth2/authorization/keycloak">
          <p className="">키클록</p>
        </Link>
        {/* <button onClick={login}>키클록</button> */}
      </div>
    </div>
  );
}
