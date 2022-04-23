import './App.css';
import {
  Link,
  useRoutes
} from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import {Fragment, useEffect} from "react";
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

const App = () => {

  const routes = useRoutes([
    { path: '/', element: <HomePage /> },
    { path: '/user-info-page', element: <UserInfoPage /> },
    { path: '/authorization-info-page', element: <AuthorizationInfoPage /> },
    { path: '/redirect-user/:originCountry/:destinationCountry/:userTid', element: <RedirectUser /> },
    { path: '/change-subject', element: <ChangeSubjectPage /> },
    { path: '/registration-form-entry', element: <RegistrationFormPage /> },
    { path: '/simulate-authorization-service', element: <SimulateAuthorizationServicePage /> },
    { path: '/logout', element: <LogoutPage /> },

  ]);

  // get navbar state from redux toolkit store
  const navbarSelectors = useSelector(state => state.navigationBar);
  const userData = useSelector(state => state.userData);
  console.log(userData, 'sessionIndex')
  // replace navbar on navbar property change
  useNavbarEffect(navbarSelectors, userData);

  return (
      <div className="App">
        <div className="egradani_traka-holder" style={{background: '#eeeeee'}}/>
        <div>
          <nav className="navbar navbar-expand" style={{backgroundColor: '#343a40'}}>
            <div className="navbar-nav mr-auto">
              <li className="nav-item">
                <Link to={"/"} className="nav-link" style={{color:'white'}}>
                  Home
                </Link>
              </li>
               {/*TODO: HIDE THIS IF NOT LOGGED IN*/}
              <li className="nav-item">
                <Link to={"/user-info-page"} className="nav-link" style={{color:'white'}}>
                  User info
                </Link>
              </li>
              {/*TODO: HIDE THIS IF NOT LOGGED IN*/}
              <li>
                <Link to={"/authorization-info-page"} className="nav-link" style={{color:'white'}}>
                  Authorization info
                </Link>
              </li>
              {/*TODO: HIDE THIS IF NOT LOGGED IN*/}
              <li>
                <Link to={"/simulate-authorization-service"} className="nav-link" style={{color:'white'}}>
                  Simulate authorization service
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
