'use client';

import React from 'react';
import BoeingLogo from "../icons/Boeing_full_logo.svg.png"
import Link from 'next/link';
import Image from 'next/image';

/**
 * Main header bar with pressable logo to lead to home page
 */
const Header = () => {

  return (
    <div
      className={`fixed top-0 z-30 w-full transition-all border-b border-gray-20 bg-indigo-300`}>
      <div className="flex h-[64px] items-center justify-between px-4">
        <div className="flex items-center space-x-4">
          <Link
            href="/"
            className="flex flex-row space-x-3 items-center justify-center md:hidden"
          >
            <Image src={BoeingLogo} alt="Boeing Logo" priority={true} width={200} height={200} />
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Header;
