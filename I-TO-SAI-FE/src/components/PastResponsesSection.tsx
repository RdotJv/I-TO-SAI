import { useState } from "react";
import { Accordion } from "@radix-ui/react-accordion";
import { ChartColumnBig } from "lucide-react";
import type { DayResponse } from "@/App";
import { PastResponse } from "./PastResponse";

interface Props {
  pastResponses: DayResponse[];
}
const PAGE_SIZE = 10;

export const PastResponsesSection = ({ pastResponses }: Props) => {
  // const [visibleCount, setVisibleCount] = useState(10);

  // const visibleResponses = pastResponses.slice(0, visibleCount);

  // const handleShowMore = () => {
  //   setVisibleCount((c) => Math.min(c + 10, pastResponses.length));
  // };

  // const handleShowLess = () => {
  //   setVisibleCount(10);
  // };
  const [pageIndex, setPageIndex] = useState(0);
  const totalPages = Math.ceil(pastResponses.length / PAGE_SIZE);

  const start = pageIndex * PAGE_SIZE;
  const end = Math.min(start + PAGE_SIZE, pastResponses.length);
  const visibleResponses = pastResponses.slice(start, end);

  const handleNext = () => {
    setPageIndex((i) => Math.min(i + 1, totalPages - 1));
  };

  const handlePrev = () => {
    setPageIndex((i) => Math.max(i - 1, 0));
  };


  return (
    <div className={`w-1/2 ${pastResponses.length === 0 ? "mr-40" : "mr-20"}`}>
      {pastResponses.length === 0 ? (
        <div className="flex flex-col items-center justify-center bg-white border border-gray-200 rounded-2xl shadow-lg p-8 mt-5 space-y-4 min-w-75">
          <ChartColumnBig className="h-12 w-12 text-yellow-500" />
          <h3 className="text-xl font-semibold text-gray-800">
            No Reflections Yet
          </h3>
          <p className="text-gray-600 text-center max-w-xs">
            Once you submit your first daily reflection, it’ll show up here.  
            Let’s get started!
          </p>
        </div>
      ) : (
        <>
          <Accordion
            type="single"
            collapsible
            className="w-full max-w-md min-w-55 [@media(min-width:1200px)]:mx-auto my-2 bg-white border border-gray-200 rounded-xl shadow font-sans"
          >
            {visibleResponses.map((dr) => (
              <PastResponse key={dr.dayIndex} dayResponse={dr} />
            ))}
          </Accordion>

          {/* <div className="flex justify-center space-x-4 mt-4">
            {visibleCount < pastResponses.length && (
              <button
                onClick={handleShowMore}
                className="
                  px-4 py-2 font-semibold text-white rounded-xl 
                  bg-gradient-to-r from-yellow-400 to-yellow-500 
                  hover:from-yellow-500 hover:to-yellow-600 
                  transition-shadow shadow-md
                "
              >
                Show more
              </button>
            )}
            {visibleCount > 10 && (
              <button
                onClick={handleShowLess}
                className="
                  px-4 py-2 font-semibold text-white rounded-xl 
                  bg-gradient-to-r from-orange-400 to-orange-500 
                  hover:from-orange-500 hover:to-orange-600 
                  transition-shadow shadow-md
                "
              >
                Show less
              </button>
            )}
          </div> */}
          <div className="flex flex-col items-center justify-center mt-4">
          <div className="flex items-center justify-center space-x-4">
            <button
              onClick={handlePrev}
              disabled={pageIndex === 0}
              className={`px-4 py-2 font-semibold text-white rounded-xl transition-shadow shadow-md
                bg-gradient-to-r from-orange-400 to-orange-500
                hover:from-orange-500 hover:to-orange-600
                ${pageIndex === 0 ? "opacity-50 cursor-not-allowed" : ""}`}
            >
              Prev
            </button>

            <button
              onClick={handleNext}
              disabled={pageIndex >= totalPages - 1}
              className={`px-4 py-2 font-semibold text-white rounded-xl transition-shadow shadow-md
                bg-gradient-to-r from-yellow-400 to-yellow-500
                hover:from-yellow-500 hover:to-yellow-600
                ${pageIndex >= totalPages - 1 ? "opacity-50 cursor-not-allowed" : ""}`}
            >
              Next
            </button>
          </div>
            <span className="text-gray-500 font-medium">
              {start + 1}–{end} of {pastResponses.length}
            </span>

          </div>
        </>
      )}
    </div>
  );
};
