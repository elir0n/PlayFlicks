import style from '../../styles/login.module.css'
import {Link} from "react-router-dom";

function ErrorAuthentication(props) {
    return (
        <div className={style.authenticationError}
             style={{display: props.display}}>
            Sorry, we can't find an account with this username. Please try again or&nbsp;
            <Link to='/register'>
                create a new account
            </Link>
        </div>
    );
}


export default ErrorAuthentication;