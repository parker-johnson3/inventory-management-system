/**
 * Method to format cost field
 * @param cost the cost field from a table
 * @returns the cost formatted with commas
 */
export const formatCost = (cost) => {
    return cost.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}