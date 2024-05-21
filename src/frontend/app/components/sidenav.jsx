'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import BoeingLogo from "../icons/Boeing_full_logo.svg.png"
import { usePathname } from 'next/navigation';
import { SIDENAV_ITEMS } from '@/app/styles/constants';

/**
 *Side Navigation Bar with links, always on a page
 */
const SideNav = () => {
    // State to track the currently selected tab
  const [displaySelectedTab, setDisplaySelectedTab] = useState("Master Inventory");

  return (
    <div className="md:w-60 bg-slate-100 h-screen flex-1 fixed border-r-1 border-zinc-200 hidden md:flex top-0">
      <div className="flex flex-col space-y-6 w-full">
        <div
          className="flex flex-row space-x-3 items-center justify-center md:justify-start md:px-6 bg-indigo-300 border-zinc-200 h-16 w-full"
        >
            {/* Logo as extra link to homepage */}
          <Link href="/">
            <Image src={BoeingLogo} alt="Boeing Logo" priority={true} />
          </Link>
        </div>
        <div
          className="flex flex-row space-x-3 items-center justify-center md:justify-start md:px-6 border-b-2 border-zinc-200 h-12 w-full"
        >
          <span className="font-semibold text-xl hidden md:flex text-blue-800">{displaySelectedTab}</span>
        </div>

        <div className="flex flex-col space-y-2  md:px-6 ">
            {/* Map over the sidebar items for links and highlight current tab */}
          {SIDENAV_ITEMS.map((item, index) => {
            return <MenuItem key={index} item={item} setDisplaySelectedTab={setDisplaySelectedTab} />;
          })}
        </div>
      </div>
    </div>
  );
};

export default SideNav;

const MenuItem = ({ item, setDisplaySelectedTab }) => {
  const pathname = usePathname();
  const handleClick = () => {
    setDisplaySelectedTab(item.title);
  };

  return (
    <div className="">
      <Link
        href={item.path}
        onClick={handleClick}
        className={`flex flex-row space-x-4 items-center p-2 rounded-lg hover:bg-zinc-200 ${item.path === pathname ? 'bg-zinc-200' : ''
          }`}
      >
        {item.icon}
        <span className="font-medium text-md flex text-slate-600">{item.title}</span>
      </Link>
    </div>
  );
};
