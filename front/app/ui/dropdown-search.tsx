'use client';

import { LegalAddress } from '@/app/types/definitions';
import { ChevronRightIcon } from '@heroicons/react/24/outline';
import clsx from 'clsx';
import {
  ChangeEvent,
  Dispatch,
  FocusEvent,
  MouseEvent,
  RefObject,
  SetStateAction,
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import { SelectedAddress } from '../start/start-form';

type AddressDepth = 'sido' | 'gungu' | 'eupmyundong' | 'li';

export default function DropdownSearch({
  address,
  depth,
  selectedAddress,
  setSelectedAddress,
}: {
  address: LegalAddress[];
  depth: AddressDepth;
  selectedAddress: SelectedAddress;
  setSelectedAddress: Dispatch<SetStateAction<SelectedAddress>>;
}) {
  const [currentAddress, setCurrentAddress] = useState<string>('');
  const [filtered, setFiltered] = useState<string[]>([]);
  const inputRef = useRef<HTMLInputElement | null>(null);
  const [visible, setVisible] = useState<boolean>(false);

  useEffect(() => {
    const { sido, gungu, eupmyundong, li } = selectedAddress;
    switch (depth) {
      case 'li': {
        setVisible(!!sido && !!gungu && !!eupmyundong);
        break;
      }
      case 'eupmyundong': {
        setVisible(!!sido && !!gungu);
        break;
      }
      case 'gungu': {
        setVisible(!!sido);
        break;
      }
      default:
        setVisible(true);
    }
  }, [selectedAddress, depth]);

  function handleChange(event: ChangeEvent<HTMLInputElement>): void {
    console.log('change event!');
    event.preventDefault();
    const val = event.target.value;
    setCurrentAddress(val);
    const filtered = filterAddress(address, depth, val, selectedAddress);
    setFiltered(filtered);

    handleSelectAddress(val);
  }

  const handleSelectAddress = useCallback(
    (val: string) => {
      switch (depth) {
        case 'sido': {
          if (address.filter((d) => d.sido === val).length > 0) {
            setSelectedAddress({
              ...selectedAddress,
              sido: val,
              gungu: '',
              eupmyundong: '',
            });
            setFiltered([]);
          }
          break;
        }
        case 'gungu': {
          if (
            address.filter(
              (d) => d.sido === selectedAddress.sido && d.gungu === val
            ).length > 0
          ) {
            setSelectedAddress({
              ...selectedAddress,
              gungu: val,
              eupmyundong: '',
            });
            setFiltered([]);
          }
          break;
        }
        default: {
          if (
            address.filter(
              (d) =>
                d.sido === selectedAddress.sido &&
                d.gungu === selectedAddress.gungu &&
                d.eupmyundong === val
            ).length > 0
          ) {
            setSelectedAddress({
              ...selectedAddress,
              eupmyundong: val,
            });
            setFiltered([]);
          }
        }
      }
    },
    [address, depth, selectedAddress, setSelectedAddress]
  );

  function handleFocus(event: FocusEvent<HTMLInputElement, Element>): void {
    switch (depth) {
      case 'sido': {
        setSelectedAddress({
          sido: '',
          gungu: '',
          eupmyundong: '',
          li: '',
        });
        break;
      }
      case 'gungu': {
        setSelectedAddress({
          ...selectedAddress,
          gungu: '',
          eupmyundong: '',
          li: '',
        });
        break;
      }
      case 'eupmyundong': {
        setSelectedAddress({
          ...selectedAddress,
          eupmyundong: '',
          li: '',
        });
        break;
      }
      case 'li': {
        setSelectedAddress({
          ...selectedAddress,
          li: '',
        });
        break;
      }
    }
  }

  function handleClick(event: MouseEvent<HTMLInputElement>): void {
    event.preventDefault();
    const filtered = filterAddress(
      address,
      depth,
      event.currentTarget.innerHTML,
      selectedAddress
    );
    setFiltered(filtered);
  }

  return (
    <div
      className={clsx('w-full h-full', {
        'opacity-0': !visible,
      })}
    >
      <label
        htmlFor={depth}
        className="mb-2 text-sm font-medium text-gray-900 sr-only"
      >
        Search
      </label>
      <div className="relative group">
        <div className="absolute end-2 w-10 h-full flex justify-center items-center">
          <ChevronRightIcon className="w-8 h-8 group-focus-within:rotate-90 transition-all text-neutral-300" />
        </div>
        <input
          ref={inputRef}
          onClick={handleClick}
          onChange={handleChange}
          onFocus={handleFocus}
          autoComplete="off"
          type="text"
          id={depth}
          value={currentAddress}
          className="block w-full border-0 ring-1 ring-inset ring-neutral-500 rounded-md p-4 ps-10 text-sm text-gray-900 outline-none"
          placeholder={
            depth === 'sido' ? '시/도' : depth === 'gungu' ? '군/구' : '동'
          }
          required
        />
        <div className="absolute w-full bg-white z-30 rounded-md border-0 ring-1 ring-inset ring-neutral-500 opacity-0 group-focus-within:opacity-100 transition-all">
          <div className="flex flex-col max-h-40 overflow-y-scroll">
            {filtered.map((f, i) => (
              <InnerList
                key={i}
                data={f}
                inputRef={inputRef}
                setCurrentAddress={setCurrentAddress}
                setFiltered={setFiltered}
                handleSelectAddress={handleSelectAddress}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

function InnerList({
  data,
  inputRef,
  setCurrentAddress,
  setFiltered,
  handleSelectAddress,
}: {
  data: string;
  inputRef: RefObject<HTMLInputElement>;
  setCurrentAddress: Dispatch<SetStateAction<string>>;
  setFiltered: Dispatch<SetStateAction<string[]>>;
  handleSelectAddress: (val: string) => void;
}) {
  function handleClick(event: MouseEvent<HTMLDivElement>): void {
    event.preventDefault();
    if (!inputRef.current) return;

    const val = event.currentTarget.innerHTML;
    setCurrentAddress(val);
    setFiltered([]);
    handleSelectAddress(val);
  }

  return (
    <div className="w-full hover:bg-neutral-300 rounded-md p-1">
      <div className="ps-10 py-2 text-xs font-extralight" onClick={handleClick}>
        {data}
      </div>
    </div>
  );
}

function filterAddress(
  address: LegalAddress[],
  depth: AddressDepth,
  val: string,
  selectedAddress: SelectedAddress
) {
  let preFiltered;
  switch (depth) {
    case 'gungu': {
      preFiltered = address.filter((d) => d.sido === selectedAddress.sido);
      break;
    }
    case 'eupmyundong': {
      preFiltered = address.filter(
        (d) =>
          d.sido === selectedAddress.sido && d.gungu === selectedAddress.gungu
      );
      break;
    }
    default: {
      preFiltered = address;
    }
  }

  const arr: string[] = preFiltered
    .filter((d) => d[depth].startsWith(val))
    .map((d) => d[depth]);
  const set = new Set(arr);
  const result: string[] = Array.from(set);

  return result;
}
