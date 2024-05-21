import React from 'react'
import {getStageClass} from "@/app/styles/get-stage-class";
import {formatCost} from "@/app/styles/format-cost";

//Loads in the data mapped from inventory/page.jsx 
//Checkboxes missing functionality 
//Edit missing functionality
const TableFacilities = (props) => {
  return (

    <tr className="bg-white border-b hover:bg-gray-50">
      <td className="w-4 p-4">
        <div className="flex items-center">
          <input id="checkbox-table-search-1" type="checkbox" className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500" />
          <label htmlFor="checkbox-table-search-1" className="sr-only">checkbox</label>
        </div>
      </td>
      <th scope="row" className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap">
        {props.product}
      </th>
      <td className="px-6 py-4">
        {props.type}
      </td>
      <td className="px-6 py-4">
          {'$'}{formatCost(props.cost.toFixed(2))}
      </td>
      <td className="px-6 py-4 text-black">
        <span className={`px-2 py-1 rounded opacity-50 ${getStageClass(props.stage)}`}>
            {props.stage}
        </span>
      </td>
      <td className="px-6 py-4">
        {props.id}
      </td>
      <td className="px-6 py-4">
        <a href="#" className="font-medium text-blue-600 hover:underline">Edit</a>
      </td>
    </tr>
  )
}

export default TableFacilities