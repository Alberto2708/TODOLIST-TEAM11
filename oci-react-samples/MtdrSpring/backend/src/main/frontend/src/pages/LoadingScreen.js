import { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../styles/ManagerTaskVis.css';
import React from 'react';

export default function LoadingScreen(){

    return(
        <div className="loading-screen">
            <div className="spinner"></div>
        </div>
    );

}