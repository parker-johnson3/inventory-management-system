import React from 'react'

const InventoryModal = (props) => {
    if (!props.showAirplaneModal) return null;

    return (
        <div className='fixed inset-0 flex backdrop-blur-sm justify-center items-center'>
            <div className='bg-indigo-200 p-4 w-full max-w-md max-h-full shadow-2xl rounded-lg'>
                <div className="flex items-center justify-between pb-4">
                    <h3 className="text-lg font-semibold text-gray-900">
                        Add Airplane
                    </h3>
                    <button type="button" onClick={props.onClose} className="text-black bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center" data-modal-toggle="crud-modal">
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
                        <label htmlFor="city" className="block text-gray-700">City:</label>
                        <input type="text" id="city" placeholder="City" name="city" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="state" className="block text-gray-700">State:</label>
                        <input type="text" id="city" placeholder="State" name="state" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="production stage" className="block text-gray-700">Production Stage:</label>
                        <input type="text" id="production stage" placeholder="Production Stage" name="production stage" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="cost" className="block text-gray-700">Cost:</label>
                        <input type="number" id="cost" name="cost" min='0' className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="start date" className="block text-gray-700">Start Date:</label>
                        <input type="text" id="components" placeholder="Start Date" name="start date" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="end date" className="block text-gray-700">End Date:</label>
                        <input type="text" id="end date" placeholder="End Date" name="end date" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="seating capacity" className="block text-gray-700">Seating Capacity:</label>
                        <input type="number" id="seating capacity" placeholder="Seating Capacity" min='0' name="seating capacity" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>
                    <div className="mb-6">
                        <label htmlFor="has first class" className="block text-gray-700">Has First Class:</label>
                        <input type="boolean" id="has first class" placeholder="True/False" name="seating capacity" className="form-input mt-1 pl-2 block w-full rounded" />
                    </div>

                    {/*When full implemented this would submit a POST request to API given the data inputed in the modal*/}
                    <button type="submit" className="float-right text-white inline-flex items-center bg-green-700 hover:bg-green-800 font-semibold rounded-lg text-sm px-3 py-1.5 text-center">
                        <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                        Add New Airplane
                    </button>
                </form>
            </div>
        </div>
    )
}

export default InventoryModal;
