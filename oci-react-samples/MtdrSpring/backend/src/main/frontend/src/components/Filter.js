import React from "react";
import '../styles/HeaderMngr.css';

function Filter({
    employees = [],
    selectedDeveloper = "all",
    setSelectedDeveloper = () => {},
}) {

    return (
        <div className="filter-container">
        <label htmlFor="developer-filter">Filter by Developer:</label>
        <select
            id="developer-filter"
            value={selectedDeveloper}
            onChange={(e) => {
            const value = e.target.value === "all" ? "all" : parseInt(e.target.value);
            setSelectedDeveloper(value);
            }}
            className="filter-select"
        >
            <option value="all">All Developers</option>
            {employees.map((employee) => (
            <option key={employee.id} value={employee.id}>
                {employee.name}
            </option>
            ))}
        </select>
        </div>
        )
    }
export default Filter;