import { nanumGothic } from '@/app/ui/fonts';
import './globals.css';

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`${nanumGothic.className}`}>{children}</body>
    </html>
  );
}
