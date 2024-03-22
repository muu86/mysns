'use server';

import { auth } from '@/app/api/auth/[...nextauth]/auth';
import { Profile } from 'next-auth';
import { redirect } from 'next/navigation';

const SERVER_URL = 'http://localhost:8080';

export async function getUser(profile: Profile) {
  const response = await fetch(
    `${SERVER_URL}/user/exists?issuer=${profile.iss}&subject=${profile.sub}`,
    {
      method: 'GET',
      cache: 'no-store',
    }
  );
  if (response.status === 404) {
    return null;
  }
  const user = await response.json();
  return user;
}

export async function createUser(formData: FormData) {
  const session = await auth();
  if (!session) return;

  formData.append('sub', session.profile.sub as string);
  formData.append('iss', session.profile.iss as string);
  formData.append('first', session.profile.given_name as string);
  formData.append('last', session.profile.family_name as string);
  formData.append('email', session.profile.email as string);
  formData.append('emailVerified', session.profile.email_verified ? '1' : '0');

  console.log(session);

  const response = await fetch(`${process.env.SERVER_BASE_URL}/user`, {
    method: 'POST',
    body: formData,
  });
  const result = await response.json();

  console.log(result);

  switch (response.status) {
    case 400: {
      return {
        error: true,
        body: result,
      };
    }
    case 500:
      redirect('/');
    case 409:
    default:
      return {
        message: 'success',
      };
  }
}
