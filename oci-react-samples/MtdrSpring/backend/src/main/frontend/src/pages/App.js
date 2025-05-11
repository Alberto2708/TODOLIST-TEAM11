import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Login";
import UserTaskVisualization from "./UserTaskVisualization";
import ManagerTaskVis from "./ManagerTaskVis";
import LoadingScreen from "./LoadingScreen";
import CompletedTasks from "./CompletedTasks";
import Stats from "./Stats";
import UserCompletedTasks from "./UserCompletedTasks";
import { AuthProvider } from "../context/AuthContext"; // 👈 Import your context
import SprintCarousel from "./SprintCarousel";
import HistoricalStats from "./HistoricalStats";

const App = () => {
  return (
    <AuthProvider> 
      <Router>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/usertasks" element={<UserTaskVisualization />} />
          <Route path="/managertasks" element={<ManagerTaskVis />} />
          <Route path="/loading" element={<LoadingScreen />} />
          <Route path="/completedtasks" element={<CompletedTasks />} />
          <Route path="/stats" element={<Stats />} />
          <Route path="/usercompletedtasks" element={<UserCompletedTasks />} />
          <Route path="/sprintcarousel" element={<SprintCarousel />} />
          <Route path="/historicalstats" element={<HistoricalStats />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;
