import style from '../../styles/register.module.css'
import {Link} from "react-router-dom";
function Header() {
    return (
        <div className={style.header}>
            <Link className={style.signIn} to='/login'>Sign In</Link>
        </div>
    );
}

export default Header;