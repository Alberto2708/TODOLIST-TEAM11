import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Login";
import UserTaskVisualization from "./UserTaskVisualization";
import ManagerTaskVis from "./ManagerTaskVis";
import LoadingScreen from "./LoadingScreen";
import CompletedTasks from "./CompletedTasks";
import Stats from "./Stats";


const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/usertasks" element= {<UserTaskVisualization/>} />
        <Route path="/managertasks" element= {<ManagerTaskVis/>} />
        <Route path="/loading" element={<LoadingScreen/>} />
        <Route path="/completedtasks" element={<CompletedTasks/>} />
        <Route path="/stats" element={<Stats/>} />
      </Routes>
    </Router>
  );
};

export default App;
