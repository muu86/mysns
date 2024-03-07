import { Inter, Nanum_Pen_Script } from 'next/font/google';
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
