import React from 'react';

const StaffModal = (props) => {
    if(!props.showModal) return null;

    return (
        <div className='fixed inset-0 flex backdrop-blur-sm justify-center items-center'>
            <div className='bg-indigo-200 p-4 w-full max-w-md max-h-full shadow-2xl rounded-lg'>
                <div className="flex items-center justify-between pb-4 dark:border-gray-600">
                    <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
                        Add New Staff
                    </h3>
                    <button type="button" onClick={props.onClose} className="text-black bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white" data-modal-toggle="crud-modal">
                        <svg className="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                            <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
                        </svg>
                    </button>
                </div>
                <form >
                    <div className="mb-4">
                        <label htmlFor="name" className="block text-gray-700">Name:</label>
                        <input type="text" id="name" placeholder="Name" name="name" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="position" className="block text-gray-700">Position:</label>
                        <input type="text" id="position" placeholder="Position" name="position" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="department" className="block text-gray-700">Department:</label>
                        <input type="text" id="department" placeholder="Department" name="department" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="email" className="block text-gray-700">Email:</label>
                        <input type="email" id="email" placeholder="Email" name="email" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="phone" className="block text-gray-700">Phone:</label>
                        <input type="tel" id="phone" placeholder="Phone" name="phone" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="address" className="block text-gray-700">Address:</label>
                        <input type="text" id="address" placeholder="Address" name="address" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <button type="submit" className="text-white inline-flex items-center bg-green-700 hover:bg-green-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center">
                        <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                        Add New Staff
                    </button>
                </form>
            </div>
        </div>
    );
}

export default StaffModal;