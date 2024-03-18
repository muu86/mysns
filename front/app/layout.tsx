import { nanumGothic, notoSerifKorean } from '@/app/ui/fonts';
import './globals.css';
import Sidebar from './sidebar';

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`${notoSerifKorean.className}`}>
        <div className="w-full h-screen flex flex-col-reverse sm:flex-row">
          <Sidebar />
          {children}
        </div>
      </body>
    </html>
  );
}
