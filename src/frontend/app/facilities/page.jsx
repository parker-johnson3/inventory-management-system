'use client';

import React, { useState, useEffect } from 'react';
import DropdownCard from '@/app/components/dropdown-card';
import FacilityModal from "@/app/components/facility-modal";

/**
 * Main page for facilities, containing dropdown cards to display each facility
 */
const Facilities = () => {

  //Get env variables for fetching
  const host = process.env.WEBSERVER_HOST || 'localhost';
  const port = process.env.WEBSERVER_PORT || 5000;
  const url = `http://${host}:${port}`;

  const [facilities, setFacilities] = useState([]);
  const [componentData, setComponentData] = useState([]);
  const [airplaneData, setAirplaneData] = useState([]);
  const [showModal, setShowModal] = useState(false);


  /**
   * Maps facility fields from API to more readable fields for use on frontend
   *
   * @param facility
   * @returns facility object
   */
  const mapFacilityToCard = (facility) => {
    return {
      id: facility.ID,
      city: facility.city,
      state: facility.state,
      components: facility.components_completed,
      componentsInProgress: facility.components_in_production,
      description: facility.description,
      employees: facility.employee_count,
      manager: facility.manager_id,
      airplanes: facility.models_completed,
      airplanesInProgress: facility.models_in_production,
      name: facility.name,
      type: facility.type
    }
  }

  //This will load in all the data from API
  useEffect(() => {
    fetch(`${url}/facility`)
    .then(res => res.json())
    .then(data => data.map(mapFacilityToCard))
    .then((facility) => setFacilities(facility))
  }, []);

  //console log for checking
  useEffect(() => {
    console.log(facilities);
  }, [facilities]);

  //Load in all the data from API into corresponding arrays using concurrent fetching
  useEffect(() => {
    const fetchAirplaneData = fetch(`${url}/airplane`).then(res => res.json());
    const fetchComponentData = fetch(`${url}/component`).then(res => res.json());

    //simultaneously get airplane and component data
    Promise.all([fetchAirplaneData, fetchComponentData])
        .then(([airplaneData, componentData]) => {
          setAirplaneData(airplaneData);
          setComponentData(componentData);
        })
        .catch(error => console.error('Error fetching airplane and component data:', error));
  }, [url]);

  /**
   * Makes DELETE request to API to delete facility
   * and updates displayed facilities
   *
   * @param id the id of the plane to be deleted
   */
  const handleDelete = (id) => {
    console.log(`Deleted item with ID: ${id}`);

    fetch(`${url}/facility/${id}`, {
      method: 'DELETE'
    })
        .then(() => {
          const updatedFacilities = facilities.filter((facility) => facility.id !== id);
          setFacilities(updatedFacilities);
        })
        .catch((error) => console.error('Error deleting item:', error));
  }

  //sets showing modal to false when pressing exit button
  const handleOnModalClose = () => {
    setShowModal(false)
  }

  return (
    <>
      {/* Header containing button to add facility */}
      <header className="mt-6 mb-4">
        <div className="lg:flex lg:items-center lg:justify-between">
          <div className="min-w-0 flex-1 ml-4">
            <h2 className="text-2xl font-medium leading-7 text-gray-900">All Facilities ({facilities.length})</h2>
          </div>

          {/* Button to bring up modal to add a facility */}
          <div className="mt-5 flex lg:ml-4 lg:mt-0">
            <div className="flex justify-center">
              <button className="font-medium bg-indigo-300 hover:bg-indigo-400 rounded-md px-4 py-2"
                      onClick={() => setShowModal(true)}>
                Add Facility
              </button>
            </div>
          </div>

        </div>
      </header>

      {/* Modal is rendered for main page, but displayed based on {showModal} */}
      <FacilityModal showModal={showModal} onClose={handleOnModalClose} />
      <div>

        {/* Pass airplane data, component data, and delete button to be contained in dropdown */}
        {
          facilities.map(facility => {
            return <DropdownCard
                props={facility}
                airplaneData={airplaneData}
                componentData={componentData}
                onDelete={handleDelete}
                key={facility.id}
            />
          })
        }
      </div>
    </>
  )
}

export default Facilities
