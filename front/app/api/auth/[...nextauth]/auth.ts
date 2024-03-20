import { getUser } from '@/app/lib/actions/user';
import NextAuth from 'next-auth';
import type { NextAuthConfig, Profile } from 'next-auth';
import keycloak from 'next-auth/providers/keycloak';

export const {
  handlers: { GET, POST },
  auth,
} = NextAuth({
  providers: [
    keycloak({
      issuer: 'http://localhost:3333/realms/master',
    }),
  ],
  secret: process.env.AUTH_SECRET,
  pages: {
    signIn: '/signin',
  },
  session: {
    strategy: 'jwt',
  },
  callbacks: {
    async signIn({ user, account, profile }) {
      if (!profile) return false;

      const exisingUser = await getUser(profile!);
      user.isExists = false;

      if (exisingUser) {
        user.isExists = true;
      }

      return true;
    },
    async jwt({ token, user, account, profile, trigger, session }) {
      if (account) {
        token.provider = account?.provider;
        token.accessToken = account?.access_token;
        token.refreshToken = account?.refresh_token;
        token.idToken = account?.id_token;
        token.profile = profile;
      }
      if (user) {
        token.isExists = user.isExists;
      }
      if (trigger === 'update' && session.isExists === true) {
        token.isExists = true;
      }
      return token;
    },
    async session({ session, token, user }) {
      session.isExists = token.isExists as boolean;
      session.accessToken = token.accessToken as string;
      session.refreshToken = token.refreshToken as string;
      session.idToken = token.idToken as string;
      session.profile = token.profile as Profile;
      return session;
    },
  },
} satisfies NextAuthConfig);