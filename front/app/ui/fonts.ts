import { Inter, Nanum_Pen_Script, Noto_Serif_KR } from 'next/font/google';
import { Nanum_Gothic } from 'next/font/google';

export const inter = Inter({ subsets: ['latin'] });

export const nanumGothic = Nanum_Gothic({
  subsets: ['latin'],
  weight: ['400', '700', '800'],
  // preload: false,
});

export const nanumPenScript = Nanum_Pen_Script({
  subsets: ['latin'],
  weight: ['400'],
  // preload: false,
});

export const notoSerifKorean = Noto_Serif_KR({
  subsets: ['latin'],
  weight: ['200', '300', '400', '500', '700'],
});
