import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Login";
import UserTaskVisualization from "./UserTaskVisualization";
import ManagerTaskVis from "./ManagerTaskVis";
import TaskCreation from "./TaskCreation";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/home" element={<h2>Welcome to Home Page</h2>} />
        <Route path="/usertasks" element= {<UserTaskVisualization/>} />
        <Route path="/managertasks" element= {<ManagerTaskVis/>} />
        <Route path="/tc" element= {<TaskCreation/>} />
      </Routes>
    </Router>
  );
};

export default App;
