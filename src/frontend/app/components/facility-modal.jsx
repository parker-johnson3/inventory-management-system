import React from 'react'

const FacilityModal = (props) => {

    //if not displayed already then no need to return anything, and stay hidden
    if (!props.showModal) return null;

    return (
        <div className='fixed inset-0 flex backdrop-blur-sm justify-center items-center'>
            <div className='bg-indigo-200 p-4 w-full max-w-md max-h-full shadow-2xl rounded-lg'>
                <div className="flex items-center justify-between pb-4 dark:border-gray-600">
                    <h3 className="text-lg font-semibold text-gray-900">
                        Add New Facility
                    </h3>

                    {/* Button to close modal */}
                    <button type="button" onClick={props.onClose} className="text-black bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white" data-modal-toggle="crud-modal">
                        <svg className="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                            <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
                        </svg>
                    </button>
                </div>

                {/* Main form to fill out to add a facility */}
                <form >
                    {/* City */}
                    <div className="mb-4">
                        <label htmlFor="city" className="block text-gray-700">City:</label>
                        <input type="text" id="city" placeholder="City" name="city" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>

                    {/* State */}
                    <div className="mb-4">
                        <label htmlFor="state" className="block text-gray-700">State:</label>
                        <input type="text" id="state" placeholder="State" name="state" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>

                    {/* Number of airplanes */}
                    <div className="mb-4">
                        <label htmlFor="airplanes" className="block text-gray-700">Number of Airplanes:</label>
                        <input type="number" id="airplanes" placeholder="0" min="0" name="airplanes" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>

                    {/* Number of components */}
                    <div className="mb-4">
                        <label htmlFor="components" className="block text-gray-700">Number of Components:</label>
                        <input type="number" id="components" placeholder="0" min="0" name="components" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>

                    {/* Number of employees */}
                    <div className="mb-4">
                        <label htmlFor="employees" className="block text-gray-700">Number of Employees:</label>
                        <input type="number" id="employees" placeholder="0" min="0" name="employees" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>

                    {/* Name of manager */}
                    <div className="mb-4">
                        <label htmlFor="manager" className="block text-gray-700">Manager:</label>
                        <input type="text" id="manager" placeholder="Manager" name="manager" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>

                    {/* Submit button */}
                    <button type="submit" className="text-white inline-flex items-center bg-green-700 hover:bg-green-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center">
                        <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                        Add New Facility
                    </button>
                </form>
            </div>
        </div>
    )
}

export default FacilityModal;
