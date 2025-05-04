import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Login";
import UserTaskVisualization from "./UserTaskVisualization";
import ManagerTaskVis from "./ManagerTaskVis";
import LoadingScreen from "./LoadingScreen";
import CompletedTasks from "./CompletedTasks";
import Stats from "./Stats";
import UserCompletedTasks from "./UserCompletedTasks";
import { AuthProvider } from "../context/AuthContext"; // 👈 Import your context

const App = () => {
  return (
    <AuthProvider> {/* 👈 Wrap the whole Router inside AuthProvider */}
      <Router>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/usertasks" element={<UserTaskVisualization />} />
          <Route path="/managertasks" element={<ManagerTaskVis />} />
          <Route path="/loading" element={<LoadingScreen />} />
          <Route path="/completedtasks" element={<CompletedTasks />} />
          <Route path="/stats" element={<Stats />} />
          <Route path="/usercompletedtasks" element={<UserCompletedTasks />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;
