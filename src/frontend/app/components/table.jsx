import React from 'react'
import Link from 'next/link';
import {formatCost} from "@/app/styles/format-cost";
import {getStageClass} from "@/app/styles/get-stage-class";

//Loads in the data mapped from inventory/page.jsx 
//Checkboxes missing functionality 
//Edit missing functionality


const Table = (props) => {
  return (
    <tr className="bg-white border-b hover:bg-gray-50">
      <td className="w-4 p-4">
        <div className="flex items-center">
          <input type="checkbox" className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500" />
        </div>
      </td>
      <th scope="row" className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap">
        {
        props.type == "Airplane" ? 
        <div className='text-blue-600'>
          <Link href="../inventory/[id]" as={`../inventory/${props.id}`}>{props.product}</Link>
        </div>
        :
          props.product
        }
      </th>
      <td className="px-6 py-4">
      {props.city + ', ' + props.state}
      </td>
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
    </tr>
  )
}

export default Table