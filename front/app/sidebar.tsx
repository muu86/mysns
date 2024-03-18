import {
  ChatBubbleLeftIcon,
  MegaphoneIcon,
  PhotoIcon,
  UserIcon,
} from '@heroicons/react/24/outline';

const menus = [
  {
    name: '갤러리',
    row: 'row-start-2',
    icon: PhotoIcon,
  },
  {
    name: '육아친구',
    row: 'row-start-3',
    icon: ChatBubbleLeftIcon,
  },
  {
    name: '프로필',
    row: 'row-start-4',
    icon: UserIcon,
  },
  {
    name: '메세지',
    row: 'row-start-5',
    icon: MegaphoneIcon,
  },
];

export default function Sidebar() {
  return (
    <nav className="z-20 shrink-0 w-full sm:w-20 h-20 sm:min-h-full">
      <div className="min-h-full flex flex-row justify-evenly items-center sm:grid sm:grid-cols-1 sm:grid-rows-12 sm:place-items-center">
        {menus.map((menu, i) => (
          <Button key={i} row={menu.row} name={menu.name} Icon={menu.icon} />
        ))}
      </div>
      {/* </div> */}
    </nav>
  );
}

const gridRowStartOffset = 1;

function Button({
  name,
  row,
  Icon,
}: {
  name: string;
  row: string;
  Icon: React.ForwardRefExoticComponent<
    Omit<React.SVGProps<SVGSVGElement>, 'ref'> & {
      title?: string | undefined;
      titleId?: string | undefined;
    } & React.RefAttributes<SVGSVGElement>
  >;
}) {
  return (
    <div className={row}>
      <div className="w-full h-full flex flex-col justify-center items-center gap-1">
        <Icon className="w-9 text-neutral-600" />
        <div className="text-sm font-extralight hidden sm:block">
          <span>{name}</span>
        </div>
      </div>
    </div>
  );
}
