import {Navigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {isAuthenticated} from "../viewmodel/Authenticate";

function PrivateRoute(props) {

    const [authenticated, setAuthenticated] = useState(true);

    useEffect(() => {
        const checkAuthentication = async () => {
            const token = localStorage.getItem("token");
            const authenticated = token && await isAuthenticated(token);
            if (!authenticated) {
                localStorage.removeItem("token");
            }
            setAuthenticated(authenticated);
            console.log(authenticated);
        };

        checkAuthentication();
    }, []);

    return authenticated ? props.children : < Navigate to="/login"/>;
}


export default PrivateRoute;



