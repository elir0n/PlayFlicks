import {Link} from "react-router-dom";

import styles from  '../../styles/login.module.css'

function RegisterPrompt() {
    return <h1 className={styles.registerTextLink}>
        New to Playflex? <Link className={styles.registerLink} to='/register'>Sign up now.</Link>
    </h1>
}


export default RegisterPrompt;