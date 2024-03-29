import './App.css';
import {
  Link,
  useRoutes
} from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import HomePage from "./pages/HomePage/HomePage";
import {useSelector} from 'react-redux'
import RedirectUser from "./pages/RedirectUser/RedirectUser";
import {useNavbarEffect} from "./hooks/useNavbarEffect";
import UserInfoPage from "./pages/UserInfoPage/UserInfoPage";
import ChangeSubjectPage from "./pages/ChangeSubjectPage/ChangeSubjectPage";
import AuthorizationInfoPage from "./pages/AuthorizationInfoPage/AuthorizationInfoPage";
import RegistrationFormPage from "./pages/RegistrationFormPage/RegistrationFormPage";
import SimulateAuthorizationServicePage
  from "./pages/SimulateAuthorizationServicePage/SimulateAuthorizationServicePage";
import LogoutPage from "./pages/LogoutPage/LogoutPage";
import SimulateSendMessageToOkpPage from "./pages/SimulateSendMessageToOkpPage/SimulateSendMessageToOkpPage";

const App = () => {

  const routes = useRoutes([
    { path: '/', element: <HomePage /> },
    { path: '/user-info-page', element: <UserInfoPage /> },
    { path: '/authorization-info-page', element: <AuthorizationInfoPage /> },
    { path: '/redirect-user/:originCountry/:destinationCountry/:userTid', element: <RedirectUser /> },
    { path: '/change-subject', element: <ChangeSubjectPage /> },
    { path: '/registration-form-entry', element: <RegistrationFormPage /> },
    { path: '/simulate-authorization-service', element: <SimulateAuthorizationServicePage /> },
    { path: '/simulate-send-message-to-okp', element: <SimulateSendMessageToOkpPage /> },
    { path: '/logout', element: <LogoutPage /> },

  ]);

  // get navbar state from redux toolkit store
  const navbarSelectors = useSelector(state => state.navigationBar);
  const userData = useSelector(state => state.userData);
  // replace navbar on navbar property change
  useNavbarEffect(navbarSelectors, userData);

  return (
      <div className="App">
        <div className="egradani_traka-holder" style={{background: '#eeeeee'}}/>
        <div>
          <nav className="navbar navbar-expand" style={{backgroundColor: '#343a40'}}>
            <div className="navbar-nav mr-auto">
              <li className="nav-item" key="home">
                <Link to={"/"} className="nav-link" style={{color:'white'}}>
                  Home
                </Link>
              </li>
               {/*TODO: HIDE THIS IF NOT LOGGED IN*/}
              <li className="nav-item" key="user-info">
                <Link to={"/user-info-page"} className="nav-link" style={{color:'white'}}>
                  User info
                </Link>
              </li>
              {/*TODO: HIDE THIS IF NOT LOGGED IN*/}
              <li key="authorization-info">
                <Link to={"/authorization-info-page"} className="nav-link" style={{color:'white'}}>
                  Authorization info
                </Link>
              </li>
              {/*TODO: HIDE THIS IF NOT LOGGED IN*/}
              <li key="simulate-authorization-service">
                <Link to={"/simulate-authorization-service"} className="nav-link" style={{color:'white'}}>
                  Simulate authorization service
                </Link>
              </li>
              <li key="simulate-send-message-okp">
                <Link to={"/simulate-send-message-to-okp"} className="nav-link" style={{color:'white'}}>
                  Send message to OKP
                </Link>
              </li>
            </div>
          </nav>
        </div>
        <div >
          {routes}
        </div>
    </div>
  );
}

export default App;
