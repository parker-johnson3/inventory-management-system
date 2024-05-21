import React from 'react';

const StaffCard = ({ props, onDelete }) => {
    const { id, name, position, department, email, phone, address } = props;

    return(
        <div className='bg-white p-4 mb-4 shadow-lg rounded-md'>
            <div className='flex justify-between items-center'>
                <h3 className='text-lg font-semibold text-gray-900'>
                    {name}
                </h3>
                <button onClick={() => onDelete(id)} className='text-red-600 hover:text-red-700 focus:outline-none'>
                    <svg className='w-4 h-4' fill='none' stroke='currentColor' viewBox='0 0 24 24' xmlns='http://www.w3.org/2000/svg'>
                        <path strokeLinecap='round' strokeLinejoin='round' strokeWidth='2' d='M6 18L18 6M6 6l12 12'></path>
                    </svg>
                </button>
            </div>
            <p className='text-sm text-gray-500'>
                {position}, {department}
            </p>
            <p className='mt-2 text-sm text-gray-500'>
                {email}
            </p>
            <p className='text-sm text-gray-500'>
                {phone}
            </p>
            <p className='text-sm text-gray-500'>
                {address}
            </p>
        </div>
    )
}

export default StaffCard;