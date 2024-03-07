import type { Metadata } from 'next';
import { inter, nanumPenScript } from '@/app/ui/fonts';
import { nanumGothic } from '@/app/ui/fonts';
import './globals.css';
import { AuthProvider } from './lib/AuthContext';

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`${nanumGothic.className}`}>
        <main className="flex min-h-screen flex-col items-center justify-between p-24">
          {children}
        </main>
      </body>
    </html>
  );
}
