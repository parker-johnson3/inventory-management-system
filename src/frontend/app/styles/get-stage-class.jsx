/**
 * Conditionally renders a color scheme for different stages
 * @param stage the stage the product is in - Unstarted, In-Progress, or Finished
 * @returns CSS for stage
 */
export const getStageClass = (stage) => {
    switch (stage) {
        case "Unstarted":
            return "bg-red-500";
        case "In-Progress":
            return "bg-yellow-500 ";
        case "Finished":
            return "bg-green-500";
        default:
            return "";
    }
}