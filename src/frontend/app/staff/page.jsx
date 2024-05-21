'use client'

import React, { useState, useEffect } from 'react';
import StaffCard from '@/app/components/staff-card';
import StaffModal from '@/app/components/staff-modal';

const Staff = () => {
  
  const [staff, setStaff] = useState([]);
  const [showModal, setShowModal] = useState(false);

  const mockStaffData = [
    {
      id: 1,
      name: "John Doe",
      position: "Warehouse Manager",
      department: "Management",
      email: "john.doe@example.com",
      phone: "+1234567890",
      address: "123 Main St, City, Country"
    },
    {
      id: 2,
      name: "Jane Smith",
      position: "Marketing Manager",
      department: "Marketing",
      email: "jane.smith@example.com",
      phone: "+0987654321",
      address: "456 Elm St, City, Country"
    },
  ];

  useEffect(() => {
    setStaff(mockStaffData);
  }, []);

  const handleDelete = (id) => {
    const updatedStaff = staff.filter((staffMember) => staffMember.id !== id);
    setStaff(updatedStaff);
  }

  const handleAddStaff = (newStaffMember) => {
    setStaff([...staff, newStaffMember]);
    setShowModal(false);
  }

  const handleOnModalClose = () => {
    setShowModal(false);
  }

  return (
    <>
      <header className='mt-6 mb-4'> 
        <div className='lg:flex lg:items-center lg:justify-between'>
          <div className='min-w-0 flex-1 ml-4'>
            <h2 className='text-2xl font-medium leading-7 text-gray-900'>
              Staff Members ({staff.length})
            </h2>
          </div>
          <div className='mt-5 flex lg:ml-4 lg:mt-0'>
            <div className='flex justify-center'>
              <button className='font-medium bg-indigo-300 hover:bg-indigo-400 rounded-md px-4 py-2'
                onClick={() => setShowModal(true)}>
                Add Staff
              </button>
            </div>
          </div>
        </div>
      </header>
      <StaffModal showModal={showModal} onClose={handleOnModalClose} onAdd={handleAddStaff} />
      <div>
        {
          staff.map(staffMember => {
            return <StaffCard 
              props={staffMember}
              onDelete={handleDelete}
              key={staffMember.id}
            />
          })
        }
      </div>
    </>
  )
}

export default Staff