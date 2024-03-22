import { User, Session, Profile } from 'next-auth';

declare module 'next-auth' {
  interface User {
    username?: string;
  }

  interface Session {
    accessToken: string;
    refreshToken: string;
    idToken: string;
    profile: Profile;
  }
}
