'use client';

import React, { useState, useEffect } from 'react';
import Table from '@/app/components/table';
import AirplaneModal from '@/app/components/inventory-modal-airplane';
import ComponentModal from '@/app/components/inventory-modal-component';



const MasterInventory = () => {

  //Data loading
  const [fullInventory, setFullInventory] = useState([]);
  const [fullInventoryDisplay, setFullInventoryDisplay] = useState([]);
  const [componentData, setComponentData] = useState([]);
  const [airplaneData, setAirplaneData] = useState([]);
  //Modal toggling
  const [showAirplaneModal, setShowAirplaneModal] = useState(false);
  const [showComponentModal, setShowComponentModal] = useState(false);
  //Pagination (active page state)
  const [pageActive, setPageActive] = useState(1);
  //Sort, Filter, and Search variables
  const [prodLookup, setProdLookup] = useState("");
  const [filterIsOpen, setFilterIsOpen] = useState(false);
  const [sortBy, setSortBy] = useState(null);
  const [sortOrder, setSortOrder] = useState('asc');
  //Declares the filter checkboxes as unchecked by default
  const [filters, setFilters] = useState({
    finished: false,
    inProgress: false,
    unstarted: false,
    airplane: false,
    component: false,
    lessThan50k: false,
    between50kAnd99k: false,
    between100kAnd999k: false,
    between1mAnd10m: false,
    greaterThan10m: false
  })
  const host = process.env.WEBSERVER_HOST || 'localhost';
  const port = process.env.WEBSERVER_PORT || 5000;
  const url = `http://${host}:${port}`;

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

  // Merge fetched data into one array at runtime
  useEffect(() => {
    if (airplaneData && componentData) {
      setFullInventory([...airplaneData, ...componentData])
      setFullInventoryDisplay([...airplaneData, ...componentData])
    }
  }, [airplaneData, componentData]);

  //Search & Filter helper
  useEffect(() => {
    const results = fullInventoryDisplay.filter((data) => {
      //Search logic
      const matchesSearch =
        (data.ID.toString().includes(prodLookup.trim())) ||
        ((data.name.toString()).toLowerCase().includes(prodLookup.toLowerCase().trim()));

      //Filter logic
      const meetsProductionStageFilter =
        (!filters.finished || data.production_stage === 'Finished') &&
        (!filters.inProgress || data.production_stage === 'In-Progress') &&
        (!filters.unstarted || data.production_stage === 'Unstarted')

      const meetsTypeFilter =
        (!filters.airplane || data.type === 'Airplane') &&
        (!filters.component || data.type === 'Component')

      const meetsCostFilter =
        (!filters.lessThan50k || data.cost < 50000) &&
        (!filters.between50kAnd99k || (data.cost >= 50000 && data.cost <= 99999.99)) &&
        (!filters.between100kAnd999k || (data.cost >= 100000 && data.cost <= 999999.99)) &&
        (!filters.between1mAnd10m || (data.cost >= 1000000 && data.cost <= 10000000)) &&
        (!filters.greaterThan10m || data.cost > 10000000)

      return matchesSearch && meetsProductionStageFilter && meetsTypeFilter && meetsCostFilter;
    });

    setFullInventory(results);
  }, [prodLookup, filters, fullInventoryDisplay]);


  const handleModalAirplaneClose = () => {
    setShowAirplaneModal(false)
  }
  const handleModalComponentClose = () => {
    setShowComponentModal(false)
  }

  const handleSearchReset = () => {
    setProdLookup("");
  }

  const handleCheckboxChange = (event) => {
    const { name, checked } = event.target;
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: checked
    }));
  }

  //Pagination helper
  let pages = []
  const pageBuilder = () => {
    const numPages = Math.ceil(fullInventory.length / 12);
    for (let i = 1; i <= numPages; i++) { //Used to declare an active page and push to pages array for navigation
      if (pageActive === i) {
        pages.push(
          <a className="flex items-center justify-center px-3 h-8 leading-tight text-blue-600 border border-gray-300 bg-blue-50 hover:bg-blue-100 hover:text-blue-700"
            aria-current="true"
            key={i}
            onClick={() => setPageActive(i)}>{i}</a>)
      }
      else {
        pages.push(
          <a className="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700"
            aria-current="false"
            key={i}
            onClick={() => setPageActive(i)}>{i}</a>)
      }
    }
    return pages
  }

  //Previous and Next button functionality
  const handleNextPage = () => {
    setPageActive(prevPage => prevPage + 1);
  }
  const handlePreviousPage = () => {
    setPageActive(prevPage => prevPage - 1);
  }

  //Sort handler
  const handleSort = (columnName) => {
    if (sortBy === columnName) {
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(columnName);
      setSortOrder('asc');
    }
    setFullInventory(fullInventory.sort((a, b) => {
      let valueA = a[sortBy];
      let valueB = b[sortBy];

      if (typeof valueA === 'number' && typeof valueB === 'number') {
        return sortOrder === 'asc' ? valueA - valueB : valueB - valueA;
      } else {
        return sortOrder === 'asc' ?
          String(valueA).localeCompare(String(valueB)) : String(valueB).localeCompare(String(valueA));
      }
    })
    );
  }

  return (
    <>
      {/*Header to displays results, filter button, and search bar*/}
      <header className="mt-6 mb-4">
        <div className="lg:flex lg:items-center lg:justify-between">
          <div className="min-w-0 flex-1 ml-4">
            <h2 className="text-2xl font-medium leading-7 text-gray-900">All Products ({fullInventory.length})</h2>
          </div>
          <div className="mt-5 flex lg:ml-4 lg:mt-0">

            {/*Filter button*/}
            <div className="ml-3 hidden sm:block mr-2 relative">
              <button onClick={() => setFilterIsOpen(!filterIsOpen)} type="button" className="inline-flex items-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-700 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50">
                <span className="sr-only">filter button</span>
                Filter
                <svg className="w-2.5 h-2.5 ms-2.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 10 6">
                  <path stroke="currentColor" strokeWidth="2" d="m1 1 4 4 4-4" />
                </svg>
              </button>

              {/*Dropdown from filter to select what you want to filter by*/}
              {filterIsOpen && (
                <div className="z-10 w-48 p-3 bg-white rounded-lg shadow absolute top-full mt-1 right-0">
                  <h6 className="mb-2 text-md font-bold text-gray-700">Production Stage</h6>
                  <ul className="space-y-2 text-md mb-4">
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="finished"
                        checked={filters.finished}
                        onChange={handleCheckboxChange}
                        disabled={filters.inProgress || filters.unstarted} />
                      <label className="ml-2 text-md font-medium text-gray-700">Finished</label>
                    </li>
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="inProgress"
                        checked={filters.inProgress}
                        onChange={handleCheckboxChange}
                        disabled={filters.finished || filters.unstarted} />
                      <label className="ml-2 text-md font-medium text-gray-700">In-Progress</label>
                    </li>
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="unstarted"
                        checked={filters.unstarted}
                        onChange={handleCheckboxChange}
                        disabled={filters.finished || filters.inProgress} />
                      <label className="ml-2 text-md font-medium text-gray-700">Unstarted</label>
                    </li>
                  </ul>
                  <h6 className="mb-2 text-md font-bold text-gray-700">Type</h6>
                  <ul className="space-y-2 text-md mb-4">
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="airplane"
                        checked={filters.airplane}
                        onChange={handleCheckboxChange}
                        disabled={filters.component} />
                      <label className="ml-2 text-md font-medium text-gray-700">Airplane</label>
                    </li>
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="component"
                        checked={filters.component}
                        onChange={handleCheckboxChange}
                        disabled={filters.airplane} />
                      <label className="ml-2 text-md font-medium text-gray-700">Component</label>
                    </li>
                  </ul>
                  <h6 className="mb-2 text-md font-bold text-gray-700">Cost</h6>
                  <ul className="space-y-2 text-md mb-3">
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="lessThan50k"
                        checked={filters.lessThan50k}
                        onChange={handleCheckboxChange}
                        disabled={filters.between50kAnd99k || filters.between100kAnd999k || filters.between1mAnd10m || filters.greaterThan10m} />
                      <label className="ml-2 text-md font-medium text-gray-700">{"< $50k"}</label>
                    </li>
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="between50kAnd99k"
                        checked={filters.between50kAnd99k}
                        onChange={handleCheckboxChange}
                        disabled={filters.lessThan50k || filters.between100kAnd999k || filters.between1mAnd10m || filters.greaterThan10m} />
                      <label className="ml-2 text-md font-medium text-gray-700">{"$50k - $99k"}</label>
                    </li>
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="between100kAnd999k"
                        checked={filters.between100kAnd999k}
                        onChange={handleCheckboxChange}
                        disabled={filters.lessThan50k || filters.between50kAnd99k || filters.between1mAnd10m || filters.greaterThan10m} />
                      <label className="ml-2 text-md font-medium text-gray-700">{"$100k - $999k"}</label>
                    </li>
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="between1mAnd10m"
                        checked={filters.between1mAnd10m}
                        onChange={handleCheckboxChange}
                        disabled={filters.lessThan50k || filters.between50kAnd99k || filters.between100kAnd999k || filters.greaterThan10m} />
                      <label className="ml-2 text-md font-medium text-gray-700">{"$1m - $10m"}</label>
                    </li>
                    <li className="flex items-center">
                      <input
                        className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                        type="checkbox"
                        name="greaterThan10m"
                        checked={filters.greaterThan10m}
                        onChange={handleCheckboxChange}
                        disabled={filters.lessThan50k || filters.between50kAnd99k || filters.between100kAnd999k || filters.between1mAnd10m} />
                      <label className="ml-2 text-md font-medium text-gray-700">{"> $10m"}</label>
                    </li>
                  </ul>
                </div>
              )}
            </div>


            {/*Search bar - search by ID and/or Product Name*/}
            <div className="flex items-center flex-column flex-wrap md:flex-row ml-3 hidden sm:block mr-4">
              <div className="relative">
                <input id="search" className="block w-full rounded-md bg-white px-3 py-1.5 text-base font-normal text-gray-700 shadow-sm ring-1 ring-inset ring-gray-300 transition duration-200 ease-in-out placeholder:text-gray-400 focus:border-primary focus:shadow-inset focus:outline-none motion-reduce:transition-none"
                  placeholder="Search"
                  maxLength={10}
                  onChange={(e) => setProdLookup(e.target.value)}
                  value={prodLookup} />

                {/*Reset search button*/}
                <button className="text-white absolute end-1 bottom-1 bg-blue-600 hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-md text-sm px-3 py-1"
                  onClick={handleSearchReset}>
                  Reset
                </button>
              </div>
            </div>
          </div>
        </div>
      </header>

      {/*Table display*/}
      <div className="relative overflow-x-auto shadow-md sm:rounded-lg bg-gray-100">
        <table className="w-full text-sm text-left rtl:text-right text-gray-500">

          {/*Table header*/}
          <thead className="text-xs text-gray-700 uppercase bg-gray-100">
            <tr>
              <th scope="col" className="p-4">
              </th>
              <th scope="col" className="px-6 py-3" onClick={() => handleSort('name')}>
                Product {sortBy === 'name' && (sortOrder === 'asc' ? '▲' : '▼')}
              </th>
              <th scope="col" className="px-6 py-3">
                Facility
              </th>
              <th scope="col" className="px-6 py-3" onClick={() => handleSort('type')}>
                Type {sortBy === 'type' && (sortOrder === 'asc' ? '▲' : '▼')}
              </th>
              <th scope="col" className="px-6 py-3" onClick={() => handleSort('cost')}>
                Cost {sortBy === 'cost' && (sortOrder === 'asc' ? '▲' : '▼')}
              </th>
              <th scope="col" className="px-6 py-3" onClick={() => handleSort('production_stage')}>
                Production Stage {sortBy === 'production_stage' && (sortOrder === 'asc' ? '▲' : '▼')}
              </th>
              <th scope="col" className="px-6 py-3" onClick={() => handleSort('ID')}>
                ID {sortBy === 'ID' && (sortOrder === 'asc' ? '▲' : '▼')}
              </th>
            </tr>
          </thead>

          {/*Table body (data sent as props to components/tables.jsx)*/}
          <tbody>
            {
              fullInventory.slice(12 * (pageActive - 1), 12 * pageActive).map(data => {
                return <Table key={data.ID}
                  city={data.city}
                  state={data.state}
                  cost={data.cost}
                  product={data.name}
                  type={data.type}
                  stage={data.production_stage}
                  id={data.ID} />
              })
            }
          </tbody>
        </table>

        <div className="flex items-center justify-between mt-2 mb-2 mr-4">
          {/*Page navigation*/}
          <nav className="flex items-center flex-column flex-wrap md:flex-row justify-between mt-2 mb-2 ml-4">
            <ul className="inline-flex -space-x-px rtl:space-x-reverse text-sm h-8">
              <button onClick={handlePreviousPage} disabled={(pageActive === 1)} className="flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700">Previous
              </button>
              {pageBuilder()}
              <button onClick={handleNextPage} disabled={(pageActive === (pages.length))} className="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700">Next
              </button>
            </ul>
          </nav>

          {/*Add product buttons*/}
          <div className="flex">

            {/*Add new airplane to full inventory (opens modal from components/inventory-modal-airplane.jsx to enter information)*/}
            <button className="mr-2 inline-flex items-center rounded-md bg-blue-300 px-3 py-1.5 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-blue-300 hover:bg-blue-700"
              onClick={() => setShowAirplaneModal(true)}>
              <svg className="h-5 w-5 mr-2" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" />
              </svg>
              Add Airplane
            </button>

            {/*Add new component to full inventory (opens modal from components/inventory-modal-component.jsx to enter information)*/}
            <button className="inline-flex items-center rounded-md bg-blue-300 px-3 py-1.5 text-sm font-semibold text-white shadow-sm ring-1 ring-inset ring-blue-300 hover:bg-blue-700"
              onClick={() => setShowComponentModal(true)}>
              <svg className="h-5 w-5 mr-2" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" />
              </svg>
              Add Component
            </button>
          </div>

        </div>
        <AirplaneModal showAirplaneModal={showAirplaneModal} onClose={handleModalAirplaneClose} />
        <ComponentModal showComponentModal={showComponentModal} onClose={handleModalComponentClose} />
      </div>
    </>
  )
}

export default MasterInventory
