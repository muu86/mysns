import { User, Session, Profile } from 'next-auth';

declare module 'next-auth' {
  interface User {
    isExists: boolean;
  }

  interface Session {
    isExists: boolean;
    accessToken: string;
    refreshToken: string;
    idToken: string;
    profile: Profile;
  }
}
