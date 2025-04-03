import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Login";
import UserTaskVisualization from "./UserTaskVisualization";
import ManagerTaskVis from "./ManagerTaskVis";
import LoadingScreen from "./LoadingScreen";


const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/usertasks" element= {<UserTaskVisualization/>} />
        <Route path="/managertasks" element= {<ManagerTaskVis/>} />
        <Route path="/loading" element={<LoadingScreen/>} />
      </Routes>
    </Router>
  );
};

export default App;
